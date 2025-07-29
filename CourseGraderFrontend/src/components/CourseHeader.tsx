import React from "react";

interface Props {
  courseCollege: string | undefined;
  courseDepartment: string | undefined;
  courseCode: string | undefined;
  numReviews: number | undefined;
  noPreReqs: boolean | undefined;
  courseTitle: string | undefined;
}

const CourseHeader = ({
  courseCollege,
  courseDepartment,
  courseCode,
  numReviews,
  noPreReqs,
  courseTitle,
}: Props) => {
  return (
    <div className="card border-danger rounded-0 mb-4">
      <div className="card-header bg-danger text-white rounded-0">
        <div className="row align-items-center">
          <div className="col-md-8">
            <h1 className="mb-0">
              {courseCollege} {courseDepartment} {courseCode}
            </h1>
            <h4 className="mb-0 mt-2 fw-normal">{courseTitle}</h4>
          </div>
          <div className="col-md-4 text-end">
            {noPreReqs && (
              <span className="badge bg-light text-dark fs-6 m-2">
                No Prerequisites
              </span>
            )}
            <span className="badge bg-light text-dark fs-6 m-2">
              {numReviews} {numReviews === 1 ? "review" : "reviews"}
            </span>
          </div>
        </div>
      </div>
    </div>
  );
};

export default CourseHeader;
