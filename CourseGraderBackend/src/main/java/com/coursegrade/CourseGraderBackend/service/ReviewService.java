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
        courseService.updateCourseRatings(courseId);

        return convertToResponseDTO(savedReview, user);
    }

    @Transactional
    public ReviewResponseDTO updateReview(User user, Long reviewId, CreateReviewDTO reviewDTO) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Can only update own reviews");
        }

        review.setDifficultyRating(reviewDTO.getDifficultyRating());
        review.setTeacherRating(reviewDTO.getTeacherRating());
        review.setWorkloadRating(reviewDTO.getWorkloadRating());
        review.setUsefulnessRating(reviewDTO.getUsefulnessRating());
        review.setInterestRating(reviewDTO.getInterestRating());
        review.setTeacherName(reviewDTO.getTeacherName());
        review.setReviewText(reviewDTO.getReviewText());
        if (reviewDTO.getSemester() != null) {
            review.setSemester(reviewDTO.getSemester());
        }
        if (reviewDTO.getHoursPerWeek() != null) {
            review.setHoursPerWeek(reviewDTO.getHoursPerWeek());
        }
        if (reviewDTO.getAssignmentTypes() != null) {
            review.setAssignmentTypes(reviewDTO.getAssignmentTypes());
        }
        if (reviewDTO.getAttendanceRequired() != null) {
            review.setAttendanceRequired(reviewDTO.getAttendanceRequired());
        }

        Review savedReview = reviewRepository.save(review);
        courseService.updateCourseRatings(review.getCourse().getId());

        return convertToResponseDTO(savedReview, user);
    }

    @Transactional
    public void deleteReview(User user, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Can only delete own reviews");
        }

        Course course = review.getCourse();
        reviewRepository.delete(review);
        courseService.updateCourseRatings(course.getId());
    }

    public ReviewResponseDTO getReviewById(Long reviewId, User currentUser) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        return convertToResponseDTO(review, currentUser);
    }

    public List<ReviewResponseDTO> getReviewsByCourse(Long courseId, User user) {
        Course course = courseService.getCourseById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        List<Review> reviews = reviewRepository.findByCourseOrderByCreatedAtDesc(course);
        List<ReviewResponseDTO> dtos = new ArrayList<>();
        for (Review review : reviews) {
            dtos.add(convertToResponseDTO(review, user));
        }
        return dtos;
    }

    public List<ReviewResponseDTO> getReviewsByTeacher(Long courseId, String teacher, User currentUser) {
        Course course = courseService.getCourseById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        List<Review> reviews = reviewRepository.findByCourseAndTeacherNameContainingIgnoreCase(course, teacher);
        List<ReviewResponseDTO> dtos = new ArrayList<>();
        for (Review review : reviews) {
            dtos.add(convertToResponseDTO(review, currentUser));
        }
        return dtos;
    }

    public List<ReviewResponseDTO> getMyReviews(User currentUser) {
        List<Review> reviews = reviewRepository.findByUser(currentUser);
        List<ReviewResponseDTO> dtos = new ArrayList<>();
        for (Review review : reviews) {
            dtos.add(convertToResponseDTO(review, currentUser));
        }
        return dtos;
    }

    public ReviewResponseDTO convertToResponseDTO(Review review, User currentUser) {
        ReviewResponseDTO dto = new ReviewResponseDTO();

        dto.setId(review.getId());
        dto.setCourseId(review.getCourse().getId());

        dto.setUsefulnessRating(review.getUsefulnessRating());
        dto.setDifficultyRating(review.getDifficultyRating());
        dto.setWorkloadRating(review.getWorkloadRating());
        dto.setInterestRating(review.getInterestRating());
        dto.setTeacherRating(review.getTeacherRating());
        dto.setOverallRating(((6.0 - review.getDifficultyRating()) +  // Invert difficulty
                (6.0 - review.getWorkloadRating()) +  // Invert workload
                review.getInterestRating() +
                review.getUsefulnessRating() +
                review.getTeacherRating()) / 5.0);
        dto.setTeacherName(review.getTeacherName());
        dto.setReviewText(review.getReviewText());
        dto.setSemester(review.getSemester());
        dto.setHoursPerWeek(review.getHoursPerWeek());
        dto.setAssignmentTypes(review.getAssignmentTypes());
        dto.setAttendanceRequired(review.getAttendanceRequired());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setOwner(review.getUser().getId().equals(currentUser.getId()));

        return dto;
    }
}
