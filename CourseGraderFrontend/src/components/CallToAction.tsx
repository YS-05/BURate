import React from "react";
import { Link } from "react-router-dom";

const CallToAction = () => {
  return (
    <div className="bg-bu-red py-5">
      <div className="container py-5 text-center">
        <h2 className="fw-bold mb-3 display-5">
          Ready to Make Better Course Decisions?
        </h2>
        <p className=" mb-3">
          Join the list of BU students who are using real reviews to shape their
          academic journey today.
        </p>
        <div className="d-flex flex-column flex-sm-row justify-content-center align-items-center gap-2">
          <Link to="/register" className="btn btn-light px-3 m-0">
            <span className="bu-red me-2">Get Started Free</span>
            <i className="bi bi-arrow-right bu-red"></i>
          </Link>

          <Link to="/search" className="btn btn-light px-3 m-0">
            <span className="bu-red">Explore Courses</span>
          </Link>
        </div>
        <p className="mt-3 mb-0">No credit card required • Free forever</p>
      </div>
    </div>
  );
};

export default CallToAction;
