import { BrowserRouter, Routes, Route } from "react-router-dom";
import { AuthProvider, useAuth } from "./auth/AuthProvider";

import NavbarLayout from "./layouts/NavbarLayout";
import SidebarLayout from "./layouts/SidebarLayout";

import Landing from "./pages/guest/Landing";
import Login from "./pages/guest/Login";
import Register from "./pages/guest/Register";
import Verify from "./pages/guest/Verify";
import Search from "./pages/shared/Search";

import Dashboard from "./pages/user/Dashboard";
import HubProgress from "./pages/user/HubProgress";
import MyCourses from "./pages/user/MyCourses";
import MyReviews from "./pages/user/MyReviews";
import SavedCourses from "./pages/user/SavedCourses";
import "./App.css";

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <AppRoutesBasedOnAuth />
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;

const AppRoutesBasedOnAuth = () => {
  const { user, loading } = useAuth();
  if (loading) {
    return (
      <div
        className="d-flex justify-content-center align-items-center"
        style={{ minHeight: "100vh" }}
      >
        <div className="spinner-border text-danger" role="status">
          <span className="visually-hidden">Loading...</span>
        </div>
      </div>
    );
  }

  return (
    <Routes>
      {user && user.enabled ? (
        <Route path="/" element={<SidebarLayout />}>
          <Route index element={<Dashboard />} />
          <Route path="dashboard" element={<Dashboard />} />
          <Route path="hub-progress" element={<HubProgress />} />
          <Route path="my-courses" element={<MyCourses />} />
          <Route path="saved-courses" element={<SavedCourses />} />
          <Route path="my-reviews" element={<MyReviews />} />
          <Route path="search" element={<Search />} />
          <Route path="*" element={<Dashboard />} />
        </Route>
      ) : (
        <Route path="/" element={<NavbarLayout />}>
          <Route index element={<Landing />} />
          <Route path="login" element={<Login />} />
          <Route path="register" element={<Register />} />
          <Route path="verify" element={<Verify />} />
          <Route path="search" element={<Search />} />
          <Route path="*" element={<Landing />} />
        </Route>
      )}
    </Routes>
  );
};
