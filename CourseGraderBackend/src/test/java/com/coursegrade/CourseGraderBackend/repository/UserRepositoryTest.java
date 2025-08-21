package com.coursegrade.CourseGraderBackend.repository;

import com.coursegrade.CourseGraderBackend.model.User;
import com.coursegrade.CourseGraderBackend.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmail_WithNullEmail_ShouldReturnEmpty() {
        // When
        Optional<User> found = userRepository.findByEmail(null);

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void existsByEmail_WithNullEmail_ShouldReturnFalse() {
        // When
        boolean exists = userRepository.existsByEmail(null);

        // Then
        assertThat(exists).isFalse();
    }

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