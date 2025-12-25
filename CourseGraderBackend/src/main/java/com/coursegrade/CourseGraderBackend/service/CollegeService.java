package com.coursegrade.CourseGraderBackend.service;

import com.coursegrade.CourseGraderBackend.model.College;
import com.coursegrade.CourseGraderBackend.repository.CollegeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CollegeService {

    private final CollegeRepository collegeRepository;

    public Set<String> getMajorsByCollege(String college) {
        College col = collegeRepository.findById(college)
                .orElseThrow(() -> new RuntimeException("College not found"));
        return col.getMajors();
    }

    public Set<String> getAllColleges() {
        Set<String> cols = new HashSet<>();
        List<College> colleges = collegeRepository.findAll();
        for (College col : colleges) {
            cols.add(col.getFullName());
        }
        return cols;
    }
}
