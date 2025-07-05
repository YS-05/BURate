import { BrowserRouter, Routes, Route } from "react-router-dom";
import { AuthProvider, useAuth } from "./auth/AuthProvider";

import NavbarLayout from "./layouts/NavbarLayout";
import SidebarLayout from "./layouts/SidebarLayout";

import Landing from "./pages/guest/Landing";
import Login from "./pages/guest/Login";
import Register from "./pages/guest/Register";
import Verify from "./pages/guest/Verify";
import Search from "./pages/guest/Search";

import Dashboard from "./pages/user/Dashboard";

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
    return <div>Loading...</div>;
  }

  return (
    <Routes>
      {user && user.enabled ? (
        <Route path="/" element={<SidebarLayout />}>
          <Route path="dashboard" element={<Dashboard />} />
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
