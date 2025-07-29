import React from "react";
import RatingDisplay from "./RatingDisplay";

interface Props {
  overallRating: number | undefined;
  usefulRating: number | undefined;
  difficultRating: number | undefined;
  workloadRating: number | undefined;
  interestRating: number | undefined;
  teacherRating: number | undefined;
}

const RatingsOverview = ({
  overallRating,
  usefulRating,
  difficultRating,
  workloadRating,
  interestRating,
  teacherRating,
}: Props) => {
  return (
    <div className="card rounded-0 border-danger mb-4">
      <div className="card-header">
        <h5 className="mb-0">Ratings overview</h5>
      </div>
      <div className="card-body">
        <div className="row text-center">
          <div className="col-md-2">
            <RatingDisplay name="Overall" rating={overallRating} />
          </div>
          <div className="col-md-2">
            <RatingDisplay name="Usefulness" rating={usefulRating} />
          </div>
          <div className="col-md-2">
            <RatingDisplay name="Difficulty" rating={difficultRating} />
          </div>
          <div className="col-md-2">
            <RatingDisplay name="Workload" rating={workloadRating} />
          </div>
          <div className="col-md-2">
            <RatingDisplay name="Interest" rating={interestRating} />
          </div>
          <div className="col-md-2">
            <RatingDisplay name="Teacher" rating={teacherRating} />
          </div>
        </div>
      </div>
    </div>
  );
};

export default RatingsOverview;
