package com.coursegrade.CourseGraderBackend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewDTO {
    @NotNull
    @Min(1) @Max(5)
    private Integer usefulnessRating;
    @NotNull
    @Min(1) @Max(5)
    private Integer difficultyRating;
    @NotNull
    @Min(1) @Max(5)
    private Integer workloadRating;
    @NotNull
    @Min(1) @Max(5)
    private Integer interestRating;
    @NotNull
    @Min(1) @Max(5)
    private Integer teacherRating;
    @Size(max = 100)
    private String teacherName;
    @Size(max = 2000)
    private String reviewText;
    @Size(max = 100)
    private String semester;
    @Min(0) @Max(40)
    private Integer hoursPerWeek;
    @Size(max = 100)
    private String assignmentTypes; // "Essays, Problem Sets, Group Project"
    private Boolean attendanceRequired;
}