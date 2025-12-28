package com.coursegrade.CourseGraderBackend.service;

import com.coursegrade.CourseGraderBackend.dto.*;
import com.coursegrade.CourseGraderBackend.model.*;
import com.coursegrade.CourseGraderBackend.repository.*;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CourseRepository courseRepository;
    private final CourseService courseService;
    private final ReviewRepository reviewRepository;
    private final VoteRepository voteRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Transactional
    public HubProgressDTO getHubProgress(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
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
    public void addCompletedCourse(Long courseId, Long userId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (currentUser.getCompletedCourses().contains(course)) {
            throw new RuntimeException("Course already completed");
        }
        currentUser.getCompletedCourses().add(course);
        currentUser.updateHubProgress();
        userRepository.save(currentUser);
    }

    @Transactional
    public void removeCompletedCourse(Long courseId, Long userId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!currentUser.getCompletedCourses().contains(course)) {
            throw new RuntimeException("Course not in completed list");
        }
        currentUser.getCompletedCourses().remove(course);
        currentUser.updateHubProgress();
        userRepository.save(currentUser);
    }

    public List<CourseDisplayDTO> getCompletedCourses(User currentUser) {
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<CourseDisplayDTO> completedCourses = new ArrayList<>();
        for (Course course : user.getCompletedCourses()) {
            CourseDisplayDTO courseDTO = courseService.convertToDisplayDTO(course);
            completedCourses.add(courseDTO);
        }
        return completedCourses;
    }

    public AccountDTO getAccountSettings(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return AccountDTO.builder()
                .expectedGrad(user.getExpectedGrad())
                .college(user.getCollege())
                .major(user.getMajor())
                .build();
    }

    public void updateAccountSettings(AccountDTO accountUpdateDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (accountUpdateDTO.getExpectedGrad() != null) {
            user.setExpectedGrad(accountUpdateDTO.getExpectedGrad());
        }
        if (accountUpdateDTO.getCollege() != null) {
            user.setCollege(accountUpdateDTO.getCollege());
        }
        if (accountUpdateDTO.getMajor() != null) {
            user.setMajor(accountUpdateDTO.getMajor());
        }
        userRepository.save(user);
    }

    @Transactional
    public UserDashboardDTO getDashboard(User currentUser) {
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Review> reviews = reviewRepository.findByUser(user);
        Set<String> coursesToReview = new HashSet<>();
        for (Course course : user.getCompletedCourses()) {
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
                .email(user.getEmail())
                .expectedGrad(user.getExpectedGrad())
                .college(user.getCollege())
                .major(user.getMajor())
                .coursesCompleted(user.getCompletedCourses().size())
                .coursesReviewed(reviews.size())
                .totalUpvotes(upVotes)
                .averageReviewScore(reviews.isEmpty() ? 0.0 : (double) (upVotes - downVotes) / reviews.size())
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

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Vote> votes = voteRepository.findByUser(user);
        voteRepository.deleteAll(votes);
        Set<Long> affectedCourseIds = new HashSet<>();
        List<Review> reviews = reviewRepository.findByUser(user);
        for (Review review : reviews) {
            affectedCourseIds.add(review.getCourse().getId());
        }
        reviewRepository.deleteAll(reviews);
        Optional<PasswordResetToken> token = passwordResetTokenRepository.findByUser(user);
        token.ifPresent(passwordResetTokenRepository::delete);
        user.getCompletedCourses().clear();
        user.getHubProgress().clear();
        userRepository.save(user);
        userRepository.delete(user);
        for (Long courseId : affectedCourseIds) {
            courseService.updateCourseRatings(courseId);
        }
    }
}