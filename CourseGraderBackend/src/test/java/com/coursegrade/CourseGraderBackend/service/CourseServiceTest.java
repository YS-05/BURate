package com.coursegrade.CourseGraderBackend.service;

import com.coursegrade.CourseGraderBackend.dto.CourseDisplayDTO;
import com.coursegrade.CourseGraderBackend.dto.CourseDTO;
import com.coursegrade.CourseGraderBackend.model.*;
import com.coursegrade.CourseGraderBackend.repository.CourseRepository;
import com.coursegrade.CourseGraderBackend.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private CourseService courseService;

    private Course testCourse;
    private User testUser;
    private Review testReview;

    @BeforeEach
    void setUp() {
        testCourse = createTestCourse();
        testUser = createTestUser();
        testReview = createTestReview();
    }

    @Test
    void createCourse_CourseAlreadyExists_ShouldReturnExistingCourse() {
        // Given
        when(courseRepository.findByCourseCodeAndDepartmentAndCollege("CS101", "CS", "CAS"))
                .thenReturn(Optional.of(testCourse));

        // When
        Course result = courseService.createCourse("Intro to CS", "CAS", "CS", "CS101", "http://example.com");

        // Then
        assertThat(result).isEqualTo(testCourse);
        verify(courseRepository, never()).save(any(Course.class)); // Should never get to save line
    }

    @Test
    void searchCoursesWithCollege_FilterByCollege_ShouldReturnFilteredResults() {
        // Given
        Course casCourse = createCourseWithCollege("CAS");
        Course engCourse = createCourseWithCollege("ENG");
        when(courseRepository.findAll()).thenReturn(List.of(casCourse, engCourse));

        Set<String> collegeFilter = Set.of("CAS");
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<CourseDisplayDTO> result = courseService.searchCoursesWithCollege(
                null, collegeFilter, null, null, null, null, null, null, null, null, null, null, null, pageable
        );

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getCollege()).isEqualTo("CAS");
    }

    @Test
    void searchCoursesWithCollege_FilterByMinRatingAndSort_ShouldReturnFilteredAndSortedResults() {
        // Given
        Course highestRatedCourse = createCourseWithRating(4.8);
        Course midRatedCourse = createCourseWithRating(4.2);
        Course lowRatedCourse = createCourseWithRating(2.0);
        when(courseRepository.findAll()).thenReturn(List.of(lowRatedCourse, midRatedCourse, highestRatedCourse));

        Pageable pageable = PageRequest.of(0, 10);

        // When - Filter by min rating 4.0 AND sort by rating
        Page<CourseDisplayDTO> result = courseService.searchCoursesWithCollege(
                null, null, null, null, null, 4.0, null, null, null, null, null, null, "byRating", pageable
        );

        // Then - Should return 2 courses (both ≥4.0) sorted by rating (highest first)
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getAverageOverallRating()).isEqualTo(4.8); // Highest first
        assertThat(result.getContent().get(1).getAverageOverallRating()).isEqualTo(4.2); // Second highest
        // lowRatedCourse (2.0) should be filtered out
    }

    private Course createCourseWithRating(double rating) {
        Course course = createTestCourse();
        course.setAverageOverallRating(rating);
        return course;
    }

    @Test
    void updateCourseRatings_WithReviews_ShouldCalculateCorrectAverages() {
        // Given
        Review review1 = createReviewWithRatings(5, 4, 3, 4, 5); // usefulness, difficulty, workload, interest, teacher
        Review review2 = createReviewWithRatings(3, 2, 4, 5, 4);
        List<Review> reviews = List.of(review1, review2);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        when(reviewRepository.findByCourse(testCourse)).thenReturn(reviews);
        when(courseRepository.save(any(Course.class))).thenReturn(testCourse);

        // When
        courseService.updateCourseRatings(1L);

        // Then
        assertThat(testCourse.getTotalReviews()).isEqualTo(2);
        assertThat(testCourse.getAverageUsefulnessRating()).isEqualTo(4.0); // (5+3)/2
        assertThat(testCourse.getAverageDifficultyRating()).isEqualTo(3.0); // (4+2)/2
        assertThat(testCourse.getAverageWorkloadRating()).isEqualTo(3.5); // (3+4)/2
        assertThat(testCourse.getAverageInterestRating()).isEqualTo(4.5); // (4+5)/2
        assertThat(testCourse.getAverageTeacherRating()).isEqualTo(4.5); // (5+4)/2
    }

    @Test
    void calculateOverallRating_ShouldUseCorrectFormula() {
        // Given - Set up a course with known ratings
        testCourse.setTotalReviews(1);
        testCourse.setAverageDifficultyRating(2.0); // Inverted: 6-2 = 4
        testCourse.setAverageWorkloadRating(3.0);   // Inverted: 6-3 = 3
        testCourse.setAverageInterestRating(4.0);   // Direct: 4
        testCourse.setAverageUsefulnessRating(5.0); // Direct: 5
        testCourse.setAverageTeacherRating(3.0);    // Direct: 3

        // When
        Double result = courseService.calculateOverallRating(testCourse);

        // Then - Should be (4+3+4+5+3)/5 = 3.8
        assertThat(result).isEqualTo(3.8);
    }

    @Test
    void convertHubNamesToEnum_MixedValidAndInvalid_ShouldReturnOnlyValid() {
        // Given
        List<String> hubNames = List.of("Quantitative Reasoning I", "Invalid Name", "Ethical Reasoning");

        // When
        Set<HubRequirement> result = courseService.convertHubNamesToEnum(hubNames);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).contains(HubRequirement.QR1, HubRequirement.ETR);
    }

    @Test
    void convertToFullDTO_WithUserWhoReviewed_ShouldSetUserReviewedTrue() {
        // Given
        when(reviewRepository.findByCourseAndUser(testCourse, testUser)).thenReturn(Optional.of(testReview));
        when(reviewRepository.findByCourseOrderByCreatedAtDesc(testCourse)).thenReturn(List.of(testReview));

        // When
        CourseDTO result = courseService.convertToFullDTO(testCourse, testUser);

        // Then
        assertThat(result.isUserReviewed()).isTrue();
    }

    @Test
    void convertToFullDTO_WithUserWhoDidNotReview_ShouldSetUserReviewedFalse() {
        // Given
        when(reviewRepository.findByCourseAndUser(testCourse, testUser)).thenReturn(Optional.empty());
        when(reviewRepository.findByCourseOrderByCreatedAtDesc(testCourse)).thenReturn(List.of());

        // When
        CourseDTO result = courseService.convertToFullDTO(testCourse, testUser);

        // Then
        assertThat(result.isUserReviewed()).isFalse();
    }

    @Test
    void convertToFullDTO_WithNullUser_ShouldSetUserReviewedFalse() {
        // Given
        when(reviewRepository.findByCourseOrderByCreatedAtDesc(testCourse)).thenReturn(List.of());

        // When
        CourseDTO result = courseService.convertToFullDTO(testCourse, null);

        // Then
        assertThat(result.isUserReviewed()).isFalse();
    }

    // Helper methods
    private Course createTestCourse() {
        Course course = new Course();
        course.setId(1L);
        course.setTitle("Test Course");
        course.setCollege("CAS");
        course.setDepartment("CS");
        course.setCourseCode("CS101");
        course.setBaseUrl("http://example.com");
        course.setHubRequirements(new HashSet<>());
        course.setTotalReviews(0);
        course.setAverageOverallRating(0.0);
        course.setAverageUsefulnessRating(0.0);
        course.setAverageDifficultyRating(0.0);
        course.setAverageWorkloadRating(0.0);
        course.setAverageInterestRating(0.0);
        course.setAverageTeacherRating(0.0);
        return course;
    }

    private Course createCourseWithCollege(String college) {
        Course course = createTestCourse();
        course.setCollege(college);
        return course;
    }

    private User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setRole(Role.STUDENT);
        return user;
    }

    private Review createTestReview() {
        Review review = new Review();
        review.setId(1L);
        review.setCourse(testCourse);
        review.setUser(testUser);
        review.setUsefulnessRating(5);
        review.setDifficultyRating(3);
        review.setWorkloadRating(4);
        review.setInterestRating(4);
        review.setTeacherRating(5);
        review.setCreatedAt(LocalDateTime.now());
        return review;
    }

    private Review createReviewWithRatings(int usefulness, int difficulty, int workload, int interest, int teacher) {
        Review review = new Review();
        review.setCourse(testCourse);
        review.setUsefulnessRating(usefulness);
        review.setDifficultyRating(difficulty);
        review.setWorkloadRating(workload);
        review.setInterestRating(interest);
        review.setTeacherRating(teacher);
        return review;
    }
}