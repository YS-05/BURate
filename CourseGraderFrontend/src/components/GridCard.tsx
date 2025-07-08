import React from "react";
import Bookmark from "../assets/bookmark.svg";

interface Course {
  id: string;
  title: string;
  college: string;
  department: string;
  courseCode: string;
  noPreReqs: boolean;
  numReviews: number;
  averageOverallRating: number;
  averageUsefulnessRating: number;
  averageDifficultyRating: number;
  averageWorkloadRating: number;
  averageInterestRating: number;
  averageTeacherRating: number;
}

interface GridCardProps {
  course: Course;
}

const GridCard = ({ course }: GridCardProps) => {
  return (
    <div className="card h-100 border-danger">
      <div className="card-body">
        <div className="d-flex justify-content-between align-items-center mb-2">
          <div className="d-flex align-items-center">
            <img
              src={Bookmark}
              alt="Bookmark"
              style={{ width: "18px", height: "18px", marginRight: "6px" }}
            />
            <h6 className="mb-0 fw-bold">
              {course.college} {course.department} {course.courseCode}
            </h6>
          </div>
          <div className="d-flex align-items-center gap-2">
            {course.noPreReqs && (
              <span className="badge bg-info">No Pre-reqs</span>
            )}
            <span className="text-muted small">
              {course.numReviews} reviews
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
        </div>
        <div className="d-flex gap-2">
          <button className="btn btn-info btn-sm flex-fill">View Course</button>
          <button className="btn btn-info btn-sm flex-fill">Add Course</button>
        </div>
      </div>
    </div>
  );
};

export default GridCard;
