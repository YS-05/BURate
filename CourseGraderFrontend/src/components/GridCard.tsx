import { useEffect, useState } from "react";
import Bookmark from "../assets/bookmark.svg";
import FilledBookmark from "../assets/bookmark-fill.svg";
import { useNavigate } from "react-router-dom";
import { CourseDisplayDTO } from "../auth/AuthDTOs";
import { useAuth } from "../auth/AuthProvider";
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

interface GridCardProps {
  course: CourseDisplayDTO;
}

const GridCard = ({ course }: GridCardProps) => {
  const navigate = useNavigate();
  const { user } = useAuth();

  const [isCompleted, setIsCompleted] = useState(false);
  const [isSaved, setIsSaved] = useState(false);
  const [isInProgress, setIsInProgress] = useState(false);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (user) {
      checkCourseStatus();
    }
  }, [user]);

  const checkCourseStatus = async () => {
    try {
      const [completed, saved, inProgress] = await Promise.all([
        fetchCompletedCourses(),
        fetchSavedCourses(),
        fetchCoursesInProgress(),
      ]);
      setIsCompleted(completed.some((c) => c.id === course.id));
      setIsSaved(saved.some((c) => c.id === course.id));
      setIsInProgress(inProgress.some((c) => c.id === course.id));
    } catch (err) {
      console.log("Failed to check status", err);
    }
  };

  const handleCompletedToggle = async () => {
    if (!user) {
      navigate("/login");
      return;
    }
    try {
      setLoading(true);
      if (isCompleted) {
        await removeCompletedCourse(course.id);
        setIsCompleted(false);
      } else {
        await addCompletedCourse(course.id);
        setIsCompleted(true);
      }
    } catch (err) {
      console.log("Failed to toggle completed courses", err);
    } finally {
      setLoading(false);
    }
  };

  const handleSavedToggle = async () => {
    if (!user) {
      navigate("/login");
      return;
    }
    try {
      setLoading(true);
      if (isSaved) {
        await removeSavedCourse(course.id);
        setIsSaved(false);
      } else {
        await addSavedCourse(course.id);
        setIsSaved(true);
      }
    } catch (error) {
      console.error("Failed to toggle saved status:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleInProgressToggle = async () => {
    if (!user) {
      navigate("/login");
      return;
    }
    try {
      setLoading(true);
      if (isInProgress) {
        await removeInProgressCourse(course.id);
        setIsInProgress(false);
      } else {
        await addInProgressCourse(course.id);
        setIsInProgress(true);
      }
    } catch (error) {
      console.error("Failed to toggle in progress status:", error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="card h-100 border-danger rounded-0">
      <div className="card-body">
        <div className="d-flex justify-content-between align-items-center mb-2">
          <div className="d-flex align-items-center">
            <img
              src={isSaved ? FilledBookmark : Bookmark}
              alt="Bookmark"
              style={{
                width: "18px",
                height: "18px",
                marginRight: "6px",
                cursor: "pointer",
              }}
              onClick={handleSavedToggle}
              title={
                user
                  ? isSaved
                    ? "Remove bookmark"
                    : "Bookmark course"
                  : "Login to bookmark"
              }
            />
            <h6 className="mb-0 fw-bold">
              {course.college} {course.department} {course.courseCode}
            </h6>
          </div>
          <div className="d-flex align-items-center gap-2">
            {course.noPreReqs && (
              <span className="badge bg-success-subtle text-success">
                No prereq
              </span>
            )}
            <span className="text-muted small">
              {course.numReviews}{" "}
              {course.numReviews === 1 ? "review" : "reviews"}
            </span>
          </div>
        </div>

        <div className="row text-center mb-2">
          <div className="col-4">
            <div className="small text-muted">Overall</div>
            <div className="fw-bold">
              {course.averageOverallRating.toFixed(1)}
            </div>
          </div>
          <div className="col-4">
            <div className="small text-muted">Useful</div>
            <div className="fw-bold">
              {course.averageUsefulnessRating.toFixed(1)}
            </div>
          </div>
          <div className="col-4">
            <div className="small text-muted">Difficulty</div>
            <div className="fw-bold">
              {course.averageDifficultyRating.toFixed(1)}
            </div>
          </div>
        </div>

        <div className="row text-center mb-3">
          <div className="col-4">
            <div className="small text-muted">Workload</div>
            <div className="fw-bold">
              {course.averageWorkloadRating.toFixed(1)}
            </div>
          </div>
          <div className="col-4">
            <div className="small text-muted">Interest</div>
            <div className="fw-bold">
              {course.averageInterestRating.toFixed(1)}
            </div>
          </div>
          <div className="col-4">
            <div className="small text-muted">Teacher</div>
            <div className="fw-bold">
              {course.averageTeacherRating.toFixed(1)}
            </div>
          </div>
          <div className="mb-1">
            <div className="small text-muted text-center mb-1">
              Fulfilling Hubs
            </div>
            <div className="d-flex flex-wrap justify-content-center gap-2">
              {course.hubRequirements.length > 0 ? (
                course.hubRequirements.map((hub, index) => (
                  <span key={index} className="badge bg-danger text-light">
                    {hub.name}
                  </span>
                ))
              ) : (
                <span className="badge bg-secondary text-light">N/A</span>
              )}
            </div>
          </div>
        </div>
        <div className="d-flex flex-column gap-2">
          <div className="d-flex gap-2">
            <button
              className="btn btn-sm flex-fill"
              style={{
                backgroundColor: "#495057" /* Bootstrap dark gray */,
                borderColor: "#495057",
                color: "white",
              }}
              onClick={handleCompletedToggle}
              disabled={loading}
            >
              {loading ? "..." : isCompleted ? "Remove Done" : "Mark Done"}
            </button>
            <button
              className="btn btn-outline-secondary btn-sm flex-fill"
              onClick={handleInProgressToggle}
              disabled={loading}
            >
              {loading ? "..." : isInProgress ? "End Future" : "Add Future"}
            </button>
          </div>
          <button
            className="btn btn-danger btn-sm w-100"
            onClick={() => navigate(`/course/${course.id}`)}
          >
            View Course
          </button>
        </div>
      </div>
    </div>
  );
};

export default GridCard;
