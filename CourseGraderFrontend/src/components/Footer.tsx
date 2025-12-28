import React from "react";
import { Link } from "react-router-dom";

const Footer = () => {
  return (
    <div className="container pt-5">
      <div className="row">
        <div className="col-md-6">
          <h5 className="mb-3">BU Rate</h5>
          <p className="text-muted">
            Empowering BU students to make informed course decisions.
          </p>

          <h5 className="mb-3">Contact Information</h5>
          <ul className="list-unstyled">
            <li className="mt-1 text-muted">buratesupport@gmail.com</li>
            <li className="mt-1">
              <a
                href="https://www.linkedin.com/in/yash-sharma-ys05/"
                target="_blank"
                rel="noopener noreferrer"
                className="text-decoration-none text-muted"
              >
                <i className="fab fa-linkedin"></i>
                LinkedIn
              </a>
            </li>
          </ul>
        </div>
        <div className="col-md-3">
          <h5 className="mb-3">Quick Links</h5>
          <ul className="list-unstyled">
            <li className="mt-1">
              <Link to="/" className="text-decoration-none text-muted">
                Home
              </Link>
            </li>
            <li className="mt-1">
              <Link to="/search" className="text-decoration-none text-muted">
                Search
              </Link>
            </li>
            <li className="mt-1">
              <Link to="/register" className="text-decoration-none text-muted">
                Register
              </Link>
            </li>
            <li className="mt-1">
              <Link to="/login" className="text-decoration-none text-muted">
                Login
              </Link>
            </li>
          </ul>
        </div>
        <div className="col-md-3">
          <h5 className="mb-3">Legal</h5>
          <ul className="list-unstyled">
            <li className="mt-1">
              <Link to="/privacy" className="text-decoration-none text-muted">
                Privacy
              </Link>
            </li>
            <li className="mt-1">
              <Link to="/terms" className="text-decoration-none text-muted">
                Terms
              </Link>
            </li>
            <li className="mt-1">
              <Link to="/contact" className="text-decoration-none text-muted">
                Contact
              </Link>
            </li>
          </ul>
        </div>
      </div>
      <p className="text-center my-4">© 2025 BU Rate. All rights reserved.</p>
    </div>
  );
};

export default Footer;
