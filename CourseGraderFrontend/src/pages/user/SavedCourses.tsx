import React, { useEffect, useState } from "react";
import { useAuth } from "../../auth/AuthProvider";
import { fetchSavedCourses } from "../../api/axios";
import { CourseDisplayDTO } from "../../auth/AuthDTOs";
import CourseGrid from "../../components/CourseGrid";
import Bookmark from "../../assets/bookmark.svg";
import { useNavigate } from "react-router-dom";

const SavedCourses = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [savedCourses, setSavedCourses] = useState<CourseDisplayDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (user) {
      loadSavedCourses();
    }
  }, [user]);

  const loadSavedCourses = async () => {
    try {
      setLoading(true);
      const courses = await fetchSavedCourses();
      setSavedCourses(courses);
    } catch (err) {
      console.error(
        "Failed to fetch saved courses, try re-logging in or at a different time"
      );
      setError(
        "Failed to fetch saved courses, try re-logging in or at a different time"
      );
    } finally {
      setLoading(false);
    }
  };

  if (!user) {
    return (
      <div className="container mt-5">
        <div className="alert alert-warning" role="alert">
          Error getting your saved courses, please re-log in
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="container mt-5">
        <div className="alert alert-danger" role="alert">
          {error}
        </div>
      </div>
    );
  }

  if (loading) {
    return (
      <div className="container mt-5">
        <div className="d-flex justify-content-center">
          <div className="spinner-border text-danger" role="status">
            <span className="visually-hidden">Loading...</span>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div
      className="min-vh-100"
      style={{ backgroundColor: "#f5f5f5", padding: "30px" }}
    >
      <div className="container">
        <div className="row mb-4">
          <div className="col-12">
            <h3 className="mb-4 text-center">Saved Courses</h3>
            <p className="text-muted text-center">
              Courses you've bookmarked for future reference:{" "}
              {savedCourses.length}{" "}
              {savedCourses.length === 1 ? "course" : "courses"}
            </p>
          </div>
        </div>
      </div>
      {savedCourses.length === 0 ? (
        <div className="row">
          <div className="col-12">
            <div className="card border-4 border-danger h-100 d-flex">
              <div className="card-body text-center py-5">
                <img src={Bookmark} height={64} className="mb-4" />
                <h4 className="text-muted mb-3">No saved courses yet</h4>
                <p className="text-muted mb-4">
                  Start exploring courses and bookmark the ones you're
                  interested in!
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
        <CourseGrid courses={savedCourses} onRefresh={loadSavedCourses} />
      )}
    </div>
  );
};

export default SavedCourses;
