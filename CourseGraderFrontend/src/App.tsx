import { BrowserRouter, Routes, Route } from "react-router-dom";
import { AuthProvider, useAuth } from "./auth/AuthProvider";

import NavbarLayout from "./layouts/NavbarLayout";
import UserNavbarLayout from "./layouts/UserNavbarLayout";

import Landing from "./pages/guest/Landing";
import Login from "./pages/guest/Login";
import Register from "./pages/guest/Register";
import Verify from "./pages/guest/Verify";
import Search from "./pages/shared/Search";

import Dashboard from "./pages/user/Dashboard";
import MyReviews from "./pages/user/MyReviews";
import Account from "./pages/user/Account";
import "./App.css";
import CoursePage from "./pages/shared/CoursePage";
import Review from "./pages/user/Review";
import Contact from "./pages/guest/Contact";
import Privacy from "./pages/guest/Privacy";
import Terms from "./pages/guest/Terms";
import Reset from "./pages/guest/Reset";
import SetPassword from "./pages/guest/SetPassword";
import AskAi from "./pages/user/AskAi";
import AllReviews from "./pages/admin/AllReviews";
import AdminDashboard from "./pages/admin/AdminDashboard";

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
      {user && user.role === "ADMIN" ? (
        <Route path="/" element={<UserNavbarLayout />}>
          <Route index element={<Dashboard />} />
          <Route path="dashboard" element={<Dashboard />} />
          <Route path="all-reviews" element={<AllReviews />} />
          <Route path="admin-commands" element={<AdminDashboard />} />
          <Route path="my-reviews" element={<MyReviews />} />
          <Route path="search" element={<Search />} />
          <Route path="ask-ai" element={<AskAi />} />
          <Route path="course/:courseId" element={<CoursePage />} />
          <Route path="course/:courseId/review" element={<Review />} />
          <Route path="account" element={<Account />} />
          <Route path="*" element={<Dashboard />} />
        </Route>
      ) : user && user.enabled ? (
        <Route path="/" element={<UserNavbarLayout />}>
          <Route index element={<Dashboard />} />
          <Route path="dashboard" element={<Dashboard />} />
          <Route path="my-reviews" element={<MyReviews />} />
          <Route path="search" element={<Search />} />
          <Route path="ask-ai" element={<AskAi />} />
          <Route path="course/:courseId" element={<CoursePage />} />
          <Route path="course/:courseId/review" element={<Review />} />
          <Route path="account" element={<Account />} />
          <Route path="*" element={<Dashboard />} />
        </Route>
      ) : (
        <Route path="/" element={<NavbarLayout />}>
          <Route index element={<Landing />} />
          <Route path="login" element={<Login />} />
          <Route path="register" element={<Register />} />
          <Route path="verify" element={<Verify />} />
          <Route path="reset" element={<Reset />}></Route>
          <Route path="reset-password" element={<SetPassword />} />
          <Route path="search" element={<Search />} />
          <Route path="course/:courseId" element={<CoursePage />} />
          <Route path="contact" element={<Contact />} />
          <Route path="privacy" element={<Privacy />} />
          <Route path="terms" element={<Terms />} />
          <Route path="*" element={<Landing />} />
        </Route>
      )}
    </Routes>
  );
};
