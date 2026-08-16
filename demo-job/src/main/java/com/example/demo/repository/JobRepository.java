package com.example.demo.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.springframework.stereotype.Repository;

import com.example.demo.dto.JobMatchResponse;

@Repository
public class JobRepository {

    private final Driver driver;


    public JobRepository(Driver driver) {

        this.driver = driver;
    }


    // ============================================================
    // FIND ALL JOB MATCHES
    // ============================================================

    public List<JobMatchResponse> findJobMatches(
            String candidateId) {

        String cypher = """

            MATCH (c:Candidate {id: $candidateId})

            MATCH (j:Job)-[:REQUIRES]->(required:Skill)

            WITH
                c,
                j,
                collect(DISTINCT required) AS requiredSkills

            UNWIND requiredSkills AS requiredSkill

            OPTIONAL MATCH
                (c)-[:HAS_SKILL]->(matchedSkill:Skill)

            WHERE
                toLower(matchedSkill.name)
                =
                toLower(requiredSkill.name)

            WITH
                j,
                requiredSkills,
                collect(DISTINCT matchedSkill)
                    AS matchedSkillNodes

            WITH
                j,
                requiredSkills,

                [
                    s IN matchedSkillNodes
                    WHERE s IS NOT NULL
                ]
                AS matchedSkills

            WITH
                j,
                requiredSkills,
                matchedSkills,

                [
                    s IN requiredSkills
                    WHERE NOT any(
                        m IN matchedSkills
                        WHERE
                            toLower(m.name)
                            =
                            toLower(s.name)
                    )
                ]
                AS missingSkills

            RETURN

                j.id
                    AS jobId,

                j.title
                    AS job,

                [s IN matchedSkills | s.name]
                    AS matchedSkills,

                [s IN missingSkills | s.name]
                    AS missingSkills,

                size(requiredSkills)
                    AS requiredSkillCount,

                size(matchedSkills)
                    AS matchedSkillCount,

                CASE

                    WHEN size(requiredSkills) = 0
                    THEN 0

                    ELSE round(
                        (
                            toFloat(size(matchedSkills))
                            /
                            size(requiredSkills)
                        ) * 100
                    )

                END
                    AS matchPercentage,

                j.applicationUrl
                    AS applicationUrl

            ORDER BY matchPercentage DESC

            """;


        try (Session session = driver.session()) {

            return session.run(
                    cypher,
                    Map.of(
                            "candidateId",
                            candidateId
                    )
            ).list(
                    this::mapRecord
            );
        }
    }


    // ============================================================
    // FIND ONE JOB MATCH
    // ============================================================

    public JobMatchResponse findJobMatch(
            String candidateId,
            String jobId) {

        String cypher = """

            MATCH (c:Candidate {id: $candidateId})

            MATCH (j:Job {id: $jobId})
                  -[:REQUIRES]->
                  (required:Skill)

            WITH
                c,
                j,
                collect(DISTINCT required)
                    AS requiredSkills

            UNWIND requiredSkills AS requiredSkill

            OPTIONAL MATCH
                (c)-[:HAS_SKILL]->(matchedSkill:Skill)

            WHERE
                toLower(matchedSkill.name)
                =
                toLower(requiredSkill.name)

            WITH
                j,
                requiredSkills,
                collect(DISTINCT matchedSkill)
                    AS matchedSkillNodes

            WITH
                j,
                requiredSkills,

                [
                    s IN matchedSkillNodes
                    WHERE s IS NOT NULL
                ]
                AS matchedSkills

            WITH
                j,
                requiredSkills,
                matchedSkills,

                [
                    s IN requiredSkills
                    WHERE NOT any(
                        m IN matchedSkills
                        WHERE
                            toLower(m.name)
                            =
                            toLower(s.name)
                    )
                ]
                AS missingSkills

            RETURN

                j.id
                    AS jobId,

                j.title
                    AS job,

                [s IN matchedSkills | s.name]
                    AS matchedSkills,

                [s IN missingSkills | s.name]
                    AS missingSkills,

                size(requiredSkills)
                    AS requiredSkillCount,

                size(matchedSkills)
                    AS matchedSkillCount,

                CASE

                    WHEN size(requiredSkills) = 0
                    THEN 0

                    ELSE round(
                        (
                            toFloat(size(matchedSkills))
                            /
                            size(requiredSkills)
                        ) * 100
                    )

                END
                    AS matchPercentage,

                j.applicationUrl
                    AS applicationUrl

            """;


        try (Session session = driver.session()) {

            List<Record> records =
                    session.run(
                            cypher,
                            Map.of(
                                    "candidateId",
                                    candidateId,

                                    "jobId",
                                    jobId
                            )
                    ).list();


            if (records.isEmpty()) {

                return null;
            }


            return mapRecord(
                    records.get(0)
            );
        }
    }


    // ============================================================
    // FIND JOB MATCHES WITH FILTERS
    // ============================================================

    public List<JobMatchResponse> findJobMatches(
            String candidateId,
            String location,
            Integer minSalary,
            Integer experience) {

        String cypher = """

            MATCH (c:Candidate {id: $candidateId})

            MATCH (j:Job)-[:REQUIRES]->(required:Skill)

            WHERE

                (
                    $location IS NULL

                    OR $location = ""

                    OR toLower(
                        coalesce(j.location, "")
                    )
                    =
                    toLower($location)
                )

                AND

                (
                    $minSalary IS NULL

                    OR coalesce(
                        j.salaryMax,
                        0
                    )
                    >= $minSalary
                )

                AND

                (
                    $experience IS NULL

                    OR (

                        $experience
                        >= coalesce(
                            j.experienceMin,
                            0
                        )

                        AND

                        $experience
                        <= coalesce(
                            j.experienceMax,
                            100
                        )

                    )
                )

            WITH
                c,
                j,
                collect(DISTINCT required)
                    AS requiredSkills

            UNWIND requiredSkills AS requiredSkill

            OPTIONAL MATCH
                (c)-[:HAS_SKILL]->(matchedSkill:Skill)

            WHERE
                toLower(matchedSkill.name)
                =
                toLower(requiredSkill.name)

            WITH
                j,
                requiredSkills,
                collect(DISTINCT matchedSkill)
                    AS matchedSkillNodes

            WITH
                j,
                requiredSkills,

                [
                    s IN matchedSkillNodes
                    WHERE s IS NOT NULL
                ]
                AS matchedSkills

            WITH
                j,
                requiredSkills,
                matchedSkills,

                [
                    s IN requiredSkills
                    WHERE NOT any(
                        m IN matchedSkills
                        WHERE
                            toLower(m.name)
                            =
                            toLower(s.name)
                    )
                ]
                AS missingSkills

            RETURN

                j.id
                    AS jobId,

                j.title
                    AS job,

                [s IN matchedSkills | s.name]
                    AS matchedSkills,

                [s IN missingSkills | s.name]
                    AS missingSkills,

                size(requiredSkills)
                    AS requiredSkillCount,

                size(matchedSkills)
                    AS matchedSkillCount,

                CASE

                    WHEN size(requiredSkills) = 0
                    THEN 0

                    ELSE round(
                        (
                            toFloat(size(matchedSkills))
                            /
                            size(requiredSkills)
                        ) * 100
                    )

                END
                    AS matchPercentage,

                j.applicationUrl
                    AS applicationUrl

            ORDER BY matchPercentage DESC

            """;


        Map<String, Object> parameters =
                new HashMap<>();


        parameters.put(
                "candidateId",
                candidateId
        );


        parameters.put(
                "location",
                location
        );


        parameters.put(
                "minSalary",
                minSalary
        );


        parameters.put(
                "experience",
                experience
        );


        try (Session session = driver.session()) {

            return session.run(
                    cypher,
                    parameters
            ).list(
                    this::mapRecord
            );
        }
    }


    // ============================================================
    // SAVE INTERNET JOB
    // ============================================================
    //
    // This is used by the internet-job search service.
    //
    // If the same external job is found again,
    // MERGE prevents duplicate Job nodes.
    //
    // ============================================================

    public String saveInternetJob(
            String externalId,
            String title,
            String company,
            String location,
            String applicationUrl,
            String postedAt,
            Integer salaryMin,
            Integer salaryMax,
            Integer experienceMin,
            Integer experienceMax) {

        String jobId =
                "WEB-" + externalId;


        String cypher = """

            MERGE (j:Job {
                externalId: $externalId
            })

            ON CREATE SET
                j.id = $jobId

            SET

                j.title =
                    $title,

                j.company =
                    $company,

                j.location =
                    $location,

                j.applicationUrl =
                    $applicationUrl,

                j.postedAt =
                    $postedAt,

                j.salaryMin =
                    $salaryMin,

                j.salaryMax =
                    $salaryMax,

                j.experienceMin =
                    $experienceMin,

                j.experienceMax =
                    $experienceMax,

                j.source =
                    "JOOBLE"

            RETURN j.id AS jobId

            """;


        Map<String, Object> parameters =
                new HashMap<>();


        parameters.put(
                "externalId",
                externalId
        );


        parameters.put(
                "jobId",
                jobId
        );


        parameters.put(
                "title",
                title
        );


        parameters.put(
                "company",
                company
        );


        parameters.put(
                "location",
                location
        );


        parameters.put(
                "applicationUrl",
                applicationUrl
        );


        parameters.put(
                "postedAt",
                postedAt
        );


        parameters.put(
                "salaryMin",
                salaryMin
        );


        parameters.put(
                "salaryMax",
                salaryMax
        );


        parameters.put(
                "experienceMin",
                experienceMin
        );


        parameters.put(
                "experienceMax",
                experienceMax
        );


        try (Session session = driver.session()) {

            return session.run(
                    cypher,
                    parameters
            )
            .single()
            .get("jobId")
            .asString();
        }
    }


    // ============================================================
    // ADD JOB SKILLS
    // ============================================================
    //
    // Example:
    //
    // Job
    //  |
    //  +-- REQUIRES --> Java
    //  +-- REQUIRES --> Spring Boot
    //  +-- REQUIRES --> SQL
    //
    // ============================================================

    public void saveJobSkills(
            String jobId,
            List<String> skills) {

        if (
                skills == null
                ||
                skills.isEmpty()
        ) {

            return;
        }


        String cypher = """

            MATCH (j:Job {
                id: $jobId
            })

            UNWIND $skills AS skillName

            MERGE (s:Skill {
                name: skillName
            })

            MERGE
                (j)-[:REQUIRES]->(s)

            """;


        try (Session session = driver.session()) {

            session.run(
                    cypher,
                    Map.of(
                            "jobId",
                            jobId,

                            "skills",
                            skills
                    )
            ).consume();
        }
    }


    // ============================================================
    // FIND CANDIDATE SKILLS
    // ============================================================

    public List<String> findCandidateSkills(
            String candidateId) {

        String cypher = """

            MATCH
                (c:Candidate {
                    id: $candidateId
                })
                -[:HAS_SKILL]->
                (s:Skill)

            RETURN DISTINCT
                s.name AS skill

            ORDER BY skill

            """;


        try (Session session = driver.session()) {

            return session.run(
                    cypher,
                    Map.of(
                            "candidateId",
                            candidateId
                    )
            )
            .list(
                    record ->
                            record
                            .get("skill")
                            .asString()
            );
        }
    }


    // ============================================================
    // CHECK IF CANDIDATE HAS RESUME SKILLS
    // ============================================================

    public boolean candidateHasSkills(
            String candidateId) {

        String cypher = """

            MATCH
                (c:Candidate {
                    id: $candidateId
                })
                -[:HAS_SKILL]->
                (s:Skill)

            RETURN count(s) > 0
                AS hasSkills

            """;


        try (Session session = driver.session()) {

            return session.run(
                    cypher,
                    Map.of(
                            "candidateId",
                            candidateId
                    )
            )
            .single()
            .get("hasSkills")
            .asBoolean();
        }
    }


    // ============================================================
    // GET APPLICATION URL
    // ============================================================

    public String findApplicationUrl(
            String jobId) {

        String cypher = """

            MATCH (j:Job {
                id: $jobId
            })

            RETURN
                j.applicationUrl
                AS applicationUrl

            """;


        try (Session session = driver.session()) {

            List<Record> records =
                    session.run(
                            cypher,
                            Map.of(
                                    "jobId",
                                    jobId
                            )
                    ).list();


            if (records.isEmpty()) {

                return null;
            }


            return records
                    .get(0)
                    .get("applicationUrl")
                    .asString("");
        }
    }


    // ============================================================
    // MAP NEO4J RECORD → DTO
    // ============================================================

    private JobMatchResponse mapRecord(
            Record record) {

        List<String> matchedSkills =
                record
                .get("matchedSkills")
                .asList(
                    value -> value.asString()
                );


        List<String> missingSkills =
                record
                .get("missingSkills")
                .asList(
                    value -> value.asString()
                );


        String applicationUrl =
                record
                .get("applicationUrl")
                .isNull()
                    ? ""
                    : record
                        .get("applicationUrl")
                        .asString();


        return new JobMatchResponse(

                record
                    .get("jobId")
                    .asString(),

                record
                    .get("job")
                    .asString(),

                matchedSkills,

                missingSkills,

                record
                    .get("requiredSkillCount")
                    .asInt(),

                record
                    .get("matchedSkillCount")
                    .asInt(),

                record
                    .get("matchPercentage")
                    .asInt(),

                "",

                "",

                applicationUrl
        );
    }
}