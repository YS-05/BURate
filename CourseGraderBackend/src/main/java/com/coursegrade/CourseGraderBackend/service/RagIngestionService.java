package com.coursegrade.CourseGraderBackend.service;

import com.coursegrade.CourseGraderBackend.model.Course;
import com.coursegrade.CourseGraderBackend.model.HubRequirement;
import com.coursegrade.CourseGraderBackend.model.Review;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RagIngestionService {

    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final JdbcTemplate jdbcTemplate; // Allows running fast SQL deletes

    @Transactional
    public void ingestCourses(List<Course> courses) {
        log.info("Clearing old COURSE embeddings...");
        jdbcTemplate.update("DELETE FROM embeddings WHERE metadata ->> 'type' = ?", "COURSE");
        log.info("Starting ingestion of {} courses...", courses.size());

        for (Course course : courses) {
            try {
                String content = buildCourseContent(course);
                Metadata metadata = new Metadata();
                metadata.put("type", "COURSE");
                metadata.put("course_id", String.valueOf(course.getId()));
                metadata.put("code", course.getCourseCode());
                metadata.put("department", course.getDepartment());
                metadata.put("college", course.getCollege());
                metadata.put("overall rating", course.getAverageOverallRating());
                metadata.put("workload rating", course.getAverageWorkloadRating());
                metadata.put("usefulness rating", course.getAverageUsefulnessRating());
                metadata.put("interest rating", course.getAverageInterestRating());
                metadata.put("professor rating", course.getAverageTeacherRating());
                metadata.put("difficulty rating", course.getAverageDifficultyRating());
                metadata.put("hubs", collectHubs(course));
                TextSegment segment = TextSegment.from(content, metadata);
                Embedding embedding = embeddingModel.embed(segment).content();
                embeddingStore.add(embedding, segment);
            } catch (Exception e) {
                log.error("Failed to ingest course: {}", course.getCourseCode(), e);
            }
        }
        log.info("Finished ingesting courses!");
    }

    @Transactional
    public void ingestReviews(List<Review> reviews) {
        log.info("Clearing old REVIEW embeddings...");
        jdbcTemplate.update("DELETE FROM embeddings WHERE metadata ->> 'type' = ?", "REVIEW");
        log.info("Starting ingestion of {} reviews...", reviews.size());
        for (Review review : reviews) {
            try {
                if (review.getNetReviewScore() < -5) {
                    continue;
                }
                String content = buildReviewContent(review);
                Metadata metadata = new Metadata();
                metadata.put("type", "REVIEW");
                metadata.put("course_id", String.valueOf(review.getCourse().getId()));
                TextSegment segment = TextSegment.from(content, metadata);
                Embedding embedding = embeddingModel.embed(segment).content();
                embeddingStore.add(embedding, segment);
            } catch (Exception e) {
                log.error("Failed to ingest review ID: {}", review.getId(), e);
            }
        }
        log.info("Finished ingesting reviews!");
    }

    private String buildCourseContent(Course course) {
        Boolean noPreReqs = course.getNoPreReqs();
        String preReqText =
                Boolean.TRUE.equals(noPreReqs) ? "None"
                        : Boolean.FALSE.equals(noPreReqs) ? "Required"
                        : "Not specified";
        return "Course: " + course.courseDisplay() + "\n" +
                "Title: " + course.getTitle() + "\n" +
                "Description: " + course.getCourseDesc() + "\n" +
                "Overall Rating: " + course.getAverageOverallRating() + "/5 \n" +
                "Difficulty Rating: " + course.getAverageDifficultyRating() + "/5 \n" +
                "Workload Rating: " + course.getAverageWorkloadRating() + "/5 \n" +
                "Usefulness Rating: " + course.getAverageUsefulnessRating() + "/5 \n" +
                "Interest Rating: " + course.getAverageInterestRating() + "/5 \n" +
                "Teacher Rating: " + course.getAverageTeacherRating() + "/5 \n" +
                "Hubs: " + collectHubs(course) + "\n" +
                "Pre-requisites: " + preReqText + "\n" + // boolean
                "Ratings based on: " + course.getTotalReviews() + " reviews" + "\n";
    }

    private String buildReviewContent(Review review) {
        return "Review for " + review.getCourse().courseDisplay() + "\n" +
                "Professor: " + review.getTeacherName() + "\n" +
                "Assignment types: " + review.getAssignmentTypes() + "\n" +
                "Attendance required: " + review.getAttendanceRequired() + "\n" +
                "Student text: " + review.getReviewText() + "\n" +
                "Hours per week: " + review.getHoursPerWeek() + "\n" +
                "Semester: " + review.getSemester() + "\n" +
                "Overall Rating: " + review.getOverallRating() + "/5 \n" +
                "Difficulty Rating: " + review.getDifficultyRating() + "/5 \n" +
                "Workload Rating: " + review.getWorkloadRating() + "/5 \n" +
                "Usefulness Rating: " + review.getUsefulnessRating() + "/5 \n" +
                "Interest Rating: " + review.getInterestRating() + "/5 \n" +
                "Teacher Rating: " + review.getTeacherRating() + "/5 \n";
    }

    private String collectHubs(Course course) {
        if (course.getHubRequirements() == null || course.getHubRequirements().isEmpty()) {
            return "None";
        }
        return course.getHubRequirements().stream()
                .map(HubRequirement::getCode)
                .collect(Collectors.joining(", "));
    }
}