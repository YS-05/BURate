package com.coursegrade.CourseGraderBackend.controller;

import com.coursegrade.CourseGraderBackend.dto.CourseDTO;
import com.coursegrade.CourseGraderBackend.dto.CourseDisplayDTO;
import com.coursegrade.CourseGraderBackend.model.HubRequirement;
import com.coursegrade.CourseGraderBackend.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    public ResponseEntity<Page<CourseDisplayDTO>> getAllCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "courseCode") String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<CourseDisplayDTO> courses = courseService.getAllCoursesPaginated(pageable);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable Long id) {
        CourseDTO course = courseService.getCourseDTOById(id);
            return ResponseEntity.ok(course);
    }

    @GetMapping("/search/{college}")
    public ResponseEntity<Page<CourseDisplayDTO>> searchCourses(
            @PathVariable String college,
            @RequestParam(required = false, defaultValue = "0") Integer minCourseCode,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) Set<HubRequirement> hubRequirements,
            @RequestParam(required = false) Boolean noPreReqs,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Double maxDifficulty,
            @RequestParam(required = false) Double maxWorkLoad,
            @RequestParam(required = false) Double minUseful,
            @RequestParam(required = false) Double minInterest,
            @RequestParam(required = false) Double minTeacher,
            @RequestParam(required = false) Integer reviewCount,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CourseDisplayDTO> courses = courseService.searchCoursesWithCollege(
                minCourseCode, college, hubRequirements, department, noPreReqs, minRating, maxDifficulty,
                maxWorkLoad, minUseful, minInterest, minTeacher, reviewCount, pageable
        );
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/colleges")
    public ResponseEntity<List<String>> getAllColleges() {
        List<String> colleges = courseService.getAllColleges();
        return ResponseEntity.ok(colleges);
    }

    @GetMapping("/departments/{college}")
    public ResponseEntity<List<String>> getDepartmentsByCollege(@PathVariable String college) {
        List<String> departments = courseService.getDepartmentsByCollege(college);
        return ResponseEntity.ok(departments);
    }
}
