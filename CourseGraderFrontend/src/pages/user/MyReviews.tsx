import React, { useEffect, useState } from "react";
import { fetchMyReviews } from "../../api/axios";
import { ReviewResponseDTO } from "../../auth/AuthDTOs";
import ErrorDisplay from "../../components/ErrorDisplay";
import Spinner from "../../components/Spinner";
import ReviewItem from "../../components/ReviewItem";

const MyReviews = () => {
  const [reviews, setReviews] = useState<ReviewResponseDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const loadReviews = async () => {
      try {
        setLoading(true);
        const reviewData = await fetchMyReviews();
        console.log(reviewData);
        setReviews(reviewData);
      } catch (err: any) {
        setError("Error fetching your reviews, please try again later");
        console.log(err);
      } finally {
        setLoading(false);
      }
    };
    loadReviews();
  }, []);

  if (error) {
    return <ErrorDisplay error={error} />;
  }

  if (loading) {
    return <Spinner />;
  }

  return (
    <div
      className="min-vh-100"
      style={{ backgroundColor: "#f5f5f5", padding: "30px" }}
    >
      <h3 className="mb-4 text-center text-danger">My Reviews</h3>
      <p className="text-muted text-center">
        You have currently done {reviews.length}{" "}
        {reviews.length === 1 ? "review" : "reviews"}
      </p>
      <div className="border-top pt-3">
        {reviews.length > 0 ? (
          <div className="d-grid gap-3">
            {reviews.map((review) => (
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

export default MyReviews;
