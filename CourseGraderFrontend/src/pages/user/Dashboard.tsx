import React, { useEffect, useState } from "react";
import { useAuth } from "../../auth/AuthProvider";
import { UserDashboardDTO } from "../../auth/AuthDTOs";
import { fetchDashboardData, fetchCompletedCourses } from "../../api/axios";
import { useNavigate } from "react-router-dom";

const Dashboard = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [dashboardData, setDashboardData] = useState<UserDashboardDTO | null>(
    null
  );
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadDashboardData = async () => {
      try {
        const data = await fetchDashboardData();
        const convertedData = {
          ...data,
          coursesToReview: new Set(data.coursesToReview),
        };
        setDashboardData(convertedData);
      } catch (err) {
        setError("Failed to load dashboard data");
      } finally {
        setLoading(false);
      }
    };
    loadDashboardData();
  }, []);

  if (loading) {
    return (
      <div
        className="d-flex justify-content-center align-items-center"
        style={{ minHeight: "100vh" }}
      >
        <div className="spinner-border text-danger" role="status">
          <span className="visually-hidden">Loading...</span>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="container mt-5">
        <div className="alert alert-danger" role="alert">
          Error loading dashboard: {error}
        </div>
      </div>
    );
  }

  return (
    <div
      className="p-5"
      style={{
        backgroundColor: "#f5f5f5",
        minHeight: "100vh",
      }}
    >
      <div className="container">
        <div className="row mb-4">
          <div className="col-12">
            <h2
              className="text-center mb-4"
              style={{
                fontSize: "clamp(1rem, 2.5vw, 2.5rem)",
                lineHeight: "1.2",
              }}
            >
              Overview of your metrics, {dashboardData?.email}
            </h2>
            <div className="text-center text-muted">
              {dashboardData?.college && dashboardData?.major && (
                <p className="mb-1">
                  {dashboardData.major} • {dashboardData.college}
                </p>
              )}
              {dashboardData?.expectedGrad && (
                <p className="mb-0">
                  Expected Graduation: {dashboardData.expectedGrad}
                </p>
              )}
            </div>
          </div>
        </div>
        <div className="row g-4 mb-4">
          <h2
            className="mb-2"
            style={{
              fontSize: "clamp(1rem, 1.5vw, 1.5rem)",
              lineHeight: "1.2",
            }}
          >
            Courses and reviews info:
          </h2>
          <div className="col-12 col-md-6 col-lg-4">
            <div className="card border-4 h-100">
              <div className="card-body text-center p-4">
                <h2 className="fw-bold text-danger mb-2">
                  {dashboardData?.coursesCompleted || 0}
                </h2>
                <h4>Number of courses completed</h4>
              </div>
            </div>
          </div>
          <div className="col-12 col-md-6 col-lg-4">
            <div className="card border-4 h-100">
              <div className="card-body text-center p-4">
                <h2 className="fw-bold text-danger mb-2">
                  {dashboardData?.coursesInProgress || 0}
                </h2>
                <h4>Number of courses in progress</h4>
              </div>
            </div>
          </div>
          <div className="col-12 col-md-6 col-lg-4">
            <div className="card border-4 h-100">
              <div className="card-body text-center p-4">
                <h2 className="fw-bold text-danger mb-2">
                  {dashboardData?.coursesSaved || 0}
                </h2>
                <h4>Number of courses bookmarked</h4>
              </div>
            </div>
          </div>
          <div className="col-12 col-md-6 col-lg-4">
            <div className="card border-4 h-100">
              <div className="card-body text-center p-4">
                <h2 className="fw-bold text-danger mb-2">
                  {dashboardData?.coursesReviewed || 0}
                </h2>
                <h4>Number of courses Reviewed</h4>
              </div>
            </div>
          </div>
          <div className="col-12 col-md-6 col-lg-4">
            <div className="card border-4 h-100">
              <div className="card-body text-center p-4">
                <h2 className="fw-bold text-danger mb-2">
                  {dashboardData?.totalUpvotes || 0}
                </h2>
                <h4>Total upvotes from your reviews</h4>
              </div>
            </div>
          </div>
          <div className="col-12 col-md-6 col-lg-4">
            <div className="card border-4 h-100">
              <div className="card-body text-center p-4">
                <h2 className="fw-bold text-danger mb-2">
                  {dashboardData?.averageReviewScore !== undefined &&
                  dashboardData?.averageReviewScore !== null
                    ? dashboardData.averageReviewScore.toFixed(1)
                    : "0.0"}
                </h2>
                <h4>Average review score by votes</h4>
              </div>
            </div>
          </div>
        </div>
        <div className="row g-4 mb-3">
          <h2
            className="mb-2"
            style={{
              fontSize: "clamp(1rem, 1.5vw, 1.5rem)",
              lineHeight: "1.2",
            }}
          >
            Next courses to review:
          </h2>
          <div className="col">
            {dashboardData &&
              dashboardData.coursesCompleted < 5 &&
              dashboardData.coursesToReview.size === 0 && (
                <div className="card border-4">
                  <div className="card-body text-center p-5">
                    <h4 className="text-muted mb-3">
                      Start your course journey!
                    </h4>
                    <p className="text-muted mb-4">
                      Search for courses and add them to your completed list to
                      start reviewing.
                    </p>
                    <button
                      className="btn btn-outline-danger btn-lg"
                      onClick={() => navigate("/search")}
                    >
                      Add more courses
                    </button>
                  </div>
                </div>
              )}
            {dashboardData &&
              dashboardData.coursesCompleted >= 5 &&
              dashboardData.coursesToReview.size === 0 && (
                <div className="card border-4">
                  <div className="card-body text-center p-5">
                    <h4 className="text-success mb-3">All caught up!</h4>
                    <p className="text-muted mb-4">
                      You've reviewed all your completed courses. Great job
                      helping the community!
                    </p>
                    <button
                      className="btn btn-outline-danger btn-lg"
                      onClick={() => navigate("/search")}
                    >
                      Add More Courses
                    </button>
                  </div>
                </div>
              )}
            {/* Add last case here to display course names when functionality for adding courses is done */}
            {dashboardData && dashboardData.coursesToReview.size > 0 && (
              <div className="card border-4">
                <div className="card-body p-4">
                  <h4 className="text-center mb-4">
                    Courses ready for review (
                    {dashboardData.coursesToReview.size})
                  </h4>
                  <div className="row g-3">
                    {Array.from(dashboardData.coursesToReview).map(
                      (courseId, index) => (
                        <div
                          key={courseId}
                          className="col-12 col-md-6 col-lg-4"
                        >
                          <div className="card bg-light border-2 border-danger">
                            <div className="card-body text-center p-3">
                              <h6 className="text-muted mb-2">
                                Course Name: {courseId}
                              </h6>
                              <button
                                className="btn btn-danger btn-sm"
                                onClick={() =>
                                  navigate(`/course/${courseId}/review`)
                                }
                              >
                                Write Review
                              </button>
                            </div>
                          </div>
                        </div>
                      )
                    )}
                  </div>
                  <div className="text-center mt-4">
                    <p className="text-muted mb-3">
                      Help other students by sharing your experience with these
                      courses!
                    </p>
                    <button
                      className="btn btn-outline-danger"
                      onClick={() => navigate("/my-courses")}
                    >
                      View All My Courses
                    </button>
                  </div>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
