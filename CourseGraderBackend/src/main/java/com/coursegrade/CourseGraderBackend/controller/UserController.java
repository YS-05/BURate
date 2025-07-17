package com.coursegrade.CourseGraderBackend.controller;

import com.coursegrade.CourseGraderBackend.dto.*;
import com.coursegrade.CourseGraderBackend.model.User;
import com.coursegrade.CourseGraderBackend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/hub-progress")
    public ResponseEntity<HubProgressDTO> getHubProgress(@AuthenticationPrincipal User currentUser) {
        HubProgressDTO progress = userService.getHubProgress(currentUser);
        return ResponseEntity.ok(progress);
    }

    @PostMapping("/completed-courses/{courseId}")
    private ResponseEntity<Map<String, String>> addCompletedCourse(
            @PathVariable Long courseId,
            @AuthenticationPrincipal User currentUser
    ) {
        userService.addCompletedCourse(courseId, currentUser);
        return ResponseEntity.ok(Map.of("message", "Course added to completed courses"));
    }

    @PostMapping("/saved-courses/{courseId}")
    private ResponseEntity<Map<String, String>> addSavedCourse(
            @PathVariable Long courseId,
            @AuthenticationPrincipal User currentUser
    ) {
        userService.addSavedCourse(courseId, currentUser);
        return ResponseEntity.ok(Map.of("message", "Course added to saved courses"));
    }

    @PostMapping("courses-in-progress/{courseId}")
    private ResponseEntity<Map<String, String>> addInProgressCourse(
            @PathVariable Long courseId,
            @AuthenticationPrincipal User currentUser
    ) {
        userService.addInProgressCourse(courseId, currentUser);
        return ResponseEntity.ok(Map.of("message", "Course added to courses in progress"));
    }

    @DeleteMapping("/completed-courses/{courseId}")
    private ResponseEntity<Map<String, String>> removeCompletedCourse(
            @PathVariable Long courseId,
            @AuthenticationPrincipal User currentUser
    ) {
        userService.removeCompletedCourse(courseId, currentUser);
        return ResponseEntity.ok(Map.of("message", "Course removed from completed courses"));
    }

    @DeleteMapping("/saved-courses/{courseId}")
    private ResponseEntity<Map<String, String>> removeSavedCourse(
            @PathVariable Long courseId,
            @AuthenticationPrincipal User currentUser
    ) {
        userService.removeSavedCourse(courseId, currentUser);
        return ResponseEntity.ok(Map.of("message", "Course removed from saved courses"));
    }

    @DeleteMapping("/courses-in-progress/{courseId}")
    private ResponseEntity<Map<String, String>> removeInProgressCourse(
            @PathVariable Long courseId,
            @AuthenticationPrincipal User currentUser
    ) {
        userService.removeInProgressCourse(courseId, currentUser);
        return ResponseEntity.ok(Map.of("message", "Course removed from courses in progress"));
    }

    @GetMapping("/completed-courses")
    private ResponseEntity<List<CourseDisplayDTO>> getCompletedCourses(@AuthenticationPrincipal User currentUser) {
        List<CourseDisplayDTO> courses = userService.getCompletedCourses(currentUser);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/saved-courses")
    private ResponseEntity<List<CourseDisplayDTO>> getSavedCourses(@AuthenticationPrincipal User currentUser) {
        List<CourseDisplayDTO> courses = userService.getSavedCourses(currentUser);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/courses-in-progress")
    private ResponseEntity<List<CourseDisplayDTO>> getCoursesInProgress(@AuthenticationPrincipal User currentUser) {
        List<CourseDisplayDTO> courses = userService.getInProgressCourses(currentUser);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/dashboard")
    private ResponseEntity<UserDashboardDTO> getDashboard(@AuthenticationPrincipal User currentUser) {
        UserDashboardDTO dashboard = userService.getDashboard(currentUser);
        return ResponseEntity.ok(dashboard);
    }

    @PostMapping("/change-password")
    private ResponseEntity<Map<String, String>> changePassword(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody ChangePasswordDTO passwordDTO
            ) {
        userService.changePassword(currentUser, passwordDTO);
        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }
}