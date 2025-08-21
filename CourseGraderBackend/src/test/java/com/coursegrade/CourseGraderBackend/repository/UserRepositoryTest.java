package com.coursegrade.CourseGraderBackend.repository;

import com.coursegrade.CourseGraderBackend.model.User;
import com.coursegrade.CourseGraderBackend.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmail_WhenUserExists_ShouldReturnUser() {
        // Given
        User user = createTestUser("test@example.com", "password123");
        entityManager.persistAndFlush(user);
        // When
        Optional<User> found = userRepository.findByEmail("test@example.com");
        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
        assertThat(found.get().getCollege()).isEqualTo("CAS");
        assertThat(found.get().getMajor()).isEqualTo("Computer Science");
    }

    @Test
    void findByEmail_WhenUserDoesNotExist_ShouldReturnEmpty() {
        // When
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");
        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void findByEmail_WithNullEmail_ShouldReturnEmpty() {
        // When
        Optional<User> found = userRepository.findByEmail(null);
        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void existsByEmail_WhenUserExists_ShouldReturnTrue() {
        // Given
        User user = createTestUser("existing@example.com", "password123");
        entityManager.persistAndFlush(user);
        // When
        boolean exists = userRepository.existsByEmail("existing@example.com");
        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsByEmail_WhenUserDoesNotExist_ShouldReturnFalse() {
        // When
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");
        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void existsByEmail_WithNullEmail_ShouldReturnFalse() {
        // When
        boolean exists = userRepository.existsByEmail(null);
        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void save_ShouldPersistUser() {
        // Given
        User user = createTestUser("save@example.com", "password123");
        // When
        User saved = userRepository.save(user);
        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEmail()).isEqualTo("save@example.com");
        assertThat(saved.isEnabled()).isFalse(); // default value
        assertThat(saved.getRole()).isEqualTo(Role.STUDENT); // default value
    }

    @Test
    void findById_WhenUserExists_ShouldReturnUser() {
        // Given
        User user = createTestUser("findbyid@example.com", "password123");
        User saved = entityManager.persistAndFlush(user);
        // When
        Optional<User> found = userRepository.findById(saved.getId());
        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("findbyid@example.com");
    }

    @Test
    void findById_WhenUserDoesNotExist_ShouldReturnEmpty() {
        // When
        Optional<User> found = userRepository.findById(999L);
        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void deleteById_ShouldRemoveUser() {
        // Given
        User user = createTestUser("delete@example.com", "password123");
        User saved = entityManager.persistAndFlush(user);
        // When
        userRepository.deleteById(saved.getId());
        entityManager.flush();
        // Then
        Optional<User> found = userRepository.findById(saved.getId());
        assertThat(found).isEmpty();
    }

    @Test
    void findAll_ShouldReturnAllUsers() {
        // Given
        User user1 = createTestUser("user1@example.com", "password1");
        User user2 = createTestUser("user2@example.com", "password2");
        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);
        // When
        var users = userRepository.findAll();
        // Then
        assertThat(users).hasSize(2);
        assertThat(users).extracting(User::getEmail)
                .containsExactlyInAnyOrder("user1@example.com", "user2@example.com");
    }

    @Test
    void save_UserWithCompleteData_ShouldPersistAllFields() {
        // Given
        User user = new User();
        user.setEmail("complete@example.com");
        user.setPassword("encodedPassword");
        user.setExpectedGrad(2025);
        user.setCollege("ENG");
        user.setMajor("Electrical Engineering");
        user.setRole(Role.STUDENT);
        user.setEnabled(true);
        // When
        User saved = userRepository.save(user);
        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEmail()).isEqualTo("complete@example.com");
        assertThat(saved.getPassword()).isEqualTo("encodedPassword");
        assertThat(saved.getExpectedGrad()).isEqualTo(2025);
        assertThat(saved.getCollege()).isEqualTo("ENG");
        assertThat(saved.getMajor()).isEqualTo("Electrical Engineering");
        assertThat(saved.getRole()).isEqualTo(Role.STUDENT);
        assertThat(saved.isEnabled()).isTrue();
    }

    @Test
    void save_UserWithMinimalData_ShouldUseDefaults() {
        // Given
        User user = new User();
        user.setEmail("minimal@example.com");
        user.setPassword("password");
        // When
        User saved = userRepository.save(user);
        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEmail()).isEqualTo("minimal@example.com");
        assertThat(saved.getRole()).isEqualTo(Role.STUDENT); // default
        assertThat(saved.isEnabled()).isFalse(); // default
        assertThat(saved.getCompletedCourses()).isEmpty(); // default
        assertThat(saved.getSavedCourses()).isEmpty(); // default
        assertThat(saved.getCoursesInProgress()).isEmpty(); // default
        assertThat(saved.getHubProgress()).isEmpty(); // default
    }

    // Helper method to create test users
    private User createTestUser(String email, String password) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setExpectedGrad(2024);
        user.setCollege("CAS");
        user.setMajor("Computer Science");
        user.setRole(Role.STUDENT);
        user.setEnabled(false);
        return user;
    }
}