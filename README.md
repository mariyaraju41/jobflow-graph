# JobFlow - AI-Powered Job Matching and Application Tracker

JobFlow is a job discovery and application tracking platform backed by a graph database built for the WEXA AI / CognoDB take-home assignment. It connects candidates, skills, resumes, jobs, and applications as a graph so the application can answer questions that depend on relationships such as which jobs best match a candidate's extracted skills and which jobs a candidate has applied to.

## Why We Chose This Problem

Finding the right job is a common problem for students and job seekers, especially when a candidate has to compare their skills with hundreds of different job requirements.

Traditional job searching usually depends on manually checking job descriptions, comparing required skills, keeping track of applications, and remembering which jobs have already been applied to. This becomes difficult when a candidate is searching across multiple job sites and locations.

We chose this problem because it combines several real-world relationships:

- candidates have multiple skills
- resumes contain different skills and experience
- jobs require different combinations of skills
- candidates can apply to multiple jobs
- applications need to be tracked over time

JobFlow brings these relationships together in one application. It uses the candidate's resume and skills to find relevant jobs, calculate skill matches, search external job opportunities, redirect the candidate to the actual application page, and track the application afterward.

This makes job search more organized for the candidate and provides a practical use case where graph relationships are important.

## Live Demo

- **Frontend:** https://jobflow-graph-63gt.vercel.app
- **Backend API:** https://jobflow-graph.onrender.com
- **Health check:** https://jobflow-graph.onrender.com/api/health

The frontend and backend are deployed separately. The frontend uses the backend base URL `https://jobflow-graph.onrender.com/api` in the deployed build.

## What JobFlow Solves

Job seekers often have to search across multiple job boards, compare requirements manually, and maintain a separate record of where they applied. JobFlow brings these steps into one workflow:

1. A candidate registers and logs in.
2. A resume can be uploaded and processed to extract skills.
3. Candidate skills are stored as graph relationships.
4. Jobs are matched against those skills and scored by percentage.
5. Candidates can search live Internet jobs by keyword/skills, location, salary, and posted-date range.
6. Search results can be searched across multiple locations.
7. Clicking **Apply** records the application as `APPLIED` and then opens the external job/application page.
8. Applications are available in an application tracker.

## Why a Graph Database?

CognoDB is used because the core problem is about **connections** rather than isolated rows. The application naturally models relationships such as:

- a candidate **has** skills;
- a job **requires** skills;
- a candidate **submits** an application;
- an application **is for** a job.

This makes graph traversal a natural fit for matching and tracking. A relational design could represent the same information, but queries that repeatedly traverse candidate -> skills -> jobs -> required skills and candidate -> applications -> jobs would require more joins and intermediate tables. The graph model keeps those relationships explicit and makes relationship-heavy queries easier to express.

The README includes a section explaining why a graph database is useful for this project.

## Technology Stack

### Frontend

- React
- Vite
- Axios
- CSS
- Vercel deployment

### Backend

- Java 17
- Spring Boot
- Spring Web
- Spring Security
- JWT authentication
- Spring Security OAuth2 Resource Server / JWT support
- Maven
- Docker
- Render deployment

### Data & Integrations

- CognoDB managed graph database
- Neo4j Java Driver / official Neo4j driver
- OpenCypher
- Jooble Internet Job Search API

CognoDB exposes openCypher over Bolt and supports official Neo4j drivers, including Java.

## Architecture

                           +──────────────────────────+
                           |        JobFlow UI        |
                           |    React + Vite + Axios  |
                           |          Vercel          |
                           +────────────+─────────────+
                                        | HTTPS / REST
                                        v
                           +──────────────────────────+
                           |     Spring Boot API      |
                           | Java 17 + Spring Security|
                           |       Docker / Render    |
                           +───────+─────────+────────+
                                   |         |
                         Bolt/Neo4j |         | HTTPS
                                   v         v
                    +──────────────────+  +──────────────────+
                    |     CognoDB      |  | Jooble Job Search|
                    |  Graph Database  |  |       API        |
                    +──────────────────+  +──────────────────+
```

A simple project structure and graph data model are included below.

## Graph Data Model


(Candidate)
    |
    |-──[:HAS_SKILL]──────────►(:Skill)
    |                              ^
    |                              |
    |                              |[:REQUIRES]
    |                              |
    |                         (:Job)
    |                           ^
    |                           |
    |                       [:FOR_JOB]
    |                           |
    v                           |
(:Application) ─────────────────+

Candidate ──[:SUBMITTED]──► Application ──[:FOR_JOB]──► Job
Candidate ──[:HAS_SKILL]──────────────────────────────► Skill
Job       ──[:REQUIRES]───────────────────────────────► Skill


### Main entities

| Node | Important properties | Purpose |
|---|---|---|
| `Candidate` | `id`, `name`, `email`, `phone` | User profile and identity |
| `Skill` | `id`, `name` | Normalized candidate/job skill vocabulary |
| `Job` | `id`, `title`, `location`, `salaryMin`, `salaryMax`, `experienceMin`, `experienceMax`, `applicationUrl` | Job opportunities |
| `Application` | `id`, `matchPercentage`, `decision`, `status`, `createdAt`, `appliedAt` | Candidate application tracking |
| `User` | `email`, `passwordHash`, `candidateId` | Authentication account |

## Relationship Model

- Candidate -[:HAS_SKILL]-> Skill`
- Job -[:REQUIRES]-> Skill`
-Candidate -[:SUBMITTED]-> Application`
- Application -[:FOR_JOB]-> Job`

For externally discovered jobs, JobFlow can create/merge a `Job` node and record an `Application` with `status = APPLIED` before opening the external application URL.

## Example Graph Queries

The project uses parameterized Cypher through the Neo4j driver rather than string-concatenated Cypher. The queries use parameters through the Neo4j Java driver instead of building Cypher strings with user input.

### 1. Candidate skills

cypher
MATCH (c:Candidate {id: $candidateId})
      -[:HAS_SKILL]->(s:Skill)
RETURN DISTINCT s.name AS skill;


### 2. Job match calculation

cypher
MATCH (c:Candidate {id: $candidateId})
MATCH (j:Job)-[:REQUIRES]->(required:Skill)

WITH c, j, collect(DISTINCT required) AS requiredSkills
UNWIND requiredSkills AS requiredSkill

OPTIONAL MATCH (c)-[:HAS_SKILL]->(matchedSkill:Skill)
WHERE matchedSkill.id = requiredSkill.id

WITH j,
     requiredSkills,
     collect(DISTINCT matchedSkill) AS matchedSkillNodes

WITH j,
     requiredSkills,
     [s IN matchedSkillNodes WHERE s IS NOT NULL] AS matchedSkills

WITH j,
     requiredSkills,
     matchedSkills,
     [s IN requiredSkills
      WHERE NOT any(m IN matchedSkills WHERE m.id = s.id)] AS missingSkills

RETURN
    j.id AS jobId,
    j.title AS job,
    [s IN matchedSkills | s.name] AS matchedSkills,
    [s IN missingSkills | s.name] AS missingSkills,
    size(requiredSkills) AS requiredSkillCount,
    size(matchedSkills) AS matchedSkillCount,
    round(
        (toFloat(size(matchedSkills)) / size(requiredSkills)) * 100
    ) AS matchPercentage
ORDER BY matchPercentage DESC;


### 3. Multi-hop application tracking query

This traverses more than one relationship and connects a candidate to the jobs they applied for:

cypher
MATCH (c:Candidate {id: $candidateId})
      -[:SUBMITTED]->(a:Application)
      -[:FOR_JOB]->(j:Job)
RETURN
    a.id AS applicationId,
    j.id AS jobId,
    j.title AS jobTitle,
    a.matchPercentage AS matchPercentage,
    a.decision AS decision,
    a.status AS status,
    a.createdAt AS createdAt
ORDER BY a.createdAt DESC;


 4. External-application tracking

The external application flow uses a graph write that associates the candidate, discovered job, and application:

cypher
MATCH (c:Candidate {id: $candidateId})
MERGE (j:Job {id: $jobId})
SET
    j.title = $jobTitle,
    j.applicationUrl = $applicationUrl,
    j.company = $company,
    j.location = $location

MERGE (a:Application {id: $applicationId})
SET
    a.matchPercentage = 0,
    a.decision = "EXTERNAL_APPLY",
    a.status = "APPLIED",
    a.createdAt = coalesce(a.createdAt, $createdAt),
    a.appliedAt = $appliedAt

MERGE (c)-[:SUBMITTED]->(a)
MERGE (a)-[:FOR_JOB]->(j);


Key Features

### Authentication

- Candidate registration
- Login
- JWT token generation
- JWT validation for protected API endpoints
- Password hashing with BCrypt
- Logout by clearing client-side token/session data

### Resume & Skills

- Resume upload
- Resume list and deletion
- Skill extraction
- Candidate skills stored in the graph
- New users without a resume start with no matched jobs

### Job Matching

- Required-vs-candidate skill comparison
- Matched skill list
- Missing skill list
- Match percentage
- Strong match / manual review decisioning
- Location, salary, and experience filters

### Live Job Search

- Jooble-powered Internet job search
- Search by keyword/skills
- Search using extracted resume skills
- Search across multiple locations
- Minimum salary filter
- Posted-date range filter
- Search results automatically saved/merged into the graph
- External application URL redirection

### Application Tracking

- Internal job applications
- External job application tracking
- Clicking **Apply** records the application as `APPLIED` before redirecting
- Application history visible in the Applications page

## Project Structure

text
jobflow-graph/
|-── frontend/
|   |-── src/
|   |   |-── components/
|   |   |   |-── JobFilters.jsx
|   |   |   |-── Jobcard.jsx
|   |   |   +── Navbar.jsx
|   |   |-── pages/
|   |   |   |-── Applications.jsx
|   |   |   |-── Dashboard.jsx
|   |   |   |-── JobSearch.jsx
|   |   |   |-── Login.jsx
|   |   |   |-── Profile1.jsx
|   |   |   |-── Register.jsx
|   |   |   +── Resume.jsx
|   |   +── services/
|   |       +── api.js
|   |-── package.json
|   |-── package-lock.json
|   +── vite.config.js
|
|-── demo-job/
|   |-── src/main/java/com/example/demo/
|   |   |-── config/
|   |   |-── controller/
|   |   |-── dto/
|   |   |-── repository/
|   |   +── service/
|   |-── src/main/resources/
|   |   |-── application.properties
|   |   +── cypher/seed.cypher
|   |-── Dockerfile
|   |-── .dockerignore
|   |-── pom.xml
|   |-── mvnw
|   +── mvnw.cmd
|
+── README.md


## Environment Variables

Secrets are intentionally loaded from environment variables rather than committed to GitHub. This follows the assignment requirement that connection details and credentials must never be committed to the repository.

Backend environment variables:


COGNODB_URI=bolt+s://<instance-id>.databases.cognodb.cloud
COGNODB_USERNAME=cognodb
COGNODB_PASSWORD=<your-cognodb-password>
JOOBLE_API_KEY=<your-jooble-api-key>


The application reads them from `application.properties`:


spring.application.name=demo
server.port=${PORT:1918}

spring.neo4j.uri=${COGNODB_URI}
spring.neo4j.authentication.username=${COGNODB_USERNAME}
spring.neo4j.authentication.password=${COGNODB_PASSWORD}
jooble.api.key=${JOOBLE_API_KEY}


## CognoDB Setup

The WEXA assignment specifies CognoDB as the graph database layer and describes the free `c0` instance and Bolt connection details.

1. Create a CognoDB Cloud account.
2. Create a free `c0` instance and select a region.
3. Save the generated Bolt URI and password.
4. Use username `cognodb` and configure the application with environment variables.
5. Keep the password out of Git and deployment logs.

The assignment notes that the free instance is intentionally small (0.5 vCPU burstable, 256 MB RAM, 1 GB disk, and up to 200 connections), so the demonstration dataset should remain appropriately sized.

## Seed Data

The repository includes:

demo-job/src/main/resources/cypher/seed.cypher


Use this script to load realistic/demo graph data before testing matching and traversal flows. The assignment requires realistic seed data loaded by a script included in the repository.

## Local Setup

### Prerequisites

- Java 17+
- Maven wrapper included in the backend
- Node.js + npm
- A CognoDB instance
- A Jooble API key for Internet job search

### 1. Clone


git clone https://github.com/mariyaraju41/jobflow-graph.git
cd jobflow-graph


### 2. Configure backend environment variables

Windows PowerShell example:


$env:COGNODB_URI="bolt+s://<instance-id>.databases.cognodb.cloud"
$env:COGNODB_USERNAME="cognodb"
$env:COGNODB_PASSWORD="<your-password>"
$env:JOOBLE_API_KEY="<your-jooble-key>"

### 3. Run backend

cd demo-job
./mvnw spring-boot:run


The local API runs on port `1918` by default.

### 4. Run frontend

In a second terminal:

cd frontend
npm install
npm run dev```

The Vite development server normally runs on `http://localhost:5173`.

## Docker / Deployment

### Backend

The backend is containerized with `demo-job/Dockerfile` and deployed as a Docker web service on Render.

### Frontend

The frontend is a Vite/React application deployed on Vercel.

### Production URLs


Frontend: https://jobflow-graph-63gt.vercel.app
Backend:  https://jobflow-graph.onrender.com
API:      https://jobflow-graph.onrender.com/api


## API Overview

### Authentication


POST /api/auth/register
POST /api/auth/login


### Profile


GET /api/profile/{candidateId}
PUT /api/profile/{candidateId}


### Job matching


GET /api/jobs/matches/{candidateId}
GET /api/jobs/matches/{candidateId}/filter


### Internet job search


GET /api/job-search/internet/{candidateId}


Supported query parameters include:


keywords
location
minSalary
dateRangeDays


### Resumes


POST   /api/resumes/upload
GET    /api/resumes/{candidateId}
DELETE /api/resumes/{candidateId}/{resumeId}


### Applications


POST /api/applications
POST /api/applications/external
GET  /api/applications/{candidateId}


### Health



GET /api/health


## Error Handling & Empty States

JobFlow is designed to handle common failure states without silently failing:

- missing candidate session
- unable to load jobs
- unable to load resumes
- failed job search
- failed application tracking
- empty job-match result
- external application URL unavailable
- database connectivity/configuration errors during startup

The assignment explicitly evaluates graceful database-unreachable handling, clean loading/empty/error states, and thoughtful UX.





## Design / UX Highlights

- Clear top-level navigation for Jobs, Resume, Applications, and Profile
- Loading states for API-backed views
- Empty states when a candidate has no matching jobs or resumes
- Consistent blue oval action buttons for search, upload, and apply actions
- Responsive layout for smaller screens
- Profile and application views designed for non-technical users

The assignment explicitly makes design effort, readable typography, sensible navigation, loading states, and empty states part of the evaluation.

## Security Notes

- Passwords are hashed with BCrypt.
- JWT authentication protects private endpoints.
- CORS is restricted to the deployed Vercel frontend pattern plus local development.
- Database credentials and external API keys are supplied through environment variables.
- No secrets should be committed to the repository.

## Submission Checklist

The WEXA assignment asks for a GitHub repository, README, hosted demo, and a short screen recording; the repository must include the application source, data-loading scripts, and Cypher queries.

Before submission:

- [x] Full frontend source code
- [x] Full Spring Boot backend source code
- [x] CognoDB graph database integration
- [x] Seed Cypher script
- [x] Parameterized Neo4j/Cypher queries
- [x] Multi-hop graph traversal
- [x] Skill-based job matching
- [x] Resume upload and extraction
- [x] Authentication and JWT
- [x] Internet job search
- [x] Application tracking
- [x] Hosted frontend demo
- [x] Hosted backend
- [ ] Final UI screenshots added to `docs/screenshots/`
- [ ] Short screen recording prepared
- [ ] Final README reviewed before submission

## Assignment Reference

This project was built against the WEXA AI take-home brief, which asks candidates to choose a real-world graph-friendly problem, provide a thoughtful graph model and seed data, implement Cypher traversals with an official Neo4j driver, build a functional UI, keep credentials in environment variables, and provide a hosted demo.

The brief also states that AI coding assistants are permitted, provided the candidate can explain and defend the implementation during the follow-up interview.


**Repository:** https://github.com/mariyaraju41/jobflow-graph

**Frontend:** https://jobflow-graph-63gt.vercel.app

**Backend:** https://jobflow-graph.onrender.com
