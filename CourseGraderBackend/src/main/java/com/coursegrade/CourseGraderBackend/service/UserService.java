package com.coursegrade.CourseGraderBackend.service;

import com.coursegrade.CourseGraderBackend.dto.*;
import com.coursegrade.CourseGraderBackend.model.Course;
import com.coursegrade.CourseGraderBackend.model.HubRequirement;
import com.coursegrade.CourseGraderBackend.model.Review;
import com.coursegrade.CourseGraderBackend.model.User;
import com.coursegrade.CourseGraderBackend.repository.CourseRepository;
import com.coursegrade.CourseGraderBackend.repository.ReviewRepository;
import com.coursegrade.CourseGraderBackend.repository.UserRepository;
import com.coursegrade.CourseGraderBackend.repository.VoteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CourseRepository courseRepository;
    private final CourseService courseService;
    private final ReviewRepository reviewRepository;

    @Transactional
    public HubProgressDTO getHubProgress(User user) {
        user.updateHubProgress();
        HubProgressDTO hubProgressDTO = new HubProgressDTO();
        List<HubProgressItem> items = new ArrayList<>();
        for (HubRequirement hub : HubRequirement.values()) {
            HubProgressItem item = new HubProgressItem();
            item.setCategory(hub.getCategory());
            item.setHubName(hub.getName());
            item.setHubCode(hub.getCode());
            item.setRequired(hub.getReqCount());
            item.setFulfilled(user.isHubFulfilled(hub));

            int completed = user.getHubProgress().getOrDefault(hub, 0);
            item.setCompleted(completed);

            // Courses that fulfill the hub
            List<String> fulfillingCourses = new ArrayList<>();
            for (Course course : user.getCompletedCourses()) {
                if (course.getHubRequirements().contains(hub)) {
                    String courseName = course.courseDisplay();
                    fulfillingCourses.add(courseName);
                }
            }
            item.setFulfillingCourses(fulfillingCourses);
            items.add(item);
        }
        hubProgressDTO.setHubProgress(items);
        // Group by category later for frontend
        return hubProgressDTO;
    }

    @Transactional
    public void addCompletedCourse(Long courseId, User currentUser) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        if (currentUser.getCompletedCourses().contains(course)) {
            throw new RuntimeException("Course already completed");
        }
        currentUser.getCompletedCourses().add(course);
        currentUser.updateHubProgress();
        userRepository.save(currentUser);
    }

    @Transactional
    public void addSavedCourse(Long courseId, User currentUser) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        if (currentUser.getSavedCourses().contains(course)) {
            throw new RuntimeException("Course already saved");
        }
        currentUser.getSavedCourses().add(course);
        userRepository.save(currentUser);
    }

    @Transactional
    public void addInProgressCourse(Long courseId, User currentUser) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        if (currentUser.getCoursesInProgress().contains(course)) {
            throw new RuntimeException("Course already in progress");
        }
        currentUser.getCoursesInProgress().add(course);
        userRepository.save(currentUser);
    }

    @Transactional
    public void removeCompletedCourse(Long courseId, User currentUser) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        if (!currentUser.getCompletedCourses().contains(course)) {
            throw new RuntimeException("Course not in completed list");
        }
        currentUser.getCompletedCourses().remove(course);
        currentUser.updateHubProgress();
        userRepository.save(currentUser);
    }

    @Transactional
    public void removeSavedCourse(Long courseId, User currentUser) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        if (!currentUser.getSavedCourses().contains(course)) {
            throw new RuntimeException("Course not in saved list");
        }
        currentUser.getSavedCourses().remove(course);
        userRepository.save(currentUser);
    }

    @Transactional
    public void removeInProgressCourse(Long courseId, User currentUser) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        if (!currentUser.getCoursesInProgress().contains(course)) {
            throw new RuntimeException("Course not in in progress list");
        }
        currentUser.getCoursesInProgress().remove(course);
        userRepository.save(currentUser);
    }

    public List<CourseDisplayDTO> getCompletedCourses(User currentUser) {
        List<CourseDisplayDTO> completedCourses = new ArrayList<>();
        for (Course course : currentUser.getCompletedCourses()) {
            CourseDisplayDTO courseDTO = courseService.convertToDisplayDTO(course);
            completedCourses.add(courseDTO);
        }
        return completedCourses;
    }

    public List<CourseDisplayDTO> getSavedCourses(User currentUser) {
        List<CourseDisplayDTO> savedCourses = new ArrayList<>();
        for (Course course : currentUser.getSavedCourses()) {
            CourseDisplayDTO courseDTO = courseService.convertToDisplayDTO(course);
            savedCourses.add(courseDTO);
        }
        return savedCourses;
    }

    public List<CourseDisplayDTO> getInProgressCourses(User currentUser) {
        List<CourseDisplayDTO> inProgressCourses = new ArrayList<>();
        for (Course course : currentUser.getCoursesInProgress()) {
            CourseDisplayDTO courseDTO = courseService.convertToDisplayDTO(course);
            inProgressCourses.add(courseDTO);
        }
        return inProgressCourses;
    }

    public UserDashboardDTO getDashboard(User currentUser) {
        List<Review> reviews = reviewRepository.findByUser(currentUser);
        Set<String> coursesToReview = new HashSet<>();
        for (Course course : currentUser.getCompletedCourses()) {
            coursesToReview.add(course.courseDisplay()); // Ex: CAS CS 460
        }
        int upVotes = 0;
        int downVotes = 0;
        for (Review review : reviews) {
            upVotes += review.getUpvoteCount();
            downVotes += review.getDownvoteCount();
            coursesToReview.remove(review.getCourse().courseDisplay());
        }
        return UserDashboardDTO.builder()
                .email(currentUser.getEmail())
                .coursesCompleted(currentUser.getCompletedCourses().size())
                .coursesInProgress(currentUser.getCoursesInProgress().size())
                .coursesReviewed(reviews.size())
                .totalUpvotes(upVotes)
                .averageReviewScore(reviews.isEmpty() ? 0.0 : (double)(upVotes - downVotes) / reviews.size())
                .coursesToReview(coursesToReview)
                .build();
    }

    @Transactional
    public void changePassword(User user, ChangePasswordDTO passwordResetDTO) {
        if (!passwordResetDTO.getNewPassword().equals(passwordResetDTO.getConfirmNewPassword())) {
            throw new RuntimeException("Passwords do not match");
        }
        if (!passwordEncoder.matches(passwordResetDTO.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(passwordResetDTO.getNewPassword()));
        userRepository.save(user);
    }
}