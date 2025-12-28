import React from "react";
import { Link } from "react-router-dom";
import Image from "../assets/LandingImg.jpg";

const LandingHeader = () => {
  return (
    <div className="container my-5 py-5">
      <div className="row align-items-center">
        <div className="col-lg-6">
          <h1 className="fw-bold mb-4 display-3">
            Find the Best <span className="bu-red">Courses at BU</span>
          </h1>
          <p className="text-muted mb-4">
            Read honest reviews from fellow BU Students. Make informed decisions
            about your courses with insights from students who've been there.
          </p>
          <Link to="/search" className="btn btn-bu-red px-4 m-2 ms-0">
            <span className="fw-bold me-3">Browse Reviews</span>
            <i className="bi bi-arrow-right"></i>
          </Link>
          <Link to="/register" className="btn btn-light px-4 m-2 ms-0">
            <span>Share Your Experience</span>
          </Link>
          <h2 className="fw-bold bu-red mt-4 mb-0">7500+</h2>
          <p className="text-muted">Courses available to search from</p>
        </div>
        <div className="col-lg-6">
          <img
            src={Image}
            alt="Landing Image"
            className="img-fluid rounded-4 golden-shadow"
          />
        </div>
      </div>
    </div>
  );
};

export default LandingHeader;
