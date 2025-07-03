import React from "react";
import { Outlet } from "react-router-dom";
import Navbar from "../components/Navbar";

const NavbarLayout = () => {
  return (
    <>
      <Navbar />
      <main style={{ padding: "0" }}>
        <Outlet />
      </main>
    </>
  );
};

export default NavbarLayout;
