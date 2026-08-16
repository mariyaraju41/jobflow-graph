import { useEffect, useRef, useState } from "react";
import { getProfile } from "../services/api";

function Navbar({ page, setPage, onLogout }) {

  const candidateId = localStorage.getItem("candidateId");

  const [profile, setProfile] = useState(null);
  const [menuOpen, setMenuOpen] = useState(false);

  const menuRef = useRef(null);

  useEffect(() => {

    if (!candidateId) return;

    getProfile(candidateId)
      .then((data) => {
        setProfile(data);
      })
      .catch((error) => {
        console.error("Profile loading error:", error);
      });

  }, [candidateId]);


  useEffect(() => {

    const handleOutsideClick = (event) => {

      if (
        menuRef.current &&
        !menuRef.current.contains(event.target)
      ) {
        setMenuOpen(false);
      }

    };

    document.addEventListener(
      "mousedown",
      handleOutsideClick
    );

    return () => {
      document.removeEventListener(
        "mousedown",
        handleOutsideClick
      );
    };

  }, []);


  const navigate = (target) => {

    setPage(target);
    setMenuOpen(false);

  };


  const logout = () => {

    setMenuOpen(false);
    onLogout();

  };


  const name =
    profile?.name || "Candidate";

  const email =
    profile?.email || "";

  const initial =
    name.charAt(0).toUpperCase();


  return (

    <header
      style={{
        height: "72px",
        background: "#ffffff",
        borderBottom: "1px solid #e5e7eb",
        display: "flex",
        alignItems: "center",
        padding: "0 42px",
        position: "relative",
        zIndex: 1000,
        boxSizing: "border-box"
      }}
    >

      {/* LOGO */}

      <div
        onClick={() => navigate("jobs")}
        style={{
          fontSize: "30px",
          fontWeight: "800",
          letterSpacing: "-1.5px",
          cursor: "pointer",
          color: "#171923",
          marginRight: "70px"
        }}
      >
        Job<span style={{ fontWeight: "500" }}>Flow</span>
      </div>


      {/* NAVIGATION */}

      <nav
        style={{
          display: "flex",
          alignItems: "center",
          gap: "8px",
          flex: 1
        }}
      >

        <NavButton
          active={page === "jobs"}
          onClick={() => navigate("jobs")}
        >
          Jobs
        </NavButton>


        <NavButton
          active={page === "resume"}
          onClick={() => navigate("resume")}
        >
          Resume
        </NavButton>


        <NavButton
          active={page === "applications"}
          onClick={() => navigate("applications")}
        >
          Applications
        </NavButton>
        <button
  type="button"
  onClick={() => setPage("search")}
>
  Search Jobs
</button>

      </nav>


      {/* PROFILE */}

      <div
        ref={menuRef}
        style={{
          position: "relative"
        }}
      >

        <button
          type="button"
          onClick={() =>
            setMenuOpen((value) => !value)
          }
          style={{
            border: "none",
            outline: "none",
            background: menuOpen
              ? "#f3f4f6"
              : "transparent",
            display: "flex",
            alignItems: "center",
            gap: "9px",
            padding: "6px 10px",
            borderRadius: "10px",
            cursor: "pointer",
            fontFamily: "inherit"
          }}
        >

          {/* AVATAR */}

          <span
            style={{
              width: "36px",
              height: "36px",
              minWidth: "36px",
              borderRadius: "50%",
              background: "#171923",
              color: "#ffffff",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              fontSize: "14px",
              fontWeight: "800"
            }}
          >
            {initial}
          </span>


          {/* NAME */}

          <span
            style={{
              fontSize: "13px",
              fontWeight: "700",
              color: "#171923",
              maxWidth: "150px",
              overflow: "hidden",
              textOverflow: "ellipsis",
              whiteSpace: "nowrap"
            }}
          >
            {name}
          </span>


          {/* ARROW */}

          <span
            style={{
              fontSize: "13px",
              color: "#777b87",
              transform: menuOpen
                ? "rotate(180deg)"
                : "rotate(0deg)",
              transition: "transform 0.2s"
            }}
          >
            ▼
          </span>

        </button>


        {/* DROPDOWN */}

        {menuOpen && (

          <div
            style={{
              position: "absolute",
              top: "48px",
              right: "0",
              width: "270px",
              background: "#ffffff",
              border: "1px solid #e1e3e8",
              borderRadius: "14px",
              boxShadow:
                "0 18px 45px rgba(0,0,0,0.15)",
              padding: "8px",
              zIndex: 99999,
              boxSizing: "border-box"
            }}
          >

            {/* USER HEADER */}

            <div
              style={{
                display: "flex",
                alignItems: "center",
                gap: "12px",
                padding: "12px"
              }}
            >

              <div
                style={{
                  width: "44px",
                  height: "44px",
                  minWidth: "44px",
                  borderRadius: "50%",
                  background: "#171923",
                  color: "#ffffff",
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                  fontSize: "17px",
                  fontWeight: "800"
                }}
              >
                {initial}
              </div>


              <div
                style={{
                  minWidth: 0
                }}
              >

                <div
                  style={{
                    fontSize: "14px",
                    fontWeight: "700",
                    color: "#171923",
                    marginBottom: "4px"
                  }}
                >
                  {name}
                </div>


                <div
                  style={{
                    fontSize: "11px",
                    color: "#858995",
                    overflow: "hidden",
                    textOverflow: "ellipsis",
                    whiteSpace: "nowrap",
                    maxWidth: "185px"
                  }}
                >
                  {email}
                </div>

              </div>

            </div>


            {/* DIVIDER */}

            <div
              style={{
                height: "1px",
                background: "#eeeef2",
                margin: "5px 2px"
              }}
            />


            {/* PROFILE */}

            <MenuButton
              icon="👤"
              text="Profile"
              onClick={() => navigate("profile")}
            />


            {/* RESUMES */}

            <MenuButton
              icon="📄"
              text="My Resumes"
              onClick={() => navigate("resume")}
            />


            {/* APPLICATIONS */}

            <MenuButton
              icon="📋"
              text="Applications"
              onClick={() =>
                navigate("applications")
              }
            />


            {/* DIVIDER */}

            <div
              style={{
                height: "1px",
                background: "#eeeef2",
                margin: "5px 2px"
              }}
            />


            {/* LOGOUT */}

            <MenuButton
              icon="↪"
              text="Logout"
              danger
              onClick={logout}
            />

          </div>

        )}

      </div>

    </header>
  );
}


/* ============================================================
   NAV BUTTON
   ============================================================ */

function NavButton({
  active,
  children,
  onClick
}) {

  return (

    <button
      type="button"
      onClick={onClick}
      style={{
        border: "none",
        background: active
          ? "#f0f1f5"
          : "transparent",
        color: active
          ? "#171923"
          : "#707481",
        fontSize: "14px",
        fontWeight: "700",
        padding: "11px 20px",
        borderRadius: "10px",
        cursor: "pointer",
        fontFamily: "inherit"
      }}
    >
      {children}
    </button>

  );
}


/* ============================================================
   DROPDOWN BUTTON
   ============================================================ */

function MenuButton({
  icon,
  text,
  onClick,
  danger = false
}) {

  const [hover, setHover] = useState(false);

  return (

    <button
      type="button"
      onClick={onClick}

      onMouseEnter={() => setHover(true)}
      onMouseLeave={() => setHover(false)}

      style={{
        width: "100%",
        border: "none",
        outline: "none",
        background: hover
          ? danger
            ? "#fff1f1"
            : "#f4f5f8"
          : "transparent",
        color: danger
          ? "#b04444"
          : "#444751",
        display: "flex",
        alignItems: "center",
        gap: "11px",
        padding: "11px 12px",
        margin: "2px 0",
        borderRadius: "9px",
        cursor: "pointer",
        textAlign: "left",
        fontFamily: "inherit",
        fontSize: "13px",
        fontWeight: "600"
      }}
    >

      <span
        style={{
          width: "22px",
          minWidth: "22px",
          textAlign: "center",
          fontSize: "16px"
        }}
      >
        {icon}
      </span>

      <span>
        {text}
      </span>

    </button>

  );
}


export default Navbar;