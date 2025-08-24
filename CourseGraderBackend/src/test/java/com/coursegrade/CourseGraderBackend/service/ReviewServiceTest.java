package com.coursegrade.CourseGraderBackend.service;

import com.coursegrade.CourseGraderBackend.dto.CreateReviewDTO;
import com.coursegrade.CourseGraderBackend.dto.ReviewResponseDTO;
import com.coursegrade.CourseGraderBackend.model.Course;
import com.coursegrade.CourseGraderBackend.model.Review;
import com.coursegrade.CourseGraderBackend.model.Role;
import com.coursegrade.CourseGraderBackend.model.User;
import com.coursegrade.CourseGraderBackend.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private CourseService courseService;

    @InjectMocks
    private ReviewService reviewService;

    private User testUser;
    private User otherUser;
    private Course testCourse;
    private Review testReview;
    private CreateReviewDTO testReviewDTO;

    @BeforeEach
    void setUp() {
        testUser = createTestUser(1L, "test@example.com");
        otherUser = createTestUser(2L, "other@example.com");
        testCourse = createTestCourse();
        testReview = createTestReview();
        testReviewDTO = createTestReviewDTO();
    }

    @Test
    void createReview_ValidReview_ShouldCreateAndUpdateRatings() {
        // Given
        when(courseService.getCourseById(1L)).thenReturn(Optional.of(testCourse));
        when(reviewRepository.findByCourseAndUser(testCourse, testUser)).thenReturn(Optional.empty());
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        ReviewResponseDTO expectedDTO = new ReviewResponseDTO();
        when(courseService.convertToResponseDTO(testReview, testUser)).thenReturn(expectedDTO);

        // When
        ReviewResponseDTO result = reviewService.createReview(testUser, 1L, testReviewDTO);

        // Then
        verify(reviewRepository).save(any(Review.class));
        verify(courseService).updateCourseRatings(1L); // Should trigger course rating recalculation
        assertThat(result).isEqualTo(expectedDTO);
    }

    @Test
    void formatTeacherName_ValidNames_ShouldCapitalizeCorrectly() {
        // Given & When & Then
        assertThat(reviewService.formatTeacherName(null)).isNull();
        assertThat(reviewService.formatTeacherName("")).isEqualTo("");
        assertThat(reviewService.formatTeacherName("   ")).isEqualTo("   ");
        assertThat(reviewService.formatTeacherName("john smith")).isEqualTo("John Smith");
        assertThat(reviewService.formatTeacherName("MARY JANE")).isEqualTo("Mary Jane");
        assertThat(reviewService.formatTeacherName("dr. robert")).isEqualTo("Dr. Robert");
        assertThat(reviewService.formatTeacherName("single")).isEqualTo("Single");
    }

    @Test
    void updateReview_NotOwner_ShouldThrowException() {
        // Given
        testReview.setUser(otherUser); // Review belongs to different user
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));

        // When & Then
        assertThatThrownBy(() -> reviewService.updateReview(testUser, 1L, testReviewDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Can only update own reviews");

        verify(reviewRepository, never()).save(any(Review.class));
        verify(courseService, never()).updateCourseRatings(anyLong());
    }

    @Test
    void updateReview_Owner_ShouldUpdateAndRecalculateRatings() {
        // Given
        testReview.setUser(testUser); // Review belongs to current user
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        ReviewResponseDTO expectedDTO = new ReviewResponseDTO();
        when(courseService.convertToResponseDTO(testReview, testUser)).thenReturn(expectedDTO);

        // When
        ReviewResponseDTO result = reviewService.updateReview(testUser, 1L, testReviewDTO);

        // Then
        verify(reviewRepository).save(testReview);
        verify(courseService).updateCourseRatings(testReview.getCourse().getId());
        assertThat(result).isEqualTo(expectedDTO);
    }

    @Test
    void deleteReview_NotOwner_ShouldThrowException() {
        // Given
        testReview.setUser(otherUser); // Review belongs to different user
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));

        // When & Then
        assertThatThrownBy(() -> reviewService.deleteReview(testUser, 1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Can only delete own reviews");

        verify(reviewRepository, never()).delete(any(Review.class));
        verify(courseService, never()).updateCourseRatings(anyLong());
    }

    @Test
    void deleteReview_Owner_ShouldDeleteAndRecalculateRatings() {
        // Given
        testReview.setUser(testUser); // Review belongs to current user
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));

        // When
        reviewService.deleteReview(testUser, 1L);

        // Then
        verify(reviewRepository).delete(testReview);
        verify(courseService).updateCourseRatings(testCourse.getId()); // Should recalculate after deletion
    }

    // TEACHER FILTERING - Test complex filtering logic
    @Test
    void getReviewsByCourseAndTeacher_WithTeacherFilter_ShouldReturnMatchingReviews() {
        // Given
        Review review1 = createReviewWithTeacher("Dr. Smith");
        Review review2 = createReviewWithTeacher("Dr. Johnson");
        Review review3 = createReviewWithTeacher("Dr. Smith");

        when(courseService.getCourseById(1L)).thenReturn(Optional.of(testCourse));
        when(reviewRepository.findByCourseOrderByCreatedAtDesc(testCourse))
                .thenReturn(List.of(review1, review2, review3));

        ReviewResponseDTO dto1 = new ReviewResponseDTO();
        ReviewResponseDTO dto3 = new ReviewResponseDTO();
        when(courseService.convertToResponseDTO(review1, testUser)).thenReturn(dto1);
        when(courseService.convertToResponseDTO(review3, testUser)).thenReturn(dto3);

        // When
        List<ReviewResponseDTO> result = reviewService.getReviewsByCourseAndTeacher(1L, testUser, "Dr. Smith");

        // Then - Should only return reviews by Dr. Smith
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(dto1, dto3);
    }


    // Helper methods
    private User createTestUser(Long id, String email) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setRole(Role.STUDENT);
        return user;
    }

    private Course createTestCourse() {
        Course course = new Course();
        course.setId(1L);
        course.setTitle("Test Course");
        course.setCollege("CAS");
        course.setDepartment("CS");
        course.setCourseCode("CS101");
        return course;
    }

    private Review createTestReview() {
        Review review = new Review();
        review.setId(1L);
        review.setCourse(testCourse);
        review.setUser(testUser);
        review.setTeacherName("Dr. Test");
        review.setUsefulnessRating(5);
        review.setDifficultyRating(3);
        review.setWorkloadRating(4);
        review.setInterestRating(4);
        review.setTeacherRating(5);
        review.setCreatedAt(LocalDateTime.now());
        return review;
    }

    private Review createReviewWithTeacher(String teacherName) {
        Review review = createTestReview();
        review.setTeacherName(teacherName);
        return review;
    }

    private Review createReviewWithTeacherRating(String teacherName, int rating) {
        Review review = createTestReview();
        review.setTeacherName(teacherName);
        review.setTeacherRating(rating);
        return review;
    }

    private CreateReviewDTO createTestReviewDTO() {
        CreateReviewDTO dto = new CreateReviewDTO();
        dto.setUsefulnessRating(5);
        dto.setDifficultyRating(3);
        dto.setWorkloadRating(4);
        dto.setInterestRating(4);
        dto.setTeacherRating(5);
        dto.setTeacherName("dr. test");
        dto.setReviewText("Great course!");
        dto.setSemester("Fall 2024");
        dto.setHoursPerWeek(10);
        dto.setAttendanceRequired(true);
        return dto;
    }
}