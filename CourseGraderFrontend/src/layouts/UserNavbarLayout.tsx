import { Outlet } from "react-router-dom";
import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import UserNavBar from "../components/UserNavBar";

const UserNavbarLayout = () => {
  return (
    <>
      <UserNavBar />
      <main style={{ padding: "0" }}>
        <Outlet />
      </main>
    </>
  );
};

export default UserNavbarLayout;
