import { Link, useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "../auth/AuthProvider";
import Logo from "../assets/BURateLogo.svg";
import House from "../assets/house.svg";
import Chart from "../assets/bar-chart.svg";
import Book from "../assets/book.svg";
import Bookmark from "../assets/bookmark.svg";
import Pen from "../assets/pen.svg";
import Search from "../assets/search.svg";

const Sidebar = () => {
  const { user, logout } = useAuth();
  const location = useLocation();
  const navigate = useNavigate();

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
    // can add icons to this later, optionally can add settings
    {
      name: "Dashboard",
      path: "/dashboard",
      icon: House,
    },
    {
      name: "Hub Progress",
      path: "/hub-progress",
      icon: Chart,
    },
    {
      name: "My Courses",
      path: "/my-courses",
      icon: Book,
    },
    {
      name: "Saved Courses",
      path: "/saved-courses",
      icon: Bookmark,
    },
    {
      name: "My Reviews",
      path: "/my-reviews",
      icon: Pen,
    },
    {
      name: "Search Courses",
      path: "/search",
      icon: Search,
    },
  ];

  return (
    <div
      className="d-flex flex-column border-end border-secondary"
      style={{
        minWidth: "250px",
        minHeight: "100vh",
        backgroundColor: "#f5f5f5",
      }}
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
                <img
                  src={item.icon}
                  alt={`${item.name} icon`}
                  style={{ height: "25px", width: "25px" }}
                  className="me-4"
                />
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
