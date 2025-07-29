import React, { useEffect, useState } from "react";
import {
  addCompletedCourse,
  addSavedCourse,
  addInProgressCourse,
  removeCompletedCourse,
  removeSavedCourse,
  removeInProgressCourse,
  fetchCompletedCourses,
  fetchSavedCourses,
  fetchCoursesInProgress,
} from "../api/axios";
import { useAuth } from "../auth/AuthProvider";
import { useNavigate } from "react-router-dom";

interface Props {
  id: string | undefined;
}

const CourseAction = ({ id }: Props) => {
  const { user } = useAuth();
  const navigate = useNavigate();

  const [isCompleted, setIsCompleted] = useState(false);
  const [isSaved, setIsSaved] = useState(false);
  const [isInProgress, setIsInProgress] = useState(false);
  const [actionLoading, setActionLoading] = useState(false);

  useEffect(() => {
    if (user && id) {
      checkCourseStatus();
    }
  }, [user, id]);

  const checkCourseStatus = async () => {
    if (!id) return;

    try {
      const [completed, saved, inProgress] = await Promise.all([
        fetchCompletedCourses(),
        fetchSavedCourses(),
        fetchCoursesInProgress(),
      ]);
      setIsCompleted(completed.some((c) => c.id === id));
      setIsSaved(saved.some((c) => c.id === id));
      setIsInProgress(inProgress.some((c) => c.id === id));
    } catch (err) {
      console.log("Failed to check course status", err);
    }
  };

  const handleCompletedToggle = async () => {
    if (!user) {
      navigate("/login");
      return;
    }
    if (!id) {
      navigate("/dashboard");
      return;
    }
    try {
      setActionLoading(true);
      if (isCompleted) {
        await removeCompletedCourse(id);
        setIsCompleted(false);
      } else {
        await addCompletedCourse(id);
        setIsCompleted(true);
      }
    } catch (err) {
      console.log("Failed to toggle completed status", err);
    } finally {
      setActionLoading(false);
    }
  };

  const handleSavedToggle = async () => {
    if (!user) {
      navigate("/login");
      return;
    }
    if (!id) {
      navigate("/dashboard");
      return;
    }
    try {
      setActionLoading(true);
      if (isSaved) {
        await removeSavedCourse(id);
        setIsSaved(false);
      } else {
        await addSavedCourse(id);
        setIsSaved(true);
      }
    } catch (error) {
      console.error("Failed to toggle saved status:", error);
    } finally {
      setActionLoading(false);
    }
  };

  const handleInProgressToggle = async () => {
    if (!user) {
      navigate("/login");
      return;
    }
    if (!id) {
      navigate("/dashboard");
      return;
    }
    try {
      setActionLoading(true);
      if (isInProgress) {
        await removeInProgressCourse(id);
        setIsInProgress(false);
      } else {
        await addInProgressCourse(id);
        setIsInProgress(true);
      }
    } catch (error) {
      console.error("Failed to toggle in progress status:", error);
    } finally {
      setActionLoading(false);
    }
  };

  return (
    <div className="card border-danger rounded-0 mb-4">
      <div className="card-header">
        <h5 className="mb-0">Course Actions</h5>
      </div>
      <div className="card-body">
        <div className="d-grid gap-3">
          {user ? (
            <>
              <button
                className={`btn ${isCompleted ? "" : "btn-outline-secondary"}`}
                style={
                  isCompleted
                    ? {
                        backgroundColor: "#20c997",
                        borderColor: "#20c997",
                        color: "white",
                      }
                    : {}
                }
                onClick={handleCompletedToggle}
                disabled={actionLoading}
              >
                {actionLoading
                  ? "Loading..."
                  : isCompleted
                  ? "Completed"
                  : "Mark as Completed"}
              </button>
              <button
                className={`btn ${
                  isInProgress ? "btn-info" : "btn-outline-secondary"
                }`}
                onClick={handleInProgressToggle}
                disabled={actionLoading}
              >
                {actionLoading
                  ? "Loading..."
                  : isInProgress
                  ? "Currently Taking"
                  : "Add to Current Courses"}
              </button>
              <button
                className={`btn ${
                  isSaved ? "btn-danger" : "btn-outline-secondary"
                }`}
                onClick={handleSavedToggle}
                disabled={actionLoading}
              >
                {actionLoading
                  ? "Loading..."
                  : isSaved
                  ? "Saved"
                  : "Save for Later"}
              </button>
            </>
          ) : (
            <div className="text-center">
              <p className="text-muted mb-3">
                Please log in to interact with courses
              </p>
              <button
                className="btn btn-outline-danger"
                onClick={() => navigate("/login")}
              >
                Log In
              </button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default CourseAction;
