package com.coursegrade.CourseGraderBackend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordDTO {
    @NotBlank(message = "Old password cannot be blank")
    private String currentPassword;
    @NotBlank(message = "New password cannot be blank")
    @Size(min = 8, message = "Password must be 8 characters long")
    private String newPassword;
    @NotBlank(message = "Confirm new password cannot be blank")
    @Size(min = 8, message = "Password must be 8 characters long")
    private String confirmNewPassword;

}
