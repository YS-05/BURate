package com.coursegrade.CourseGraderBackend.service;

import com.coursegrade.CourseGraderBackend.dto.UserResponseDTO;
import com.coursegrade.CourseGraderBackend.model.PasswordResetToken;
import com.coursegrade.CourseGraderBackend.model.Role;
import com.coursegrade.CourseGraderBackend.model.User;
import com.coursegrade.CourseGraderBackend.model.VerificationToken;
import com.coursegrade.CourseGraderBackend.repository.PasswordResetTokenRepository;
import com.coursegrade.CourseGraderBackend.repository.UserRepository;
import com.coursegrade.CourseGraderBackend.repository.VerificationTokenRepository;
import com.coursegrade.CourseGraderBackend.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;
    private final JwtService jwtService;

    @Transactional
    public void register(String email, String college, String major, Integer expectedGrad, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setEmail(email);
        user.setMajor(major);
        user.setCollege(college);
        user.setExpectedGrad(expectedGrad);
        user.setPassword(passwordEncoder.encode(password));
        if (user.getEmail().equals("ysharma@bu.edu")) {
            user.setRole(Role.ADMIN);
        }
        else {
            user.setRole(Role.STUDENT);
        }
        user.setEnabled(false);

        userRepository.save(user);

        String verificationCode = generateVerificationCode();
        verificationTokenRepository.save(new VerificationToken(verificationCode, user));
        userRepository.save(user);
        emailService.sendVerificationEmail(user, verificationCode);
    }

    @Transactional
    public boolean verify(String email, String verificationCode) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not found"));

        if (user.isEnabled()) throw new RuntimeException("User is already verified");

        VerificationToken token = verificationTokenRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Verification token not found"));

        if (token.isExpired()) {
            verificationTokenRepository.delete(token);
            throw new RuntimeException("Verification token is expired");
        }
        if (!verificationCode.equals(token.getToken())) {
            throw new RuntimeException("Verification code is incorrect");
        }
        user.setEnabled(true);
        userRepository.save(user);
        verificationTokenRepository.delete(token);
        return true;
    }

    @Transactional
    public Map<String, Object> login(String email, String password) {
        boolean enabledError = false;
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Invalid username or password"));

            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new RuntimeException("Invalid username or password");
            }

            if (!user.isEnabled()) {
                enabledError = true;
                throw new RuntimeException("Please verify your email before logging in");
            }

            String token = jwtService.generateToken(user);
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", convertToUserResponseDTO(user));
            return response;
        } catch (Exception e) {
            if (enabledError) {
                System.out.println("User is not enabled");
                throw new RuntimeException("Please verify your email before logging in");
            }
            System.out.println("Invalid username or password is thrown");
            throw new RuntimeException("Invalid username or password");
        }
    }

    @Transactional
    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not found"));
        if (user.isEnabled()) {
            throw new RuntimeException("User is already verified");
        }
        verificationTokenRepository.findByUser(user).ifPresent(verificationTokenRepository::delete);
        String code = generateVerificationCode();
        VerificationToken token = new VerificationToken(code, user);
        verificationTokenRepository.save(token);
        emailService.resendVerificationEmail(user, code);
    }

    @Transactional
    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No account found with this email."));
        passwordResetTokenRepository.findByUser(user).ifPresent(passwordResetTokenRepository::delete);
        String verificationCode = generateVerificationCode();
        PasswordResetToken token = new PasswordResetToken(verificationCode, user);
        passwordResetTokenRepository.save(token);
        emailService.sendPasswordResetEmail(email, verificationCode);
    }

    @Transactional
    public void resetPassword(String email, String resetCode, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not found"));
        PasswordResetToken token = passwordResetTokenRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Verification token not found"));
        if (token.isExpired()) {
            passwordResetTokenRepository.delete(token);
            throw new RuntimeException("Reset password token is expired");
        }
        if (!resetCode.equals(token.getToken())) {
            throw new RuntimeException("Reset code is incorrect");
        }
        passwordResetTokenRepository.delete(token);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional
    public void updatePassword(String oldPassword, String newPassword, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public UserResponseDTO getCurrentUser(String token) {
        String email = jwtService.extractEmail(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return convertToUserResponseDTO(user);
    }

    public UserResponseDTO convertToUserResponseDTO(User user) {
        UserResponseDTO response = new UserResponseDTO();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setEnabled(user.isEnabled());
        return response;
    }

    public String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // generates 6-digit code
        return String.valueOf(code);
    }


}
