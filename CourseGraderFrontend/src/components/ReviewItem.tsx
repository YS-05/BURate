import { ReviewResponseDTO } from "../auth/AuthDTOs";

interface Props {
  review: ReviewResponseDTO;
}

const ReviewItem = ({ review }: Props) => {
  return (
    <div className="border-bottom pb-3">
      <div className="d-flex flex-column flex-md-row justify-content-md-between align-items-md-center text-muted mb-3 gap-1">
        <div>
          Overall:{" "}
          <span className="fw-bold text-black">
            {review.overallRating.toFixed(1)}/5
          </span>
        </div>
        <div>
          Professor:{" "}
          <span className="fw-bold text-black">{review.teacherName}</span>
        </div>
        <div>
          Semester:{" "}
          <span className="fw-bold text-black">{review.semester}</span>
        </div>
        <div>
          Date:{" "}
          <span className="fw-bold text-black">
            {new Date(review.createdAt).toLocaleDateString("en-US", {
              year: "numeric",
              month: "long",
              day: "numeric",
            })}
          </span>
        </div>
      </div>

      <div className="d-flex flex-column flex-md-row justify-content-md-between align-items-md-center text-muted mb-3 gap-1">
        <div>
          Usefulness:{" "}
          <span className="fw-bold text-black">{review.usefulnessRating}</span>
        </div>
        <div>
          Difficulty:{" "}
          <span className="fw-bold text-black">{review.difficultyRating}</span>
        </div>
        <div>
          Workload:{" "}
          <span className="fw-bold text-black">{review.workloadRating}</span>
        </div>
        <div>
          Interest:{" "}
          <span className="fw-bold text-black">{review.interestRating}</span>
        </div>
        <div>
          Teacher:{" "}
          <span className="fw-bold text-black">{review.teacherRating}</span>
        </div>
      </div>

      <div className="d-flex flex-column flex-md-row justify-content-md-between align-items-md-center text-muted mb-3 gap-1">
        <div>
          Attendance needed:{" "}
          <span className="fw-bold text-black">
            {review.attendanceRequired ? "Yes" : "No"}
          </span>
        </div>
        <div>
          Hours per week:{" "}
          <span className="fw-bold text-black">{review.hoursPerWeek}</span>
        </div>
      </div>

      <div className="mb-3 text-muted">
        Assignment Types:{" "}
        <p className="fw-bold text-black mb-0">{review.assignmentTypes}</p>
      </div>

      <div className="mb-3 text-muted">
        Review: <p className="fw-bold text-black mb-0">{review.reviewText}</p>
      </div>
    </div>
  );
};

export default ReviewItem;
