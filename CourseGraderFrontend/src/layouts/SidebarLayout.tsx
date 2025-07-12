import { Outlet } from "react-router-dom";
import Sidebar from "../components/Sidebar";

const SidebarLayout = () => {
  return (
    <div className="d-flex">
      <Sidebar />
      <main className="flex-grow-1" style={{ padding: "0", overflow: "auto" }}>
        <Outlet />
      </main>
    </div>
  );
};

export default SidebarLayout;
