import { Link } from "react-router-dom";
import { useState } from "react";
import Logo from "../assets/BURateLogo.svg";
import "./Navbar.css";

const NavBar = () => {
  const [isMenuOpen, setIsMenuOpen] = useState(false);

  const toggleMenu = () => {
    setIsMenuOpen(!isMenuOpen);
  };

  const closeMenu = () => {
    setIsMenuOpen(false);
  };

  return (
    <nav className="navbar shadow-sm" style={{ backgroundColor: "#f5f5f5" }}>
      <div className="container d-flex justify-content-between align-items-center">
        <Link to="/" className="navbar-brand">
          <img
            src={Logo}
            alt="logo"
            style={{
              height: "60px",
            }}
          />
        </Link>

        {/* Desktop Menu */}
        <div className="d-none d-lg-flex align-items-center gap-3">
          <Link to="/search" className="nav-link text-dark m-2 custom-nav-link">
            Search Courses
          </Link>
          <Link
            to="/contact"
            className="nav-link text-dark m-2 custom-nav-link"
          >
            Contact
          </Link>
          <Link to="/login" className="nav-link text-dark m-2 custom-nav-link">
            Log In
          </Link>
          <Link
            to="/register"
            className="btn btn-outline-danger m-2"
            style={{ width: "120px", height: "40px" }}
          >
            Sign Up
          </Link>
        </div>

        {/* Mobile Burger Menu Button */}
        <button
          className="d-lg-none btn btn-link p-0 border-0 bg-transparent text-danger"
          type="button"
          onClick={toggleMenu}
          aria-label="Toggle navigation"
        >
          <svg
            width="24"
            height="24"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
            strokeLinecap="round"
            strokeLinejoin="round"
          >
            {isMenuOpen ? (
              // X icon when menu is open
              <>
                <line x1="18" y1="6" x2="6" y2="18"></line>
                <line x1="6" y1="6" x2="18" y2="18"></line>
              </>
            ) : (
              // Hamburger icon when menu is closed
              <>
                <line x1="3" y1="6" x2="21" y2="6"></line>
                <line x1="3" y1="12" x2="21" y2="12"></line>
                <line x1="3" y1="18" x2="21" y2="18"></line>
              </>
            )}
          </svg>
        </button>
      </div>

      {/* Mobile Menu Dropdown */}
      {isMenuOpen && (
        <div className="d-lg-none" style={{ backgroundColor: "#f5f5f5" }}>
          <div className="container py-3">
            <div className="d-flex flex-column gap-3">
              <Link
                to="/search"
                className="nav-link text-dark custom-nav-link"
                onClick={closeMenu}
              >
                Search Courses
              </Link>
              <Link
                to="/contact"
                className="nav-link text-dark custom-nav-link"
                onClick={closeMenu}
              >
                Contact
              </Link>
              <Link
                to="/login"
                className="nav-link text-dark custom-nav-link"
                onClick={closeMenu}
              >
                Log In
              </Link>
              <Link
                to="/register"
                className="nav-link text-dark custom-nav-link"
                onClick={closeMenu}
              >
                Sign Up
              </Link>
            </div>
          </div>
        </div>
      )}
    </nav>
  );
};

export default NavBar;
