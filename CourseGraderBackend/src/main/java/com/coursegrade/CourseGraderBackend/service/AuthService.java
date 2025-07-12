package com.coursegrade.CourseGraderBackend.service;

import com.coursegrade.CourseGraderBackend.dto.CourseDTO;
import com.coursegrade.CourseGraderBackend.dto.CourseDisplayDTO;
import com.coursegrade.CourseGraderBackend.dto.UserResponseDTO;
import com.coursegrade.CourseGraderBackend.model.Course;
import com.coursegrade.CourseGraderBackend.model.Role;
import com.coursegrade.CourseGraderBackend.model.User;
import com.coursegrade.CourseGraderBackend.model.VerificationToken;
import com.coursegrade.CourseGraderBackend.repository.UserRepository;
import com.coursegrade.CourseGraderBackend.repository.VerificationTokenRepository;
import com.coursegrade.CourseGraderBackend.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository verificationTokenRepository;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CourseService courseService;

    @Transactional
    public void register(String email, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already registered");
        }
        /* // Commented out for testing purposes
        if (!email.endsWith("@bu.edu")) {
            throw new RuntimeException("Must be a BU email");
        }
         */

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(Role.STUDENT);
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
    public Map<String, Object> login(String email, String password) { // TODO: Implement more features here and checks
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            User user = (User) auth.getPrincipal();
            if (!user.isEnabled()) {
                throw new RuntimeException("Please verify your email before logging in");
            }
            String token = jwtService.generateToken(user);
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", convertToUserResponseDTO(user));
            return response;
        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
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
                .orElseThrow(() -> new RuntimeException("No account with this email"));
        verificationTokenRepository.findByUser(user).ifPresent(verificationTokenRepository::delete);
        String verificationCode = generateVerificationCode();
        VerificationToken token = new VerificationToken(verificationCode, user);
        verificationTokenRepository.save(token);
        emailService.sendPasswordResetEmail(user, verificationCode);
    }

    @Transactional
    public void resetPassword(String email, String resetCode, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not found"));
        VerificationToken token = verificationTokenRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Verification token not found"));
        if (token.isExpired()) {
            verificationTokenRepository.delete(token);
            throw new RuntimeException("Reset password token is expired");
        }
        if (!resetCode.equals(token.getToken())) {
            throw new RuntimeException("Reset code is incorrect");
        }
        verificationTokenRepository.delete(token);
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
        Set<Course> courses = user.getCompletedCourses();
        Set<CourseDisplayDTO> courseDTOs = new HashSet<>();
        for (Course course : courses) {
            CourseDisplayDTO dto = courseService.convertToDisplayDTO(course);
            courseDTOs.add(dto);
        }
        response.setCompletedCourses(courseDTOs);
        response.setHubsCompleted(user.getHubProgress());
        return response;
    }

    public String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // generates 6-digit code
        return String.valueOf(code);
    }


}
