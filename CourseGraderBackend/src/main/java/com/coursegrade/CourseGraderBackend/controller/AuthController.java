package com.coursegrade.CourseGraderBackend.controller;

import com.coursegrade.CourseGraderBackend.dto.*;
import com.coursegrade.CourseGraderBackend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody @Valid RegisterLoginUserDTO request) {
        authService.register(request.getEmail(), request.getPassword());
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
    public ResponseEntity<Map<String, Object>> login(@RequestBody @Valid RegisterLoginUserDTO request) {
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

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> me(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7);
        UserResponseDTO user = authService.getCurrentUser(jwt);
        return ResponseEntity.ok(user);
    }
}
