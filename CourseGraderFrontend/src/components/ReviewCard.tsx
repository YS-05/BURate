import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { voteOnReview, fetchReviewVotes, deleteReview } from "../api/axios";
import { useAuth } from "../auth/AuthProvider";
import StarRating from "../components/StarRating";

type ReviewCardProps = {
  review: any;
};

const ReviewCard = ({ review }: ReviewCardProps) => {
  const { user } = useAuth();
  const navigate = useNavigate();

  const [sumVote, setSumVote] = useState(0);
  const [userVote, setUserVote] = useState<string | null>(null);
  const [voting, setVoting] = useState(false);
  const [deleting, setDeleting] = useState(false);

  useEffect(() => {
    const loadVotes = async () => {
      try {
        const data = await fetchReviewVotes(review.id.toString());
        setSumVote(data.voteCount);
        setUserVote(data.userVote);
      } catch (err) {
        console.error("Failed to load votes", err);
      }
    };

    loadVotes();
  }, [review.id]);

  const handleVote = async (type: "UPVOTE" | "DOWNVOTE") => {
    if (!user) {
      navigate("/login");
      return;
    }

    try {
      setVoting(true);
      const res = await voteOnReview(review.id.toString(), type);
      setSumVote(res.voteCount);
      setUserVote(res.userVote);
    } finally {
      setVoting(false);
    }
  };

  const handleDelete = async () => {
    if (!window.confirm("Delete this review? This cannot be undone.")) return;

    try {
      setDeleting(true);
      await deleteReview(review.id.toString());
      window.location.reload(); // simple + safe
    } catch (err) {
      console.error("Delete failed", err);
    } finally {
      setDeleting(false);
    }
  };

  const isOwner = review.owner === true;

  return (
    <div className="border-top py-4">
      {/* Header */}
      <div className="d-flex justify-content-between align-items-center mb-2">
        <div className="fw-bold">{review.teacherName}</div>
        <div className="text-muted">
          {new Date(review.createdAt).toLocaleDateString("en-US", {
            month: "long",
            day: "numeric",
            year: "numeric",
          })}
        </div>
      </div>

      {/* Rating */}
      <div className="mb-2">
        <StarRating rating={review.overallRating ?? 0} />
        <span className="ms-2">{review.overallRating}/5</span>
      </div>

      {/* Category ratings */}
      <div className="d-flex justify-content-between mb-2 flex-wrap">
        <div>Usefulness: {review.usefulnessRating}/5</div>
        <div>Difficulty: {review.difficultyRating}/5</div>
        <div>Workload: {review.workloadRating}/5</div>
        <div>Interest: {review.interestRating}/5</div>
        <div>Teacher: {review.teacherRating}/5</div>
      </div>

      {/* Assignment types */}
      {review.assignmentTypes && (
        <div className="mb-3">Assignment Types: {review.assignmentTypes}</div>
      )}

      {/* Review text */}
      <div className="mb-3">{review.reviewText}</div>

      {/* Footer */}
      <div className="d-flex align-items-center justify-content-between">
        {/* Voting */}
        <div className="d-flex align-items-center gap-2">
          <button
            className="btn p-0 border-0 bg-transparent"
            disabled={voting}
            onClick={() => handleVote("UPVOTE")}
          >
            <i
              className={`bi ${
                userVote === "UPVOTE"
                  ? "bi-arrow-up-square-fill bu-red"
                  : "bi-arrow-up-square bu-red"
              } fs-4`}
            />
          </button>

          <span className="fw-semibold">{sumVote}</span>

          <button
            className="btn p-0 border-0 bg-transparent"
            disabled={voting}
            onClick={() => handleVote("DOWNVOTE")}
          >
            <i
              className={`bi ${
                userVote === "DOWNVOTE"
                  ? "bi-arrow-down-square-fill bu-red"
                  : "bi-arrow-down-square bu-red"
              } fs-4`}
            />
          </button>
        </div>

        {/* Owner actions */}
        {isOwner && (
          <div className="d-flex gap-3">
            <button
              className="btn p-0 border-0 bg-transparent"
              title="Edit review"
              onClick={() =>
                navigate(`/course/${review.courseId}/review?edit=${review.id}`)
              }
            >
              <i className="bi bi-pencil-square fs-5 text-secondary" />
            </button>

            <button
              className="btn p-0 border-0 bg-transparent"
              title="Delete review"
              disabled={deleting}
              onClick={handleDelete}
            >
              <i className="bi bi-trash fs-5 text-danger" />
            </button>
          </div>
        )}
      </div>
    </div>
  );
};

export default ReviewCard;
