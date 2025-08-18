package com.coursegrade.CourseGraderBackend.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactUsDTO {
    private String email;
    @Size(max = 200, message = "Subject must be less than 200 characters")
    private String subject;
    @Size(max = 3000, message = "Message must be less than 3000 characters") // Updated from 1000 to 3000
    private String message;
}
