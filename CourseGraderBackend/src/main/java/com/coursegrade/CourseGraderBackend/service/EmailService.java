package com.coursegrade.CourseGraderBackend.service;

import com.coursegrade.CourseGraderBackend.dto.ContactUsDTO;
import com.coursegrade.CourseGraderBackend.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${resend.api.key}")
    private String apiKey;

    @Value("${resend.from}")
    private String from;

    @Value("${app.base-url:http://localhost:3000}")
    private String baseUrl;

    private static final String RESEND_URL = "https://api.resend.com/emails";

    private void sendEmail(String to, String subject, String htmlContent) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("from", from);
            payload.put("to", new String[]{to});
            payload.put("subject", subject);
            payload.put("html", htmlContent);

            String json = objectMapper.writeValueAsString(payload);

            RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
            Request request = new Request.Builder()
                    .url(RESEND_URL)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new RuntimeException("Failed to send email: " + response.body().string());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error sending email", e);
        }
    }

    public void sendVerificationEmail(User user, String verificationCode) {
        String content = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>" +
                "<h2 style='color: #4f46e5;'>Welcome to BU Rate! 🎓</h2>" +
                "<p>Thank you for registering. Please use the verification code below to complete your registration:</p>" +
                "<div style='background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 20px; font-size: 32px; text-align: center; letter-spacing: 8px; font-weight: bold; margin: 20px 0; border-radius: 10px;'>" +
                verificationCode +
                "</div>" +
                "<p style='color: #dc2626; font-weight: bold;'>⏰ This code will expire in 15 minutes.</p>" +
                "<p>If you didn't create an account, please ignore this email.</p>" +
                "<hr style='border: none; border-top: 1px solid #e5e7eb; margin: 30px 0;'>" +
                "<p style='color: #6b7280; font-size: 12px; text-align: center;'>© 2025 BU Rate - Your BU Course Review Platform</p>" +
                "</div>";

        sendEmail(user.getEmail(), "🎓 Verify Your BU Rate Account", content);
    }

    public void resendVerificationEmail(User user, String verificationCode) {
        String content = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>" +
                "<h2 style='color: #ec4899;'>Verification Code Resent 📧</h2>" +
                "<p>Hi there,</p>" +
                "<p>You requested a new verification code. Please use the code below to complete your registration:</p>" +
                "<div style='background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); color: white; padding: 20px; font-size: 32px; text-align: center; letter-spacing: 8px; font-weight: bold; margin: 20px 0; border-radius: 10px;'>" +
                verificationCode +
                "</div>" +
                "<p style='color: #dc2626; font-weight: bold;'>⏰ This code will expire in 15 minutes.</p>" +
                "<p style='color: #6b7280;'><strong>Note:</strong> Your previous verification code has been invalidated.</p>" +
                "<p>If you didn't request this, please ignore this email.</p>" +
                "<hr style='border: none; border-top: 1px solid #e5e7eb; margin: 30px 0;'>" +
                "<p style='color: #6b7280; font-size: 12px; text-align: center;'>© 2025 BU Rate - Your BU Course Review Platform</p>" +
                "</div>";

        sendEmail(user.getEmail(), "🔄 New BU Rate Verification Code", content);
    }

    public void sendPasswordResetEmail(String email, String resetCode) {
        String content = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>" +
                "<h2 style='color: #f59e0b;'>Password Reset Request 🔐</h2>" +
                "<p>We received a request to reset your password. Please use the code below:</p>" +
                "<div style='background: linear-gradient(135deg, #fa709a 0%, #fee140 100%); color: white; padding: 20px; font-size: 32px; text-align: center; letter-spacing: 8px; font-weight: bold; margin: 20px 0; border-radius: 10px;'>" +
                resetCode +
                "</div>" +
                "<p style='color: #dc2626; font-weight: bold;'>⏰ This code will expire in 1 hour for security reasons.</p>" +
                "<p>If you didn't request this, please ignore this email and your password will remain unchanged.</p>" +
                "<hr style='border: none; border-top: 1px solid #e5e7eb; margin: 30px 0;'>" +
                "<p style='color: #6b7280; font-size: 12px; text-align: center;'>© 2025 BU Rate - Your BU Course Review Platform</p>" +
                "</div>";

        sendEmail(email, "🔐 Reset Your BU Rate Password", content);
    }

    public void sendContactUsEmail(ContactUsDTO contactDTO) {
        String content = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>" +
                "<h2 style='color: #0891b2;'>New Contact Us Message 📬</h2>" +
                "<div style='background-color: #f0f9ff; padding: 20px; border-left: 4px solid #0891b2; margin: 20px 0; border-radius: 5px;'>" +
                "<p><strong>From:</strong> " + contactDTO.getEmail() + "</p>" +
                "<p><strong>Subject:</strong> " + contactDTO.getSubject() + "</p>" +
                "</div>" +
                "<div style='background-color: #fff; padding: 20px; border: 1px solid #e5e7eb; margin: 20px 0; border-radius: 10px;'>" +
                "<h3 style='color: #374151; margin-top: 0;'>Message:</h3>" +
                "<p style='line-height: 1.6; color: #6b7280;'>" +
                contactDTO.getMessage().replace("\n", "<br>") +
                "</p>" +
                "</div>" +
                "<div style='background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); color: white; padding: 15px; margin: 20px 0; text-align: center; border-radius: 10px;'>" +
                "<p style='margin: 0; font-weight: bold;'>💬 Reply directly to this email to respond to the user</p>" +
                "</div>" +
                "<hr style='border: none; border-top: 1px solid #e5e7eb; margin: 30px 0;'>" +
                "<p style='color: #6b7280; font-size: 12px; text-align: center;'>© 2025 BU Rate - Your BU Course Review Platform</p>" +
                "</div>";

        sendEmail("bucourserate@gmail.com", "📬 Contact Us Request: " + contactDTO.getSubject(), content);
    }
}