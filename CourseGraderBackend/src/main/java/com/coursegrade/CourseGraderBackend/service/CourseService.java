package com.coursegrade.CourseGraderBackend.service;

import com.coursegrade.CourseGraderBackend.dto.CourseDTO;
import com.coursegrade.CourseGraderBackend.dto.CourseDisplayDTO;
import com.coursegrade.CourseGraderBackend.dto.HubRequirementDTO;
import com.coursegrade.CourseGraderBackend.dto.ReviewResponseDTO;
import com.coursegrade.CourseGraderBackend.model.Course;
import com.coursegrade.CourseGraderBackend.model.HubRequirement;
import com.coursegrade.CourseGraderBackend.model.Review;
import com.coursegrade.CourseGraderBackend.model.User;
import com.coursegrade.CourseGraderBackend.repository.CourseRepository;
import com.coursegrade.CourseGraderBackend.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final ReviewRepository reviewRepository;

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

    public Page<CourseDisplayDTO> getAllCoursesPaginated(Pageable pageable) {
        Page<Course> coursePage = courseRepository.findAll(pageable);
        return coursePage.map(course -> this.convertToDisplayDTO(course));
    }

    public CourseDTO getCourseDTOById(Long id, User user) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        return convertToFullDTO(course, user);
    }

    public Page<CourseDisplayDTO> searchCoursesWithCollege(
            Integer minCourseCode, Set<String> colleges, Set<String> hubReqs,
            Set<String> departments, Boolean noPreReqs, Double minRating, Double maxDifficulty, Double maxWorkload,
            Double minUsefulness, Double minInterest, Double minTeacher, Integer reviewCount, String sortBy,
            Pageable pageable
    ) {
        HashSet<HubRequirement> hubRequirements = new HashSet<>();
        if (hubReqs != null && !hubReqs.isEmpty()) {
            for (String req : hubReqs) {
                for (HubRequirement hubRequirement : HubRequirement.values()) {
                    if (req.equals(hubRequirement.name())) {
                        hubRequirements.add(hubRequirement);
                        break;
                    }
                }
            }
        }
        List<Course> courses = new ArrayList<>(getAllCourses());
        if (colleges != null && !colleges.isEmpty()) {
            Iterator<Course> iterator = courses.iterator();
            while (iterator.hasNext()) {
                Course course = iterator.next();
                if (!colleges.contains(course.getCollege())) {
                    iterator.remove();
                }
            }
        }
        if (minCourseCode != null && minCourseCode > 0) {
            Iterator<Course> iterator = courses.iterator();
            while (iterator.hasNext()) {
                Course course = iterator.next();
                try {
                    int courseCode = Integer.parseInt(course.getCourseCode());
                    if (courseCode < minCourseCode) {
                        iterator.remove();
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid course code");
                }
            }
        }
        if (!hubRequirements.isEmpty()) {
            Iterator<Course> iterator = courses.iterator();
            while (iterator.hasNext()) {
                Course course = iterator.next();
                Set<HubRequirement> courseHubReqs = course.getHubRequirements();
                if (courseHubReqs == null || courseHubReqs.isEmpty()) {
                    iterator.remove();
                    continue;
                }
                boolean hasAllRequiredHubs = true;
                for (HubRequirement hub : hubRequirements) {
                    if (!courseHubReqs.contains(hub)) {
                        hasAllRequiredHubs = false;
                        break;
                    }
                }
                if (!hasAllRequiredHubs) {
                    iterator.remove();
                }
            }
        }
        if (departments != null && !departments.isEmpty()) {
            Iterator<Course> iterator = courses.iterator();
            while (iterator.hasNext()) {
                Course course = iterator.next();
                String courseDepart = course.getDepartment();
                if (!departments.contains(courseDepart)) {
                    iterator.remove();
                }
            }
        }
        if (noPreReqs != null && noPreReqs) {
            Iterator<Course> iterator = courses.iterator();
            while (iterator.hasNext()) {
                Course course = iterator.next();
                if (course.getNoPreReqs() == null || !course.getNoPreReqs()) {
                    iterator.remove();
                }
            }
        }
        if (minRating != null && minRating > 0) {
            Iterator<Course> iterator = courses.iterator();
            while (iterator.hasNext()) {
                Course course = iterator.next();
                if (course.getAverageOverallRating() == null || course.getAverageOverallRating() < minRating) {
                    iterator.remove();
                }
            }
        }
        if (maxDifficulty != null && maxDifficulty > 0) {
            Iterator<Course> iterator = courses.iterator();
            while (iterator.hasNext()) {
                Course course = iterator.next();
                if (course.getAverageDifficultyRating() == null || course.getAverageDifficultyRating() > maxDifficulty) {
                    iterator.remove();
                }
            }
        }
        if (maxWorkload != null && maxWorkload > 0) {
            Iterator<Course> iterator = courses.iterator();
            while (iterator.hasNext()) {
                Course course = iterator.next();
                if (course.getAverageWorkloadRating() == null || course.getAverageWorkloadRating() > maxWorkload) {
                    iterator.remove();
                }
            }
        }
        if (minUsefulness != null && minUsefulness > 0) {
            Iterator<Course> iterator = courses.iterator();
            while (iterator.hasNext()) {
                Course course = iterator.next();
                if (course.getAverageUsefulnessRating() == null || course.getAverageUsefulnessRating() < minUsefulness) {
                    iterator.remove();
                }
            }
        }
        if (minInterest != null && minInterest > 0) {
            Iterator<Course> iterator = courses.iterator();
            while (iterator.hasNext()) {
                Course course = iterator.next();
                if (course.getAverageInterestRating() == null || course.getAverageInterestRating() < minInterest) {
                    iterator.remove();
                }
            }
        }
        if (minTeacher != null && minTeacher > 0) {
            Iterator<Course> iterator = courses.iterator();
            while (iterator.hasNext()) {
                Course course = iterator.next();
                if (course.getAverageTeacherRating() == null || course.getAverageTeacherRating() < minTeacher) {
                    iterator.remove();
                }
            }
        }
        if (reviewCount != null && reviewCount > 0) {
            Iterator<Course> iterator = courses.iterator();
            while (iterator.hasNext()) {
                Course course = iterator.next();
                if (course.getTotalReviews() == null || course.getTotalReviews() < reviewCount) {
                    iterator.remove();
                }
            }
        }
        List<CourseDisplayDTO> dtos = new ArrayList<>();
        for (Course course : courses) {
            CourseDisplayDTO dto = convertToDisplayDTO(course);
            dtos.add(dto);
        }

        if (sortBy != null) {
            if (sortBy.equals("byCourseCode")) {
                dtos.sort((a, b) -> {
                    try {
                        int aCode = Integer.parseInt(a.getCourseCode());
                        int bCode = Integer.parseInt(b.getCourseCode());
                        return Integer.compare(aCode, bCode);
                    } catch (NumberFormatException e) {
                        return a.getCourseCode().compareTo(b.getCourseCode());
                    }
                });
            }
            else if (sortBy.equals("byRating")) {
                dtos.sort((a,b) -> Double.compare(b.getAverageOverallRating(), a.getAverageOverallRating())); // Descending order
            }
            else if (sortBy.equals("byReviews")) {
                dtos.sort((a, b) -> Integer.compare(b.getNumReviews(), a.getNumReviews())); // Descending
            }
        }

        int start = (int) pageable.getOffset();
        int end = Math.min(pageable.getPageSize() + start, dtos.size());
        if (start > dtos.size()) { // Error handling
            return new PageImpl<>(new ArrayList<>(), pageable, dtos.size());
        }
        return new PageImpl<>(dtos.subList(start, end), pageable, dtos.size());
    }

    public List<String> getAllColleges() {
        return List.of("CAS", "KHC", "HUB", "MED", "COM", "ENG", "CFA", "CGS",
                "CDS", "GMS", "SDM", "GMS", "MET", "QST", "SAR", "SHA", "LAW", "SPH", "SSW",
                "STH", "WED");
    }

    public List<String> getDepartmentsByCollege(String college) {
        List<Course> courses = getCoursesByCollege(college);
        Set<String> departments = new HashSet<>();
        for (Course course : courses) {
            departments.add(course.getDepartment());
        }
        return new ArrayList<>(departments);
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

    public Course updateCourseWithHubReqsAndDescription(String baseUrl, List<String> hubNames, String description) {
        Optional<Course> courseOptional = courseRepository.findByBaseUrl(baseUrl);
        if (courseOptional.isEmpty()) {
            System.out.println("No course found with URL: " + baseUrl);
            return null;
        }
        Course course = courseOptional.get();
        Set<HubRequirement> hubRequirements = convertHubNamesToEnum(hubNames);
        course.setHubRequirements(hubRequirements);
        course.setCourseDesc(description);
        if (description != null) {
            String descLower = description.toLowerCase();
            course.setNoPreReqs(!descLower.contains("prerequisite") && !descLower.contains("pre-requisite"));
        }
        else {
            course.setNoPreReqs(true);
        }
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
    public void updateCourseRatings(Long courseId) {
        Course course = getCourseById(courseId)
                .orElseThrow(() -> new RuntimeException("Invalid course ID"));
        List<Review> reviews = reviewRepository.findByCourse(course);
        course.setTotalReviews(reviews.size());
        if (reviews.isEmpty()) {
            course.setAverageWorkloadRating(0.0);
            course.setAverageDifficultyRating(0.0);
            course.setAverageUsefulnessRating(0.0);
            course.setAverageInterestRating(0.0);
            course.setAverageTeacherRating(0.0);
            course.setAverageOverallRating(0.0);
            return;
        }
        int totalWorkload = 0;
        int totalDifficulty = 0;
        int totalUsefulness = 0;
        int totalInterest = 0;
        int totalTeacher = 0;
        for (Review review : reviews) {
            totalWorkload += review.getWorkloadRating();
            totalDifficulty += review.getDifficultyRating();
            totalUsefulness += review.getUsefulnessRating();
            totalInterest += review.getInterestRating();
            totalTeacher += review.getTeacherRating();
        }
        course.setAverageWorkloadRating((double) totalWorkload / reviews.size());
        course.setAverageDifficultyRating((double) totalDifficulty / reviews.size());
        course.setAverageUsefulnessRating((double) totalUsefulness / reviews.size());
        course.setAverageInterestRating((double) totalInterest / reviews.size());
        course.setAverageTeacherRating((double) totalTeacher / reviews.size());
        courseRepository.save(course); // So can calculate overall rating with the helper function
        course.setAverageOverallRating(calculateOverallRating(course));
        courseRepository.save(course);
    }

    public CourseDisplayDTO convertToDisplayDTO(Course course) {
        Set<HubRequirementDTO> hubs = new HashSet<>();
        for (HubRequirement hubReq : course.getHubRequirements()) {
            HubRequirementDTO hubReqDTO = new HubRequirementDTO();
            hubReqDTO.setName(hubReq.getCode());
            hubs.add(hubReqDTO);
        }
        return CourseDisplayDTO.builder()
                .id(course.getId().toString())
                .title(course.getTitle())
                .college(course.getCollege())
                .department(course.getDepartment())
                .courseCode(course.getCourseCode())
                .noPreReqs(course.getNoPreReqs())
                .numReviews(course.getTotalReviews())
                .averageOverallRating(course.getAverageOverallRating())
                .averageUsefulnessRating(course.getAverageUsefulnessRating())
                .averageDifficultyRating(course.getAverageDifficultyRating())
                .averageWorkloadRating(course.getAverageWorkloadRating())
                .averageInterestRating(course.getAverageInterestRating())
                .averageTeacherRating(course.getAverageTeacherRating())
                .hubRequirements(hubs)
                .build();
    }

    public CourseDTO convertToFullDTO(Course course, User user) {
        Set<HubRequirementDTO> hubs = new HashSet<>();
        for (HubRequirement hubReq : course.getHubRequirements()) {
            HubRequirementDTO hubReqDTO = new HubRequirementDTO();
            hubReqDTO.setName(hubReq.getCode());
            hubs.add(hubReqDTO);
        }
        List<Review> reviews = reviewRepository.findByCourseOrderByCreatedAtDesc(course);
        List<ReviewResponseDTO> reviewDTOs = new ArrayList<>();
        for (Review review : reviews) {
            reviewDTOs.add(convertToResponseDTO(review, user));
        }

        return CourseDTO.builder()
                .id(course.getId().toString())
                .title(course.getTitle())
                .college(course.getCollege())
                .department(course.getDepartment())
                .courseCode(course.getCourseCode())
                .description(course.getCourseDesc())
                .noPreReqs(course.getNoPreReqs())
                .numReviews(course.getTotalReviews())
                .averageOverallRating(course.getAverageOverallRating())
                .averageUsefulnessRating(course.getAverageUsefulnessRating())
                .averageDifficultyRating(course.getAverageDifficultyRating())
                .averageWorkloadRating(course.getAverageWorkloadRating())
                .averageInterestRating(course.getAverageInterestRating())
                .averageTeacherRating(course.getAverageTeacherRating())
                .courseReviews(reviewDTOs)
                .hubRequirements(hubs)
                .build();

    }

    public ReviewResponseDTO convertToResponseDTO(Review review, User currentUser) { // Moved here to fix circular dependency
        ReviewResponseDTO dto = new ReviewResponseDTO();

        dto.setId(review.getId());
        dto.setCourseId(review.getCourse().getId());

        dto.setUsefulnessRating(review.getUsefulnessRating());
        dto.setDifficultyRating(review.getDifficultyRating());
        dto.setWorkloadRating(review.getWorkloadRating());
        dto.setInterestRating(review.getInterestRating());
        dto.setTeacherRating(review.getTeacherRating());
        dto.setOverallRating(((6.0 - review.getDifficultyRating()) +  // Invert difficulty
                (6.0 - review.getWorkloadRating()) +  // Invert workload
                review.getInterestRating() +
                review.getUsefulnessRating() +
                review.getTeacherRating()) / 5.0);
        dto.setTeacherName(review.getTeacherName());
        dto.setReviewText(review.getReviewText());
        dto.setSemester(review.getSemester());
        dto.setHoursPerWeek(review.getHoursPerWeek());
        dto.setAssignmentTypes(review.getAssignmentTypes());
        dto.setAttendanceRequired(review.getAttendanceRequired());
        dto.setCreatedAt(review.getCreatedAt());
        if (currentUser == null) { // when the user is not logged in
            dto.setOwner(false);
        }
        else {
            dto.setOwner(review.getUser().getId().equals(currentUser.getId()));
        }
        return dto;
    }

}
