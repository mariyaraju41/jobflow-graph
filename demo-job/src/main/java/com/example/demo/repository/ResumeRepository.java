package com.example.demo.repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.Record;
import org.springframework.stereotype.Repository;

@Repository
public class ResumeRepository {

    private final Driver driver;

    public ResumeRepository(Driver driver) {
        this.driver = driver;
    }


    // ============================================================
    // SAVE RESUME
    // ============================================================

    public String saveResume(
            String candidateId,
            String fileName,
            String email,
            String phone,
            List<String> skills) {

        String resumeId =
                "RES-" +
                java.util.UUID.randomUUID()
                    .toString()
                    .substring(0, 8);

        String cypher = """
            MATCH (c:Candidate {id: $candidateId})

            CREATE (r:Resume {
                id: $resumeId,
                fileName: $fileName,
                email: $email,
                phone: $phone,
                uploadedAt: $uploadedAt
            })

            CREATE (c)-[:HAS_RESUME]->(r)

            WITH c

            UNWIND $skills AS skillName

            MERGE (s:Skill {name: skillName})

            MERGE (c)-[:HAS_SKILL]->(s)
            """;


        try (Session session = driver.session()) {

            session.run(
                cypher,
                Map.of(
                    "candidateId", candidateId,
                    "resumeId", resumeId,
                    "fileName", fileName,
                    "email", email,
                    "phone", phone,
                    "skills",
                        skills == null
                            ? List.of()
                            : skills,
                    "uploadedAt",
                        Instant.now().toString()
                )
            ).consume();
        }

        return resumeId;
    }


    // ============================================================
    // GET ALL RESUMES FOR CANDIDATE
    // ============================================================

    public List<Map<String, Object>> getResumes(
            String candidateId) {

        String cypher = """
            MATCH (c:Candidate {id: $candidateId})
                  -[:HAS_RESUME]->(r:Resume)

            RETURN
                r.id AS resumeId,
                r.fileName AS fileName,
                r.email AS email,
                r.phone AS phone,
                r.uploadedAt AS uploadedAt

            ORDER BY r.uploadedAt DESC
            """;


        try (Session session = driver.session()) {

            return session.run(
                    cypher,
                    Map.of(
                        "candidateId",
                        candidateId
                    )
                )
                .list(record -> {

                    Map<String, Object> result =
                        new java.util.HashMap<>();

                    result.put(
                        "resumeId",
                        record.get("resumeId")
                              .asString()
                    );

                    result.put(
                        "fileName",
                        record.get("fileName")
                              .asString()
                    );

                    result.put(
                        "email",
                        record.get("email")
                              .isNull()
                            ? ""
                            : record.get("email")
                                  .asString()
                    );

                    result.put(
                        "phone",
                        record.get("phone")
                              .isNull()
                            ? ""
                            : record.get("phone")
                                  .asString()
                    );

                    result.put(
                        "uploadedAt",
                        record.get("uploadedAt")
                              .asString()
                    );

                    return result;
                });
        }
    }


    // ============================================================
    // DELETE RESUME
    // ============================================================

    public boolean deleteResume(
            String candidateId,
            String resumeId) {

        String cypher = """
            MATCH (c:Candidate {id: $candidateId})
                  -[rel:HAS_RESUME]->
                  (r:Resume {id: $resumeId})

            DELETE rel, r

            RETURN count(r) AS deleted
            """;


        try (Session session = driver.session()) {

            Record record =
                session.run(
                    cypher,
                    Map.of(
                        "candidateId",
                        candidateId,
                        "resumeId",
                        resumeId
                    )
                )
                .single();

            return record
                .get("deleted")
                .asInt() > 0;
        }
    }
}