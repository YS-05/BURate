import React from "react";

interface Props {
  error: string | null;
}

const ErrorDisplay = ({ error }: Props) => {
  return (
    <div className="container mt-5">
      <div className="alert alert-bu-red" role="alert">
        Error loading: {error}
      </div>
    </div>
  );
};

export default ErrorDisplay;
