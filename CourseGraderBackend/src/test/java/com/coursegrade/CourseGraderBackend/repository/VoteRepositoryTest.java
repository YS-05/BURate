package com.coursegrade.CourseGraderBackend.repository;

import com.coursegrade.CourseGraderBackend.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
class VoteRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private VoteRepository voteRepository;

    @Test
    void save_ShouldPersistVote() {
        // Given
        User user = createTestUser("voter@email.com");
        Course course = createTestCourse("Voting Course", "CAS", "CS", "101");
        Review review = createTestReview(course, user, "Dr. Vote", 4, 3, 3, 4, 5);

        entityManager.persistAndFlush(user);
        entityManager.persistAndFlush(course);
        entityManager.persistAndFlush(review);

        Vote vote = Vote.builder()
                .user(user)
                .review(review)
                .voteType(VoteType.UPVOTE)
                .build();

        // When
        Vote saved = voteRepository.save(vote);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUser()).isEqualTo(user);
        assertThat(saved.getReview()).isEqualTo(review);
        assertThat(saved.getVoteType()).isEqualTo(VoteType.UPVOTE);
    }

    @Test
    void findByUserAndReview_WhenVoteExists_ShouldReturnVote() {
        // Given
        User user = createTestUser("finder@email.com");
        Course course = createTestCourse("Find Course", "CAS", "CS", "101");
        Review review = createTestReview(course, user, "Dr. Find", 4, 3, 3, 4, 5);

        entityManager.persistAndFlush(user);
        entityManager.persistAndFlush(course);
        entityManager.persistAndFlush(review);

        Vote vote = Vote.builder()
                .user(user)
                .review(review)
                .voteType(VoteType.DOWNVOTE)
                .build();
        entityManager.persistAndFlush(vote);

        // When
        Optional<Vote> found = voteRepository.findByUserAndReview(user, review);

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getUser()).isEqualTo(user);
        assertThat(found.get().getReview()).isEqualTo(review);
        assertThat(found.get().getVoteType()).isEqualTo(VoteType.DOWNVOTE);
    }

    @Test
    void findByUserAndReview_WhenVoteDoesNotExist_ShouldReturnEmpty() {
        // Given
        User user = createTestUser("novote@email.com");
        Course course = createTestCourse("No Vote Course", "CAS", "CS", "101");
        Review review = createTestReview(course, user, "Dr. NoVote", 4, 3, 3, 4, 5);

        entityManager.persistAndFlush(user);
        entityManager.persistAndFlush(course);
        entityManager.persistAndFlush(review);

        // When
        Optional<Vote> found = voteRepository.findByUserAndReview(user, review);

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void findByReview_ShouldReturnAllVotesForReview() {
        // Given
        User user1 = createTestUser("voter1@email.com");
        User user2 = createTestUser("voter2@email.com");
        Course course = createTestCourse("Popular Course", "CAS", "CS", "101");
        Review review = createTestReview(course, user1, "Dr. Popular", 4, 3, 3, 4, 5);

        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);
        entityManager.persistAndFlush(course);
        entityManager.persistAndFlush(review);

        Vote upvote = Vote.builder()
                .user(user1)
                .review(review)
                .voteType(VoteType.UPVOTE)
                .build();

        Vote downvote = Vote.builder()
                .user(user2)
                .review(review)
                .voteType(VoteType.DOWNVOTE)
                .build();

        entityManager.persistAndFlush(upvote);
        entityManager.persistAndFlush(downvote);

        // When
        List<Vote> votes = voteRepository.findByReview(review);

        // Then
        assertThat(votes).hasSize(2);
        assertThat(votes).extracting(Vote::getVoteType)
                .containsExactlyInAnyOrder(VoteType.UPVOTE, VoteType.DOWNVOTE);
        assertThat(votes).allMatch(vote -> vote.getReview().equals(review));
    }

    @Test
    void findByUser_ShouldReturnAllVotesByUser() {
        // Given
        User user = createTestUser("activevoter@email.com");
        User otherUser = createTestUser("other@email.com");
        Course course1 = createTestCourse("Course 1", "CAS", "CS", "101");
        Course course2 = createTestCourse("Course 2", "CAS", "CS", "102");

        Review review1 = createTestReview(course1, otherUser, "Dr. One", 4, 3, 3, 4, 5);
        Review review2 = createTestReview(course2, otherUser, "Dr. Two", 3, 4, 2, 3, 4);

        entityManager.persistAndFlush(user);
        entityManager.persistAndFlush(otherUser);
        entityManager.persistAndFlush(course1);
        entityManager.persistAndFlush(course2);
        entityManager.persistAndFlush(review1);
        entityManager.persistAndFlush(review2);

        Vote vote1 = Vote.builder()
                .user(user)
                .review(review1)
                .voteType(VoteType.UPVOTE)
                .build();

        Vote vote2 = Vote.builder()
                .user(user)
                .review(review2)
                .voteType(VoteType.DOWNVOTE)
                .build();

        entityManager.persistAndFlush(vote1);
        entityManager.persistAndFlush(vote2);

        // When
        List<Vote> userVotes = voteRepository.findByUser(user);

        // Then
        assertThat(userVotes).hasSize(2);
        assertThat(userVotes).extracting(Vote::getVoteType)
                .containsExactlyInAnyOrder(VoteType.UPVOTE, VoteType.DOWNVOTE);
        assertThat(userVotes).allMatch(vote -> vote.getUser().equals(user));
    }

    @Test
    void save_UpvoteAndDownvote_ShouldPersistBothTypes() {
        // Given
        User user1 = createTestUser("upvoter@email.com");
        User user2 = createTestUser("downvoter@email.com");
        Course course = createTestCourse("Vote Course", "CAS", "CS", "101");
        Review review = createTestReview(course, user1, "Dr. Vote", 4, 3, 3, 4, 5);

        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);
        entityManager.persistAndFlush(course);
        entityManager.persistAndFlush(review);

        Vote upvote = Vote.builder()
                .user(user1)
                .review(review)
                .voteType(VoteType.UPVOTE)
                .build();

        Vote downvote = Vote.builder()
                .user(user2)
                .review(review)
                .voteType(VoteType.DOWNVOTE)
                .build();

        // When
        Vote savedUpvote = voteRepository.save(upvote);
        Vote savedDownvote = voteRepository.save(downvote);

        // Then
        assertThat(savedUpvote.getVoteType()).isEqualTo(VoteType.UPVOTE);
        assertThat(savedDownvote.getVoteType()).isEqualTo(VoteType.DOWNVOTE);
        assertThat(savedUpvote.getUser()).isEqualTo(user1);
        assertThat(savedDownvote.getUser()).isEqualTo(user2);
        assertThat(savedUpvote.getReview()).isEqualTo(review);
        assertThat(savedDownvote.getReview()).isEqualTo(review);
    }

    @Test
    void save_DuplicateUserReviewCombination_ShouldThrowException() {
        // Given
        User user = createTestUser("duplicate@email.com");
        Course course = createTestCourse("Duplicate Course", "CAS", "CS", "101");
        Review review = createTestReview(course, user, "Dr. Duplicate", 4, 3, 3, 4, 5);

        entityManager.persistAndFlush(user);
        entityManager.persistAndFlush(course);
        entityManager.persistAndFlush(review);

        Vote firstVote = Vote.builder()
                .user(user)
                .review(review)
                .voteType(VoteType.UPVOTE)
                .build();
        voteRepository.save(firstVote);
        entityManager.flush();

        Vote duplicateVote = Vote.builder()
                .user(user)
                .review(review)
                .voteType(VoteType.DOWNVOTE)
                .build();

        // When & Then
        assertThatThrownBy(() -> {
            voteRepository.save(duplicateVote);
            entityManager.flush();
        }).isInstanceOf(Exception.class); // Unique constraint violation
    }

    @Test
    void findById_WhenVoteExists_ShouldReturnVote() {
        // Given
        User user = createTestUser("findbyid@email.com");
        Course course = createTestCourse("FindById Course", "CAS", "CS", "101");
        Review review = createTestReview(course, user, "Dr. FindById", 4, 3, 3, 4, 5);

        entityManager.persistAndFlush(user);
        entityManager.persistAndFlush(course);
        entityManager.persistAndFlush(review);

        Vote vote = Vote.builder()
                .user(user)
                .review(review)
                .voteType(VoteType.UPVOTE)
                .build();
        Vote saved = entityManager.persistAndFlush(vote);

        // When
        Optional<Vote> found = voteRepository.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(saved.getId());
        assertThat(found.get().getVoteType()).isEqualTo(VoteType.UPVOTE);
    }

    @Test
    void deleteById_ShouldRemoveVote() {
        // Given
        User user = createTestUser("delete@email.com");
        Course course = createTestCourse("Delete Course", "CAS", "CS", "101");
        Review review = createTestReview(course, user, "Dr. Delete", 4, 3, 3, 4, 5);

        entityManager.persistAndFlush(user);
        entityManager.persistAndFlush(course);
        entityManager.persistAndFlush(review);

        Vote vote = Vote.builder()
                .user(user)
                .review(review)
                .voteType(VoteType.UPVOTE)
                .build();
        Vote saved = entityManager.persistAndFlush(vote);

        // When
        voteRepository.deleteById(saved.getId());
        entityManager.flush();

        // Then
        Optional<Vote> found = voteRepository.findById(saved.getId());
        assertThat(found).isEmpty();
    }

    @Test
    void findAll_ShouldReturnAllVotes() {
        // Given
        User user1 = createTestUser("user1@email.com");
        User user2 = createTestUser("user2@email.com");
        Course course = createTestCourse("All Votes Course", "CAS", "CS", "101");
        Review review = createTestReview(course, user1, "Dr. All", 4, 3, 3, 4, 5);

        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);
        entityManager.persistAndFlush(course);
        entityManager.persistAndFlush(review);

        Vote vote1 = Vote.builder()
                .user(user1)
                .review(review)
                .voteType(VoteType.UPVOTE)
                .build();

        Vote vote2 = Vote.builder()
                .user(user2)
                .review(review)
                .voteType(VoteType.DOWNVOTE)
                .build();

        entityManager.persistAndFlush(vote1);
        entityManager.persistAndFlush(vote2);

        // When
        List<Vote> allVotes = voteRepository.findAll();

        // Then
        assertThat(allVotes).hasSize(2);
        assertThat(allVotes).extracting(Vote::getVoteType)
                .containsExactlyInAnyOrder(VoteType.UPVOTE, VoteType.DOWNVOTE);
    }

    @Test
    void count_ShouldReturnCorrectCount() {
        // Given
        User user = createTestUser("counter@email.com");
        Course course = createTestCourse("Count Course", "CAS", "CS", "101");
        Review review = createTestReview(course, user, "Dr. Count", 4, 3, 3, 4, 5);

        entityManager.persistAndFlush(user);
        entityManager.persistAndFlush(course);
        entityManager.persistAndFlush(review);

        Vote vote = Vote.builder()
                .user(user)
                .review(review)
                .voteType(VoteType.UPVOTE)
                .build();
        entityManager.persistAndFlush(vote);

        // When
        long count = voteRepository.count();

        // Then
        assertThat(count).isEqualTo(1);
    }

    @Test
    void save_VoteWithBuilder_ShouldUseBuilderPattern() {
        // Given
        User user = createTestUser("builder@email.com");
        Course course = createTestCourse("Builder Course", "CAS", "CS", "101");
        Review review = createTestReview(course, user, "Dr. Builder", 4, 3, 3, 4, 5);

        entityManager.persistAndFlush(user);
        entityManager.persistAndFlush(course);
        entityManager.persistAndFlush(review);

        // When - Using builder pattern
        Vote vote = Vote.builder()
                .user(user)
                .review(review)
                .voteType(VoteType.UPVOTE)
                .build();

        Vote saved = voteRepository.save(vote);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUser()).isEqualTo(user);
        assertThat(saved.getReview()).isEqualTo(review);
        assertThat(saved.getVoteType()).isEqualTo(VoteType.UPVOTE);
    }

    @Test
    void findByReview_WhenNoVotes_ShouldReturnEmptyList() {
        // Given
        User user = createTestUser("noreviewvotes@email.com");
        Course course = createTestCourse("No Votes Course", "CAS", "CS", "101");
        Review review = createTestReview(course, user, "Dr. NoVotes", 4, 3, 3, 4, 5);

        entityManager.persistAndFlush(user);
        entityManager.persistAndFlush(course);
        entityManager.persistAndFlush(review);

        // When
        List<Vote> votes = voteRepository.findByReview(review);

        // Then
        assertThat(votes).isEmpty();
    }

    @Test
    void findByUser_WhenNoVotes_ShouldReturnEmptyList() {
        // Given
        User user = createTestUser("nouservotes@email.com");
        entityManager.persistAndFlush(user);

        // When
        List<Vote> votes = voteRepository.findByUser(user);

        // Then
        assertThat(votes).isEmpty();
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