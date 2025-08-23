package com.coursegrade.CourseGraderBackend.service;

import com.coursegrade.CourseGraderBackend.dto.UserResponseDTO;
import com.coursegrade.CourseGraderBackend.model.*;
import com.coursegrade.CourseGraderBackend.repository.PasswordResetTokenRepository;
import com.coursegrade.CourseGraderBackend.repository.UserRepository;
import com.coursegrade.CourseGraderBackend.repository.VerificationTokenRepository;
import com.coursegrade.CourseGraderBackend.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private VerificationTokenRepository verificationTokenRepository;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private Authentication authentication;

    @Mock
    private EmailService emailService; // Used indirectly by the AuthService method, fails without

    @InjectMocks
    private AuthService authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = createTestUser();
    }

    @Test
    void register_EmailAlreadyExists_ShouldThrowException() {
        // Given
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> authService.register("existing@example.com", "Engineering", "CS", 2025, "password"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Email already registered");

        // Verify no database mutations occurred
        verify(userRepository, never()).save(any(User.class));
        verify(verificationTokenRepository, never()).save(any(VerificationToken.class));
    }

    // VERIFY - Test the complex token validation and expiration logic
    @Test
    void verify_ExpiredToken_ShouldDeleteTokenAndThrowException() {
        // Given
        testUser.setEnabled(false);
        VerificationToken expiredToken = new VerificationToken("123456", testUser);
        expiredToken.setExpiryDate(LocalDateTime.now().minusHours(1)); // Expired

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(verificationTokenRepository.findByUser(testUser)).thenReturn(Optional.of(expiredToken));

        // When & Then
        assertThatThrownBy(() -> authService.verify("test@example.com", "123456"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Verification token is expired");

        // Verify cleanup happened
        verify(verificationTokenRepository).delete(expiredToken);
        verify(userRepository, never()).save(testUser); // User should not be enabled
    }

    @Test
    void verify_SuccessfulVerification_ShouldEnableUserAndCleanupToken() {
        // Given
        testUser.setEnabled(false);
        VerificationToken validToken = new VerificationToken("123456", testUser);
        validToken.setExpiryDate(LocalDateTime.now().plusHours(1));

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(verificationTokenRepository.findByUser(testUser)).thenReturn(Optional.of(validToken));

        // When
        boolean result = authService.verify("test@example.com", "123456");

        // Then
        assertThat(result).isTrue();
        assertThat(testUser.isEnabled()).isTrue();
        verify(userRepository).save(testUser);
        verify(verificationTokenRepository).delete(validToken);
    }

    // LOGIN - Test complex authentication flow and user state validation
    @Test
    void login_UserNotEnabled_ShouldThrowSpecificException() {
        // Given
        testUser.setEnabled(false);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);

        // When & Then
        assertThatThrownBy(() -> authService.login("test@example.com", "password123"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Please verify your email before logging in");
    }

    @Test
    void login_AuthenticationFailure_ShouldReturnGenericError() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Bad credentials"));

        // When & Then
        assertThatThrownBy(() -> authService.login("test@example.com", "wrongpassword"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid username or password");
    }

    // RESEND VERIFICATION - Test token cleanup and regeneration logic
    @Test
    void resendVerificationEmail_ShouldCleanupOldTokenAndCreateNew() {
        // Given
        testUser.setEnabled(false);
        VerificationToken oldToken = new VerificationToken("old-token", testUser);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(verificationTokenRepository.findByUser(testUser)).thenReturn(Optional.of(oldToken));

        // When
        authService.resendVerificationEmail("test@example.com");

        // Then - Verify old token cleanup and new token creation
        verify(verificationTokenRepository).delete(oldToken);
        verify(verificationTokenRepository).save(any(VerificationToken.class));
    }

    // PASSWORD RESET - Test the complex token-based password reset flow
    @Test
    void resetPassword_ExpiredToken_ShouldCleanupAndThrow() {
        // Given
        PasswordResetToken expiredToken = new PasswordResetToken("123456", testUser);
        expiredToken.setExpiryDate(LocalDateTime.now().minusHours(1));

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordResetTokenRepository.findByUser(testUser)).thenReturn(Optional.of(expiredToken));

        // When & Then
        assertThatThrownBy(() -> authService.resetPassword("test@example.com", "123456", "newPassword"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Reset password token is expired");

        // Verify cleanup of expired token
        verify(passwordResetTokenRepository).delete(expiredToken);
        verify(userRepository, never()).save(testUser);
    }

    @Test
    void resetPassword_ValidToken_ShouldUpdatePasswordAndCleanup() {
        // Given
        PasswordResetToken validToken = new PasswordResetToken("123456", testUser);
        validToken.setExpiryDate(LocalDateTime.now().plusHours(1));

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordResetTokenRepository.findByUser(testUser)).thenReturn(Optional.of(validToken));
        when(passwordEncoder.encode("newPassword123")).thenReturn("encodedNewPassword");

        // When
        authService.resetPassword("test@example.com", "123456", "newPassword123");

        // Then - Verify password update and token cleanup
        verify(passwordResetTokenRepository).delete(validToken);
        verify(passwordEncoder).encode("newPassword123");
        verify(userRepository).save(testUser);
        assertThat(testUser.getPassword()).isEqualTo("encodedNewPassword");
    }

    // UPDATE PASSWORD - Test old password validation logic
    @Test
    void updatePassword_IncorrectOldPassword_ShouldThrowException() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongOldPassword", testUser.getPassword())).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> authService.updatePassword("wrongOldPassword", "newPassword", 1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Old password is incorrect");

        // Verify password was not changed
        verify(userRepository, never()).save(testUser);
    }

    // JWT TOKEN EXTRACTION - Test the token-to-user resolution logic
    @Test
    void getCurrentUser_ValidToken_ShouldExtractAndReturnUser() {
        // Given
        when(jwtService.extractEmail("valid-token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // When
        UserResponseDTO result = authService.getCurrentUser("valid-token");

        // Then - Test the DTO conversion logic
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getRole()).isEqualTo(Role.STUDENT);
        assertThat(result.isEnabled()).isTrue();
    }

    // Helper method
    private User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setCollege("Engineering");
        user.setMajor("Computer Science");
        user.setExpectedGrad(2025);
        user.setRole(Role.STUDENT);
        user.setEnabled(true);
        return user;
    }
}