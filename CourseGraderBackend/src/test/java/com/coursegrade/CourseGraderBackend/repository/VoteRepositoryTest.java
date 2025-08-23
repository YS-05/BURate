package com.coursegrade.CourseGraderBackend.repository;

import com.coursegrade.CourseGraderBackend.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
class VoteRepositoryTest {

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void save_DuplicateUserReviewCombination_ShouldThrowException() {
        // Given - Create and persist the entities in correct order
        User user = createTestUser("duplicate@email.com");
        user = entityManager.persistAndFlush(user);

        Course course = createTestCourse("Duplicate Course", "CAS", "CS", "101");
        course = entityManager.persistAndFlush(course);

        Review review = createTestReview(course, user, "Dr. Duplicate", 4, 3, 3, 4, 5);
        review = entityManager.persistAndFlush(review);

        // Create and save the first vote
        Vote firstVote = Vote.builder()
                .user(user)
                .review(review)
                .voteType(VoteType.UPVOTE)
                .build();
        voteRepository.saveAndFlush(firstVote);

        // Try to create a duplicate vote (same user + review combination)
        Vote duplicateVote = Vote.builder()
                .user(user)
                .review(review)
                .voteType(VoteType.DOWNVOTE) // Different vote type, but same user+review
                .build();

        // Then - Should throw constraint violation exception
        assertThatThrownBy(() -> {
            voteRepository.saveAndFlush(duplicateVote);
        }).isInstanceOf(DataIntegrityViolationException.class);
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