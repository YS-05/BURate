package com.coursegrade.CourseGraderBackend.repository;

import com.coursegrade.CourseGraderBackend.model.PasswordResetToken;
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
class PasswordResetTokenRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Test
    void save_ShouldPersistPasswordResetToken() {
        // Given
        User user = createTestUser("test@email.com");
        entityManager.persistAndFlush(user);

        PasswordResetToken token = new PasswordResetToken("reset123", user);

        // When
        PasswordResetToken saved = passwordResetTokenRepository.save(token);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getToken()).isEqualTo("reset123");
        assertThat(saved.getUser()).isEqualTo(user);
        assertThat(saved.getExpiryDate()).isNotNull();
    }

    @Test
    void findByUser_WhenTokenExists_ShouldReturnToken() {
        // Given
        User user = createTestUser("user@email.com");
        entityManager.persistAndFlush(user);

        PasswordResetToken token = new PasswordResetToken("token456", user);
        entityManager.persistAndFlush(token);

        // When
        Optional<PasswordResetToken> found = passwordResetTokenRepository.findByUser(user);

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
        Optional<PasswordResetToken> found = passwordResetTokenRepository.findByUser(user);

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void findById_WhenTokenExists_ShouldReturnToken() {
        // Given
        User user = createTestUser("findid@email.com");
        entityManager.persistAndFlush(user);

        PasswordResetToken token = new PasswordResetToken("findid123", user);
        PasswordResetToken saved = entityManager.persistAndFlush(token);

        // When
        Optional<PasswordResetToken> found = passwordResetTokenRepository.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getToken()).isEqualTo("findid123");
        assertThat(found.get().getId()).isEqualTo(saved.getId());
    }

    @Test
    void findById_WhenTokenDoesNotExist_ShouldReturnEmpty() {
        // When
        Optional<PasswordResetToken> found = passwordResetTokenRepository.findById(999L);

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void deleteById_ShouldRemoveToken() {
        // Given
        User user = createTestUser("delete@email.com");
        entityManager.persistAndFlush(user);

        PasswordResetToken token = new PasswordResetToken("delete123", user);
        PasswordResetToken saved = entityManager.persistAndFlush(token);

        // When
        passwordResetTokenRepository.deleteById(saved.getId());
        entityManager.flush();

        // Then
        Optional<PasswordResetToken> found = passwordResetTokenRepository.findById(saved.getId());
        assertThat(found).isEmpty();
    }

    @Test
    void save_TokenWithCustomExpiryDate_ShouldPersistCorrectly() {
        // Given
        User user = createTestUser("custom@email.com");
        entityManager.persistAndFlush(user);

        PasswordResetToken token = new PasswordResetToken();
        token.setToken("custom123");
        token.setUser(user);
        LocalDateTime customExpiry = LocalDateTime.now().plusHours(1);
        token.setExpiryDate(customExpiry);

        // When
        PasswordResetToken saved = passwordResetTokenRepository.save(token);

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

        PasswordResetToken token = new PasswordResetToken();
        token.setUser(user);
        token.setExpiryDate(LocalDateTime.now().plusMinutes(15));

        // When
        PasswordResetToken saved = passwordResetTokenRepository.save(token);

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

        PasswordResetToken token1 = new PasswordResetToken("token_for_user1", user1);
        PasswordResetToken token2 = new PasswordResetToken("token_for_user2", user2);
        entityManager.persistAndFlush(token1);
        entityManager.persistAndFlush(token2);

        // When
        Optional<PasswordResetToken> found1 = passwordResetTokenRepository.findByUser(user1);
        Optional<PasswordResetToken> found2 = passwordResetTokenRepository.findByUser(user2);

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

        PasswordResetToken token1 = new PasswordResetToken("token1", user1);
        PasswordResetToken token2 = new PasswordResetToken("token2", user2);
        entityManager.persistAndFlush(token1);
        entityManager.persistAndFlush(token2);

        // When
        List<PasswordResetToken> tokens = passwordResetTokenRepository.findAll();

        // Then
        assertThat(tokens).hasSize(2);
        assertThat(tokens).extracting(PasswordResetToken::getToken)
                .containsExactlyInAnyOrder("token1", "token2");
    }

    @Test
    void count_ShouldReturnCorrectCount() {
        // Given
        User user1 = createTestUser("count1@email.com");
        User user2 = createTestUser("count2@email.com");
        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);

        PasswordResetToken token1 = new PasswordResetToken("count1", user1);
        PasswordResetToken token2 = new PasswordResetToken("count2", user2);
        entityManager.persistAndFlush(token1);
        entityManager.persistAndFlush(token2);

        // When
        long count = passwordResetTokenRepository.count();

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    void existsById_WhenTokenExists_ShouldReturnTrue() {
        // Given
        User user = createTestUser("exists@email.com");
        entityManager.persistAndFlush(user);

        PasswordResetToken token = new PasswordResetToken("exists123", user);
        PasswordResetToken saved = entityManager.persistAndFlush(token);

        // When
        boolean exists = passwordResetTokenRepository.existsById(saved.getId());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsById_WhenTokenDoesNotExist_ShouldReturnFalse() {
        // When
        boolean exists = passwordResetTokenRepository.existsById(999L);

        // Then
        assertThat(exists).isFalse();
    }

    // Entity Logic Tests (convenient to test here since we're creating entities)

    @Test
    void isExpired_WhenTokenIsNotExpired_ShouldReturnFalse() {
        // Given
        User user = createTestUser("notexpired@email.com");
        PasswordResetToken token = new PasswordResetToken("valid123", user);

        // When
        boolean isExpired = token.isExpired();

        // Then
        assertThat(isExpired).isFalse();
    }

    @Test
    void isExpired_WhenTokenIsExpired_ShouldReturnTrue() {
        // Given
        User user = createTestUser("expired@email.com");
        PasswordResetToken token = new PasswordResetToken();
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
        PasswordResetToken token = new PasswordResetToken("constructor123", user);

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
        user.setEnabled(true);
        return user;
    }
}