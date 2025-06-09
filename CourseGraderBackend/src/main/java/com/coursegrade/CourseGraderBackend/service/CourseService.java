package com.coursegrade.CourseGraderBackend.service;

import com.coursegrade.CourseGraderBackend.model.Course;
import com.coursegrade.CourseGraderBackend.model.HubRequirement;
import com.coursegrade.CourseGraderBackend.model.Review;
import com.coursegrade.CourseGraderBackend.model.User;
import com.coursegrade.CourseGraderBackend.repository.CourseRepository;
import jakarta.transaction.Transactional;
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

    @Transactional
    public Course createCourse(String title, String college, String department, String courseCode, String baseUrl) {
        Optional<Course> existingCourse = courseRepository.findByCourseCodeAndDepartmentAndCollege(courseCode, department, college);
        if (existingCourse.isPresent()) {
            System.out.println("Course already exists");
            return existingCourse.get();
        }
        Course course = new Course();
        course.setTitle(title);
        course.setCollege(college);
        course.setDepartment(department);
        course.setCourseCode(courseCode);
        course.setBaseUrl(baseUrl);
        course.setHubRequirements(new HashSet<>());

        course.setTotalReviews(0);
        course.setAverageOverallRating(0.0);
        course.setAverageUsefulnessRating(0.0);
        course.setAverageDifficultyRating(0.0);
        course.setAverageWorkloadRating(0.0);
        course.setAverageInterestRating(0.0);
        course.setAverageTeacherRating(0.0);
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
        return ((6.0 - course.getAverageDifficultyRating()) + // Lower is better inverted
                (6.0 - course.getAverageWorkloadRating()) + // Lower is better inverted
                course.getAverageInterestRating() +
                course.getAverageUsefulnessRating() +
                course.getAverageTeacherRating()) / 5.0;
    }

    public Course saveScrapedCourse(String college, String department, String courseNum, String title, String baseUrl) {
        return createCourse(title, college, department, courseNum, baseUrl);
    }

    public Course updateCourseWithHubReqs(String baseUrl, List<String> hubNames) {
        Optional<Course> courseOptional = courseRepository.findByBaseUrl(baseUrl);
        if (courseOptional.isEmpty()) {
            System.out.println("No course found with URL: " + baseUrl);
            return null;
        }
        Course course = courseOptional.get();
        Set<HubRequirement> hubRequirements = convertHubNamesToEnum(hubNames);
        course.setHubRequirements(hubRequirements);
        return courseRepository.save(course);
    }

    public Set<HubRequirement> convertHubNamesToEnum(List<String> hubNames) {
        Set<HubRequirement> hubRequirements = new HashSet<>();
        for (String hub : hubNames) {
            for (HubRequirement hubReq : HubRequirement.values()) {
                if (hubReq.getName().equalsIgnoreCase(hub)) {
                    hubRequirements.add(hubReq);
                    break;
                }
            }
        }
        return hubRequirements;
    }

    @Transactional
    public void updateCourseRatings(Review savedReview, Boolean add) {
        Course course = savedReview.getCourse();
        int numReviews = course.getTotalReviews();
        double usefulRating = course.getAverageUsefulnessRating();
        double interestRating = course.getAverageInterestRating();
        double workloadRating = course.getAverageWorkloadRating();
        double difficultyRating = course.getAverageDifficultyRating();
        double teacherRating = course.getAverageTeacherRating();
        if (add) {
            usefulRating = (numReviews * usefulRating) + savedReview.getUsefulnessRating();
            interestRating = (numReviews * interestRating) + savedReview.getInterestRating();
            workloadRating = (numReviews * workloadRating) + savedReview.getWorkloadRating();
            difficultyRating = (numReviews * difficultyRating) + savedReview.getDifficultyRating();
            teacherRating = (numReviews * teacherRating) + savedReview.getTeacherRating();
            numReviews++;
        }
        else {
            usefulRating = (numReviews * usefulRating) - savedReview.getUsefulnessRating();
            interestRating = (numReviews * interestRating) - savedReview.getInterestRating();
            workloadRating = (numReviews * workloadRating) - savedReview.getWorkloadRating();
            difficultyRating = (numReviews * difficultyRating) - savedReview.getDifficultyRating();
            teacherRating = (numReviews * teacherRating) - savedReview.getTeacherRating();
            numReviews--;
        }
        course.setTotalReviews(numReviews);
        course.setAverageUsefulnessRating(usefulRating/numReviews);
        course.setAverageInterestRating(interestRating/numReviews);
        course.setAverageTeacherRating(teacherRating/numReviews);
        course.setAverageDifficultyRating(difficultyRating/numReviews);
        course.setAverageWorkloadRating(workloadRating/numReviews);

        Double overallRating = calculateOverallRating(course);
        course.setAverageOverallRating(overallRating);
    }
}
