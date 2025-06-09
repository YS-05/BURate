package com.coursegrade.CourseGraderBackend.repository;

import com.coursegrade.CourseGraderBackend.model.Course;
import com.coursegrade.CourseGraderBackend.model.HubRequirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByCourseCodeAndDepartmentAndCollege(String courseCode, String department, String college);
    List<Course> findByDepartment(String department);
    List<Course> findByCollege(String college);
    List<Course> findByHubRequirementsIn(Set<HubRequirement> requirements);
    Optional<Course> findByBaseUrl(String baseUrl);
}
