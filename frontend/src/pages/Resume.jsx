import { useEffect, useState } from "react";

import {
  uploadResume,
  getResumes,
  deleteResume
} from "../services/api";


function Resume() {

  const candidateId =
    localStorage.getItem("candidateId");


  const [file, setFile] =
    useState(null);

  const [resumes, setResumes] =
    useState([]);

  const [loading, setLoading] =
    useState(true);

  const [uploading, setUploading] =
    useState(false);

  const [deleting, setDeleting] =
    useState("");

  const [error, setError] =
    useState("");

  const [success, setSuccess] =
    useState("");


  /*
   * ============================================================
   * LOAD SAVED RESUMES
   * ============================================================
   */

  useEffect(() => {

    loadResumes();

  }, []);


  const loadResumes = async () => {

    try {

      setLoading(true);
      setError("");

      const data =
        await getResumes(candidateId);

      setResumes(
        Array.isArray(data)
          ? data
          : []
      );

    } catch (err) {

      console.error(
        "Failed to load resumes:",
        err
      );

      setError(
        "Unable to load your saved resumes."
      );

    } finally {

      setLoading(false);
    }
  };


  /*
   * ============================================================
   * SELECT FILE
   * ============================================================
   */

  const handleFileChange = (e) => {

    const selectedFile =
      e.target.files?.[0];

    if (!selectedFile) {
      return;
    }


    if (
      selectedFile.type !==
      "application/pdf"
    ) {

      setError(
        "Please select a PDF resume."
      );

      setFile(null);

      return;
    }


    setError("");
    setSuccess("");

    setFile(selectedFile);
  };


  /*
   * ============================================================
   * UPLOAD
   * ============================================================
   */

  const handleUpload = async () => {

    if (!file) {

      setError(
        "Please select a PDF resume first."
      );

      return;
    }


    try {

      setUploading(true);
      setError("");
      setSuccess("");


      await uploadResume(
        candidateId,
        file
      );


      setFile(null);


      /*
       * Clear file input
       */

      const input =
        document.getElementById(
          "resume-file"
        );

      if (input) {
        input.value = "";
      }


      setSuccess(
        "Resume uploaded and saved successfully."
      );


      /*
       * Reload resumes from Neo4j
       */

      await loadResumes();

    } catch (err) {

      console.error(
        "Resume upload failed:",
        err
      );

      setError(
        err.response?.data?.message ||
        "Unable to upload resume."
      );

    } finally {

      setUploading(false);
    }
  };


  /*
   * ============================================================
   * DELETE
   * ============================================================
   */

  const handleDelete = async (
    resumeId,
    fileName
  ) => {

    const confirmed =
      window.confirm(
        `Delete "${fileName}"?`
      );

    if (!confirmed) {
      return;
    }


    try {

      setDeleting(resumeId);
      setError("");
      setSuccess("");


      await deleteResume(
        candidateId,
        resumeId
      );


      /*
       * Remove immediately from UI
       */

      setResumes(
        (current) =>
          current.filter(
            (resume) =>
              resume.resumeId !==
              resumeId
          )
      );


      setSuccess(
        "Resume deleted successfully."
      );

    } catch (err) {

      console.error(
        "Resume deletion failed:",
        err
      );

      setError(
        err.response?.data?.message ||
        "Unable to delete resume."
      );

    } finally {

      setDeleting("");
    }
  };


  /*
   * ============================================================
   * DATE
   * ============================================================
   */

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

    <div className="resume-page">


      {/* ======================================================
          HEADER
          ====================================================== */}

      <div className="resume-header">

        <div>

          <p className="eyebrow">
            RESUME MANAGER
          </p>

          <h1>
            Your Resumes
          </h1>

          <p>
            Upload and manage the resumes
            you use for job matching.
          </p>

        </div>


        <div className="resume-count">

          <strong>
            {resumes.length}
          </strong>

          <span>
            {resumes.length === 1
              ? "Resume"
              : "Resumes"}
          </span>

        </div>

      </div>


      {/* ======================================================
          MESSAGES
          ====================================================== */}

      {error && (

        <div className="message error-message">
          {error}
        </div>

      )}


      {success && (

        <div className="message">
          {success}
        </div>

      )}


      {/* ======================================================
          UPLOAD
          ====================================================== */}

      <div className="upload-card">

        <div className="upload-icon">
          ↑
        </div>

        <h3>
          Upload a new resume
        </h3>

        <p>
          Upload a PDF and JobFlow will
          extract your contact details
          and technical skills.
        </p>


        <input
          id="resume-file"
          type="file"
          accept=".pdf,application/pdf"
          onChange={handleFileChange}
        />


        {file && (

          <div className="selected-file">

            📄 {file.name}

          </div>

        )}


        <button
          className="primary-button"
          onClick={handleUpload}
          disabled={
            !file ||
            uploading
          }
        >

          {uploading
            ? "Uploading & Analyzing..."
            : "Upload & Analyze"}

        </button>

      </div>


      {/* ======================================================
          SAVED RESUMES
          ====================================================== */}

      <section className="saved-resumes">

        <div className="section-heading">

          <div>

            <h2>
              Saved Resumes
            </h2>

            <p>
              Your uploaded resume history
            </p>

          </div>

          <span className="job-count">
            {resumes.length} saved
          </span>

        </div>


        {loading ? (

          <div className="loading">
            Loading saved resumes...
          </div>

        ) : resumes.length === 0 ? (

          <div className="empty">

            <h3>
              No resumes uploaded yet
            </h3>

            <p>
              Upload your first resume above
              to start matching jobs.
            </p>

          </div>

        ) : (

          <div className="resume-list">

            {resumes.map(
              (resume) => (

                <div
                  className="saved-resume"
                  key={resume.resumeId}
                >

                  {/* ICON */}

                  <div className="resume-file-icon">
                    PDF
                  </div>


                  {/* DETAILS */}

                  <div className="saved-resume-info">

                    <h3>
                      {resume.fileName ||
                        "Resume.pdf"}
                    </h3>

                    <p>
                      Uploaded{" "}
                      {formatDate(
                        resume.uploadedAt
                      )}
                      {" "}
                      at{" "}
                      {formatTime(
                        resume.uploadedAt
                      )}
                    </p>

                    {resume.email && (

                      <span>
                        {resume.email}
                      </span>

                    )}

                  </div>


                  {/* DELETE */}

                  <button
                    className="delete-resume-button"
                    onClick={() =>
                      handleDelete(
                        resume.resumeId,
                        resume.fileName
                      )
                    }
                    disabled={
                      deleting ===
                      resume.resumeId
                    }
                  >

                    {deleting ===
                    resume.resumeId
                      ? "Deleting..."
                      : "Delete"}

                  </button>

                </div>

              )
            )}

          </div>

        )}

      </section>

    </div>
  );
}


export default Resume;