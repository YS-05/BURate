package com.coursegrade.CourseGraderBackend.repository;

import com.coursegrade.CourseGraderBackend.model.Course;
import com.coursegrade.CourseGraderBackend.model.Review;
import com.coursegrade.CourseGraderBackend.model.User;
import com.coursegrade.CourseGraderBackend.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByCourseAndTeacherNameContainingIgnoreCase_ShouldBeCaseInsensitive() {
        // Given
        User user = createAndSaveTestUser("casetest@email.com");
        Course course = createAndSaveTestCourse("Case Course", "CAS", "CS", "101");

        Review review = createTestReview(course, user, "Dr. UPPERCASE", 4, 3, 3, 4, 5);
        reviewRepository.save(review);

        // When
        List<Review> reviews = reviewRepository.findByCourseAndTeacherNameContainingIgnoreCase(course, "uppercase");

        // Then
        assertThat(reviews).hasSize(1);
        assertThat(reviews.get(0).getTeacherName()).isEqualTo("Dr. UPPERCASE");
    }

    @Test
    void getNetReviewScore_ShouldCalculateCorrectly() {
        // Given
        User user = createAndSaveTestUser("score@email.com");
        Course course = createAndSaveTestCourse("Score Course", "CAS", "CS", "101");

        Review review = createTestReview(course, user, "Dr. Score", 4, 3, 3, 4, 5);
        review.setUpvoteCount(10);
        review.setDownvoteCount(3);

        // When
        Integer netScore = review.getNetReviewScore();

        // Then
        assertThat(netScore).isEqualTo(7); // 10 - 3 = 7
    }

    @Test
    void getOverallRating_ShouldCalculateAverageWithInvertedDifficultyAndWorkload() {
        // Given
        User user = createAndSaveTestUser("average@email.com");
        Course course = createAndSaveTestCourse("Average Course", "CAS", "CS", "101");

        Review review = createTestReview(course, user, "Dr. Average", 4, 2, 3, 5, 1);
        // Average: (4+4+3+5+1)/5 = 17/5 = 3.4 Since inverted

        // When
        Double overallRating = review.getOverallRating();

        // Then
        assertThat(overallRating).isEqualTo(3.4);
    }

    // Helper methods
    private User createAndSaveTestUser(String email) {
        User user = createTestUser(email);
        return entityManager.persistAndFlush(user);
    }

    private Course createAndSaveTestCourse(String title, String college, String department, String courseCode) {
        Course course = createTestCourse(title, college, department, courseCode);
        return entityManager.persistAndFlush(course);
    }

    private User createTestUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setPassword("password");
        user.setCollege("CAS");
        user.setMajor("Computer Science");
        user.setRole(Role.STUDENT);
        user.setEnabled(true);
        return user;
    }

    private Course createTestCourse(String title, String college, String department, String courseCode) {
        Course course = new Course();
        course.setTitle(title);
        course.setCollege(college);
        course.setDepartment(department);
        course.setCourseCode(courseCode);
        return course;
    }

    private Review createTestReview(Course course, User user, String teacherName,
                                    int usefulness, int difficulty, int workload, int interest, int teacher) {
        Review review = new Review();
        review.setCourse(course);
        review.setUser(user);
        review.setTeacherName(teacherName);
        review.setUsefulnessRating(usefulness);
        review.setDifficultyRating(difficulty);
        review.setWorkloadRating(workload);
        review.setInterestRating(interest);
        review.setTeacherRating(teacher);
        review.setCreatedAt(LocalDateTime.now());
        return review;
    }
}