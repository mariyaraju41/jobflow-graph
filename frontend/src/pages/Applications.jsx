import { useEffect, useState } from "react";

import { getApplications } from "../services/api";

function Applications() {

  const candidateId =
    localStorage.getItem("candidateId");

  const [applications, setApplications] =
    useState([]);

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState("");


  useEffect(() => {

    loadApplications();

  }, []);


  const loadApplications = async () => {

    try {

      setLoading(true);
      setError("");

      const data =
        await getApplications(candidateId);

      setApplications(
        Array.isArray(data)
          ? data
          : []
      );

    } catch (err) {

      console.error(err);

      setError(
        "Unable to load your applications."
      );

    } finally {

      setLoading(false);
    }
  };


  const formatDate = (date) => {

    if (!date) {
      return "-";
    }

    return new Date(date)
      .toLocaleDateString(
        "en-IN",
        {
          day: "2-digit",
          month: "short",
          year: "numeric"
        }
      );
  };


  const formatTime = (date) => {

    if (!date) {
      return "";
    }

    return new Date(date)
      .toLocaleTimeString(
        "en-IN",
        {
          hour: "2-digit",
          minute: "2-digit"
        }
      );
  };


  return (

    <div className="applications-page">

      {/* HEADER */}

      <div className="applications-header">

        <div>

          <p className="eyebrow">
            APPLICATION TRACKER
          </p>

          <h1>
            Your Applications
          </h1>

          <p>
            Track the jobs you've applied for
            and monitor their current status.
          </p>

        </div>


        <div className="application-total">

          <strong>
            {applications.length}
          </strong>

          <span>
            Applications
          </span>

        </div>

      </div>


      {/* ERROR */}

      {error && (

        <div className="message error-message">
          {error}
        </div>

      )}


      {/* LOADING */}

      {loading ? (

        <div className="loading">
          Loading your applications...
        </div>

      ) : applications.length === 0 ? (

        /* EMPTY */

        <div className="empty">

          <div className="empty-icon">
            ✓
          </div>

          <h3>
            No applications yet
          </h3>

          <p>
            Jobs you apply for will appear
            here so you can track them.
          </p>

        </div>

      ) : (

        /* APPLICATION LIST */

        <div className="application-list">

          {applications.map(
            (application) => {

              const isPrepared =
                application.status ===
                "PREPARED";

              const match =
                Number(
                  application.matchPercentage || 0
                );

              return (

                <div
                  className="application"
                  key={
                    application.applicationId
                  }
                >

                  {/* JOB */}

                  <div className="application-job">

                    <div className="application-icon">

                      {application.jobTitle
                        ?.charAt(0)
                        .toUpperCase() || "J"}

                    </div>

                    <div>

                      <h3>
                        {application.jobTitle}
                      </h3>

                      <p>
                        {application.jobId}
                      </p>

                    </div>

                  </div>


                  {/* MATCH */}

                  <div className="application-match">

                    <strong>
                      {match}%
                    </strong>

                    <span>
                      Skill Match
                    </span>

                  </div>


                  {/* DATE */}

                  <div className="application-date">

                    <span>
                      Applied
                    </span>

                    <strong>
                      {formatDate(
                        application.createdAt
                      )}
                    </strong>

                    <small>
                      {formatTime(
                        application.createdAt
                      )}
                    </small>

                  </div>


                  {/* STATUS */}

                  <div>

                    <span
                      className={
                        isPrepared
                          ? "status prepared"
                          : "status review"
                      }
                    >
                      {isPrepared
                        ? "Prepared"
                        : "Review Required"}
                    </span>

                  </div>

                </div>

              );
            }
          )}

        </div>

      )}

    </div>
  );
}

export default Applications;