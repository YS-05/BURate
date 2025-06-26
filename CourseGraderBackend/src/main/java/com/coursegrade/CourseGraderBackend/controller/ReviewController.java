package com.coursegrade.CourseGraderBackend.controller;

import com.coursegrade.CourseGraderBackend.dto.CreateReviewDTO;
import com.coursegrade.CourseGraderBackend.dto.ReviewResponseDTO;
import com.coursegrade.CourseGraderBackend.model.User;
import com.coursegrade.CourseGraderBackend.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/course/{courseId}")
    public ResponseEntity<ReviewResponseDTO> createReview(
            @PathVariable Long courseId,
            @Valid @RequestBody CreateReviewDTO reviewDTO,
            @AuthenticationPrincipal User currentUser
            ) {
        ReviewResponseDTO review = reviewService.createReview(currentUser, courseId, reviewDTO);
        return ResponseEntity.ok(review);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponseDTO> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody CreateReviewDTO reviewDTO,
            @AuthenticationPrincipal User currentUser
    ) {
        ReviewResponseDTO review = reviewService.updateReview(currentUser, reviewId, reviewDTO);
        return ResponseEntity.ok(review);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Map<String, String>> deleteReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal User currentUser
    ) {
        reviewService.deleteReview(currentUser, reviewId);
        return ResponseEntity.ok(Map.of(
                "message", "Review deleted successfully"
        ));
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponseDTO> getReviewById(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal User currentUser
    ) {
        ReviewResponseDTO review = reviewService.getReviewById(reviewId, currentUser);
        return ResponseEntity.ok(review);
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<ReviewResponseDTO>> getCourseReviews(
            @PathVariable Long courseId,
            @AuthenticationPrincipal User currentUser
    ) {
        List<ReviewResponseDTO> reviews = reviewService.getReviewsByCourse(courseId, currentUser);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/course/{courseId}/teacher")
    public ResponseEntity<List<ReviewResponseDTO>> getReviewsByTeacher(
            @PathVariable Long courseId,
            @RequestParam String teacherName,
            @AuthenticationPrincipal User currentUser) {

        List<ReviewResponseDTO> reviews = reviewService.getReviewsByTeacher(courseId, teacherName, currentUser);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/my-reviews")
    public ResponseEntity<List<ReviewResponseDTO>> getMyReviews(
            @AuthenticationPrincipal User currentUser) {

        List<ReviewResponseDTO> reviews = reviewService.getMyReviews(currentUser);
        return ResponseEntity.ok(reviews);
    }
}
