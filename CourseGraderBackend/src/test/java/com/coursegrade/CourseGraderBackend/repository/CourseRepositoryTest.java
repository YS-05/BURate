package com.coursegrade.CourseGraderBackend.repository;

import com.coursegrade.CourseGraderBackend.model.Course;
import com.coursegrade.CourseGraderBackend.model.HubRequirement;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CourseRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CourseRepository courseRepository;

    @Test
    void findByCourseCodeAndDepartmentAndCollege_ShouldReturnCorrectCourse() {
        // Given
        Course course = createTestCourse("Data Structures", "CAS", "CS", "210");
        entityManager.persistAndFlush(course);

        // When
        Optional<Course> found = courseRepository.findByCourseCodeAndDepartmentAndCollege("210", "CS", "CAS");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Data Structures");
    }

    @Test
    void findByHubRequirementsIn_ShouldReturnCoursesWithMatchingRequirements() {
        // Given
        Course mathCourse = createTestCourse("Calculus", "CAS", "MATH", "101");
        mathCourse.setHubRequirements(Set.of(HubRequirement.QR1, HubRequirement.QR2));

        Course scienceCourse = createTestCourse("Physics", "CAS", "PHYS", "101");
        scienceCourse.setHubRequirements(Set.of(HubRequirement.SI1, HubRequirement.SI2));

        entityManager.persistAndFlush(mathCourse);
        entityManager.persistAndFlush(scienceCourse);

        // When
        List<Course> mathAndScienceCourses = courseRepository.findByHubRequirementsIn(Set.of(HubRequirement.QR1, HubRequirement.SI1));

        // Then
        assertThat(mathAndScienceCourses).hasSize(2);
        assertThat(mathAndScienceCourses).extracting(Course::getTitle)
                .containsExactlyInAnyOrder("Calculus", "Physics");
    }

    @Test
    void courseDisplay_ShouldReturnFormattedString() {
        // Given
        Course course = createTestCourse("Test Course", "CAS", "CS", "111");

        // When
        String display = course.courseDisplay();

        // Then
        assertThat(display).isEqualTo("CAS CS 111");
    }

    private Course createTestCourse(String title, String college, String department, String courseCode) {
        Course course = new Course();
        course.setTitle(title);
        course.setCollege(college);
        course.setDepartment(department);
        course.setCourseCode(courseCode);
        course.setHubRequirements(Set.of()); // Default empty set
        return course;
    }
}