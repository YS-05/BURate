import { Link, useNavigate } from "react-router-dom";
import { useState } from "react";
import Logo from "../assets/buratelogo.png";
import "./Navbar.css";
import { useAuth } from "../auth/AuthProvider";

const UserNavBar = () => {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const toggleMenu = () => setIsMenuOpen(!isMenuOpen);
  const closeMenu = () => setIsMenuOpen(false);

  const handleSignOut = () => {
    logout();
    navigate("/");
  };

  return (
    <nav className="navbar bg-white border-bottom sticky-top m-0 p-0 px-lg-5">
      {/* NAVBAR HEADER */}
      <div className="container-fluid d-flex justify-content-between align-items-center py-2">
        {/* Logo */}
        <Link
          to="/dashboard"
          className="text-decoration-none d-flex align-items-center"
          onClick={closeMenu}
        >
          <img src={Logo} className="me-2" style={{ height: 30 }} />
          <span className="fw-bold h5 bu-red mb-0">BU Rate</span>
        </Link>

        {/* Hamburger (mobile only) */}
        <button
          className="btn d-lg-none"
          onClick={toggleMenu}
          aria-label="Toggle menu"
        >
          <i
            className={`bi ${isMenuOpen ? "bi-x-lg" : "bi-list"}`}
            style={{ fontSize: 28 }}
          ></i>
        </button>

        {/* Desktop menu */}
        <div className="d-none d-lg-flex align-items-center gap-4">
          <Link to="/dashboard" className="nav-link text-dark custom-nav-link">
            Dashboard
          </Link>
          <Link to="/search" className="nav-link text-dark custom-nav-link">
            Search Courses
          </Link>
          <Link to="/account" className="nav-link text-dark custom-nav-link">
            Account
          </Link>
          <Link to="/" className="btn btn-bu-red px-4" onClick={handleSignOut}>
            Sign Out
          </Link>
        </div>
      </div>

      {/* MOBILE MENU (FULL WIDTH) */}
      {isMenuOpen && (
        <div className="mobile-menu bg-white border-bottom d-lg-none w-100 px-4 py-3">
          <Link
            to="/search"
            className="d-block mb-3 text-dark"
            onClick={closeMenu}
          >
            Search Courses
          </Link>
          <Link
            to="/login"
            className="d-block mb-3 text-dark"
            onClick={closeMenu}
          >
            Login
          </Link>
          <Link
            to="/register"
            className="btn btn-bu-red w-100"
            onClick={closeMenu}
          >
            Sign Up
          </Link>
        </div>
      )}
    </nav>
  );
};

export default UserNavBar;
