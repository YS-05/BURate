import { Link } from "react-router-dom";
import Logo from "../assets/BURateLogo.svg";
import "./Navbar.css";

const NavBar = () => {
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
        <div className="d-flex align-items-center gap-3">
          <Link to="/search" className="nav-link text-dark m-2 custom-nav-link">
            Search courses
          </Link>
          <Link to="/login" className="nav-link text-dark m-2 custom-nav-link">
            Log in
          </Link>
          <Link
            to="/register"
            className="btn btn-outline-danger m-2"
            style={{ width: "120px", height: "40px" }}
          >
            Sign Up
          </Link>
        </div>
      </div>
    </nav>
  );
};

export default NavBar;
