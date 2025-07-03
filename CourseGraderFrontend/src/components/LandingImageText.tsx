import React from "react";
import LandingImg from "../assets/landingpageimg.jpg";
import { Link } from "react-router-dom";

const LandingImageText = () => {
  return (
    <div style={{ position: "relative" }}>
      <img
        src={LandingImg}
        style={{
          width: "100%",
          height: "60vh",
          objectFit: "cover",
          filter: "brightness(0.4)",
        }}
        alt="Landing page"
      />
      <div
        style={{
          position: "absolute",
          top: "50%",
          left: "50%",
          transform: "translate(-50%, -50%)",
          textAlign: "center",
          color: "white",
          width: "90%",
          maxWidth: "600px",
          padding: "0 1rem", // Add some padding for small screens
        }}
      >
        <h1 style={{ fontSize: "clamp(1.5rem, 5vw, 2.5rem)" }}>
          Select your next Boston University course
        </h1>
        <p style={{ fontSize: "clamp(0.9rem, 3.5vw, 1.2rem)" }}>
          Click the button below to start searching courses by the Hub
          Requirements they fulfill, whether they have prerequisites, their
          ratings and much more!
        </p>
        <Link
          to="/search"
          className="btn btn-danger mt-3"
          style={{ fontSize: "1.1rem", padding: "0.75rem 2rem" }}
        >
          Start Searching
        </Link>
      </div>
    </div>
  );
};

export default LandingImageText;
