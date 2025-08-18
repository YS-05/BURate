import React from "react";
import { Link } from "react-router-dom";

const Footer = () => {
  return (
    <footer className="bg-dark text-center py-4" style={{ height: "140px" }}>
      <div className="d-flex justify-content-center flex-wrap gap-4 mb-2">
        <Link to="/contact" className="text-secondary text-decoration-none">
          Contact Us
        </Link>
        <Link to="/privacy" className="text-secondary text-decoration-none">
          Privacy Policy
        </Link>
        <Link to="/terms" className="text-secondary text-decoration-none">
          Terms of Service
        </Link>
      </div>

      <small className="text-white mt-3 d-block">© 2025 BU Rate</small>
    </footer>
  );
};

export default Footer;
