import React from "react";
import { useAuth } from "../../auth/AuthProvider";

const Dashboard = () => {
  const { user } = useAuth();

  return (
    <div
      className="d-flex justify-content-center p-5"
      style={{
        backgroundColor: "#f5f5f5",
        height: "100vh",
      }}
    >
      <h2
        className="text-center"
        style={{
          fontSize: "clamp(1rem, 2.5vw, 2.5rem)",
          lineHeight: "1.2",
        }}
      >
        Welcome {user?.email}
      </h2>
    </div>
  );
};

export default Dashboard;
