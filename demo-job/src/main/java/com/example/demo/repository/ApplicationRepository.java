package com.example.demo.repository;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.springframework.stereotype.Repository;

import com.example.demo.dto.ApplicationResponse;
import com.example.demo.dto.ApplicationTrackerResponse;

@Repository
public class ApplicationRepository {

    private final Driver driver;

    public ApplicationRepository(Driver driver) {
        this.driver = driver;
    }


    // ============================================================
    // FIND ALL APPLICATIONS FOR CANDIDATE
    // ============================================================

    public List<ApplicationTrackerResponse> findApplications(
            String candidateId) {

        String cypher = """
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

            ORDER BY a.createdAt DESC
            """;


        try (Session session = driver.session()) {

            return session.run(
                    cypher,
                    Map.of(
                            "candidateId",
                            candidateId
                    )
            ).list(record ->
                    new ApplicationTrackerResponse(
                            record
                                    .get("applicationId")
                                    .asString(),

                            record
                                    .get("jobId")
                                    .asString(),

                            record
                                    .get("jobTitle")
                                    .asString(),

                            record
                                    .get("matchPercentage")
                                    .asInt(),

                            record
                                    .get("decision")
                                    .asString(),

                            record
                                    .get("status")
                                    .asString(),

                            record
                                    .get("createdAt")
                                    .asString()
                    )
            );
        }
    }


    // ============================================================
    // CREATE APPLICATION
    // ============================================================

    public ApplicationResponse createApplication(
            String candidateId,
            String jobId,
            int matchPercentage,
            String decision) {


        String applicationId =
                "APP-" +
                UUID.randomUUID()
                        .toString()
                        .substring(0, 8);


        /*
         * Current application status.
         *
         * AUTO_PREPARE -> PREPARED
         * MANUAL_REVIEW -> REVIEW_REQUIRED
         */

        String status =
                decision.equals("AUTO_PREPARE")
                        ? "PREPARED"
                        : "REVIEW_REQUIRED";


        /*
         * ========================================================
         * CREATE APPLICATION
         * ========================================================
         */

        String cypher = """
            MATCH (c:Candidate {id: $candidateId})
            MATCH (j:Job {id: $jobId})

            CREATE (a:Application {
                id: $applicationId,
                matchPercentage: $matchPercentage,
                decision: $decision,
                status: $status,
                createdAt: $createdAt
            })

            CREATE (c)-[:SUBMITTED]->(a)
            CREATE (a)-[:FOR_JOB]->(j)

            RETURN
                a.id AS applicationId,
                j.id AS jobId,
                j.title AS jobTitle,
                j.applicationUrl AS applicationUrl,
                a.matchPercentage AS matchPercentage,
                a.decision AS decision,
                a.status AS status
            """;


        /*
         * ========================================================
         * PARAMETERS
         * ========================================================
         */

        Map<String, Object> parameters =
                Map.of(
                        "candidateId",
                        candidateId,

                        "jobId",
                        jobId,

                        "applicationId",
                        applicationId,

                        "matchPercentage",
                        matchPercentage,

                        "decision",
                        decision,

                        "status",
                        status,

                        "createdAt",
                        Instant.now().toString()
                );


        /*
         * ========================================================
         * EXECUTE
         * ========================================================
         */

        try (Session session = driver.session()) {

            return session.executeWrite(tx -> {

                Record record =
                        tx.run(
                                cypher,
                                parameters
                        ).single();


                /*
                 * applicationUrl can be null if
                 * the Job doesn't have a URL yet.
                 */

                String applicationUrl = null;


                if (!record
                        .get("applicationUrl")
                        .isNull()) {

                    applicationUrl =
                            record
                                .get("applicationUrl")
                                .asString();
                }


                /*
                 * =================================================
                 * RESPONSE
                 * =================================================
                 */

                return new ApplicationResponse(

                        record
                                .get("applicationId")
                                .asString(),

                        record
                                .get("jobId")
                                .asString(),

                        record
                                .get("jobTitle")
                                .asString(),

                        record
                                .get("matchPercentage")
                                .asInt(),

                        record
                                .get("decision")
                                .asString(),

                        record
                                .get("status")
                                .asString(),

                        applicationUrl
                );
            });
        }
    }
}