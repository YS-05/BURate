import { useEffect, useState } from "react";
import { useAuth } from "../../auth/AuthProvider";
import { UserDashboardDTO, ReviewResponseDTO } from "../../auth/AuthDTOs";
import {
  fetchDashboardData,
  fetchMyReviews,
  fetchCompletedCourses,
} from "../../api/axios";
import { CourseDisplayDTO } from "../../auth/AuthDTOs";
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
  const [completedCourses, setCompletedCourses] = useState<CourseDisplayDTO[]>(
    []
  );

  useEffect(() => {
    const loadDashboard = async () => {
      try {
        setLoading(true);
        const [dashboard, reviewData, completed] = await Promise.all([
          fetchDashboardData(),
          fetchMyReviews(),
          fetchCompletedCourses(),
        ]);
        setDashboardData({
          ...dashboard,
          coursesToReview: new Set(dashboard.coursesToReview),
        });
        setReviews(reviewData);
        setCompletedCourses(completed);
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
        <div className="col-lg-3">
          <div className="border rounded-2 p-3 fw-bold">
            Courses completed
            <h4 className="mt-3 fw-bold">{dashboardData?.coursesCompleted}</h4>
          </div>
        </div>
        <div className="col-lg-3">
          <div className="border rounded-2 p-3 fw-bold">
            Courses reviewed
            <h4 className="mt-3 fw-bold">{dashboardData?.coursesReviewed}</h4>
          </div>
        </div>
        <div className="col-lg-3">
          <div className="border rounded-2 p-3 fw-bold">
            Total upvotes
            <h4 className="mt-3 fw-bold">{dashboardData?.totalUpvotes}</h4>
          </div>
        </div>
        <div className="col-lg-3">
          <div className="border rounded-2 p-3 fw-bold">
            Average review score
            <h4 className="mt-3 fw-bold">
              {dashboardData?.averageReviewScore}
            </h4>
          </div>
        </div>

        <div className="border rounded-2 p-3 mt-4">
          <h3 className="fw-bold">Your Completed Courses</h3>

          {completedCourses.length === 0 ? (
            <p className="text-muted m-0 mt-2">
              You haven’t marked any courses as completed yet.
            </p>
          ) : (
            <p className="m-0 mt-2">
              {completedCourses
                .map(
                  (course) =>
                    `${course.college} ${course.department} ${course.courseCode}`
                )
                .join(", ")}
            </p>
          )}
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
    </div>
  );
};

export default Dashboard;
