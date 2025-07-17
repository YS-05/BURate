package com.coursegrade.CourseGraderBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDashboardDTO {
    private String email;
    private Integer expectedGrad;
    private String college;
    private String major;
    private Integer coursesCompleted;
    private Integer coursesInProgress;
    private Integer coursesSaved;
    private Integer coursesReviewed;
    private Integer totalUpvotes; // Your reviews have helped x students
    private Double averageReviewScore; // (Upvotes - Downvotes) / num reviews
    private Set<String> coursesToReview;
}
