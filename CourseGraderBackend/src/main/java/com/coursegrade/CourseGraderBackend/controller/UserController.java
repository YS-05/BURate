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
        HubProgressDTO progress = userService.getHubProgress(currentUser.getId());
        return ResponseEntity.ok(progress);
    }

    @PostMapping("/completed-courses/{courseId}")
    private ResponseEntity<Map<String, String>> addCompletedCourse(
            @PathVariable Long courseId,
            @AuthenticationPrincipal User currentUser
    ) {
        userService.addCompletedCourse(courseId, currentUser.getId());
        return ResponseEntity.ok(Map.of("message", "Course added to completed courses"));
    }

    @DeleteMapping("/completed-courses/{courseId}")
    private ResponseEntity<Map<String, String>> removeCompletedCourse(
            @PathVariable Long courseId,
            @AuthenticationPrincipal User currentUser
    ) {
        userService.removeCompletedCourse(courseId, currentUser.getId());
        return ResponseEntity.ok(Map.of("message", "Course removed from completed courses"));
    }

    @GetMapping("/completed-courses")
    private ResponseEntity<List<CourseDisplayDTO>> getCompletedCourses(@AuthenticationPrincipal User currentUser) {
        List<CourseDisplayDTO> courses = userService.getCompletedCourses(currentUser);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/dashboard")
    private ResponseEntity<UserDashboardDTO> getDashboard(@AuthenticationPrincipal User currentUser) {
        UserDashboardDTO dashboard = userService.getDashboard(currentUser);
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/account")
    private ResponseEntity<AccountDTO> getAccountSettings(
            @AuthenticationPrincipal User currentUser
    ) {
        AccountDTO account = userService.getAccountSettings(currentUser.getId());
        return ResponseEntity.ok(account);
    }

    @PutMapping("/account")
    private ResponseEntity<Map<String, String>> updateAccountSettings(
            @RequestBody AccountDTO update,
            @AuthenticationPrincipal User currentUser
    ) {
        userService.updateAccountSettings(update, currentUser.getId());
        return ResponseEntity.ok(Map.of("message", "Account settings updated successfully"));
    }

    @PostMapping("/change-password")
    private ResponseEntity<Map<String, String>> changePassword(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody ChangePasswordDTO passwordDTO
            ) {
        userService.changePassword(currentUser, passwordDTO);
        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }

    @DeleteMapping("/delete")
    private ResponseEntity<Map<String, String>> deleteAccount(
            @AuthenticationPrincipal User currentUser
    ) {
        userService.deleteUser(currentUser.getId());
        return ResponseEntity.ok(Map.of("message", "Account deleted successfully"));
    }
}