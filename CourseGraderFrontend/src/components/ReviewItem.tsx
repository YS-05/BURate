import { ReviewResponseDTO } from "../auth/AuthDTOs";
import { voteOnReview, fetchReviewVotes } from "../api/axios";
import { useAuth } from "../auth/AuthProvider";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Spinner from "./Spinner";
import ErrorDisplay from "./ErrorDisplay";
import Upvote from "../assets/arrow-up-square.svg";
import FillUpvote from "../assets/arrow-up-square-fill.svg";
import Downvote from "../assets/arrow-down-square.svg";
import FillDownvote from "../assets/arrow-down-square-fill.svg";
import Delete from "../assets/trash.svg";
import Edit from "../assets/pencil-square.svg";
import { deleteReview } from "../api/axios";

interface Props {
  review: ReviewResponseDTO;
  onReviewDeleted?: (reviewId: number) => void;
}

const ReviewItem = ({ review, onReviewDeleted }: Props) => {
  const { user } = useAuth();
  const [loading, setLoading] = useState(true);
  const [voting, setVoting] = useState(false);
  const [deleting, setDeleting] = useState(false);
  const navigate = useNavigate();
  const [sumVote, setSumVote] = useState(0);
  const [userVote, setUserVote] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const loadVotes = async () => {
      setLoading(true);
      try {
        const voteData = await fetchReviewVotes(review.id.toString());
        setSumVote(voteData.voteCount);
        setUserVote(voteData.userVote);
      } catch (err: any) {
        setError(err);
      } finally {
        setLoading(false);
      }
    };
    loadVotes();
  }, [review.id]);

  const handleDelete = async () => {
    try {
      setDeleting(true);
      await deleteReview(review.id.toString());
      onReviewDeleted?.(review.id);
    } catch (err: any) {
      setError(err || "Failed to delete review");
    } finally {
      setDeleting(false);
    }
  };

  const handleVote = async (voteType: "UPVOTE" | "DOWNVOTE") => {
    if (!user) {
      navigate("/login");
      return;
    }
    if (!review) {
      setError("Review could not be found");
      return;
    }
    try {
      setVoting(true);
      const response = await voteOnReview(review.id.toString(), voteType);
      setSumVote(response.voteCount);
      setUserVote(response.userVote);
    } catch (err: any) {
      setError(err);
    } finally {
      setVoting(false);
    }
  };

  if (loading) return <Spinner />;

  if (error) return <ErrorDisplay error={error} />;

  return (
    <div className="border-bottom pb-3">
      <div className="d-flex flex-column flex-md-row justify-content-md-between align-items-md-center text-muted mb-3 gap-1">
        <div>
          Overall:{" "}
          <span className="fw-bold text-black">
            {review.overallRating.toFixed(1)}/5
          </span>
        </div>
        <div>
          Professor:{" "}
          <span className="fw-bold text-black">{review.teacherName}</span>
        </div>
        <div>
          Semester:{" "}
          <span className="fw-bold text-black">{review.semester}</span>
        </div>
        <div>
          Date:{" "}
          <span className="fw-bold text-black">
            {new Date(review.createdAt).toLocaleDateString("en-US", {
              year: "numeric",
              month: "long",
              day: "numeric",
            })}
          </span>
        </div>
      </div>

      <div className="d-flex flex-column flex-md-row justify-content-md-between align-items-md-center text-muted mb-3 gap-1">
        <div>
          Usefulness:{" "}
          <span className="fw-bold text-black">{review.usefulnessRating}</span>
        </div>
        <div>
          Difficulty:{" "}
          <span className="fw-bold text-black">{review.difficultyRating}</span>
        </div>
        <div>
          Workload:{" "}
          <span className="fw-bold text-black">{review.workloadRating}</span>
        </div>
        <div>
          Interest:{" "}
          <span className="fw-bold text-black">{review.interestRating}</span>
        </div>
        <div>
          Teacher:{" "}
          <span className="fw-bold text-black">{review.teacherRating}</span>
        </div>
      </div>

      <div className="d-flex flex-column flex-md-row justify-content-md-between align-items-md-center text-muted mb-3 gap-1">
        <div>
          Attendance needed:{" "}
          <span className="fw-bold text-black">
            {review.attendanceRequired ? "Yes" : "No"}
          </span>
        </div>
        <div>
          Hours per week:{" "}
          <span className="fw-bold text-black">{review.hoursPerWeek}</span>
        </div>
      </div>

      <div className="mb-3 text-muted">
        Assignment Types: <p className="mb-0">{review.assignmentTypes}</p>
      </div>

      <div className="mb-3 text-muted">
        Review: <p className="mb-0">{review.reviewText}</p>
      </div>

      <div className="d-flex align-items-center justify-content-start">
        <button
          className="btn rounded-0 btn-link"
          onClick={() => handleVote("UPVOTE")}
          disabled={voting}
        >
          <img
            src={userVote === "UPVOTE" ? FillUpvote : Upvote}
            alt="Upvote"
            style={{ height: "30px", width: "30px" }}
          />
        </button>
        <span>{sumVote}</span>
        <button
          className="btn rounded-0 btn-link"
          onClick={() => handleVote("DOWNVOTE")}
          disabled={voting}
        >
          <img
            src={userVote === "DOWNVOTE" ? FillDownvote : Downvote}
            alt="Downvote"
            style={{ height: "30px", width: "30px" }}
          />
        </button>
      </div>

      {review.owner && (
        <>
          <button
            className="btn rounded-0 btn-link"
            onClick={() =>
              navigate(`/course/${review.courseId}/review?edit=${review.id}`)
            }
          >
            <img
              src={Edit}
              alt="edit"
              style={{ height: "20px", width: "20px" }}
            />
          </button>
          <button className="btn rounded-0 btn-link" onClick={handleDelete}>
            <img
              src={Delete}
              alt="delete"
              style={{ height: "20px", width: "20px" }}
            />
          </button>
        </>
      )}
    </div>
  );
};

export default ReviewItem;
