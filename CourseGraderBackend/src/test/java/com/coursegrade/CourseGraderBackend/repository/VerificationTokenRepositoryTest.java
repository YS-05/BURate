package com.coursegrade.CourseGraderBackend.repository;

import com.coursegrade.CourseGraderBackend.model.VerificationToken;
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
class VerificationTokenRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Test
    void save_ShouldPersistVerificationToken() {
        // Given
        User user = createTestUser("verify@email.com");
        entityManager.persistAndFlush(user);

        VerificationToken token = new VerificationToken("verify123", user);

        // When
        VerificationToken saved = verificationTokenRepository.save(token);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getToken()).isEqualTo("verify123");
        assertThat(saved.getUser()).isEqualTo(user);
        assertThat(saved.getExpiryDate()).isNotNull();
    }

    @Test
    void findByUser_WhenTokenExists_ShouldReturnToken() {
        // Given
        User user = createTestUser("user@email.com");
        entityManager.persistAndFlush(user);

        VerificationToken token = new VerificationToken("token456", user);
        entityManager.persistAndFlush(token);

        // When
        Optional<VerificationToken> found = verificationTokenRepository.findByUser(user);

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getToken()).isEqualTo("token456");
        assertThat(found.get().getUser().getEmail()).isEqualTo("user@email.com");
    }

    @Test
    void findByUser_WhenTokenDoesNotExist_ShouldReturnEmpty() {
        // Given
        User user = createTestUser("notoken@email.com");
        entityManager.persistAndFlush(user);

        // When
        Optional<VerificationToken> found = verificationTokenRepository.findByUser(user);

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void findById_WhenTokenExists_ShouldReturnToken() {
        // Given
        User user = createTestUser("findid@email.com");
        entityManager.persistAndFlush(user);

        VerificationToken token = new VerificationToken("findid123", user);
        VerificationToken saved = entityManager.persistAndFlush(token);

        // When
        Optional<VerificationToken> found = verificationTokenRepository.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getToken()).isEqualTo("findid123");
        assertThat(found.get().getId()).isEqualTo(saved.getId());
    }

    @Test
    void findById_WhenTokenDoesNotExist_ShouldReturnEmpty() {
        // When
        Optional<VerificationToken> found = verificationTokenRepository.findById(999L);

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void deleteById_ShouldRemoveToken() {
        // Given
        User user = createTestUser("delete@email.com");
        entityManager.persistAndFlush(user);

        VerificationToken token = new VerificationToken("delete123", user);
        VerificationToken saved = entityManager.persistAndFlush(token);

        // When
        verificationTokenRepository.deleteById(saved.getId());
        entityManager.flush();

        // Then
        Optional<VerificationToken> found = verificationTokenRepository.findById(saved.getId());
        assertThat(found).isEmpty();
    }

    @Test
    void save_TokenWithCustomExpiryDate_ShouldPersistCorrectly() {
        // Given
        User user = createTestUser("custom@email.com");
        entityManager.persistAndFlush(user);

        VerificationToken token = new VerificationToken();
        token.setToken("custom123");
        token.setUser(user);
        LocalDateTime customExpiry = LocalDateTime.now().plusHours(2);
        token.setExpiryDate(customExpiry);

        // When
        VerificationToken saved = verificationTokenRepository.save(token);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getToken()).isEqualTo("custom123");
        assertThat(saved.getUser()).isEqualTo(user);
        assertThat(saved.getExpiryDate()).isEqualTo(customExpiry);
    }

    @Test
    void save_TokenWithNullToken_ShouldPersist() {
        // Given
        User user = createTestUser("nulltoken@email.com");
        entityManager.persistAndFlush(user);

        VerificationToken token = new VerificationToken();
        token.setUser(user);
        token.setExpiryDate(LocalDateTime.now().plusMinutes(15));

        // When
        VerificationToken saved = verificationTokenRepository.save(token);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getToken()).isNull();
        assertThat(saved.getUser()).isEqualTo(user);
    }

    @Test
    void findByUser_WithDifferentUsers_ShouldReturnCorrectTokens() {
        // Given
        User user1 = createTestUser("user1@test.com");
        User user2 = createTestUser("user2@test.com");
        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);

        VerificationToken token1 = new VerificationToken("token_for_user1", user1);
        VerificationToken token2 = new VerificationToken("token_for_user2", user2);
        entityManager.persistAndFlush(token1);
        entityManager.persistAndFlush(token2);

        // When
        Optional<VerificationToken> found1 = verificationTokenRepository.findByUser(user1);
        Optional<VerificationToken> found2 = verificationTokenRepository.findByUser(user2);

        // Then
        assertThat(found1).isPresent();
        assertThat(found1.get().getToken()).isEqualTo("token_for_user1");
        assertThat(found1.get().getUser()).isEqualTo(user1);

        assertThat(found2).isPresent();
        assertThat(found2.get().getToken()).isEqualTo("token_for_user2");
        assertThat(found2.get().getUser()).isEqualTo(user2);
    }

    @Test
    void findAll_ShouldReturnAllTokens() {
        // Given
        User user1 = createTestUser("user1@email.com");
        User user2 = createTestUser("user2@email.com");
        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);

        VerificationToken token1 = new VerificationToken("token1", user1);
        VerificationToken token2 = new VerificationToken("token2", user2);
        entityManager.persistAndFlush(token1);
        entityManager.persistAndFlush(token2);

        // When
        List<VerificationToken> tokens = verificationTokenRepository.findAll();

        // Then
        assertThat(tokens).hasSize(2);
        assertThat(tokens).extracting(VerificationToken::getToken)
                .containsExactlyInAnyOrder("token1", "token2");
    }

    @Test
    void count_ShouldReturnCorrectCount() {
        // Given
        User user1 = createTestUser("count1@email.com");
        User user2 = createTestUser("count2@email.com");
        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);

        VerificationToken token1 = new VerificationToken("count1", user1);
        VerificationToken token2 = new VerificationToken("count2", user2);
        entityManager.persistAndFlush(token1);
        entityManager.persistAndFlush(token2);

        // When
        long count = verificationTokenRepository.count();

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    void existsById_WhenTokenExists_ShouldReturnTrue() {
        // Given
        User user = createTestUser("exists@email.com");
        entityManager.persistAndFlush(user);

        VerificationToken token = new VerificationToken("exists123", user);
        VerificationToken saved = entityManager.persistAndFlush(token);

        // When
        boolean exists = verificationTokenRepository.existsById(saved.getId());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsById_WhenTokenDoesNotExist_ShouldReturnFalse() {
        // When
        boolean exists = verificationTokenRepository.existsById(999L);

        // Then
        assertThat(exists).isFalse();
    }

    // Entity Logic Tests (convenient to test here since we're creating entities)

    @Test
    void isExpired_WhenTokenIsNotExpired_ShouldReturnFalse() {
        // Given
        User user = createTestUser("notexpired@email.com");
        VerificationToken token = new VerificationToken("valid123", user);

        // When
        boolean isExpired = token.isExpired();

        // Then
        assertThat(isExpired).isFalse();
    }

    @Test
    void isExpired_WhenTokenIsExpired_ShouldReturnTrue() {
        // Given
        User user = createTestUser("expired@email.com");
        VerificationToken token = new VerificationToken();
        token.setToken("expired123");
        token.setUser(user);
        token.setExpiryDate(LocalDateTime.now().minusMinutes(1)); // 1 minute ago

        // When
        boolean isExpired = token.isExpired();

        // Then
        assertThat(isExpired).isTrue();
    }

    @Test
    void constructor_WithTokenAndUser_ShouldSetExpiryDateCorrectly() {
        // Given
        User user = createTestUser("constructor@email.com");

        // When
        VerificationToken token = new VerificationToken("constructor123", user);

        // Then
        assertThat(token.getToken()).isEqualTo("constructor123");
        assertThat(token.getUser()).isEqualTo(user);
        assertThat(token.getExpiryDate()).isNotNull();
        assertThat(token.getExpiryDate()).isAfter(LocalDateTime.now().plusMinutes(14));
        assertThat(token.getExpiryDate()).isBefore(LocalDateTime.now().plusMinutes(16));
    }

    // Helper method to create test users
    private User createTestUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setPassword("password");
        user.setCollege("CAS");
        user.setMajor("Computer Science");
        user.setRole(Role.STUDENT);
        user.setEnabled(false); // Verification tokens are for unverified users
        return user;
    }
}