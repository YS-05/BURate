import React from "react";

interface Props {
  ratings: number[];
  onRatingChange: (index: number, value: number) => void;
}

const CourseRatings = ({ ratings, onRatingChange }: Props) => {
  const ratingLabels = [
    "Usefulness:",
    "Difficulty:",
    "Workload:",
    "Interest:",
    "Teacher Score:",
  ];

  return (
    <>
      <h5 className="mb-5" style={{ color: "#e57373" }}>
        Course Ratings
      </h5>
      {ratingLabels.map((label, index) => (
        <div key={label} className="mb-0">
          <label className="form-label fw-semibold">{label}</label>
          <div className="d-flex flex-column align-items-center">
            <div className="d-flex gap-2 align-items-center justify-content-center">
              {[1, 2, 3, 4, 5].map((rating) => (
                <button
                  key={rating}
                  type="button"
                  className={`btn mx-md-4 ${
                    ratings[index] === rating
                      ? "btn-danger"
                      : "btn-outline-secondary"
                  }`}
                  onClick={() => onRatingChange(index, rating)}
                  style={{ width: "40px", height: "40px" }}
                >
                  {rating}
                </button>
              ))}
            </div>
            <span className="text-muted mt-2">
              {ratings[index] === 1
                ? "Very low"
                : ratings[index] === 2
                ? "Low"
                : ratings[index] === 3
                ? "Medium"
                : ratings[index] === 4
                ? "High"
                : ratings[index] === 5
                ? "Very high"
                : ""}
            </span>
          </div>
        </div>
      ))}
    </>
  );
};

export default CourseRatings;
