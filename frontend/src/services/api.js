import axios from "axios";


/*
 * ============================================================
 * AXIOS INSTANCE
 * ============================================================
 */

const api = axios.create({

  baseURL: "https://jobflow-graph.onrender.com",

  headers: {
    "Content-Type": "application/json"
  }

});


/*
 * ============================================================
 * JWT INTERCEPTOR
 * ============================================================
 */

api.interceptors.request.use(

  (config) => {

    const token =
      localStorage.getItem("jobflow_token");


    if (token) {

      config.headers =
        config.headers || {};

      config.headers.Authorization =
        `Bearer ${token}`;

    }


    return config;

  },

  (error) => {

    return Promise.reject(error);

  }

);


/*
 * ============================================================
 * AUTHENTICATION
 * ============================================================
 */


/*
 * REGISTER
 */

export const register = async (
  data
) => {

  const response =
    await api.post(
      "/auth/register",
      data
    );

  return response.data;
};


/*
 * LOGIN
 */

export const login = async (
  email,
  password
) => {

  const response =
    await api.post(
      "/auth/login",
      {
        email,
        password
      }
    );


  const data =
    response.data;


  if (data?.token) {

    localStorage.setItem(
      "jobflow_token",
      data.token
    );

  }


  if (data?.candidateId) {

    localStorage.setItem(
      "candidateId",
      data.candidateId
    );

  }


  return data;
};


/*
 * LOGOUT
 */

export const logout = () => {

  localStorage.removeItem(
    "jobflow_token"
  );

  localStorage.removeItem(
    "candidateId"
  );

};


/*
 * ============================================================
 * PROFILE
 * ============================================================
 */


/*
 * GET PROFILE
 */

export const getProfile = async (
  candidateId
) => {

  const response =
    await api.get(
      `/profile/${candidateId}`
    );

  return response.data;
};


/*
 * UPDATE PROFILE
 */

export const updateProfile = async (
  candidateId,
  profileData
) => {

  const response =
    await api.put(
      `/profile/${candidateId}`,
      profileData
    );

  return response.data;
};


/*
 * ============================================================
 * JOB MATCHING
 * ============================================================
 */


/*
 * GET MATCHED JOBS
 */

export const getJobMatches = async (
  candidateId
) => {

  const response =
    await api.get(
      `/jobs/matches/${candidateId}`
    );

  return response.data;
};


/*
 * GET FILTERED MATCHED JOBS
 */

export const getFilteredJobMatches = async (
  candidateId,
  filters = {}
) => {

  const params = {};


  if (
    filters.location &&
    filters.location.trim() !== ""
  ) {

    params.location =
      filters.location.trim();

  }


  if (
    filters.minSalary !== undefined &&
    filters.minSalary !== null &&
    filters.minSalary !== ""
  ) {

    params.minSalary =
      filters.minSalary;

  }


  if (
    filters.experience !== undefined &&
    filters.experience !== null &&
    filters.experience !== ""
  ) {

    params.experience =
      filters.experience;

  }


  const response =
    await api.get(
      `/jobs/matches/${candidateId}/filter`,
      {
        params
      }
    );

  return response.data;
};


/*
 * ============================================================
 * INTERNET JOB SEARCH - JOOBLE
 * ============================================================
 *
 * Backend:
 *
 * GET /api/job-search/internet/{candidateId}
 *
 * Parameters:
 *
 * keywords
 * location
 * minSalary
 * dateRangeDays
 *
 * ============================================================
 */

export const searchInternetJobs = async (
  candidateId,
  filters = {}
) => {

  const params = {};


  /*
   * Keyword
   */

  if (
    filters.keywords &&
    filters.keywords.trim() !== ""
  ) {

    params.keywords =
      filters.keywords.trim();

  }


  /*
   * Location
   */

  if (
    filters.location &&
    filters.location.trim() !== ""
  ) {

    params.location =
      filters.location.trim();

  }


  /*
   * Minimum salary
   */

  if (
    filters.minSalary !== undefined &&
    filters.minSalary !== null &&
    filters.minSalary !== ""
  ) {

    params.minSalary =
      filters.minSalary;

  }


  /*
   * Posted date
   */

  if (
    filters.dateRangeDays !== undefined &&
    filters.dateRangeDays !== null &&
    filters.dateRangeDays !== ""
  ) {

    params.dateRangeDays =
      filters.dateRangeDays;

  }


  const response =
    await api.get(
      `/job-search/internet/${candidateId}`,
      {
        params
      }
    );


  return response.data;
};


/*
 * ============================================================
 * RESUMES
 * ============================================================
 */


/*
 * UPLOAD RESUME
 */

export const uploadResume = async (
  candidateId,
  file
) => {

  const formData =
    new FormData();


  formData.append(
    "candidateId",
    candidateId
  );


  formData.append(
    "file",
    file
  );


  const response =
    await api.post(
      "/resumes/upload",
      formData,
      {
        headers: {
          "Content-Type":
            "multipart/form-data"
        }
      }
    );


  return response.data;
};


/*
 * GET RESUMES
 */

export const getResumes = async (
  candidateId
) => {

  const response =
    await api.get(
      `/resumes/${candidateId}`
    );

  return response.data;
};


/*
 * DELETE RESUME
 */

export const deleteResume = async (
  candidateId,
  resumeId
) => {

  const response =
    await api.delete(
      `/resumes/${candidateId}/${resumeId}`
    );

  return response.data;
};


/*
 * ============================================================
 * APPLICATIONS
 * ============================================================
 */


/*
 * APPLY FOR JOB
 */

export const applyForJob = async (
  candidateId,
  jobId
) => {

  const response =
    await api.post(
      "/applications",
      {
        candidateId,
        jobId
      }
    );

  return response.data;
};


/*
 * GET APPLICATIONS
 */

export const getApplications = async (
  candidateId
) => {

  const response =
    await api.get(
      `/applications/${candidateId}`
    );

  return response.data;
};


/*
 * ============================================================
 * DEFAULT EXPORT
 * ============================================================
 */

export default api;
