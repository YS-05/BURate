package com.coursegrade.CourseGraderBackend.service;

import com.coursegrade.CourseGraderBackend.model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService { // TODO: Better description for verification email + Add welcome message later

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Value("${app.base-url:http://localhost:3000}")
    private String baseUrl; // TODO: Update with confirmation page link

    public void sendVerificationEmail(User user, String verificationCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(from);
            helper.setTo(user.getEmail());
            helper.setSubject("Verify Your CourseGrader Account");

            String content = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>" +
                    "<h2 style='color: #CC0000;'>Welcome to CourseGrader!</h2>" +
                    "<p>Thank you for registering. Please use the verification code below to complete your registration:</p>" +
                    "<div style='background-color: #f5f5f5; padding: 15px; font-size: 24px; text-align: center; letter-spacing: 5px; font-weight: bold; margin: 20px 0;'>" +
                    verificationCode +
                    "</div>" +
                    "<p>This code will expire in 24 hours.</p>" +
                    "<p>If you didn't create an account, please ignore this email.</p>" +
                    "<hr style='border: none; border-top: 1px solid #eee; margin: 30px 0;'>" +
                    "<p style='color: #999; font-size: 12px; text-align: center;'>CourseGrader - Your BU Course Review Platform</p>" +
                    "</div>";

            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    public void resendVerificationEmail(User user, String token) {
        String verificationUrl = baseUrl + "/verify-email?token=" + token;

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(from);
            helper.setTo(user.getEmail());
            helper.setSubject("Resend: Verify Your CourseGrader Account");

            String content = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>" +
                    "<h2 style='color: #CC0000;'>Verification Email Resent</h2>" +
                    "<p>Hi there,</p>" +
                    "<p>You requested a new verification email. Please click the button below to verify your BU email address:</p>" +
                    "<div style='text-align: center; margin: 30px 0;'>" +
                    "<a href='" + verificationUrl + "' style='background-color: #CC0000; color: white; padding: 12px 30px; " +
                    "text-decoration: none; border-radius: 5px; display: inline-block; font-weight: bold;'>Verify Email Address</a>" +
                    "</div>" +
                    "<p>Or copy and paste this link into your browser:</p>" +
                    "<p style='color: #666; word-break: break-all; background-color: #f5f5f5; padding: 10px;'>" + verificationUrl + "</p>" +
                    "<p style='color: #999;'>This new link will expire in 24 hours.</p>" +
                    "<p style='color: #999;'><strong>Note:</strong> Your previous verification link has been invalidated.</p>" +
                    "<hr style='border: none; border-top: 1px solid #eee; margin: 30px 0;'>" +
                    "<p style='color: #999; font-size: 12px; text-align: center;'>CourseGrader - Your BU Course Review Platform</p>" +
                    "</div>";

            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to resend verification email", e);
        }
    }

    public void sendPasswordResetEmail(User user, String resetCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(from);
            helper.setTo(user.getEmail());
            helper.setSubject("Reset Your CourseGrader Password");

            String content = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>" +
                    "<h2 style='color: #CC0000;'>Password Reset Request</h2>" +
                    "<p>We received a request to reset your password. Please use the code below:</p>" +
                    "<div style='background-color: #f5f5f5; padding: 15px; font-size: 24px; text-align: center; letter-spacing: 5px; font-weight: bold; margin: 20px 0;'>" +
                    resetCode +
                    "</div>" +
                    "<p>This code will expire in 1 hour for security reasons.</p>" +
                    "<p>If you didn't request this, please ignore this email and your password will remain unchanged.</p>" +
                    "<hr style='border: none; border-top: 1px solid #eee; margin: 30px 0;'>" +
                    "<p style='color: #999; font-size: 12px; text-align: center;'>CourseGrader - Your BU Course Review Platform</p>" +
                    "</div>";

            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }


}
