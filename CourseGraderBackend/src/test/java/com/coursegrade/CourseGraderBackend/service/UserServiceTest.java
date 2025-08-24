package com.coursegrade.CourseGraderBackend.service;

import com.coursegrade.CourseGraderBackend.dto.HubProgressDTO;
import com.coursegrade.CourseGraderBackend.dto.HubProgressItem;
import com.coursegrade.CourseGraderBackend.dto.UserDashboardDTO;
import com.coursegrade.CourseGraderBackend.model.*;
import com.coursegrade.CourseGraderBackend.repository.CourseRepository;
import com.coursegrade.CourseGraderBackend.repository.ReviewRepository;
import com.coursegrade.CourseGraderBackend.repository.UserRepository;
import com.coursegrade.CourseGraderBackend.repository.VoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CourseService courseService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private Course testCourse1;
    private Course testCourse2;

    @BeforeEach
    void setUp() {
        testUser = createTestUser();
        testCourse1 = createTestCourse("CS101", Set.of(HubRequirement.QR1));
        testCourse2 = createTestCourse("CS102", Set.of(HubRequirement.WIN));
    }

    @Test
    void getHubProgress_WithCompletedCourses_ShouldCalculateProgressCorrectly() {
        // Given
        Set<Course> completedCourses = new HashSet<>();
        completedCourses.add(testCourse1); // Course fulfills QR1
        testUser.setCompletedCourses(completedCourses);

        // Mock the user's hub progress after updateHubProgress() is called
        Map<HubRequirement, Integer> hubProgress = new HashMap<>();
        hubProgress.put(HubRequirement.QR1, 1); // Completed QR1
        testUser.setHubProgress(hubProgress);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        HubProgressDTO result = userService.getHubProgress(1L);

        // Then
        List<HubProgressItem> items = result.getHubProgress();
        assertThat(items).hasSize(HubRequirement.values().length); // Should have all hub requirements

        // Find the QR1 item and verify it shows as completed
        HubProgressItem qr1Item = items.stream()
                .filter(item -> item.getHubCode().equals("QR1"))
                .findFirst()
                .orElseThrow();

        assertThat(qr1Item.getCompleted()).isEqualTo(1);
        assertThat(qr1Item.getFulfillingCourses()).contains("CAS CS CS101");
    }

    @Test
    void getDashboard_WithReviewsAndVotes_ShouldCalculateStatsCorrectly() {
        // Given
        Review review1 = createReviewWithVotes(testCourse1, 5, 2); // 5 up, 2 down
        Review review2 = createReviewWithVotes(testCourse2, 3, 1); // 3 up, 1 down
        List<Review> reviews = List.of(review1, review2);

        Set<Course> completedCourses = new HashSet<>();
        completedCourses.add(testCourse1);
        completedCourses.add(testCourse2);
        testUser.setCompletedCourses(completedCourses);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(reviewRepository.findByUser(testUser)).thenReturn(reviews);

        // When
        UserDashboardDTO result = userService.getDashboard(testUser);

        // Then
        assertThat(result.getCoursesCompleted()).isEqualTo(2);
        assertThat(result.getCoursesReviewed()).isEqualTo(2);
        assertThat(result.getTotalUpvotes()).isEqualTo(8); // 5 + 3

        // Average review score = (upvotes - downvotes) / review count = (8 - 3) / 2 = 2.5
        assertThat(result.getAverageReviewScore()).isEqualTo(2.5);

        // Courses to review should be empty since both courses are reviewed
        assertThat(result.getCoursesToReview()).isEmpty();
    }

    @Test
    void getDashboard_WithUnreviewedCourses_ShouldShowCoursesToReview() {
        // Given
        Course unreviewedCourse = createTestCourse("CS103", Set.of());
        Set<Course> completedCourses = new HashSet<>();
        completedCourses.add(testCourse1);
        completedCourses.add(unreviewedCourse);
        testUser.setCompletedCourses(completedCourses);

        Review review1 = createReviewWithVotes(testCourse1, 3, 1);
        List<Review> reviews = List.of(review1); // Only reviewed testCourse1

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(reviewRepository.findByUser(testUser)).thenReturn(reviews);

        // When
        UserDashboardDTO result = userService.getDashboard(testUser);

        // Then
        assertThat(result.getCoursesToReview()).hasSize(1);
        assertThat(result.getCoursesToReview()).contains("CAS CS CS103");
    }

    // Helper methods
    private User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setCollege("CAS");
        user.setMajor("Computer Science");
        user.setExpectedGrad(2025);
        user.setCompletedCourses(new HashSet<>());
        user.setCoursesInProgress(new HashSet<>());
        user.setSavedCourses(new HashSet<>());
        user.setHubProgress(new HashMap<>());
        // No setProjectedHubProgress - it's a calculated method, not a field
        return user;
    }

    private Course createTestCourse(String courseCode, Set<HubRequirement> hubReqs) {
        Course course = new Course();
        course.setId(Long.valueOf(courseCode.substring(2))); // CS101 -> 101
        course.setTitle("Test Course");
        course.setCollege("CAS");
        course.setDepartment("CS");
        course.setCourseCode(courseCode);
        course.setHubRequirements(hubReqs);
        return course;
    }

    private Review createTestReview(Course course) {
        Review review = new Review();
        review.setCourse(course);
        review.setUser(testUser);
        return review;
    }

    private Review createReviewWithVotes(Course course, int upvotes, int downvotes) {
        Review review = createTestReview(course);
        review.setUpvoteCount(upvotes);
        review.setDownvoteCount(downvotes);
        return review;
    }
}