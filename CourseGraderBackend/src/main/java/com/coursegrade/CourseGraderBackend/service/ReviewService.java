package com.coursegrade.CourseGraderBackend.service;

import com.coursegrade.CourseGraderBackend.dto.CreateReviewDTO;
import com.coursegrade.CourseGraderBackend.dto.ReviewResponseDTO;
import com.coursegrade.CourseGraderBackend.model.Course;
import com.coursegrade.CourseGraderBackend.model.Review;
import com.coursegrade.CourseGraderBackend.model.User;
import com.coursegrade.CourseGraderBackend.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final CourseService courseService;

    @Transactional
    public ReviewResponseDTO createReview(User user, Long courseId, CreateReviewDTO reviewDTO) {
        Course course = courseService.getCourseById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        if (reviewRepository.findByCourseAndUser(course, user).isPresent()) {
            throw new RuntimeException("Already reviewed course, can only update review");
        }
        Review review = new Review();
        review.setCourse(course);
        review.setUser(user);
        review.setDifficultyRating(reviewDTO.getDifficultyRating());
        review.setTeacherRating(reviewDTO.getTeacherRating());
        review.setWorkloadRating(reviewDTO.getWorkloadRating());
        review.setUsefulnessRating(reviewDTO.getUsefulnessRating());
        review.setInterestRating(reviewDTO.getInterestRating());
        review.setTeacherName(reviewDTO.getTeacherName());
        review.setReviewText(reviewDTO.getReviewText());
        review.setSemester(reviewDTO.getSemester());
        review.setHoursPerWeek(reviewDTO.getHoursPerWeek());
        review.setAssignmentTypes(reviewDTO.getAssignmentTypes());
        review.setAttendanceRequired(reviewDTO.getAttendanceRequired());
        review.setCreatedAt(LocalDateTime.now());
        Review savedReview = reviewRepository.save(review);

        courseService.updateCourseRatings(savedReview, true);
        return convertToResponseDTO(savedReview);
    }

    @Transactional
    public void deleteReview(User user, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        if (!review.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Can only delete own reviews");
        }
        reviewRepository.delete(review);
        courseService.updateCourseRatings(review, false);
    }

    public ReviewResponseDTO getReviewById(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        return convertToResponseDTO(review);
    }

    public List<ReviewResponseDTO> getAllReviews(Long courseID) {
        Course course = courseService.getCourseById(courseID)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        List<Review> reviews = reviewRepository.findByCourse(course);
        List<ReviewResponseDTO> reviewResponseDTOS = new ArrayList<>();
        for (Review review : reviews) {
            reviewResponseDTOS.add(convertToResponseDTO(review));
        }
        return reviewResponseDTOS;
    }

    public List<ReviewResponseDTO> getReviewsByCourseAndTeacher(Long courseId, String teacherName) {
        Course course = courseService.getCourseById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        List<Review> reviews = reviewRepository
                .findByCourseAndTeacherNameContainingIgnoreCase(course, teacherName);
        List<ReviewResponseDTO> reviewResponseDTOS = new ArrayList<>();
        for (Review review : reviews) {
            reviewResponseDTOS.add(convertToResponseDTO(review));
        }
        return reviewResponseDTOS;
    }

    public ReviewResponseDTO convertToResponseDTO(Review review) {
        ReviewResponseDTO dto = new ReviewResponseDTO();

        dto.setId(review.getId());
        dto.setCourseId(review.getCourse().getId());

        dto.setUsefulnessRating(review.getUsefulnessRating());
        dto.setDifficultyRating(review.getDifficultyRating());
        dto.setWorkloadRating(review.getWorkloadRating());
        dto.setInterestRating(review.getInterestRating());
        dto.setTeacherRating(review.getTeacherRating());
        dto.setOverallRating(((review.getDifficultyRating() +
                review.getTeacherRating()) +
                review.getInterestRating() +
                review.getInterestRating() +
                review.getUsefulnessRating())/5.0);

        dto.setTeacherName(review.getTeacherName());
        dto.setReviewText(review.getReviewText());
        dto.setSemester(review.getSemester());
        dto.setHoursPerWeek(review.getHoursPerWeek());
        dto.setAssignmentTypes(review.getAssignmentTypes());
        dto.setAttendanceRequired(review.getAttendanceRequired());
        dto.setCreatedAt(review.getCreatedAt());

        return dto;
    }
}
