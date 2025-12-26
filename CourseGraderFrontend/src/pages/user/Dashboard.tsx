import { useEffect, useState } from "react";
import { useAuth } from "../../auth/AuthProvider";
import { UserDashboardDTO, ReviewResponseDTO } from "../../auth/AuthDTOs";
import { fetchDashboardData, fetchMyReviews } from "../../api/axios";
import { useNavigate } from "react-router-dom";
import Spinner from "../../components/Spinner";
import ErrorDisplay from "../../components/ErrorDisplay";
import ReviewCard from "../../components/ReviewCard";

const Dashboard = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [dashboardData, setDashboardData] = useState<UserDashboardDTO | null>(
    null
  );
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [reviews, setReviews] = useState<ReviewResponseDTO[]>([]);

  useEffect(() => {
    const loadDashboard = async () => {
      try {
        setLoading(true);

        const [dashboard, reviewData] = await Promise.all([
          fetchDashboardData(),
          fetchMyReviews(),
        ]);

        setDashboardData({
          ...dashboard,
          coursesToReview: new Set(dashboard.coursesToReview),
        });

        setReviews(reviewData);
      } catch (err) {
        console.error(err);
        setError("Failed to load dashboard data");
      } finally {
        setLoading(false);
      }
    };

    loadDashboard();
  }, []);

  if (loading) {
    return <Spinner />;
  }

  if (error) {
    return <ErrorDisplay error={error} />;
  }

  return (
    <div className="container my-5">
      <h1 className="fw-bold">Dashboard</h1>
      <p className="text-muted">View and manage your course reviews</p>
      <div className="row g-4">
        <div className="col-lg-4">
          <div className="border rounded-2 p-3 fw-bold">
            Total course reviews
            <h4 className="mt-3 fw-bold">{dashboardData?.coursesReviewed}</h4>
          </div>
        </div>
        <div className="col-lg-4">
          <div className="border rounded-2 p-3 fw-bold">
            Total upvotes from reviews
            <h4 className="mt-3 fw-bold">{dashboardData?.totalUpvotes}</h4>
          </div>
        </div>
        <div className="col-lg-4">
          <div className="border rounded-2 p-3 fw-bold">
            Average review score
            <h4 className="mt-3 fw-bold">
              {dashboardData?.averageReviewScore}
            </h4>
          </div>
        </div>
      </div>
      <div className="border rounded-2 p-3 mt-4">
        <h3 className="fw-bold">Your Reviews</h3>

        {reviews.length === 0 ? (
          <div className="text-center py-5">
            <h5 className="fw-bold mb-2">
              You haven’t written any reviews yet
            </h5>
            <p className="text-muted mb-4">
              Start helping other students by reviewing courses you’ve taken.
            </p>
            <button
              className="btn btn-outline-bu-red"
              onClick={() => navigate("/search")}
            >
              Browse courses
            </button>
          </div>
        ) : (
          reviews.map((review) => (
            <ReviewCard key={review.id} review={review} />
          ))
        )}
      </div>
    </div>
  );
};

export default Dashboard;
