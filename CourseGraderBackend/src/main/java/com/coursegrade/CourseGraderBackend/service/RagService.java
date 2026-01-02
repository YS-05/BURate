package com.coursegrade.CourseGraderBackend.service;

import com.coursegrade.CourseGraderBackend.model.Course;
import com.coursegrade.CourseGraderBackend.model.HubRequirement;
import com.coursegrade.CourseGraderBackend.model.User;
import com.coursegrade.CourseGraderBackend.repository.UserRepository;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RagService {

    private final UserRepository userRepository;
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final EmbeddingModel embeddingModel;
    private final ChatLanguageModel chatLanguageModel;
    private final Map<Long, ChatMemory> userMemories = new ConcurrentHashMap<>();

    @Transactional
    public String askAdvisor(String userQuestion, Long userId) {
        User student = userRepository.findByIdWithCourses(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        ChatMemory chatMemory = userMemories.computeIfAbsent(userId, id ->
                MessageWindowChatMemory.withMaxMessages(5)
        );
        Embedding questionEmbedding = embeddingModel.embed(userQuestion).content();
        EmbeddingSearchRequest searchRequest = EmbeddingSearchRequest.builder()
                .queryEmbedding(questionEmbedding)
                .maxResults(5) // 5 to stay under 15k TPM limit
                .minScore(0.4)
                .build();
        List<EmbeddingMatch<TextSegment>> relevantMatches = embeddingStore.search(searchRequest).matches();
        String contextInfo = relevantMatches.stream()
                .map(match -> match.embedded().text())
                .collect(Collectors.joining("\n\n---\n\n"));
        String instructionText = buildSystemPrompt(contextInfo, student);
        List<ChatMessage> messagesToSend = new ArrayList<>(chatMemory.messages());
        String combinedPrompt = instructionText + "\n\nUSER QUESTION:\n" + userQuestion;
        messagesToSend.add(UserMessage.from(combinedPrompt));
        var response = chatLanguageModel.generate(messagesToSend);
        chatMemory.add(UserMessage.from(userQuestion));
        chatMemory.add(response.content());
        return response.content().text();
    }

    private String buildSystemPrompt(String context, User student) {
        String major = (student.getMajor() != null) ? student.getMajor() : "Undecided";
        String year = (student.getExpectedGrad() != null) ? String.valueOf(student.getExpectedGrad()) : "Unknown";
        // Default to "CAS" if unknown, or whatever safe default you prefer
        String college = (student.getCollege() != null) ? student.getCollege() : "CAS";

        String coursesTaken = "None";
        if (student.getCompletedCourses() != null && !student.getCompletedCourses().isEmpty()) {
            coursesTaken = student.getCompletedCourses().stream()
                    .map(Course::courseDisplay)
                    .collect(Collectors.joining(", "));
        }

        String missingHubs = "All Requirements Met";
        List<HubRequirement> unfulfilled = student.getUnfulfilledHubs();
        if (unfulfilled != null && !unfulfilled.isEmpty()) {
            missingHubs = unfulfilled.stream()
                    .map(HubRequirement::getCode)
                    .collect(Collectors.joining(", "));
        }
        log.info("Generating prompt for User {}. Missing Hubs String: [{}]", student.getId(), missingHubs);

        return String.format("""
            You are BU Rate's advisor, a helpful, honest academic advisor for Boston University students.
            
            STUDENT PROFILE:
            - Major: %s
            - College: %s
            - Grad Year: %s
            - COURSES ALREADY TAKEN: [%s]
            - UNFULFILLED HUB REQUIREMENTS: [%s]
            
            CONTEXT (Database Results):
            %s
            
            INSTRUCTIONS:
            1. Use the Context to answer. If the answer isn't there, say you don't know.
            2. PERSONALIZATION RULES:
               - Do NOT recommend courses listed in "COURSES ALREADY TAKEN".
               - If the user asks for a recommendation regarding hub requirements, prioritize courses that satisfy their "UNFULFILLED HUB REQUIREMENTS".
               - **ONLY recommend courses from the student's College (%s) unless explicitly asked for other colleges.**
            3. Be concise and practical.
            """,
                major, college, year, coursesTaken, missingHubs, context, college
        );
    }
}