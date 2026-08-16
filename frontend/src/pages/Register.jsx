import { useState } from "react";

import { register } from "../services/api";


function Register({ onRegister }) {

  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [phone, setPhone] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");


  const handleSubmit = async (e) => {

    e.preventDefault();

    setError("");
    setSuccess("");


    if (password !== confirmPassword) {

      setError("Passwords do not match.");

      return;
    }


    if (password.length < 6) {

      setError(
        "Password must contain at least 6 characters."
      );

      return;
    }


    try {

      setLoading(true);


      const data = await register({
        name,
        email,
        phone,
        password
      });


      console.log(
        "Registration successful:",
        data
      );


      setSuccess(
        "Account created successfully. You can now sign in."
      );


      setTimeout(() => {

        onRegister();

      }, 1000);


    } catch (err) {

      console.error(err);

      setError(
        err.response?.data?.message ||
        "Unable to create account."
      );

    } finally {

      setLoading(false);

    }
  };


  return (

    <div className="login-page">

      <div className="login-card">


        {/* BRAND */}

        <div className="login-brand">

          <div className="login-logo">
            Job<span>Flow</span>
          </div>

          <div className="login-badge">
            AI JOB MATCHING
          </div>

        </div>


        {/* HEADING */}

        <div className="login-heading">

          <h1>
            Create your account
          </h1>

          <p>
            Join JobFlow and discover jobs
            that match your skills.
          </p>

        </div>


        {/* ERROR */}

        {error && (

          <div className="login-error">

            <span>!</span>

            {error}

          </div>

        )}


        {/* SUCCESS */}

        {success && (

          <div
            className="login-success"
          >
            ✓ {success}
          </div>

        )}


        {/* FORM */}

        <form
          className="login-form"
          onSubmit={handleSubmit}
        >


          {/* NAME */}

          <div className="login-field">

            <label>
              Full name
            </label>

            <input
              type="text"
              placeholder="Raju Kumar"
              value={name}
              onChange={(e) =>
                setName(e.target.value)
              }
              autoComplete="name"
              required
            />

          </div>


          {/* EMAIL */}

          <div className="login-field">

            <label>
              Email address
            </label>

            <input
              type="email"
              placeholder="raju@gmail.com"
              value={email}
              onChange={(e) =>
                setEmail(e.target.value)
              }
              autoComplete="email"
              required
            />

          </div>


          {/* PHONE */}

          <div className="login-field">

            <label>
              Phone number
            </label>

            <input
              type="tel"
              placeholder="9876543210"
              value={phone}
              onChange={(e) =>
                setPhone(e.target.value)
              }
              autoComplete="tel"
              required
            />

          </div>


          {/* PASSWORD */}

          <div className="login-field">

            <label>
              Password
            </label>

            <input
              type="password"
              placeholder="Create a password"
              value={password}
              onChange={(e) =>
                setPassword(e.target.value)
              }
              autoComplete="new-password"
              required
            />

          </div>


          {/* CONFIRM PASSWORD */}

          <div className="login-field">

            <label>
              Confirm password
            </label>

            <input
              type="password"
              placeholder="Enter password again"
              value={confirmPassword}
              onChange={(e) =>
                setConfirmPassword(e.target.value)
              }
              autoComplete="new-password"
              required
            />

          </div>


          {/* REGISTER BUTTON */}

          <button
            type="submit"
            className="login-button"
            disabled={loading}
          >

            {loading ? (

              <>
                <span className="button-spinner" />

                Creating account...
              </>

            ) : (

              <>
                Create account

                <span className="button-arrow">
                  →
                </span>
              </>

            )}

          </button>

        </form>


        {/* FOOTER */}

        <div
          className="login-footer"
          style={{
            justifyContent: "center"
          }}
        >

          <span>
            Already have an account?
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
            Sign in
          </button>

        </div>


      </div>

    </div>
  );
}


export default Register;