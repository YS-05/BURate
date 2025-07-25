import { useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "../auth/AuthProvider";
import Logo from "../assets/BURateLogo.svg";

const Sidebar = () => {
  const { user, logout } = useAuth();
  const location = useLocation();
  const navigate = useNavigate();
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);

  const handleSignOut = () => {
    logout();
    navigate("/");
  };

  const isActive = (path: string) => {
    if (
      path === "/dashboard" &&
      (location.pathname === "/" || location.pathname === "/dashboard")
    ) {
      return true;
    }
    if (path !== "/dashboard" && location.pathname.startsWith(path)) {
      return true;
    }
    return false;
  };

  const navigationItems = [
    {
      name: "Dashboard",
      path: "/dashboard",
    },
    {
      name: "Hub Progress",
      path: "/hub-progress",
    },
    {
      name: "My Courses",
      path: "/my-courses",
    },
    {
      name: "Saved Courses",
      path: "/saved-courses",
    },
    {
      name: "My Reviews",
      path: "/my-reviews",
    },
    {
      name: "Search Courses",
      path: "/search",
    },
    {
      name: "Account",
      path: "/account",
    },
  ];

  const toggleMobileMenu = () => {
    setIsMobileMenuOpen(!isMobileMenuOpen);
  };

  const closeMobileMenu = () => {
    setIsMobileMenuOpen(false);
  };

  return (
    <>
      {/* Mobile Header - Only visible on mobile/tablet */}
      <div
        className="d-lg-none position-fixed w-100 d-flex align-items-center justify-content-between px-3 border-bottom border-secondary"
        style={{
          height: "70px",
          backgroundColor: "#f5f5f5",
          top: 0,
          left: 0,
          zIndex: 1030,
        }}
      >
        {/* Hamburger Menu Button */}
        <button
          className="btn p-2"
          onClick={toggleMobileMenu}
          style={{
            backgroundColor: "transparent",
            border: "1px solid #dee2e6",
            borderRadius: "8px",
          }}
          aria-label="Toggle navigation menu"
        >
          {isMobileMenuOpen ? (
            // Close icon
            <svg
              width="24"
              height="24"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="2"
            >
              <line x1="18" y1="6" x2="6" y2="18"></line>
              <line x1="6" y1="6" x2="18" y2="18"></line>
            </svg>
          ) : (
            // Hamburger icon
            <svg
              width="24"
              height="24"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="2"
            >
              <line x1="3" y1="6" x2="21" y2="6"></line>
              <line x1="3" y1="12" x2="21" y2="12"></line>
              <line x1="3" y1="18" x2="21" y2="18"></line>
            </svg>
          )}
        </button>

        {/* Logo in mobile header */}
        <Link to="/dashboard" className="d-flex align-items-center">
          <img
            src={Logo}
            alt="BU Rate Logo"
            style={{ height: "45px", width: "auto" }}
          />
        </Link>

        {/* Empty div for spacing */}
        <div style={{ width: "48px" }}></div>
      </div>

      {/* Overlay for mobile menu */}
      {isMobileMenuOpen && (
        <div
          className="d-lg-none position-fixed w-100 h-100"
          style={{
            top: 0,
            left: 0,
            backgroundColor: "rgba(0, 0, 0, 0.5)",
            zIndex: 1040,
          }}
          onClick={closeMobileMenu}
        />
      )}

      {/* Desktop Sidebar - Always visible on large screens */}
      <div
        className="d-none d-lg-flex flex-column border-end border-secondary"
        style={{
          minWidth: "250px",
          minHeight: "100vh",
          backgroundColor: "#f5f5f5",
        }}
      >
        <div className="p-4 border-bottom border-secondary d-flex justify-content-center">
          <Link to="/dashboard" className="d-flex align-items-center">
            <img
              src={Logo}
              alt="BU Rate Logo"
              style={{ height: "60px", width: "auto" }}
            />
          </Link>
        </div>
        <nav className="flex-grow-1 p-3">
          <ul className="list-unstyled mb-0">
            {navigationItems.map((item) => (
              <li key={item.path} className="mb-2">
                <Link
                  to={item.path}
                  className="d-flex align-items-center p-3 rounded text-decoration-none transition"
                >
                  <span
                    className={`fs-5 ${
                      isActive(item.path) ? "text-danger" : "text-dark"
                    }`}
                  >
                    {item.name}
                  </span>
                </Link>
              </li>
            ))}
          </ul>
        </nav>
        <div className="p-3 border-top border-secondary">
          <button
            onClick={handleSignOut}
            className="btn btn-danger w-100 d-flex align-items-center justify-content-center"
          >
            Sign Out
          </button>
        </div>
      </div>

      {/* Mobile Sidebar - Slides in from left */}
      <div
        className={`d-lg-none position-fixed d-flex flex-column border-end border-secondary`}
        style={{
          minWidth: "250px",
          height: "100vh",
          backgroundColor: "#f5f5f5",
          top: 0,
          left: 0,
          zIndex: 1045,
          transform: isMobileMenuOpen ? "translateX(0)" : "translateX(-100%)",
          transition: "transform 0.3s ease-in-out",
        }}
      >
        {/* Mobile sidebar header - just for spacing */}
        <div style={{ height: "70px" }}></div>

        <nav className="flex-grow-1 p-3">
          <ul className="list-unstyled mb-0">
            {navigationItems.map((item) => (
              <li key={item.path} className="mb-2">
                <Link
                  to={item.path}
                  className="d-flex align-items-center p-3 rounded text-decoration-none transition"
                  onClick={closeMobileMenu}
                >
                  <span
                    className={`fw-medium ${
                      isActive(item.path) ? "text-danger" : "text-dark"
                    }`}
                  >
                    {item.name}
                  </span>
                </Link>
              </li>
            ))}
          </ul>
        </nav>
        <div className="p-3 border-top border-secondary">
          <button
            onClick={() => {
              handleSignOut();
              closeMobileMenu();
            }}
            className="btn btn-danger w-100 d-flex align-items-center justify-content-center"
          >
            Sign Out
          </button>
        </div>
      </div>
    </>
  );
};

export default Sidebar;
