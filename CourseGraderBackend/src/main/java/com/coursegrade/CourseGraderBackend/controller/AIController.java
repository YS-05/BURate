package com.coursegrade.CourseGraderBackend.controller;

import com.coursegrade.CourseGraderBackend.dto.ChatRequestDTO;
import com.coursegrade.CourseGraderBackend.model.Course;
import com.coursegrade.CourseGraderBackend.model.Review;
import com.coursegrade.CourseGraderBackend.model.User;
import com.coursegrade.CourseGraderBackend.repository.CourseRepository;
import com.coursegrade.CourseGraderBackend.repository.ReviewRepository;
import com.coursegrade.CourseGraderBackend.service.CourseService;
import com.coursegrade.CourseGraderBackend.service.RagIngestionService;
import com.coursegrade.CourseGraderBackend.service.RagService;
import com.coursegrade.CourseGraderBackend.service.ReviewService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {

    private final RagIngestionService ragIngestionService;
    private final RagService ragService;
    private final ReviewRepository reviewRepository;
    private final CourseRepository courseRepository;

    @PostMapping("/sync-courses")
    public ResponseEntity<String> syncCourses() {
        log.info("Admin triggered Course sync...");
        List<Course> allCourses = courseRepository.findAll();
        ragIngestionService.ingestCourses(allCourses);
        return ResponseEntity.ok("Course ingestion started for " + allCourses.size() + " courses.");
    }

    @PostMapping("/sync-reviews")
    public ResponseEntity<String> syncReviews() {
        log.info("Admin triggered Review sync...");
        List<Review> allReviews = reviewRepository.findAll();
        ragIngestionService.ingestReviews(allReviews);
        return ResponseEntity.ok("Review ingestion started for " + allReviews.size() + " reviews.");
    }

    @PostMapping("/chat")
    public ResponseEntity<String> askAdvisor(@RequestBody ChatRequestDTO request, @AuthenticationPrincipal User user) {
        String response = ragService.askAdvisor(request.getMessage(), user.getId());
        return ResponseEntity.ok(response);
    }
}
