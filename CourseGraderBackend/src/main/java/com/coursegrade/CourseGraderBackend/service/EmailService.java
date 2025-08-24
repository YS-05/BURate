package com.coursegrade.CourseGraderBackend.service;

import com.coursegrade.CourseGraderBackend.dto.ContactUsDTO;
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
                    "<p>This code will expire in 15 minutes.</p>" +
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

    public void resendVerificationEmail(User user, String verificationCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(from);
            helper.setTo(user.getEmail());
            helper.setSubject("Resend: Verify Your CourseGrader Account");

            String content = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>" +
                    "<h2 style='color: #CC0000;'>Verification Code Resent</h2>" +
                    "<p>Hi there,</p>" +
                    "<p>You requested a new verification code. Please use the code below to complete your registration:</p>" +
                    "<div style='background-color: #f5f5f5; padding: 15px; font-size: 24px; text-align: center; letter-spacing: 5px; font-weight: bold; margin: 20px 0;'>" +
                    verificationCode +
                    "</div>" +
                    "<p>This code will expire in 15 minutes.</p>" +
                    "<p style='color: #999;'><strong>Note:</strong> Your previous verification code has been invalidated.</p>" +
                    "<p>If you didn't request this, please ignore this email.</p>" +
                    "<hr style='border: none; border-top: 1px solid #eee; margin: 30px 0;'>" +
                    "<p style='color: #999; font-size: 12px; text-align: center;'>CourseGrader - Your BU Course Review Platform</p>" +
                    "</div>";

            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to resend verification email", e);
        }
    }

    public void sendPasswordResetEmail(String email, String resetCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(from);
            helper.setTo(email);
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

    public void sendContactUsEmail(ContactUsDTO contactDTO) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(from);
            helper.setSubject("Contact Us Request: " + contactDTO.getSubject());
            String content = buildContactUsEmailContent(contactDTO);
            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send contact us email", e);
        }
    }

    private String buildContactUsEmailContent(ContactUsDTO contactDTO) {
        return "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>" +
                "<h2 style='color: #CC0000;'>New Contact Us Message</h2>" +
                "<div style='background-color: #f8f9fa; padding: 20px; border-left: 4px solid #CC0000; margin: 20px 0;'>" +
                "<p><strong>From:</strong> " + contactDTO.getEmail() + "</p>" +
                "<p><strong>Subject:</strong> " + contactDTO.getSubject() + "</p>" +
                "</div>" +
                "<div style='background-color: #fff; padding: 20px; border: 1px solid #eee; margin: 20px 0;'>" +
                "<h3 style='color: #333; margin-top: 0;'>Message:</h3>" +
                "<p style='line-height: 1.6; color: #555;'>" +
                contactDTO.getMessage().replace("\n", "<br>") +
                "</p>" +
                "</div>" +
                "<div style='background-color: #f5f5f5; padding: 15px; margin: 20px 0; text-align: center;'>" +
                "<p style='margin: 0; color: #666;'><strong>Reply directly to this email to respond to the user</strong></p>" +
                "</div>" +
                "<hr style='border: none; border-top: 1px solid #eee; margin: 30px 0;'>" +
                "<p style='color: #999; font-size: 12px; text-align: center;'>CourseGrader - Your BU Course Review Platform</p>" +
                "</div>";
    }
}
