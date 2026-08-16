import { useEffect, useState } from "react";

import {
  getProfile,
  updateProfile
} from "../services/api";


function Profile1() {

  const candidateId =
    localStorage.getItem("candidateId");


  const [profile, setProfile] =
    useState(null);

  const [editing, setEditing] =
    useState(false);

  const [name, setName] =
    useState("");

  const [phone, setPhone] =
    useState("");

  const [loading, setLoading] =
    useState(true);

  const [saving, setSaving] =
    useState(false);

  const [error, setError] =
    useState("");

  const [success, setSuccess] =
    useState("");


  /* ============================================================
     LOAD PROFILE
     ============================================================ */

  useEffect(() => {

    loadProfile();

  }, []);


  const loadProfile = async () => {

    try {

      setLoading(true);
      setError("");

      const data =
        await getProfile(candidateId);

      setProfile(data);

      setName(data.name || "");
      setPhone(data.phone || "");

    } catch (err) {

      console.error(
        "Profile loading failed:",
        err
      );

      setError(
        "Unable to load profile."
      );

    } finally {

      setLoading(false);

    }
  };


  /* ============================================================
     EDIT
     ============================================================ */

  const startEditing = () => {

    setName(profile.name || "");
    setPhone(profile.phone || "");

    setError("");
    setSuccess("");

    setEditing(true);
  };


  /* ============================================================
     CANCEL
     ============================================================ */

  const cancelEditing = () => {

    setName(profile.name || "");
    setPhone(profile.phone || "");

    setError("");
    setSuccess("");

    setEditing(false);
  };


  /* ============================================================
     SAVE
     ============================================================ */

  const handleSave = async () => {

    if (!name.trim()) {

      setError("Full name is required.");

      return;
    }


    if (!phone.trim()) {

      setError("Phone number is required.");

      return;
    }


    try {

      setSaving(true);
      setError("");
      setSuccess("");


      const updated =
        await updateProfile(
          candidateId,
          {
            name: name.trim(),
            phone: phone.trim()
          }
        );


      setProfile(updated);

      setName(updated.name || "");
      setPhone(updated.phone || "");

      setEditing(false);

      setSuccess(
        "Profile updated successfully."
      );

    } catch (err) {

      console.error(
        "Profile update failed:",
        err
      );

      setError(
        "Unable to save profile."
      );

    } finally {

      setSaving(false);

    }
  };


  /* ============================================================
     LOADING
     ============================================================ */

  if (loading) {

    return (

      <div
        style={{
          padding: "60px",
          textAlign: "center",
          color: "#777"
        }}
      >
        Loading profile...
      </div>

    );
  }


  /* ============================================================
     ERROR
     ============================================================ */

  if (!profile) {

    return (

      <div
        style={{
          padding: "40px",
          color: "#b42318",
          background: "#fff1f0",
          borderRadius: "12px"
        }}
      >
        {error || "Profile not found."}
      </div>

    );
  }


  const initial =
    (
      profile.name ||
      profile.email ||
      "U"
    )
      .charAt(0)
      .toUpperCase();


  return (

    <div
      style={{
        maxWidth: "1050px",
        margin: "0 auto",
        padding: "35px 10px 70px",
        boxSizing: "border-box"
      }}
    >


      {/* ========================================================
          PAGE TITLE
          ======================================================== */}

      <div
        style={{
          marginBottom: "25px"
        }}
      >

        <div
          style={{
            fontSize: "12px",
            fontWeight: "800",
            letterSpacing: "2px",
            color: "#6d6ff5",
            marginBottom: "6px"
          }}
        >
          ACCOUNT
        </div>


        <h1
          style={{
            margin: 0,
            fontSize: "32px",
            fontWeight: "800",
            color: "#171923"
          }}
        >
          My Profile
        </h1>


        <p
          style={{
            margin: "7px 0 0",
            fontSize: "14px",
            color: "#777b87"
          }}
        >
          Manage your personal information and
          JobFlow activity.
        </p>

      </div>


      {/* ========================================================
          SUCCESS MESSAGE
          ======================================================== */}

      {success && (

        <div
          style={{
            background: "#ecfdf3",
            border: "1px solid #b7ebc9",
            color: "#18733c",
            padding: "12px 15px",
            borderRadius: "10px",
            fontSize: "13px",
            fontWeight: "600",
            marginBottom: "18px"
          }}
        >
          ✓ {success}
        </div>

      )}


      {/* ========================================================
          ERROR MESSAGE
          ======================================================== */}

      {error && (

        <div
          style={{
            background: "#fff1f0",
            border: "1px solid #ffd0cc",
            color: "#b42318",
            padding: "12px 15px",
            borderRadius: "10px",
            fontSize: "13px",
            fontWeight: "600",
            marginBottom: "18px"
          }}
        >
          {error}
        </div>

      )}


      {/* ========================================================
          PROFILE HEADER
          ======================================================== */}

      <section
        style={{
          background: "#171923",
          borderRadius: "18px",
          padding: "28px",
          display: "flex",
          alignItems: "center",
          gap: "20px",
          marginBottom: "20px",
          boxSizing: "border-box"
        }}
      >

        {/* Avatar */}

        <div
          style={{
            width: "70px",
            height: "70px",
            minWidth: "70px",
            borderRadius: "50%",
            background: "#6d6ff5",
            color: "#ffffff",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            fontSize: "28px",
            fontWeight: "800"
          }}
        >
          {initial}
        </div>


        {/* User details */}

        <div
          style={{
            flex: 1,
            minWidth: 0
          }}
        >

          <div
            style={{
              color: "#aeb2ff",
              fontSize: "11px",
              fontWeight: "800",
              letterSpacing: "1.5px",
              marginBottom: "5px"
            }}
          >
            CANDIDATE PROFILE
          </div>


          <div
            style={{
              color: "#ffffff",
              fontSize: "25px",
              fontWeight: "800",
              marginBottom: "5px"
            }}
          >
            {profile.name || "Candidate"}
          </div>


          <div
            style={{
              color: "#b8bbc7",
              fontSize: "13px"
            }}
          >
            {profile.email}
          </div>


          <div
            style={{
              color: "#777b87",
              fontSize: "11px",
              marginTop: "6px"
            }}
          >
            Candidate ID: {profile.candidateId}
          </div>

        </div>


        {/* EDIT BUTTON */}

        {!editing && (

          <button
            type="button"
            onClick={startEditing}
            style={{
              border: "1px solid #55596a",
              background: "#ffffff",
              color: "#171923",
              padding: "11px 18px",
              borderRadius: "9px",
              fontSize: "13px",
              fontWeight: "800",
              cursor: "pointer",
              whiteSpace: "nowrap"
            }}
          >
            ✎ Edit Profile
          </button>

        )}

      </section>


      {/* ========================================================
          TWO COLUMN AREA
          ======================================================== */}

      <div
        style={{
          display: "grid",
          gridTemplateColumns:
            "minmax(0, 1.5fr) minmax(260px, 0.8fr)",
          gap: "20px",
          alignItems: "start"
        }}
      >


        {/* ======================================================
            PERSONAL INFORMATION
            ====================================================== */}

        <section
          style={{
            background: "#ffffff",
            border: "1px solid #e5e7eb",
            borderRadius: "16px",
            padding: "25px",
            boxSizing: "border-box"
          }}
        >

          <div
            style={{
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
              marginBottom: "22px"
            }}
          >

            <div>

              <h2
                style={{
                  margin: 0,
                  fontSize: "18px",
                  color: "#171923"
                }}
              >
                Personal Information
              </h2>

              <p
                style={{
                  margin: "5px 0 0",
                  fontSize: "12px",
                  color: "#858995"
                }}
              >
                Your account information
              </p>

            </div>


            {!editing && (

              <span
                style={{
                  fontSize: "11px",
                  color: "#777b87"
                }}
              >
                Read only
              </span>

            )}

          </div>


          {/* NAME */}

          <ProfileField label="Full Name">

            {editing ? (

              <input
                type="text"
                value={name}
                onChange={(e) =>
                  setName(e.target.value)
                }
                style={inputStyle}
              />

            ) : (

              <strong style={valueStyle}>
                {profile.name || "-"}
              </strong>

            )}

          </ProfileField>


          {/* EMAIL */}

          <ProfileField label="Email">

            <strong style={valueStyle}>
              {profile.email || "-"}
            </strong>

            <div
              style={{
                marginTop: "5px",
                color: "#999",
                fontSize: "10px"
              }}
            >
              Email is connected to your login account.
            </div>

          </ProfileField>


          {/* PHONE */}

          <ProfileField label="Phone">

            {editing ? (

              <input
                type="tel"
                value={phone}
                onChange={(e) =>
                  setPhone(e.target.value)
                }
                style={inputStyle}
              />

            ) : (

              <strong style={valueStyle}>
                {profile.phone || "-"}
              </strong>

            )}

          </ProfileField>


          {/* CANDIDATE ID */}

          <ProfileField
            label="Candidate ID"
            last
          >

            <strong style={valueStyle}>
              {profile.candidateId}
            </strong>

          </ProfileField>


          {/* SAVE BUTTONS */}

          {editing && (

            <div
              style={{
                display: "flex",
                justifyContent: "flex-end",
                gap: "10px",
                marginTop: "22px"
              }}
            >

              <button
                type="button"
                onClick={cancelEditing}
                disabled={saving}
                style={{
                  border: "1px solid #d9dce3",
                  background: "#ffffff",
                  color: "#444751",
                  padding: "10px 17px",
                  borderRadius: "8px",
                  cursor: "pointer",
                  fontWeight: "700"
                }}
              >
                Cancel
              </button>


              <button
                type="button"
                onClick={handleSave}
                disabled={saving}
                style={{
                  border: "none",
                  background: "#171923",
                  color: "#ffffff",
                  padding: "10px 19px",
                  borderRadius: "8px",
                  cursor: "pointer",
                  fontWeight: "800"
                }}
              >
                {saving
                  ? "Saving..."
                  : "Save Changes"}
              </button>

            </div>

          )}

        </section>


        {/* ======================================================
            ACTIVITY
            ====================================================== */}

        <section
          style={{
            background: "#ffffff",
            border: "1px solid #e5e7eb",
            borderRadius: "16px",
            padding: "25px"
          }}
        >

          <h2
            style={{
              margin: "0 0 5px",
              fontSize: "18px",
              color: "#171923"
            }}
          >
            JobFlow Activity
          </h2>


          <p
            style={{
              margin: "0 0 20px",
              fontSize: "12px",
              color: "#858995"
            }}
          >
            Your current activity
          </p>


          {/* RESUMES */}

          <ActivityCard
            number={profile.resumeCount || 0}
            label="Resumes Uploaded"
            icon="📄"
          />


          {/* APPLICATIONS */}

          <ActivityCard
            number={profile.applicationCount || 0}
            label="Applications"
            icon="📋"
          />

        </section>

      </div>


      {/* ========================================================
          SKILLS
          ======================================================== */}

      <section
        style={{
          background: "#ffffff",
          border: "1px solid #e5e7eb",
          borderRadius: "16px",
          padding: "25px",
          marginTop: "20px"
        }}
      >

        <div
          style={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
            marginBottom: "18px"
          }}
        >

          <div>

            <h2
              style={{
                margin: 0,
                fontSize: "18px",
                color: "#171923"
              }}
            >
              My Skills
            </h2>

            <p
              style={{
                margin: "5px 0 0",
                fontSize: "12px",
                color: "#858995"
              }}
            >
              Skills extracted automatically from your resumes
            </p>

          </div>


          <span
            style={{
              background: "#f3f4f8",
              padding: "6px 10px",
              borderRadius: "8px",
              fontSize: "11px",
              fontWeight: "700",
              color: "#555"
            }}
          >
            {profile.skills?.length || 0} skills
          </span>

        </div>


        <div
          style={{
            display: "flex",
            flexWrap: "wrap",
            gap: "8px"
          }}
        >

          {(profile.skills || []).map(
            (skill, index) => (

              <span
                key={`${skill}-${index}`}
                style={{
                  background: "#edf9f1",
                  color: "#24743e",
                  border: "1px solid #d2efda",
                  padding: "7px 10px",
                  borderRadius: "7px",
                  fontSize: "11px",
                  fontWeight: "700"
                }}
              >
                ✓ {skill}
              </span>

            )
          )}

        </div>

      </section>

    </div>

  );
}


/* ==============================================================
   PROFILE FIELD
   ============================================================== */

function ProfileField({
  label,
  children,
  last = false
}) {

  return (

    <div
      style={{
        padding: "15px 0",
        borderBottom:
          last
            ? "none"
            : "1px solid #eef0f3"
      }}
    >

      <div
        style={{
          fontSize: "11px",
          color: "#858995",
          fontWeight: "600",
          marginBottom: "7px"
        }}
      >
        {label}
      </div>

      {children}

    </div>

  );
}


/* ==============================================================
   ACTIVITY CARD
   ============================================================== */

function ActivityCard({
  number,
  label,
  icon
}) {

  return (

    <div
      style={{
        display: "flex",
        alignItems: "center",
        gap: "14px",
        padding: "15px",
        background: "#f7f8fa",
        borderRadius: "12px",
        marginBottom: "10px"
      }}
    >

      <div
        style={{
          width: "38px",
          height: "38px",
          minWidth: "38px",
          borderRadius: "9px",
          background: "#ffffff",
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          fontSize: "17px"
        }}
      >
        {icon}
      </div>


      <div>

        <div
          style={{
            fontSize: "22px",
            fontWeight: "800",
            color: "#171923",
            lineHeight: "1"
          }}
        >
          {number}
        </div>

        <div
          style={{
            fontSize: "10px",
            color: "#777b87",
            marginTop: "4px"
          }}
        >
          {label}
        </div>

      </div>

    </div>

  );
}


/* ==============================================================
   INPUT STYLE
   ============================================================== */

const inputStyle = {
  width: "100%",
  boxSizing: "border-box",
  padding: "11px 12px",
  border: "1px solid #d7dae2",
  borderRadius: "8px",
  outline: "none",
  fontSize: "13px",
  color: "#171923",
  background: "#ffffff"
};


const valueStyle = {
  fontSize: "14px",
  color: "#171923"
};


export default Profile1;