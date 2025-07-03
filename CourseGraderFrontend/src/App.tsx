import { BrowserRouter, Routes, Route } from "react-router-dom";
import { AuthProvider, useAuth } from "./auth/AuthProvider";

import NavbarLayout from "./layouts/NavbarLayout";
import SidebarLayout from "./layouts/SidebarLayout";

import Landing from "./pages/guest/Landing";
import Login from "./pages/guest/Login";
import Register from "./pages/guest/Register";

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
    return <div>Loading...</div>; // Can add a spinner later
  }
  return (
    <Routes>
      {user && user.enabled ? (
        // sidebar layout for verified users
        <Route element={<SidebarLayout />}>
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="*" element={<Dashboard />} /> {/* fallback */}
        </Route>
      ) : (
        // navbar layout for guest and unverified users
        <Route element={<NavbarLayout />}>
          <Route path="/" element={<Landing />} />
          <Route path="login" element={<Login />} />
          <Route path="register" element={<Register />} />
          <Route path="*" element={<Landing />} /> {/* fallback */}
        </Route>
      )}
    </Routes>
  );
};
