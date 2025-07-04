import React from "react";
import { Outlet } from "react-router-dom";
import Navbar from "../components/Navbar";
import Footer from "../components/Footer";

const NavbarLayout = () => {
  return (
    <>
      <Navbar />
      <main style={{ padding: "0" }}>
        <Outlet />
      </main>
      <Footer />
    </>
  );
};

export default NavbarLayout;
