package com.coursegrade.CourseGraderBackend.controller;

import com.coursegrade.CourseGraderBackend.dto.ChangePasswordDTO;
import com.coursegrade.CourseGraderBackend.dto.CourseDTO;
import com.coursegrade.CourseGraderBackend.dto.HubProgressDTO;
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

    @DeleteMapping("/completed-courses/{courseId}")
    private ResponseEntity<Map<String, String>> removeCompletedCourse(
            @PathVariable Long courseId,
            @AuthenticationPrincipal User currentUser
    ) {
        userService.removeCompletedCourse(courseId, currentUser);
        return ResponseEntity.ok(Map.of("message", "Course removed from completed courses"));
    }

    @GetMapping("/completed-courses")
    private ResponseEntity<List<CourseDTO>> getCompletedCourses(@AuthenticationPrincipal User currentUser) {
        List<CourseDTO> courses = userService.getCompletedCourses(currentUser);
        return ResponseEntity.ok(courses);
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