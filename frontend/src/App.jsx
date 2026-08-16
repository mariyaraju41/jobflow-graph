import { useState } from "react";

import Login from "./pages/Login";
import Register from "./pages/Register";
import Dashboard from "./pages/Dashboard";

import { logout } from "./services/api";

import "./index.css";


function App() {

  const [authenticated, setAuthenticated] =
    useState(
      !!localStorage.getItem("jobflow_token")
    );


  const [authPage, setAuthPage] =
    useState("login");


  const handleLogout = () => {

    logout();

    setAuthenticated(false);

    setAuthPage("login");
  };


  /*
   * ============================================================
   * AUTHENTICATED USER
   * ============================================================
   */

  if (authenticated) {

    return (
      <Dashboard
        onLogout={handleLogout}
      />
    );
  }


  /*
   * ============================================================
   * REGISTER PAGE
   * ============================================================
   */

  if (authPage === "register") {

    return (
      <Register
        onRegister={() =>
          setAuthPage("login")
        }
      />
    );
  }


  /*
   * ============================================================
   * LOGIN PAGE
   * ============================================================
   */

  return (
    <Login
      onLogin={() =>
        setAuthenticated(true)
      }

      onRegister={() =>
        setAuthPage("register")
      }
    />
  );
}


export default App;