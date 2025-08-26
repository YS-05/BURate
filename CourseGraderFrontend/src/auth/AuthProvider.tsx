import { createContext, useContext, useEffect, useState } from "react";
import { CourseDisplayDTO, HubRequirement } from "./AuthDTOs";
import api from "../api/axios";

export type User = {
  id: number;
  email: string;
  role: "STUDENT" | "ADMIN";
  enabled: boolean;
};

type AuthContextType = {
  user: User | null; // null = guest, User = logged in
  loading: boolean; // true = still checking /auth/me
  login: (token: string) => void; // call this after /login succeeds
  logout: () => void; // clears the JWT token from localstorage and resets to guest
};

const AuthContext = createContext<AuthContextType | undefined>(undefined); // AuthContext has 2 sub components, .Provider, .Consumer
export const AuthProvider = ({ children }: { children: React.ReactNode }) => {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  const login = async (token: string) => {
    localStorage.setItem("token", token);
    try {
      const res = await api.get("/auth/me");

      // Your original function checked res.ok, but axios throws on non-2xx status codes
      // so if we get here, the request was successful
      const data: User = res.data;
      setUser(data);
    } catch (err) {
      console.log("Failed to log in", err);
      logout(); // This handles the same cleanup as your original
    }
  };

  const logout = () => {
    localStorage.removeItem("token");
    setUser(null);
  };

  useEffect(() => {
    // Runs after component has rendered
    const token = localStorage.getItem("token");
    if (token) {
      login(token).finally(() => setLoading(false));
    } else {
      setLoading(false);
    }
  }, []);

  return (
    <AuthContext.Provider value={{ user, loading, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const ctx = useContext(AuthContext);
  if (!ctx) {
    throw new Error("useAuth must be used within <AuthProvider>");
  }
  return ctx;
};
