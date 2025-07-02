import { createContext, useContext, useEffect, useState } from "react";

export type HubRequirementCode =
  | "PLM"
  | "AEX"
  | "HCO"
  | "SI1"
  | "SI2"
  | "SO1"
  | "SO2"
  | "QR1"
  | "QR2"
  | "IIC"
  | "GCI"
  | "ETR"
  | "FYW"
  | "WRI"
  | "WIN"
  | "OSC"
  | "DME"
  | "CRT"
  | "RIL"
  | "TWC"
  | "CRI";

export type CourseDisplay = {
  id: string;
  title: string;
  college: string;
  department: string;
  courseCode: string;
  noPreReqs: boolean;
  numReviews: number;
  averageOverallRating: number;
  averageUsefulnessRating: number;
  averageDifficultyRating: number;
  averageWorkloadRating: number;
  averageInterestRating: number;
  averageTeacherRating: number;
};

export type User = {
  id: number;
  email: string;
  role: "STUDENT" | "ADMIN";
  enabled: boolean;
  completedCourses: CourseDisplay[];
  hubsCompleted: Record<HubRequirementCode, number>;
};

type AuthContextType = {
  user: User | null; // null = guest, User = logged in
  loading: boolean; // true = still checking /auth/me
  login: (token: string) => void; // call this after /login succeeds
  logout: () => void; // clears the JWT and resets to guest
};

const AuthContext = createContext<AuthContextType | undefined>(undefined); // AuthContext has 2 sub components, .Provider, .Consumer
export const AuthProvider = ({ children }: { children: React.ReactNode }) => {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  const login = async (token: string) => {
    localStorage.setItem("token", token);
    try {
      const res = await fetch("http://localhost:8080/api/auth/me", {
        // Replace with actual url when deploying
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
