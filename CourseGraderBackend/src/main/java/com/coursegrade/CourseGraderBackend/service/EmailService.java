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

    private void sendEmail(String to, String subject, String htmlContent, String textContent) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("from", from);
            payload.put("to", new String[]{to});
            payload.put("subject", subject);
            payload.put("html", htmlContent);
            payload.put("text", textContent);
            payload.put("reply_to", "support@burate.org");

            String json = objectMapper.writeValueAsString(payload);

            RequestBody body = RequestBody.create(
                    json,
                    MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                    .url(RESEND_URL)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new RuntimeException(
                            "Failed to send email: " +
                                    response.code() + " " +
                                    (response.body() != null ? response.body().string() : "")
                    );
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error sending email", e);
        }
    }


    public void sendVerificationEmail(User user, String verificationCode) {
        String html =
                "<p>You requested to create an account on BU Rate.</p>" +
                        "<p>Your verification code:</p>" +
                        "<p><strong>" + verificationCode + "</strong></p>" +
                        "<p>This code expires in 15 minutes.</p>" +
                        "<p>If you did not request this, you can ignore this email.</p>" +
                        "<p>— BU Rate</p>";

        String text =
                "You requested to create an account on BU Rate.\n\n" +
                        "Your verification code:\n" +
                        verificationCode + "\n\n" +
                        "This code expires in 15 minutes.\n\n" +
                        "If you did not request this, you can ignore this email.\n\n" +
                        "— BU Rate";

        sendEmail(
                user.getEmail(),
                "BU Rate – confirm your email",
                html,
                text
        );
    }


    public void resendVerificationEmail(User user, String verificationCode) {
        String html =
                "<p>You requested a new verification code for BU Rate.</p>" +
                        "<p>Your verification code:</p>" +
                        "<p><strong>" + verificationCode + "</strong></p>" +
                        "<p>This code expires in 15 minutes.</p>" +
                        "<p>If you did not request this, you can ignore this email.</p>" +
                        "<p>— BU Rate</p>";

        String text =
                "You requested a new verification code for BU Rate.\n\n" +
                        "Verification code:\n" +
                        verificationCode + "\n\n" +
                        "This code expires in 15 minutes.\n\n" +
                        "— BU Rate";

        sendEmail(
                user.getEmail(),
                "BU Rate – new verification code",
                html,
                text
        );
    }


    public void sendPasswordResetEmail(String email, String resetCode) {
        String html =
                "<p>You requested to reset your BU Rate password.</p>" +
                        "<p>Your reset code:</p>" +
                        "<p><strong>" + resetCode + "</strong></p>" +
                        "<p>This code expires in 15 minutes.</p>" +
                        "<p>If you did not request this, you can ignore this email.</p>" +
                        "<p>— BU Rate</p>";

        String text =
                "You requested to reset your BU Rate password.\n\n" +
                        "Reset code:\n" +
                        resetCode + "\n\n" +
                        "This code expires in 15 minutes.\n\n" +
                        "— BU Rate";

        sendEmail(
                email,
                "BU Rate – reset your password",
                html,
                text
        );
    }


    public void sendContactUsEmail(ContactUsDTO contactDTO) {

        String html =
                "<p>A new Contact Us message was submitted on BU Rate.</p>" +
                        "<p><strong>From:</strong> " + contactDTO.getEmail() + "</p>" +
                        "<p><strong>Subject:</strong> " + contactDTO.getSubject() + "</p>" +
                        "<hr>" +
                        "<p><strong>Message:</strong></p>" +
                        "<p>" + contactDTO.getMessage().replace("\n", "<br>") + "</p>" +
                        "<hr>" +
                        "<p>You can reply directly to this email to respond.</p>" +
                        "<p>— BU Rate</p>";

        String text =
                "New Contact Us message on BU Rate\n\n" +
                        "From: " + contactDTO.getEmail() + "\n" +
                        "Subject: " + contactDTO.getSubject() + "\n\n" +
                        "Message:\n" +
                        contactDTO.getMessage() + "\n\n" +
                        "You can reply directly to this email to respond.\n\n" +
                        "— BU Rate";

        sendEmail(
                "support@burate.org",
                "BU Rate – Contact Us message",
                html,
                text
        );
    }

}