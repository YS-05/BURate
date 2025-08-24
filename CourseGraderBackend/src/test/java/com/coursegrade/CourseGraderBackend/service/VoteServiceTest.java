package com.coursegrade.CourseGraderBackend.service;

import com.coursegrade.CourseGraderBackend.dto.VoteResponseDTO;
import com.coursegrade.CourseGraderBackend.model.*;
import com.coursegrade.CourseGraderBackend.repository.ReviewRepository;
import com.coursegrade.CourseGraderBackend.repository.VoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoteServiceTest {

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private VoteService voteService;

    private User testUser;
    private Review testReview;

    @BeforeEach
    void setUp() {
        testUser = createTestUser();
        testReview = createTestReview();
    }

    @Test
    void voteOnReview_ExistingSameVote_ShouldToggleOffVote() {
        // Given - User has already upvoted
        Vote existingUpvote = createTestVote(VoteType.UPVOTE);
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));
        when(voteRepository.findByUserAndReview(testUser, testReview))
                .thenReturn(Optional.of(existingUpvote))
                .thenReturn(Optional.empty());
        when(voteRepository.findByReview(testReview)).thenReturn(List.of()); // No votes after deletion

        // When - User clicks upvote again (toggle off)
        VoteResponseDTO result = voteService.voteOnReview(testUser, 1L, "UPVOTE");

        // Then - Should show no user vote (toggled off)
        assertThat(result.getUserVote()).isNull();
        assertThat(result.getVoteCount()).isEqualTo(0);
    }

    @Test
    void voteOnReview_ExistingDifferentVote_ShouldChangeVoteType() {
        // Given - User has upvoted, now wants to downvote
        Vote existingUpvote = createTestVote(VoteType.UPVOTE);
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));
        when(voteRepository.findByUserAndReview(testUser, testReview))
                .thenReturn(Optional.of(existingUpvote))
                .thenReturn(Optional.of(existingUpvote));
        when(voteRepository.findByReview(testReview)).thenReturn(List.of(existingUpvote));

        // Mock the vote type change
        doAnswer(invocation -> {
            existingUpvote.setVoteType(VoteType.DOWNVOTE);
            return null;
        }).when(voteRepository).save(existingUpvote);

        // When - User clicks downvote on upvoted review
        VoteResponseDTO result = voteService.voteOnReview(testUser, 1L, "DOWNVOTE");

        // Then - Should show downvote (changed from upvote)
        assertThat(result.getUserVote()).isEqualTo("DOWNVOTE");
    }

    @Test
    void voteOnReview_NoExistingVote_ShouldCreateVote() {
        // Given - User hasn't voted yet
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));
        when(voteRepository.findByUserAndReview(testUser, testReview))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(createTestVote(VoteType.UPVOTE)));
        when(voteRepository.findByReview(testReview)).thenReturn(List.of(createTestVote(VoteType.UPVOTE)));

        // When - User votes for first time
        VoteResponseDTO result = voteService.voteOnReview(testUser, 1L, "UPVOTE");

        // Then - Should show the new vote
        assertThat(result.getUserVote()).isEqualTo("UPVOTE");
        assertThat(result.getVoteCount()).isEqualTo(1);
    }

    // Helper methods
    private User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        return user;
    }

    private Review createTestReview() {
        Review review = new Review();
        review.setId(1L);
        review.setUpvoteCount(0);
        review.setDownvoteCount(0);
        return review;
    }

    private Vote createTestVote(VoteType voteType) {
        Vote vote = new Vote();
        vote.setId(1L);
        vote.setUser(testUser);
        vote.setReview(testReview);
        vote.setVoteType(voteType);
        return vote;
    }
}