import { useState } from "react";
import { login } from "../services/api";


function Login({ onLogin, onRegister }) {

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");


  const handleSubmit = async (e) => {

    e.preventDefault();

    try {

      setLoading(true);
      setError("");

      const data =
        await login(email, password);


      /*
       * JWT is stored by api.js.
       * Store candidateId for the
       * current logged-in user.
       */

      if (data?.candidateId) {

        localStorage.setItem(
          "candidateId",
          data.candidateId
        );

      }


      onLogin();


    } catch (err) {

      console.error(
        "Login failed:",
        err
      );


      setError(
        err.response?.data?.message ||
        "Invalid email or password."
      );


    } finally {

      setLoading(false);

    }
  };


  return (

    <div className="login-page">

      <div className="login-card">


        {/* =====================================================
            BRAND
            ===================================================== */}

        <div className="login-brand">

          <div className="login-logo">
            Job<span>Flow</span>
          </div>

          <div className="login-badge">
            AI JOB MATCHING
          </div>

        </div>


        {/* =====================================================
            HEADING
            ===================================================== */}

        <div className="login-heading">

          <h1>
            Welcome back
          </h1>

          <p>
            Sign in to discover jobs that
            match your skills.
          </p>

        </div>


        {/* =====================================================
            ERROR
            ===================================================== */}

        {error && (

          <div className="login-error">

            <span>!</span>

            {error}

          </div>

        )}


        {/* =====================================================
            LOGIN FORM
            ===================================================== */}

        <form
          className="login-form"
          onSubmit={handleSubmit}
        >


          {/* EMAIL */}

          <div className="login-field">

            <label>
              Email address
            </label>

            <input
              type="email"
              placeholder="demo@jobflow.com"
              value={email}
              onChange={(e) =>
                setEmail(e.target.value)
              }
              autoComplete="email"
              required
            />

          </div>


          {/* PASSWORD */}

          <div className="login-field">

            <div className="password-label">

              <label>
                Password
              </label>

              <span>
                Secure login
              </span>

            </div>


            <input
              type="password"
              placeholder="Enter your password"
              value={password}
              onChange={(e) =>
                setPassword(e.target.value)
              }
              autoComplete="current-password"
              required
            />

          </div>


          {/* LOGIN BUTTON */}

          <button
            type="submit"
            className="login-button"
            disabled={loading}
          >

            {loading ? (

              <>
                <span className="button-spinner" />
                Signing in...
              </>

            ) : (

              <>
                Sign in

                <span className="button-arrow">
                  →
                </span>
              </>

            )}

          </button>

        </form>


        {/* =====================================================
            REGISTER
            ===================================================== */}

        <div className="login-footer">

          <span>
            Don't have an account?
          </span>


          <button
            type="button"
            onClick={onRegister}
            style={{
              border: "none",
              background: "transparent",
              color: "#5f63e8",
              fontWeight: "700",
              cursor: "pointer",
              padding: 0,
              fontFamily: "inherit"
            }}
          >
            Create account
          </button>

        </div>


        {/* =====================================================
            SECURITY FOOTER
            ===================================================== */}

        <div
          style={{
            textAlign: "center",
            marginTop: "14px",
            fontSize: "10px",
            color: "#999"
          }}
        >
          Secure authentication • JWT protected
        </div>


      </div>

    </div>

  );
}


export default Login;