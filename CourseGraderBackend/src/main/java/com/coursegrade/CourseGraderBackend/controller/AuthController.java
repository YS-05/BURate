package com.coursegrade.CourseGraderBackend.controller;

import com.coursegrade.CourseGraderBackend.dto.*;
import com.coursegrade.CourseGraderBackend.service.AuthService;
import com.coursegrade.CourseGraderBackend.service.WebScraperService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final WebScraperService webScraperService;

    private Map<String, List<String>> cachedMajorsByCollege = new HashMap<>();
    private boolean dataLoaded = false;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody @Valid RegisterUserDTO request) {
        authService.register(request.getEmail(), request.getCollege(), request.getMajor(),
                request.getExpectedGrad(), request.getPassword());
        return ResponseEntity.ok(Map.of(
                "message", "Registration successful! Please check your email to verify your account."
        ));
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, String>> verifyUser(@RequestBody @Valid VerifyUserDTO request) {
        boolean verified = authService.verify(request.getEmail(), request.getVerificationCode());
        if (verified) {
            return ResponseEntity.ok(Map.of(
                    "message", "Verification successful! You may log in now."
            ));
        }
        return ResponseEntity.badRequest().body(Map.of(
                "message", "Invalid or expired verification code"
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody @Valid LoginUserDTO request) {
        Map<String, Object> response = authService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<Map<String, String>> resendVerification(@RequestBody @Valid ResendVerificationOrPasswordDTO request) {
        authService.resendVerificationEmail(request.getEmail());
        return ResponseEntity.ok(Map.of(
                "message", "Verification code sent! Please check your email"
        ));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody @Valid ResendVerificationOrPasswordDTO request) {
        authService.requestPasswordReset(request.getEmail());
        return ResponseEntity.ok(Map.of(
                "message", "Check your email to set a new password!"
        ));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody @Valid PasswordResetDTO request) {
        authService.resetPassword(request.getEmail(), request.getVerificationCode(), request.getNewPassword());
        return ResponseEntity.ok(Map.of(
                "message", "Password reset successful! You can log in with your new password."
        ));
    }

    @GetMapping("/colleges")
    public ResponseEntity<Set<String>> getAllColleges() {
        ensureDataInitialized();
        Set<String> colleges = cachedMajorsByCollege.keySet();
        return ResponseEntity.ok(colleges);
    }

    @GetMapping("/majors/{college}")
    public ResponseEntity<List<String>> getMajorsByCollege(@PathVariable String college) {
        ensureDataInitialized();
        List<String> majors = cachedMajorsByCollege.get(college);
        return ResponseEntity.ok(majors);
    }

    private void ensureDataInitialized() {
        if (!dataLoaded || cachedMajorsByCollege.isEmpty()) {
            synchronized (this) {
                if (!dataLoaded || cachedMajorsByCollege.isEmpty()) {
                    webScraperService.scrapeMajors();
                    dataLoaded = true;
                }
            }
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> me(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7);
        UserResponseDTO user = authService.getCurrentUser(jwt);
        return ResponseEntity.ok(user);
    }
}
