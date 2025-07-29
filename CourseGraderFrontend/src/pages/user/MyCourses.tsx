import React, { useEffect, useState } from "react";
import { useAuth } from "../../auth/AuthProvider";
import { fetchCompletedCourses, fetchCoursesInProgress } from "../../api/axios";
import { CourseDisplayDTO } from "../../auth/AuthDTOs";
import CourseGrid from "../../components/CourseGrid";
import Bookmark from "../../assets/bookmark.svg";
import { useNavigate } from "react-router-dom";
import Spinner from "../../components/Spinner";
import ErrorDisplay from "../../components/ErrorDisplay";

const MyCourses = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [coursesInProgress, setCoursesInProgress] = useState<
    CourseDisplayDTO[]
  >([]);
  const [completedCourses, setCompletedCourses] = useState<CourseDisplayDTO[]>(
    []
  );
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (user) {
      loadMyCourses();
    }
  }, [user]);

  const loadMyCourses = async () => {
    try {
      setLoading(true);
      const [inProgressCourses, completedCoursesData] = await Promise.all([
        fetchCoursesInProgress(),
        fetchCompletedCourses(),
      ]);
      setCoursesInProgress(inProgressCourses);
      setCompletedCourses(completedCoursesData);
    } catch (err) {
      console.error(
        "Failed to fetch your courses, try re-logging in or at a different time"
      );
      setError(
        "Failed to fetch your courses, try re-logging in or at a different time"
      );
    } finally {
      setLoading(false);
    }
  };

  if (!user) {
    return (
      <div className="container mt-5">
        <div className="alert alert-warning" role="alert">
          Error getting your courses, please re-log in
        </div>
      </div>
    );
  }

  if (error) {
    return <ErrorDisplay error={error} />;
  }

  if (loading) {
    return <Spinner />;
  }

  const totalCourses = coursesInProgress.length + completedCourses.length;
  const hasAnyCourses = totalCourses > 0;

  return (
    <div
      className="min-vh-100"
      style={{ backgroundColor: "#f5f5f5", padding: "30px" }}
    >
      <div className="container">
        <div className="row mb-4">
          <div className="col-12">
            <h3 className="mb-4 text-center text-danger">My Courses</h3>
            <p className="text-muted text-center">
              Courses you are currently taking or completed in the past:{" "}
              {totalCourses} {totalCourses === 1 ? "course" : "courses"}
            </p>
          </div>
        </div>
      </div>

      {!hasAnyCourses ? (
        <div className="row">
          <div className="col-12">
            <div className="card border-4 border-danger h-100 d-flex">
              <div className="card-body text-center py-5">
                <img src={Bookmark} height={64} className="mb-4" />
                <h4 className="text-muted mb-3">No courses enrolled yet</h4>
                <p className="text-muted mb-4">
                  Start your learning journey by enrolling in courses that
                  interest you!
                </p>
                <button
                  className="btn btn-danger btn-lg"
                  onClick={() => navigate("/search")}
                >
                  Browse Courses
                </button>
              </div>
            </div>
          </div>
        </div>
      ) : (
        <>
          {coursesInProgress.length > 0 && (
            <div className="mb-5">
              <div className="container">
                <div className="row mb-3">
                  <div className="col-12">
                    <h4 className="mb-2">Courses in Progress:</h4>
                  </div>
                </div>
              </div>
              <CourseGrid
                courses={coursesInProgress}
                onRefresh={loadMyCourses}
              />
            </div>
          )}

          {completedCourses.length > 0 && (
            <div className="mb-5">
              <div className="container">
                <div className="row mb-3">
                  <div className="col-12">
                    <h4 className="mb-2">Completed Courses:</h4>
                  </div>
                </div>
              </div>
              <CourseGrid
                courses={completedCourses}
                onRefresh={loadMyCourses}
              />
            </div>
          )}
        </>
      )}
    </div>
  );
};

export default MyCourses;
