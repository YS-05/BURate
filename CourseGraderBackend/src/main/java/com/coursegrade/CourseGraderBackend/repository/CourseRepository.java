package com.coursegrade.CourseGraderBackend.repository;

import com.coursegrade.CourseGraderBackend.model.Course;
import com.coursegrade.CourseGraderBackend.model.HubRequirement;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository {
    Optional<Course> findByCourseCodeAndDepartmentAndCollege(String courseCode, String department, String college);
    List<Course> findByDepartment(String department);
    List<Course> findByCollege(String college);
    List<Course> findByDepartmentAndCollege(String department, String college);

    List<Course> findByHubRequirementsContaining(HubRequirement hubRequirement);


}
