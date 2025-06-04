package com.coursegrade.CourseGraderBackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(nullable = false)
    private Integer usefulnessRating;
    @Column(nullable = false)
    private Integer difficultyRating;
    @Column(nullable = false)
    private Integer workloadRating;
    @Column(nullable = false)
    private Integer interestRating;
    @Column(nullable = false)
    private Integer teacherRating;
    @Column(nullable = false)
    private String teacherName;
    private String reviewText;
    private String semester;
    private LocalDateTime createdAt;
    private Integer hoursPerWeek;
    private String assignmentTypes; // "Essays, Problem Sets, Group Project"
    private Boolean attendanceRequired;

    public Double getOverallRating() {
        return (usefulnessRating + difficultyRating + workloadRating
                + interestRating + teacherRating) / 5.0;
    }
}
