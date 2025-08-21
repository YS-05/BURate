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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ReviewRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    void save_ShouldPersistReview() {
        // Given
        User user = createTestUser("reviewer@email.com");
        Course course = createTestCourse("Introduction to Testing", "CAS", "CS", "101");
        entityManager.persistAndFlush(user);
        entityManager.persistAndFlush(course);

        Review review = createTestReview(course, user, "Dr. Smith", 4, 3, 3, 5, 4);

        // When
        Review saved = reviewRepository.save(review);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCourse()).isEqualTo(course);
        assertThat(saved.getUser()).isEqualTo(user);
        assertThat(saved.getTeacherName()).isEqualTo("Dr. Smith");
        assertThat(saved.getUsefulnessRating()).isEqualTo(4);
        assertThat(saved.getDifficultyRating()).isEqualTo(3);
        assertThat(saved.getWorkloadRating()).isEqualTo(3);
        assertThat(saved.getInterestRating()).isEqualTo(5);
        assertThat(saved.getTeacherRating()).isEqualTo(4);
    }

    @Test
    void findByCourse_ShouldReturnAllReviewsForCourse() {
        // Given
        User user1 = createTestUser("user1@email.com");
        User user2 = createTestUser("user2@email.com");
        Course course1 = createTestCourse("Course 1", "CAS", "CS", "101");
        Course course2 = createTestCourse("Course 2", "CAS", "CS", "102");

        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);
        entityManager.persistAndFlush(course1);
        entityManager.persistAndFlush(course2);

        Review review1 = createTestReview(course1, user1, "Dr. A", 4, 3, 3, 4, 5);
        Review review2 = createTestReview(course1, user2, "Dr. B", 3, 4, 2, 3, 4);
        Review review3 = createTestReview(course2, user1, "Dr. C", 5, 2, 4, 5, 3);

        entityManager.persistAndFlush(review1);
        entityManager.persistAndFlush(review2);
        entityManager.persistAndFlush(review3);

        // When
        List<Review> course1Reviews = reviewRepository.findByCourse(course1);

        // Then
        assertThat(course1Reviews).hasSize(2);
        assertThat(course1Reviews).extracting(Review::getTeacherName)
                .containsExactlyInAnyOrder("Dr. A", "Dr. B");
        assertThat(course1Reviews).allMatch(review -> review.getCourse().equals(course1));
    }

    @Test
    void findByUser_ShouldReturnAllReviewsByUser() {
        // Given
        User user1 = createTestUser("prolific@email.com");
        User user2 = createTestUser("occasional@email.com");
        Course course1 = createTestCourse("Course 1", "CAS", "CS", "101");
        Course course2 = createTestCourse("Course 2", "CAS", "CS", "102");

        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);
        entityManager.persistAndFlush(course1);
        entityManager.persistAndFlush(course2);

        Review review1 = createTestReview(course1, user1, "Dr. A", 4, 3, 3, 4, 5);
        Review review2 = createTestReview(course2, user1, "Dr. B", 3, 4, 2, 3, 4);
        Review review3 = createTestReview(course1, user2, "Dr. C", 5, 2, 4, 5, 3);

        entityManager.persistAndFlush(review1);
        entityManager.persistAndFlush(review2);
        entityManager.persistAndFlush(review3);

        // When
        List<Review> user1Reviews = reviewRepository.findByUser(user1);

        // Then
        assertThat(user1Reviews).hasSize(2);
        assertThat(user1Reviews).extracting(Review::getTeacherName)
                .containsExactlyInAnyOrder("Dr. A", "Dr. B");
        assertThat(user1Reviews).allMatch(review -> review.getUser().equals(user1));
    }

    @Test
    void findByCourseOrderByCreatedAtDesc_ShouldReturnReviewsInDescendingOrder() {
        // Given
        User user = createTestUser("timeuser@email.com");
        Course course = createTestCourse("Time Course", "CAS", "CS", "101");
        entityManager.persistAndFlush(user);
        entityManager.persistAndFlush(course);

        Review oldReview = createTestReview(course, user, "Dr. Old", 4, 3, 3, 4, 5);
        oldReview.setCreatedAt(LocalDateTime.now().minusDays(2));

        Review newReview = createTestReview(course, user, "Dr. New", 3, 4, 2, 3, 4);
        newReview.setCreatedAt(LocalDateTime.now().minusHours(1));

        entityManager.persistAndFlush(oldReview);
        entityManager.persistAndFlush(newReview);

        // When
        List<Review> reviews = reviewRepository.findByCourseOrderByCreatedAtDesc(course);

        // Then
        assertThat(reviews).hasSize(2);
        assertThat(reviews.get(0).getTeacherName()).isEqualTo("Dr. New"); // Most recent first
        assertThat(reviews.get(1).getTeacherName()).isEqualTo("Dr. Old"); // Older second
    }

    @Test
    void findByCourseAndUser_WhenReviewExists_ShouldReturnReview() {
        // Given
        User user = createTestUser("unique@email.com");
        Course course = createTestCourse("Unique Course", "CAS", "CS", "101");
        entityManager.persistAndFlush(user);
        entityManager.persistAndFlush(course);

        Review review = createTestReview(course, user, "Dr. Unique", 4, 3, 3, 4, 5);
        entityManager.persistAndFlush(review);

        // When
        Optional<Review> found = reviewRepository.findByCourseAndUser(course, user);

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getCourse()).isEqualTo(course);
        assertThat(found.get().getUser()).isEqualTo(user);
        assertThat(found.get().getTeacherName()).isEqualTo("Dr. Unique");
    }

    @Test
    void findByCourseAndUser_WhenReviewDoesNotExist_ShouldReturnEmpty() {
        // Given
        User user = createTestUser("noreviews@email.com");
        Course course = createTestCourse("No Reviews Course", "CAS", "CS", "101");
        entityManager.persistAndFlush(user);
        entityManager.persistAndFlush(course);

        // When
        Optional<Review> found = reviewRepository.findByCourseAndUser(course, user);

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void findByCourseAndTeacherNameContainingIgnoreCase_ShouldReturnMatchingReviews() {
        // Given
        User user1 = createTestUser("student1@email.com");
        User user2 = createTestUser("student2@email.com");
        Course course = createTestCourse("Popular Course", "CAS", "CS", "101");

        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);
        entityManager.persistAndFlush(course);

        Review review1 = createTestReview(course, user1, "Dr. John Smith", 4, 3, 3, 4, 5);
        Review review2 = createTestReview(course, user2, "Prof. Jane Smith", 3, 4, 2, 3, 4);
        Review review3 = createTestReview(course, user1, "Dr. Bob Johnson", 5, 2, 4, 5, 3);

        entityManager.persistAndFlush(review1);
        entityManager.persistAndFlush(review2);
        entityManager.persistAndFlush(review3);

        // When
        List<Review> smithReviews = reviewRepository.findByCourseAndTeacherNameContainingIgnoreCase(course, "smith");

        // Then
        assertThat(smithReviews).hasSize(2);
        assertThat(smithReviews).extracting(Review::getTeacherName)
                .containsExactlyInAnyOrder("Dr. John Smith", "Prof. Jane Smith");
    }

    @Test
    void findByCourseAndTeacherNameContainingIgnoreCase_ShouldBeCaseInsensitive() {
        // Given
        User user = createTestUser("casetest@email.com");
        Course course = createTestCourse("Case Course", "CAS", "CS", "101");
        entityManager.persistAndFlush(user);
        entityManager.persistAndFlush(course);

        Review review = createTestReview(course, user, "Dr. UPPERCASE", 4, 3, 3, 4, 5);
        entityManager.persistAndFlush(review);

        // When
        List<Review> reviews = reviewRepository.findByCourseAndTeacherNameContainingIgnoreCase(course, "uppercase");

        // Then
        assertThat(reviews).hasSize(1);
        assertThat(reviews.get(0).getTeacherName()).isEqualTo("Dr. UPPERCASE");
    }

    @Test
    void findByUserOrderByCreatedAtDesc_ShouldReturnUserReviewsInDescendingOrder() {
        // Given
        User user = createTestUser("chronological@email.com");
        Course course1 = createTestCourse("Course 1", "CAS", "CS", "101");
        Course course2 = createTestCourse("Course 2", "CAS", "CS", "102");

        entityManager.persistAndFlush(user);
        entityManager.persistAndFlush(course1);
        entityManager.persistAndFlush(course2);

        Review oldReview = createTestReview(course1, user, "Dr. Old", 4, 3, 3, 4, 5);
        oldReview.setCreatedAt(LocalDateTime.now().minusDays(3));

        Review newReview = createTestReview(course2, user, "Dr. New", 3, 4, 2, 3, 4);
        newReview.setCreatedAt(LocalDateTime.now().minusHours(2));

        entityManager.persistAndFlush(oldReview);
        entityManager.persistAndFlush(newReview);

        // When
        List<Review> reviews = reviewRepository.findByUserOrderByCreatedAtDesc(user);

        // Then
        assertThat(reviews).hasSize(2);
        assertThat(reviews.get(0).getTeacherName()).isEqualTo("Dr. New"); // Most recent first
        assertThat(reviews.get(1).getTeacherName()).isEqualTo("Dr. Old"); // Older second
    }

    @Test
    void save_ReviewWithAllFields_ShouldPersistAllData() {
        // Given
        User user = createTestUser("complete@email.com");
        Course course = createTestCourse("Complete Course", "CAS", "CS", "101");
        entityManager.persistAndFlush(user);
        entityManager.persistAndFlush(course);

        Review review = new Review();
        review.setCourse(course);
        review.setUser(user);
        review.setUsefulnessRating(4);
        review.setDifficultyRating(3);
        review.setWorkloadRating(3);
        review.setInterestRating(5);
        review.setTeacherRating(4);
        review.setTeacherName("Dr. Complete");
        review.setReviewText("This is a comprehensive review with lots of details.");
        review.setSemester("Fall 2024");
        review.setCreatedAt(LocalDateTime.now());
        review.setHoursPerWeek(15);
        review.setAssignmentTypes("Essays, Problem Sets, Final Exam");
        review.setAttendanceRequired(true);
        review.setUpvoteCount(5);
        review.setDownvoteCount(2);

        // When
        Review saved = reviewRepository.save(review);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getReviewText()).isEqualTo("This is a comprehensive review with lots of details.");
        assertThat(saved.getSemester()).isEqualTo("Fall 2024");
        assertThat(saved.getHoursPerWeek()).isEqualTo(15);
        assertThat(saved.getAssignmentTypes()).isEqualTo("Essays, Problem Sets, Final Exam");
        assertThat(saved.getAttendanceRequired()).isTrue();
        assertThat(saved.getUpvoteCount()).isEqualTo(5);
        assertThat(saved.getDownvoteCount()).isEqualTo(2);
    }

    @Test
    void deleteById_ShouldRemoveReview() {
        // Given
        User user = createTestUser("delete@email.com");
        Course course = createTestCourse("Delete Course", "CAS", "CS", "101");
        entityManager.persistAndFlush(user);
        entityManager.persistAndFlush(course);

        Review review = createTestReview(course, user, "Dr. Delete", 4, 3, 3, 4, 5);
        Review saved = entityManager.persistAndFlush(review);

        // When
        reviewRepository.deleteById(saved.getId());
        entityManager.flush();

        // Then
        Optional<Review> found = reviewRepository.findById(saved.getId());
        assertThat(found).isEmpty();
    }

    // Entity Logic Tests

    @Test
    void getNetReviewScore_ShouldCalculateCorrectly() {
        // Given
        User user = createTestUser("score@email.com");
        Course course = createTestCourse("Score Course", "CAS", "CS", "101");

        Review review = createTestReview(course, user, "Dr. Score", 4, 3, 3, 4, 5);
        review.setUpvoteCount(10);
        review.setDownvoteCount(3);

        // When
        Integer netScore = review.getNetReviewScore();

        // Then
        assertThat(netScore).isEqualTo(7); // 10 - 3 = 7
    }

    @Test
    void getOverallRating_ShouldCalculateAverage() {
        // Given
        User user = createTestUser("average@email.com");
        Course course = createTestCourse("Average Course", "CAS", "CS", "101");

        Review review = createTestReview(course, user, "Dr. Average", 4, 2, 3, 5, 1);
        // Ratings: usefulness=4, difficulty=2, workload=3, interest=5, teacher=1
        // Average: (4+2+3+5+1)/5 = 15/5 = 3.0

        // When
        Double overallRating = review.getOverallRating();

        // Then
        assertThat(overallRating).isEqualTo(3.0);
    }

    @Test
    void save_ReviewWithDefaultValues_ShouldUseDefaults() {
        // Given
        User user = createTestUser("defaults@email.com");
        Course course = createTestCourse("Default Course", "CAS", "CS", "101");
        entityManager.persistAndFlush(user);
        entityManager.persistAndFlush(course);

        Review review = new Review();
        review.setCourse(course);
        review.setUser(user);
        review.setUsefulnessRating(4);
        review.setDifficultyRating(3);
        review.setWorkloadRating(3);
        review.setInterestRating(5);
        review.setTeacherRating(4);
        review.setTeacherName("Dr. Default");
        // Leave other fields as defaults

        // When
        Review saved = reviewRepository.save(review);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUpvoteCount()).isEqualTo(0); // Default value
        assertThat(saved.getDownvoteCount()).isEqualTo(0); // Default value
        assertThat(saved.getVotes()).isEmpty(); // Default empty list
        assertThat(saved.getReviewText()).isNull(); // Default null
        assertThat(saved.getHoursPerWeek()).isNull(); // Default null
    }

    // Helper methods
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