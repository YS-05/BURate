package com.coursegrade.CourseGraderBackend.service;

import com.coursegrade.CourseGraderBackend.dto.*;
import com.coursegrade.CourseGraderBackend.model.Course;
import com.coursegrade.CourseGraderBackend.model.HubRequirement;
import com.coursegrade.CourseGraderBackend.model.User;
import com.coursegrade.CourseGraderBackend.repository.CourseRepository;
import com.coursegrade.CourseGraderBackend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CourseRepository courseRepository;
    private final CourseService courseService;

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

    public List<CourseDTO> getCompletedCourses(User currentUser) {
        List<CourseDTO> completedCourses = new ArrayList<>();
        for (Course course : currentUser.getCompletedCourses()) {
            CourseDTO courseDTO = courseService.convertToFullDTO(course);
            completedCourses.add(courseDTO);
        }
        return completedCourses;
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