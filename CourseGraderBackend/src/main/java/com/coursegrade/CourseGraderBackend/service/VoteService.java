package com.coursegrade.CourseGraderBackend.service;

import com.coursegrade.CourseGraderBackend.dto.VoteResponseDTO;
import com.coursegrade.CourseGraderBackend.model.Review;
import com.coursegrade.CourseGraderBackend.model.User;
import com.coursegrade.CourseGraderBackend.model.Vote;
import com.coursegrade.CourseGraderBackend.model.VoteType;
import com.coursegrade.CourseGraderBackend.repository.ReviewRepository;
import com.coursegrade.CourseGraderBackend.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VoteService {
    private final VoteRepository voteRepository;
    private final ReviewRepository reviewRepository;

    public VoteResponseDTO voteOnReview(User user, Long reviewId, String voteType) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        Optional<Vote> vote = voteRepository.findByUserAndReview(user, review);
        VoteType voteTypeEnum = VoteType.valueOf(voteType);
        if (vote.isPresent()) {
            Vote curVote = vote.get();
            if (curVote.getVoteType() == voteTypeEnum) {
                voteRepository.delete(curVote);
            }
            else {
                curVote.setVoteType(voteTypeEnum);
                voteRepository.save(curVote);
            }
        }
        else {
            Vote newVote = new Vote();
            newVote.setUser(user);
            newVote.setReview(review);
            newVote.setVoteType(voteTypeEnum);
            voteRepository.save(newVote);
        }
        updateReviewVotes(review);
        return convertToResponseDTO(review, user);
    }

    public VoteResponseDTO getReviewVotes(User user, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        return convertToResponseDTO(review, user);
    }

    private void updateReviewVotes(Review review) {
        List<Vote> votes = voteRepository.findByReview(review);
        Integer upvotes = 0;
        Integer downvotes = 0;
        for (Vote vote : votes) {
            if (vote.getVoteType() == VoteType.UPVOTE) upvotes++;
            else if (vote.getVoteType() == VoteType.DOWNVOTE) downvotes++;
        }
        review.setUpvoteCount(upvotes);
        review.setDownvoteCount(downvotes);
        reviewRepository.save(review);
    }

    private VoteResponseDTO convertToResponseDTO(Review review, User user) {
        Optional<Vote> vote = voteRepository.findByUserAndReview(user, review);
        String type = null;
        if (vote.isPresent()) {
            type = vote.get().getVoteType().toString();
        }
        return VoteResponseDTO.builder()
                .reviewId(review.getId().toString())
                .voteCount(review.getUpvoteCount() - review.getDownvoteCount())
                .userVote(type)
                .build();
    }
}
