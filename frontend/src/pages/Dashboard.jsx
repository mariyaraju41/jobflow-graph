import { useEffect, useState } from "react";

import Profile from "./Profile1";
import Navbar from "../components/Navbar";
import Resume from "./Resume";
import Applications from "./Applications";
import JobSearch from "./JobSearch";

import {
  getJobMatches,
  applyForJob
} from "../services/api";


function Dashboard({ onLogout }) {

  const candidateId =
    localStorage.getItem("candidateId");


  const [page, setPage] =
    useState("jobs");


  const [jobs, setJobs] =
    useState([]);


  const [loading, setLoading] =
    useState(true);


  const [error, setError] =
    useState("");


  /*
   * ============================================================
   * LOAD MATCHED JOBS
   * ============================================================
   */

  useEffect(() => {

    if (page === "jobs") {

      loadJobs();

    }

  }, [page]);


  const loadJobs = async () => {

    if (!candidateId) {

      setError(
        "Candidate session not found. Please login again."
      );

      setLoading(false);

      return;
    }


    try {

      setLoading(true);

      setError("");


      const data =
        await getJobMatches(
          candidateId
        );


      setJobs(
        Array.isArray(data)
          ? data
          : []
      );


    } catch (err) {

      console.error(
        "Failed to load jobs:",
        err
      );


      setError(
        "Unable to load jobs. Please try again."
      );


    } finally {

      setLoading(false);

    }

  };


  /*
   * ============================================================
   * PAGE CONTENT
   * ============================================================
   */

  const renderPage = () => {


    /*
     * SEARCH
     */

    if (page === "search") {

      return <JobSearch />;

    }


    /*
     * PROFILE
     */

    if (page === "profile") {

      return <Profile />;

    }


    /*
     * RESUME
     */

    if (page === "resume") {

      return <Resume />;

    }


    /*
     * APPLICATIONS
     */

    if (page === "applications") {

      return <Applications />;

    }


    /*
     * JOBS
     */

    return (

      <>

        {error && (

          <div className="message">
            {error}
          </div>

        )}


        {/* HERO */}

        <section className="hero">

          <div>

            <p className="eyebrow">
              AI-POWERED JOB MATCHING
            </p>


            <h1>
              Find jobs that
              <br />
              actually match you.
            </h1>


            <p className="hero-text">
              JobFlow analyzes your resume,
              matches your skills against jobs,
              and helps you discover
              relevant opportunities.
            </p>

          </div>


          <div className="hero-stat">

            <strong>
              {jobs.length}
            </strong>


            <span>
              Matching Jobs
            </span>

          </div>

        </section>


        {/* JOB SECTION */}

        <section>

          <div className="section-heading">

            <div>

              <h2>
                Recommended Jobs
              </h2>


              <p>
                Ranked by your skill match
              </p>

            </div>


            <span className="job-count">

              {jobs.length}{" "}

              {jobs.length === 1
                ? "job"
                : "jobs"}

            </span>

          </div>


          {loading ? (

            <div className="loading">

              <p>
                Finding matching jobs...
              </p>

            </div>

          ) : jobs.length === 0 ? (

            <div className="empty">

              <h3>
                No matching jobs
              </h3>


              <p>
                Upload your resume to
                discover matching jobs.
              </p>

            </div>

          ) : (

            <div className="job-grid">

              {jobs.map(
                (job) => (

                  <JobCard
                    key={job.jobId}
                    job={job}
                  />

                )
              )}

            </div>

          )}

        </section>

      </>

    );

  };


  /*
   * ============================================================
   * MAIN
   * ============================================================
   */

  return (

    <div className="app">

      <Navbar
        page={page}
        setPage={setPage}
        onLogout={onLogout}
      />


      <main className="container">

        {renderPage()}

      </main>

    </div>

  );

}


/*
 * ============================================================
 * MATCHED JOB CARD
 * ============================================================
 */

function JobCard({ job }) {

  const match =
    Number(
      job.matchPercentage || 0
    );


  const matchedSkills =
    Array.isArray(
      job.matchedSkills
    )
      ? job.matchedSkills
      : [];


  const missingSkills =
    Array.isArray(
      job.missingSkills
    )
      ? job.missingSkills
      : [];


  const progress =
    Math.min(
      Math.max(match, 0),
      100
    );


  let matchLabel =
    "Needs Review";


  if (match >= 80) {

    matchLabel =
      "Strong Match";

  } else if (match >= 60) {

    matchLabel =
      "Good Match";

  } else if (match >= 40) {

    matchLabel =
      "Partial Match";

  }


  const jobInitial =
    job.job
      ? job.job
          .charAt(0)
          .toUpperCase()
      : "J";


  /*
   * ============================================================
   * APPLY
   * ============================================================
   */

  const handleApply = async () => {

    const candidateId =
      localStorage.getItem(
        "candidateId"
      );


    if (!candidateId) {

      alert(
        "Session expired. Please login again."
      );

      return;
    }


    try {

      /*
       * Save application first.
       */

      await applyForJob(
        candidateId,
        job.jobId
      );


      /*
       * Then open direct job link.
       */

      if (job.applicationUrl) {

        window.open(
          job.applicationUrl,
          "_blank",
          "noopener,noreferrer"
        );

      } else {

        alert(
          "Application link is not available."
        );

      }

    } catch (err) {

      console.error(
        "Application failed:",
        err
      );


      /*
       * If backend application saving
       * fails, don't pretend it was applied.
       */

      alert(
        err.response?.data?.message ||
        "Unable to submit application."
      );

    }

  };


  return (

    <article className="job-card">


      {/* HEADER */}

      <div className="job-top">

        <div className="company-icon">
          {jobInitial}
        </div>


        <div>

          <h3>
            {job.job ||
              "Job Opportunity"}
          </h3>


          <p>
            {job.jobId}
          </p>

        </div>

      </div>


      {/* MATCH */}

      <div className="match-row">

        <div>

          <span className="match-label">
            Skill Match
          </span>


          <div className="progress">

            <div
              className="progress-value"
              style={{
                width:
                  `${progress}%`
              }}
            />

          </div>

        </div>


        <strong className="percentage">
          {match}%
        </strong>

      </div>


      {/* LABEL */}

      <div
        style={{
          marginTop: "10px",
          fontSize: "11px",
          fontWeight: "700",
          color:
            match >= 80
              ? "#287040"
              : match >= 60
              ? "#806a18"
              : "#a85b19"
        }}
      >
        {matchLabel}
      </div>


      {/* SKILLS */}

      {(matchedSkills.length > 0 ||
        missingSkills.length > 0) && (

        <div className="skills">

          {matchedSkills.map(
            (skill, index) => (

              <span
                className="skill matched"
                key={
                  `matched-${skill}-${index}`
                }
              >
                ✓ {skill}
              </span>

            )
          )}


          {missingSkills.map(
            (skill, index) => (

              <span
                className="skill missing"
                key={
                  `missing-${skill}-${index}`
                }
              >
                + {skill}
              </span>

            )
          )}

        </div>

      )}


      {/* FOOTER */}

      <div className="job-footer">

        <span
          className={
            match >= 80
              ? "decision strong"
              : "decision review"
          }
        >

          {match >= 80
            ? "STRONG MATCH"
            : "SKILL GAP"}

        </span>


        <button
          type="button"
          onClick={handleApply}
          disabled={
            !job.applicationUrl
          }
          style={{
            border: "none",
            background: "#2563eb",
            color: "#fff",
            padding: "10px 20px",
            borderRadius: "999px",
            fontSize: "13px",
            fontWeight: "700",
            cursor:
              job.applicationUrl
                ? "pointer"
                : "not-allowed",
            opacity:
              job.applicationUrl
                ? 1
                : 0.5
          }}
        >

          {job.applicationUrl
            ? "Apply Now →"
            : "No Application Link"}

        </button>

      </div>

    </article>

  );

}


export default Dashboard;