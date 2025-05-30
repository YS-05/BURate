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
    private Long id;
    private String title;
    private String college;
    private String department;
    private String courseNum;
    private String preReqs;
    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = HubRequirement.class)
    @CollectionTable(name = "course_hub_requirements")
    private Set<HubRequirement> hubRequirements = new HashSet<>();

    private Integer totalReviews;
    private Double avgOverallRating;
    private Double avgUsefulnessRating;
    private Double avgDifficultyRating;
    private Double avgInterestRating;
    private Double avgTeacherRating;

    public String courseDisplay() {
        return college + " " + department + courseNum; // Ex: CAS CS111
    }
}
