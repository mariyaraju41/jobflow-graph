import { useState } from "react";

import {
  searchInternetJobs,
  applyForJob
} from "../services/api";


function JobSearch() {

  const candidateId =
    localStorage.getItem("candidateId");


  const [keywords, setKeywords] =
    useState("");


  const [locations, setLocations] =
    useState([""]);


  const [minSalary, setMinSalary] =
    useState("");


  const [dateRangeDays, setDateRangeDays] =
    useState("");


  const [jobs, setJobs] =
    useState([]);


  const [loading, setLoading] =
    useState(false);


  const [error, setError] =
    useState("");


  const [searched, setSearched] =
    useState(false);


  /*
   * ============================================================
   * LOCATION MANAGEMENT
   * ============================================================
   */

  const addLocation = () => {

    if (locations.length < 5) {

      setLocations([
        ...locations,
        ""
      ]);

    }

  };


  const removeLocation = (
    index
  ) => {

    if (locations.length === 1) {

      return;
    }


    setLocations(
      locations.filter(
        (_, i) => i !== index
      )
    );

  };


  const updateLocation = (
    index,
    value
  ) => {

    const updated = [
      ...locations
    ];

    updated[index] =
      value;

    setLocations(updated);

  };


  /*
   * ============================================================
   * SEARCH
   * ============================================================
   */

  const handleSearch = async (
    event
  ) => {

    event.preventDefault();


    if (!candidateId) {

      setError(
        "Candidate session not found. Please login again."
      );

      return;
    }


    const validLocations =
      locations
        .map(
          location =>
            location.trim()
        )
        .filter(
          location =>
            location.length > 0
        );


    try {

      setLoading(true);

      setError("");

      setSearched(true);

      setJobs([]);


      /*
       * If multiple locations are selected,
       * search each location separately.
       */

      let allJobs = [];


      if (validLocations.length === 0) {

        const data =
          await searchInternetJobs(
            candidateId,
            {
              keywords,
              location: "",
              minSalary,
              dateRangeDays
            }
          );


        allJobs =
          Array.isArray(data)
            ? data
            : [];

      } else {

        for (
          const location
          of validLocations
        ) {

          const data =
            await searchInternetJobs(
              candidateId,
              {
                keywords,
                location,
                minSalary,
                dateRangeDays
              }
            );


          if (Array.isArray(data)) {

            allJobs.push(
              ...data
            );

          }

        }

      }


      /*
       * Remove duplicate jobs.
       */

      const uniqueJobs =
        Array.from(
          new Map(
            allJobs.map(
              (job, index) => [

                job.jobId ||
                job.id ||
                job.applicationUrl ||
                `${job.title}-${index}`,

                job

              ]
            )
          ).values()
        );


      setJobs(uniqueJobs);


    } catch (err) {

      console.error(
        "Internet job search failed:",
        err
      );


      setError(
        err.response?.data?.message ||
        "Unable to search jobs. Please try again."
      );


    } finally {

      setLoading(false);

    }

  };


  /*
   * ============================================================
   * APPLY
   * ============================================================
   */

  const handleApply = async (
    job
  ) => {

    const jobId =
      job.jobId ||
      job.id;


    const applicationUrl =
      job.applicationUrl ||
      job.link ||
      job.url;


    try {

      if (jobId) {

        await applyForJob(
          candidateId,
          jobId
        );

      }


      if (applicationUrl) {

        window.open(
          applicationUrl,
          "_blank",
          "noopener,noreferrer"
        );

      }

    } catch (err) {

      console.error(
        "Application error:",
        err
      );


      /*
       * Still allow external job
       * link to open.
       */

      if (applicationUrl) {

        window.open(
          applicationUrl,
          "_blank",
          "noopener,noreferrer"
        );

      }

    }

  };


  /*
   * ============================================================
   * HELPERS
   * ============================================================
   */

  const getTitle = (
    job
  ) => {

    return (
      job.title ||
      job.jobTitle ||
      job.job ||
      "Job Opportunity"
    );

  };


  const getCompany = (
    job
  ) => {

    return (
      job.company ||
      job.companyName ||
      "Company"
    );

  };


  const getJobLocation = (
    job
  ) => {

    return (
      job.location ||
      "Location not specified"
    );

  };


  const getUrl = (
    job
  ) => {

    return (
      job.applicationUrl ||
      job.link ||
      job.url ||
      ""
    );

  };


  const getMatch = (
    job
  ) => {

    const value =
      Number(
        job.matchPercentage ||
        job.match ||
        0
      );


    return Math.min(
      Math.max(value, 0),
      100
    );

  };


  return (

    <div className="job-search-page">


      {/* ======================================================
          HEADER
          ====================================================== */}

      <div className="search-page-header">

        <div>

          <p className="eyebrow">
            LIVE JOB SEARCH
          </p>


          <h1>
            Find your next opportunity
          </h1>


          <p>
            Search live jobs using your skills,
            preferred locations and experience.
          </p>

        </div>

      </div>


      {/* ======================================================
          SEARCH PANEL
          ====================================================== */}

      <form
        className="job-search-panel"
        onSubmit={handleSearch}
      >


        {/* SEARCH */}

        <div className="search-main-field">

          <span className="search-icon">
            ⌕
          </span>


          <input
            type="text"
            value={keywords}
            onChange={(e) =>
              setKeywords(
                e.target.value
              )
            }
            placeholder="Search jobs, skills or job titles..."
          />

        </div>


        {/* LOCATIONS */}

        <div className="locations-section">

          <div className="filter-title-row">

            <label>
              Locations
            </label>


            <span>
              Up to 5 locations
            </span>

          </div>


          <div className="location-list">

            {locations.map(
              (
                location,
                index
              ) => (

                <div
                  className="location-input"
                  key={index}
                >

                  <span>
                    📍
                  </span>


                  <input
                    type="text"
                    value={location}
                    onChange={(e) =>
                      updateLocation(
                        index,
                        e.target.value
                      )
                    }
                    placeholder={
                      index === 0
                        ? "Hyderabad"
                        : "Bengaluru, Chennai, Pune..."
                    }
                  />


                  {locations.length > 1 && (

                    <button
                      type="button"
                      className="remove-location"
                      onClick={() =>
                        removeLocation(
                          index
                        )
                      }
                    >
                      ×
                    </button>

                  )}

                </div>

              )
            )}

          </div>


          {locations.length < 5 && (

            <button
              type="button"
              className="add-location"
              onClick={addLocation}
            >
              + Add another location
            </button>

          )}

        </div>


        {/* FILTERS */}

        <div className="search-filter-grid">


          {/* SALARY */}

          <div className="search-filter">

            <label>
              Minimum Salary
            </label>


            <input
              type="number"
              value={minSalary}
              onChange={(e) =>
                setMinSalary(
                  e.target.value
                )
              }
              placeholder="₹ 3,00,000"
            />

          </div>


          {/* POSTED */}

          <div className="search-filter">

            <label>
              Posted Date
            </label>


            <select
              value={dateRangeDays}
              onChange={(e) =>
                setDateRangeDays(
                  e.target.value
                )
              }
            >

              <option value="">
                Any time
              </option>

              <option value="1">
                Last 24 hours
              </option>

              <option value="3">
                Last 3 days
              </option>

              <option value="7">
                Last 7 days
              </option>

              <option value="14">
                Last 14 days
              </option>

              <option value="30">
                Last 30 days
              </option>

            </select>

          </div>

        </div>


        {/* RESUME INFO */}

        <div className="resume-search-info">

          <span className="resume-check">
            ✓
          </span>


          <div>

            <strong>
              Resume skills included
            </strong>

            <p>
              Your extracted skills are automatically
              considered when searching for relevant jobs.
            </p>

          </div>

        </div>


        {/* SEARCH ACTION */}

        <div className="search-action">

          <button
            type="submit"
            className="blue-pill-button"
            disabled={loading}
          >

            {loading
              ? "Searching..."
              : "Search Jobs →"}

          </button>

        </div>

      </form>


      {/* ERROR */}

      {error && (

        <div className="message">
          {error}
        </div>

      )}


      {/* LOADING */}

      {loading && (

        <div className="search-loading">

          <div className="search-spinner" />

          <p>
            Searching live jobs...
          </p>

        </div>

      )}


      {/* RESULTS */}

      {!loading &&
        searched &&
        jobs.length === 0 && (

          <div className="empty">

            <h3>
              No jobs found
            </h3>

            <p>
              Try another skill, broader
              location or date range.
            </p>

          </div>

        )}


      {!loading &&
        jobs.length > 0 && (

          <section className="search-results">

            <div className="search-results-header">

              <div>

                <p className="eyebrow">
                  RESULTS
                </p>


                <h2>
                  Jobs found for you
                </h2>

              </div>


              <span className="results-count">
                {jobs.length} jobs
              </span>

            </div>


            <div className="job-grid">

              {jobs.map(
                (
                  job,
                  index
                ) => {

                  const title =
                    getTitle(job);

                  const company =
                    getCompany(job);

                  const jobLocation =
                    getJobLocation(job);

                  const applicationUrl =
                    getUrl(job);

                  const match =
                    getMatch(job);


                  return (

                    <article
                      className="job-card"
                      key={
                        job.jobId ||
                        job.id ||
                        `${title}-${index}`
                      }
                    >

                      <div className="job-top">

                        <div className="company-icon">

                          {title
                            .charAt(0)
                            .toUpperCase()}

                        </div>


                        <div>

                          <h3>
                            {title}
                          </h3>


                          <p>
                            {company}
                          </p>


                          <p className="search-job-location">
                            📍 {jobLocation}
                          </p>

                        </div>

                      </div>


                      {match > 0 && (

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
                                    `${match}%`
                                }}
                              />

                            </div>

                          </div>


                          <strong className="percentage">
                            {match}%
                          </strong>

                        </div>

                      )}


                      {job.description && (

                        <p className="search-description">

                          {job.description}

                        </p>

                      )}


                      <div className="job-footer">

                        <span className="decision strong">

                          {match > 0
                            ? `${match}% MATCH`
                            : "NEW JOB"}

                        </span>


                        <button
                          type="button"
                          className="blue-pill-button small"
                          disabled={
                            !applicationUrl
                          }
                          onClick={() =>
                            handleApply(job)
                          }
                        >

                          Apply Now →

                        </button>

                      </div>

                    </article>

                  );

                }
              )}

            </div>

          </section>

        )}

    </div>

  );

}


export default JobSearch;