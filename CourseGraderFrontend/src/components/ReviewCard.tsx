import { ReviewResponseDTO } from "../auth/AuthDTOs";
import { useAuth } from "../auth/AuthProvider";
import { useNavigate } from "react-router-dom";

interface Props {
  reviews: ReviewResponseDTO[] | undefined;
  id: string | undefined;
}

const ReviewCard = ({ reviews, id }: Props) => {
  const { user } = useAuth();
  const navigate = useNavigate();

  const hasUserReviewed = reviews?.some((review) => review.isOwner) ?? false;

  return (
    <div className="card border-danger rounded-0 mb-4">
      <div className="card-header d-flex justify-content-between align-items-center">
        <h5 className="mb-0">Reviews: {reviews?.length || 0}</h5>
        {reviews?.map((review) => review.isOwner)}
        {user &&
          (hasUserReviewed ? (
            <span
              className="badge me-2"
              style={{ backgroundColor: "#20c997", color: "white" }}
            >
              ✓ Already Reviewed
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
      {/* Add logic for displaying reviews when review addition functionality is built */}
      <div className="card-body">
        <div className="text-center py-5">
          <div className="text-muted">
            <h6>No reviews yet!</h6>
            <p className="mb-0">Be the first one to review this course!</p>
            {!user && (
              <p className="mt-2 mb-0">
                <button
                  className="btn btn-outline-danger btn-sm"
                  onClick={() => navigate("/login")}
                >
                  Log in to write the first review
                </button>
              </p>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default ReviewCard;
