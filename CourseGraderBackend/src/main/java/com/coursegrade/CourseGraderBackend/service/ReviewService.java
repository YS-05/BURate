package com.coursegrade.CourseGraderBackend.service;

import com.coursegrade.CourseGraderBackend.dto.CreateReviewDTO;
import com.coursegrade.CourseGraderBackend.dto.ReviewResponseDTO;
import com.coursegrade.CourseGraderBackend.model.Course;
import com.coursegrade.CourseGraderBackend.model.Review;
import com.coursegrade.CourseGraderBackend.model.Role;
import com.coursegrade.CourseGraderBackend.model.User;
import com.coursegrade.CourseGraderBackend.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
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
        review.setTeacherName(formatTeacherName(reviewDTO.getTeacherName()));
        review.setReviewText(reviewDTO.getReviewText());
        review.setSemester(reviewDTO.getSemester());
        review.setHoursPerWeek(reviewDTO.getHoursPerWeek());
        review.setAssignmentTypes(reviewDTO.getAssignmentTypes());
        review.setAttendanceRequired(reviewDTO.getAttendanceRequired());
        review.setCreatedAt(LocalDateTime.now());

        Review savedReview = reviewRepository.save(review);
        courseService.updateCourseRatings(courseId);

        return courseService.convertToResponseDTO(savedReview, user);
    }

    public String formatTeacherName(String teacherName) {
        if (teacherName == null || teacherName.trim().isEmpty()) return teacherName;
        String formattedTeacher = "";
        String[] split = teacherName.split(" ");
        for (int i = 0; i < split.length; i++) {
            if (i > 0) formattedTeacher += " ";
            String firstUpper = split[i].substring(0, 1).toUpperCase() + split[i].substring(1).toLowerCase();
            formattedTeacher += firstUpper;
        }
        return formattedTeacher;
    }

    @Transactional
    public ReviewResponseDTO updateReview(User user, Long reviewId, CreateReviewDTO reviewDTO) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (!review.getUser().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            throw new RuntimeException("Can only update own reviews or need admin");
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

        return courseService.convertToResponseDTO(savedReview, user);
    }

    @Transactional
    public void deleteReview(User user, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (!review.getUser().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            throw new RuntimeException("Can only delete own reviews or need admin");
        }

        Course course = review.getCourse();
        reviewRepository.delete(review);
        courseService.updateCourseRatings(course.getId());
    }

    public ReviewResponseDTO getReviewById(Long reviewId, User currentUser) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        return courseService.convertToResponseDTO(review, currentUser);
    }

    public List<ReviewResponseDTO> getReviewsByCourseAndTeacher(Long courseId, User user, String teacher) {
        Course course = courseService.getCourseById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        List<Review> reviews = reviewRepository.findByCourseOrderByCreatedAtDesc(course);
        List<ReviewResponseDTO> dtos = new ArrayList<>();
        for (Review review : reviews) {
            if (teacher == null || teacher.isEmpty() || review.getTeacherName().equals(teacher)) {
                dtos.add(courseService.convertToResponseDTO(review, user));
            }
        }
        return dtos;
    }

    public List<ReviewResponseDTO> getReviewsByTeacher(Long courseId, String teacher, User currentUser) {
        Course course = courseService.getCourseById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        List<Review> reviews = reviewRepository.findByCourseAndTeacherNameContainingIgnoreCase(course, teacher);
        List<ReviewResponseDTO> dtos = new ArrayList<>();
        for (Review review : reviews) {
            dtos.add(courseService.convertToResponseDTO(review, currentUser));
        }
        return dtos;
    }

    public List<String> getReviewTeachers(Long courseId) {
        HashSet<String> teachers = new HashSet<>();
        Course course = courseService.getCourseById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        List<Review> reviews = reviewRepository.findByCourseOrderByCreatedAtDesc(course);
        for (Review review : reviews) {
            teachers.add(review.getTeacherName());
        }
        return new ArrayList<>(teachers);
    }

    public Double getTeacherScore(Long courseId, String teacher) {
        Course course = courseService.getCourseById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        List<Review> reviews = reviewRepository.findByCourseAndTeacherNameContainingIgnoreCase(course, teacher);
        Double teacherScore = 0.0;
        for (Review review : reviews) {
            teacherScore += review.getTeacherRating();
        }
        return teacherScore / reviews.size();
    }

    public List<ReviewResponseDTO> getMyReviews(User currentUser) {
        // Add ordering by createdAt descending
        List<Review> reviews = reviewRepository.findByUserOrderByCreatedAtDesc(currentUser);
        List<ReviewResponseDTO> dtos = new ArrayList<>();
        for (Review review : reviews) {
            dtos.add(courseService.convertToResponseDTO(review, currentUser));
        }
        return dtos;
    }

    public List<ReviewResponseDTO> getAllReviews(User currentUser) {
        if (currentUser.getRole() != Role.ADMIN) {
            throw new RuntimeException("Only admins can view all reviews together");
        }
        List<Review> reviews = reviewRepository.findAll();
        List<ReviewResponseDTO> dtos = new ArrayList<>();
        for (Review review : reviews) {
            dtos.add(courseService.convertToResponseDTO(review, currentUser));
        }
        return dtos;
    }
}
