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
    void save_ShouldPersistCourse() {
        // Given
        Course course = createTestCourse("Introduction to Computer Science", "CAS", "CS", "111");

        // When
        Course saved = courseRepository.save(course);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("Introduction to Computer Science");
        assertThat(saved.getCollege()).isEqualTo("CAS");
        assertThat(saved.getDepartment()).isEqualTo("CS");
        assertThat(saved.getCourseCode()).isEqualTo("111");
    }

    @Test
    void findByCourseCodeAndDepartmentAndCollege_WhenCourseExists_ShouldReturnCourse() {
        // Given
        Course course = createTestCourse("Data Structures", "CAS", "CS", "210");
        entityManager.persistAndFlush(course);

        // When
        Optional<Course> found = courseRepository.findByCourseCodeAndDepartmentAndCollege("210", "CS", "CAS");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Data Structures");
        assertThat(found.get().getCourseCode()).isEqualTo("210");
        assertThat(found.get().getDepartment()).isEqualTo("CS");
        assertThat(found.get().getCollege()).isEqualTo("CAS");
    }

    @Test
    void findByCourseCodeAndDepartmentAndCollege_WhenCourseDoesNotExist_ShouldReturnEmpty() {
        // When
        Optional<Course> found = courseRepository.findByCourseCodeAndDepartmentAndCollege("999", "CS", "CAS");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void findByDepartment_ShouldReturnCoursesInDepartment() {
        // Given
        Course cs111 = createTestCourse("Intro to CS", "CAS", "CS", "111");
        Course cs210 = createTestCourse("Data Structures", "CAS", "CS", "210");
        Course math101 = createTestCourse("Calculus I", "CAS", "MATH", "101");

        entityManager.persistAndFlush(cs111);
        entityManager.persistAndFlush(cs210);
        entityManager.persistAndFlush(math101);

        // When
        List<Course> csCourses = courseRepository.findByDepartment("CS");

        // Then
        assertThat(csCourses).hasSize(2);
        assertThat(csCourses).extracting(Course::getTitle)
                .containsExactlyInAnyOrder("Intro to CS", "Data Structures");
        assertThat(csCourses).allMatch(course -> course.getDepartment().equals("CS"));
    }

    @Test
    void findByDepartment_WhenNoDepartmentExists_ShouldReturnEmptyList() {
        // When
        List<Course> courses = courseRepository.findByDepartment("NONEXISTENT");

        // Then
        assertThat(courses).isEmpty();
    }

    @Test
    void findByCollege_ShouldReturnCoursesInCollege() {
        // Given
        Course casCourse = createTestCourse("CS Course", "CAS", "CS", "111");
        Course engCourse = createTestCourse("Engineering Course", "ENG", "ME", "101");
        Course comCourse = createTestCourse("Business Course", "COM", "BUS", "201");

        entityManager.persistAndFlush(casCourse);
        entityManager.persistAndFlush(engCourse);
        entityManager.persistAndFlush(comCourse);

        // When
        List<Course> casCourses = courseRepository.findByCollege("CAS");

        // Then
        assertThat(casCourses).hasSize(1);
        assertThat(casCourses.get(0).getTitle()).isEqualTo("CS Course");
        assertThat(casCourses.get(0).getCollege()).isEqualTo("CAS");
    }

    @Test
    void findByCollege_WhenNoCollegeExists_ShouldReturnEmptyList() {
        // When
        List<Course> courses = courseRepository.findByCollege("NONEXISTENT");

        // Then
        assertThat(courses).isEmpty();
    }

    @Test
    void findByHubRequirementsIn_ShouldReturnCoursesWithMatchingHubRequirements() {
        // Given
        Course mathCourse = createTestCourse("Calculus", "CAS", "MATH", "101");
        mathCourse.setHubRequirements(Set.of(HubRequirement.QR1, HubRequirement.QR2));

        Course scienceCourse = createTestCourse("Physics", "CAS", "PHYS", "101");
        scienceCourse.setHubRequirements(Set.of(HubRequirement.SI1, HubRequirement.SI2));

        Course writingCourse = createTestCourse("Writing", "CAS", "WR", "101");
        writingCourse.setHubRequirements(Set.of(HubRequirement.FYW, HubRequirement.WRI));

        entityManager.persistAndFlush(mathCourse);
        entityManager.persistAndFlush(scienceCourse);
        entityManager.persistAndFlush(writingCourse);

        // When
        List<Course> mathAndScienceCourses = courseRepository.findByHubRequirementsIn(Set.of(HubRequirement.QR1, HubRequirement.SI1));

        // Then
        assertThat(mathAndScienceCourses).hasSize(2);
        assertThat(mathAndScienceCourses).extracting(Course::getTitle)
                .containsExactlyInAnyOrder("Calculus", "Physics");
    }

    @Test
    void findByHubRequirementsIn_WhenNoMatchingRequirements_ShouldReturnEmptyList() {
        // Given
        Course course = createTestCourse("Test Course", "CAS", "CS", "111");
        course.setHubRequirements(Set.of(HubRequirement.QR1));
        entityManager.persistAndFlush(course);

        // When
        List<Course> courses = courseRepository.findByHubRequirementsIn(Set.of(HubRequirement.SI1));

        // Then
        assertThat(courses).isEmpty();
    }

    @Test
    void findByBaseUrl_WhenUrlExists_ShouldReturnCourse() {
        // Given
        Course course = createTestCourse("Test Course", "CAS", "CS", "111");
        course.setBaseUrl("https://example.com/cs111");
        entityManager.persistAndFlush(course);

        // When
        Optional<Course> found = courseRepository.findByBaseUrl("https://example.com/cs111");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Test Course");
        assertThat(found.get().getBaseUrl()).isEqualTo("https://example.com/cs111");
    }

    @Test
    void findByBaseUrl_WhenUrlDoesNotExist_ShouldReturnEmpty() {
        // When
        Optional<Course> found = courseRepository.findByBaseUrl("https://nonexistent.com");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void findById_WhenCourseExists_ShouldReturnCourse() {
        // Given
        Course course = createTestCourse("Test Course", "CAS", "CS", "111");
        Course saved = entityManager.persistAndFlush(course);

        // When
        Optional<Course> found = courseRepository.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(saved.getId());
        assertThat(found.get().getTitle()).isEqualTo("Test Course");
    }

    @Test
    void deleteById_ShouldRemoveCourse() {
        // Given
        Course course = createTestCourse("Test Course", "CAS", "CS", "111");
        Course saved = entityManager.persistAndFlush(course);

        // When
        courseRepository.deleteById(saved.getId());
        entityManager.flush();

        // Then
        Optional<Course> found = courseRepository.findById(saved.getId());
        assertThat(found).isEmpty();
    }

    @Test
    void save_CourseWithAllFields_ShouldPersistAllData() {
        // Given
        Course course = createTestCourse("Advanced Course", "CAS", "CS", "350");
        course.setCourseDesc("A comprehensive course description");
        course.setNoPreReqs(false);
        course.setHubRequirements(Set.of(HubRequirement.QR1, HubRequirement.WRI));
        course.setBaseUrl("https://example.com/cs350");
        course.setTotalReviews(25);
        course.setAverageOverallRating(4.5);
        course.setAverageUsefulnessRating(4.2);
        course.setAverageDifficultyRating(3.8);
        course.setAverageWorkloadRating(3.5);
        course.setAverageInterestRating(4.1);
        course.setAverageTeacherRating(4.3);

        // When
        Course saved = courseRepository.save(course);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("Advanced Course");
        assertThat(saved.getCourseDesc()).isEqualTo("A comprehensive course description");
        assertThat(saved.getNoPreReqs()).isFalse();
        assertThat(saved.getHubRequirements()).containsExactlyInAnyOrder(HubRequirement.QR1, HubRequirement.WRI);
        assertThat(saved.getBaseUrl()).isEqualTo("https://example.com/cs350");
        assertThat(saved.getTotalReviews()).isEqualTo(25);
        assertThat(saved.getAverageOverallRating()).isEqualTo(4.5);
        assertThat(saved.getAverageUsefulnessRating()).isEqualTo(4.2);
        assertThat(saved.getAverageDifficultyRating()).isEqualTo(3.8);
        assertThat(saved.getAverageWorkloadRating()).isEqualTo(3.5);
        assertThat(saved.getAverageInterestRating()).isEqualTo(4.1);
        assertThat(saved.getAverageTeacherRating()).isEqualTo(4.3);
    }

    @Test
    void save_CourseWithMinimalData_ShouldUseDefaults() {
        // Given
        Course course = new Course();
        course.setTitle("Minimal Course");
        course.setCollege("CAS");
        course.setDepartment("CS");
        course.setCourseCode("101");

        // When
        Course saved = courseRepository.save(course);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("Minimal Course");
        assertThat(saved.getHubRequirements()).isEmpty(); // Default empty set
        assertThat(saved.getTotalReviews()).isNull(); // Default null for Integer
        assertThat(saved.getAverageOverallRating()).isNull(); // Default null for Double
    }

    @Test
    void courseDisplay_ShouldReturnFormattedString() {
        // Given
        Course course = createTestCourse("Test Course", "CAS", "CS", "111");
        Course saved = courseRepository.save(course);

        // When
        String display = saved.courseDisplay();

        // Then
        assertThat(display).isEqualTo("CAS CS 111");
    }

    @Test
    void findAll_ShouldReturnAllCourses() {
        // Given
        Course course1 = createTestCourse("Course 1", "CAS", "CS", "111");
        Course course2 = createTestCourse("Course 2", "ENG", "ME", "101");

        entityManager.persistAndFlush(course1);
        entityManager.persistAndFlush(course2);

        // When
        List<Course> courses = courseRepository.findAll();

        // Then
        assertThat(courses).hasSize(2);
        assertThat(courses).extracting(Course::getTitle)
                .containsExactlyInAnyOrder("Course 1", "Course 2");
    }

    // Helper method to create test courses
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