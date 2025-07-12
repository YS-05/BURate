import { Link, useLocation } from "react-router-dom";
import { useAuth } from "../auth/AuthProvider";
import Logo from "../assets/BURateLogo.svg";

const Sidebar = () => {
  const { user, logout } = useAuth();
  const location = useLocation();

  const handleSignOut = () => {
    logout();
  };

  const navigationItems = [
    // can add icons to this later, optionally can add settings
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
      name: "My Reviews",
      path: "/my-reviews",
    },
    {
      name: "Search Courses",
      path: "/search",
    },
  ];

  return (
    <div
      className="d-flex flex-column border-end border-secondary"
      style={{ width: "30vw", minHeight: "100vh", backgroundColor: "#f5f5f5" }}
    >
      <div className="p-4 border-bottom border-secondary">
        <Link to="/dashboard" className="d-flex align-items-center">
          <img
            src={Logo}
            alt="BU Rate Logo"
            style={{ height: "60px", width: "auto" }}
            className="me-2"
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
                <span className="fw-medium text-danger">{item.name}</span>
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
  );
};

export default Sidebar;
