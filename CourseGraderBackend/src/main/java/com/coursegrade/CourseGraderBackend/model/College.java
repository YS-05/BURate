package com.coursegrade.CourseGraderBackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "colleges")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class College {
    @Id
    private String fullName;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "college_majors",
            joinColumns = @JoinColumn(name = "college_name"))
    @Column(name = "major_name")
    private Set<String> majors = new HashSet<>();
}
