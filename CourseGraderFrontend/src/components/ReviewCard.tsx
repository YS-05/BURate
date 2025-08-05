import { ReviewResponseDTO } from "../auth/AuthDTOs";
import { useAuth } from "../auth/AuthProvider";
import { useNavigate } from "react-router-dom";
import ReviewItem from "./ReviewItem";

interface Props {
  reviews: ReviewResponseDTO[] | undefined;
  id: string | undefined;
}

const ReviewCard = ({ reviews, id }: Props) => {
  const { user } = useAuth();
  const navigate = useNavigate();

  const hasUserReviewed = reviews?.some((review) => review.owner) ?? false;

  return (
    <div className="card border-danger rounded-0 mb-4">
      <div className="card-header d-flex justify-content-between align-items-center">
        <h5 className="mb-0">Reviews: {reviews?.length || 0}</h5>
        {user &&
          (hasUserReviewed ? (
            <span
              className="badge me-2"
              style={{ backgroundColor: "#20c997", color: "white" }}
            >
              ✓ Reviewed
            </span>
          ) : (
            <button
              className="btn btn-danger btn-sm"
              onClick={() => navigate(`/course/${id}/review`)}
            >
              Write a Review
            </button>
          ))}
      </div>

      <div className="card-body">
        {reviews && reviews.length > 0 ? (
          <div className="d-grid gap-3">
            {reviews
              .sort(
                (a, b) =>
                  new Date(b.createdAt).getTime() -
                  new Date(a.createdAt).getTime()
              )
              .map((review) => (
                <ReviewItem key={review.id} review={review} />
              ))}
          </div>
        ) : (
          <div className="text-muted">No reviews yet.</div>
        )}
      </div>
    </div>
  );
};

export default ReviewCard;
