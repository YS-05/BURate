package com.coursegrade.CourseGraderBackend.service;

import com.coursegrade.CourseGraderBackend.model.Course;
import com.coursegrade.CourseGraderBackend.model.HubRequirement;
import com.coursegrade.CourseGraderBackend.model.User;
import com.coursegrade.CourseGraderBackend.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    public Course createCourse(String title, String college, String department, String courseNum, Set<HubRequirement> hubRequirements) {
        Optional<Course> existingCourse = courseRepository.findByCourseCodeAndDepartmentAndCollege(courseNum, department, college);
        if (existingCourse.isPresent()) {
            System.out.println("Course already exists");
            return existingCourse.get();
        }
        Course course = new Course();
        course.setTitle(title);
        course.setCollege(college);
        course.setDepartment(department);
        course.setCourseNum(courseNum);
        course.setHubRequirements(hubRequirements);

        course.setTotalReviews(0);
        course.setAvgDifficultyRating(0.0);
        course.setAvgInterestRating(0.0);
        return courseRepository.save(course);
    }

    public List<Course> findCoursesByHubReqs(Set<HubRequirement> requirements) {
        if (requirements == null || requirements.isEmpty()) {
            return courseRepository.findAll();
        }
        return courseRepository.findByHubRequirementsIn(requirements);
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    public List<Course> getCoursesByCollege(String college) {
        return courseRepository.findByCollege(college);
    }

    public List<Course> getCoursesByDepartment(String department) {
        return courseRepository.findByDepartment(department);
    }

    public List<Course> recommendCoursesForUser(User user) {
        return List.of(); // TODO Later
    }

    public Double calculateOverallRating(Course course) {
        if (course.getTotalReviews() == 0) {
            return 0.0;
        }
        return ((6 - course.getAvgDifficultyRating()) + // Invert difficulty rating, lower is better
                course.getAvgInterestRating() +
                course.getAvgUsefulnessRating() +
                course.getAvgTeacherRating()) / 4.0;
    }

    public Course saveScrapedCourse(String college, String department, String courseNum, String title, List<String> hubNames) {
        Set<HubRequirement> hubRequirements = convertHubNamesToEnum(hubNames);
        return createCourse(title, college, department, courseNum, hubRequirements);
    }

    public Set<HubRequirement> convertHubNamesToEnum(List<String> hubNames) {
        Set<HubRequirement> hubRequirements = new HashSet<>();
        for (String hub : hubNames) {
            for (HubRequirement hubReq : HubRequirement.values()) {
                if (hubReq.name().equalsIgnoreCase(hub)) {
                    hubRequirements.add(hubReq);
                    break;
                }
            }
        }
        return hubRequirements;
    }

}
