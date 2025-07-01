package com.coursegrade.CourseGraderBackend.dto;

import com.coursegrade.CourseGraderBackend.model.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseDTO {
    private String id;
    private String title;
    private String college;
    private String department;
    private String courseCode;
    private String description;
    private Boolean noPreReqs;
    private Integer numReviews;
    private Double averageOverallRating;
    private Double averageUsefulnessRating;
    private Double averageDifficultyRating;
    private Double averageWorkloadRating;
    private Double averageInterestRating;
    private Double averageTeacherRating;
    private List<Review> courseReviews;
}
