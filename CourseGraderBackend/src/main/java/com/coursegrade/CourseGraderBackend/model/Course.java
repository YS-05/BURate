package com.coursegrade.CourseGraderBackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "title")
    private String title;
    @Column(name = "college")
    private String college;
    @Column(name = "department")
    private String department;
    @Column(name = "course_code")
    private String courseCode;
    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = HubRequirement.class)
    @CollectionTable(name = "course_hub_requirements")
    @Column(name = "hub_requirement")
    private Set<HubRequirement> hubRequirements = new HashSet<>();
    @Column(name = "base_url")
    private String baseUrl;
    @Column(name = "total_reviews")
    private Integer totalReviews;
    @Column(name = "average_overall_rating")
    private Double averageOverallRating;
    @Column(name = "average_usefulness_rating")
    private Double averageUsefulnessRating;
    @Column(name = "average_difficulty_rating")
    private Double averageDifficultyRating;
    @Column(name = "average_workload_rating")
    private Double averageWorkloadRating;
    @Column(name = "average_interest_rating")
    private Double averageInterestRating;
    @Column(name = "average_teacher_rating")
    private Double averageTeacherRating;

    public String courseDisplay() {
        return college + " " + department + " " + courseCode; // Ex: CAS CS 111
    }
}
