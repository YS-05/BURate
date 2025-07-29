import React from "react";

interface Props {
  name: string | undefined;
  rating: number | undefined;
}

const RatingDisplay = ({ name, rating }: Props) => {
  return (
    <>
      <div className="display-6 fw-bold" style={{ color: "#e57373" }}>
        {rating?.toFixed(1) || "0.0"}
      </div>
      <small className="text-muted">{name}</small>
    </>
  );
};

export default RatingDisplay;
