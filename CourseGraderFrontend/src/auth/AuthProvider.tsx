import { createContext, useContext, useEffect, useState } from "react";
import { CourseDisplayDTO, HubRequirement } from "./AuthDTOs";

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
      const res = await fetch("http://localhost:8080/api/auth/me", {
        // Replace with actual url when deploying instead of localhost
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (!res.ok) {
        // Checks if response is 200-299 status code
        logout();
        return;
      }

      const data: User = await res.json();
      setUser(data);
    } catch (err) {
      console.log("Failed to log in", err);
      logout();
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
