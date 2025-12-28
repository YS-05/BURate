import { useEffect, useState } from "react";
import { ReviewResponseDTO } from "../../auth/AuthDTOs";
import { fetchAllReviews } from "../../api/axios";
import ErrorDisplay from "../../components/ErrorDisplay";
import Spinner from "../../components/Spinner";
import ReviewCard from "../../components/ReviewCard";

const AllReviews = () => {
  const [reviews, setReviews] = useState<ReviewResponseDTO[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    const loadReviews = async () => {
      try {
        setLoading(true);
        const reviewData = await fetchAllReviews();
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
    <div className="container my-5">
      <h1 className="fw-bold mb-4">All Reviews</h1>
      {reviews.map((review) => (
        <ReviewCard key={review.id} review={review} />
      ))}
    </div>
  );
};

export default AllReviews;
