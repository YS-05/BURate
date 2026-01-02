package com.coursegrade.CourseGraderBackend.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import dev.langchain4j.data.segment.TextSegment;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AIConfig {

    @Bean
    ChatLanguageModel chatLanguageModel(@Value("${google.api.key}") String googleApiKey) {
        return GoogleAiGeminiChatModel.builder()
                .apiKey(googleApiKey)
                .modelName("gemma-3-12b-it")
                .maxOutputTokens(500)
                .build();
    }

    @Bean
    EmbeddingModel embeddingModel() {
        // model creates vectors with 384 dimensions
        return new AllMiniLmL6V2EmbeddingModel();
    }

    @Bean
    EmbeddingStore<TextSegment> embeddingStore(DataSource dataSource) {
        return PgVectorEmbeddingStore.datasourceBuilder()
                .datasource(dataSource)
                .table("embeddings")
                .dimension(384)
                .build();
    }
}