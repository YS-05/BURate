package com.coursegrade.CourseGraderBackend.controller;

import com.coursegrade.CourseGraderBackend.dto.RegisterLoginUserDTO;
import com.coursegrade.CourseGraderBackend.dto.VerifyUserDTO;
import com.coursegrade.CourseGraderBackend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService; //TODO: Implement this

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody @Valid RegisterLoginUserDTO request) {
        authService.register(request);
        return ResponseEntity.ok("User registered successfully. Please check your email for verification.");
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyUser(@RequestBody @Valid VerifyUserDTO request) {
        authService.verify(request);
        return ResponseEntity.ok("Email verified successfully.");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody @Valid RegisterLoginUserDTO request) {
        String token = authService.login(request);
        return ResponseEntity.ok(Map.of("token", token));
    }

}
