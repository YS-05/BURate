package com.coursegrade.CourseGraderBackend.dto;

import com.coursegrade.CourseGraderBackend.model.VoteType;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDTO {
    private Long id;
    private Long courseId;
    private Integer usefulnessRating;
    private Integer difficultyRating;
    private Integer workloadRating;
    private Integer interestRating;
    private Double overallRating;
    private Integer teacherRating;
    private String teacherName;
    private String reviewText;
    private String semester;
    private Integer hoursPerWeek;
    private String assignmentTypes; // "Essays, Problem Sets, Group Project"
    private Boolean attendanceRequired;
    private LocalDateTime createdAt;
    private boolean isOwner;
    private Integer upvoteCount;
    private Integer downvoteCount;
    private VoteType userVote;
}
