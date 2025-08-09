package com.coursegrade.CourseGraderBackend.controller;

import com.coursegrade.CourseGraderBackend.dto.VoteResponseDTO;
import com.coursegrade.CourseGraderBackend.model.User;
import com.coursegrade.CourseGraderBackend.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/votes")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @PostMapping("/review/{reviewId}")
    public ResponseEntity<VoteResponseDTO> voteOnReview(
            @PathVariable Long reviewId,
            @RequestParam String voteType,
            @AuthenticationPrincipal User currentUser) {
        VoteResponseDTO response = voteService.voteOnReview(currentUser, reviewId, voteType);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/review/{reviewId}")
    public ResponseEntity<VoteResponseDTO> getReviewVotes(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal User currentUser
    ) {
        VoteResponseDTO response = voteService.getReviewVotes(currentUser, reviewId);
        return ResponseEntity.ok(response);
    }
}
