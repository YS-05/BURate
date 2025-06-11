package com.coursegrade.CourseGraderBackend.service;

import com.coursegrade.CourseGraderBackend.dto.RegisterLoginUserDTO;
import com.coursegrade.CourseGraderBackend.dto.VerifyUserDTO;
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

import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository verificationTokenRepository;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public void register(RegisterLoginUserDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.STUDENT);

        userRepository.save(user);

        String verificationCode = generateVerificationCode();
        verificationTokenRepository.save(new VerificationToken(verificationCode, user));
        userRepository.save(user);
        emailService.sendVerificationEmail(user, verificationCode);
    }

    @Transactional
    public void verify(VerifyUserDTO request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email not found"));
        if (user.isEnabled()) throw new RuntimeException("User is already verified");
        VerificationToken verificationCode = verificationTokenRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Verification token not found"));
        if (!verificationCode.isExpired()) {
            throw new RuntimeException("Verification token is expired");
        }
        if (!verificationCode.getToken().equals(request.getVerificationCode())) {
            throw new RuntimeException("Verification code is incorrect");
        }
        user.setEnabled(true);
        userRepository.save(user);
        verificationTokenRepository.delete(verificationCode);
    }

    @Transactional
    public String login(RegisterLoginUserDTO request) { // TODO: Implement more features here and checks
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        User user = (User) authentication.getPrincipal();
        if (!user.isEnabled()) {
            throw new RuntimeException("Please verify your email before logging in");
        }
        return jwtService.generateToken(user);
    }

    public String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // generates 6-digit code
        return String.valueOf(code);
    }

}
