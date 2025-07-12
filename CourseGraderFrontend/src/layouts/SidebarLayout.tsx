import { Outlet } from "react-router-dom";
import Sidebar from "../components/Sidebar";

const SidebarLayout = () => {
  return (
    <>
      <Sidebar />
      <main style={{ padding: "0" }}>
        <Outlet />
      </main>
    </>
  );
};

export default SidebarLayout;
