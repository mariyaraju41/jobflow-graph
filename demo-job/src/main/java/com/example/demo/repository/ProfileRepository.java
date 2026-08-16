package com.example.demo.repository;

import java.util.Map;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.springframework.stereotype.Repository;

@Repository
public class ProfileRepository {

    private final Driver driver;

    public ProfileRepository(Driver driver) {
        this.driver = driver;
    }
    public void updateProfile(
            String candidateId,
            String name,
            String phone) {

        String cypher = """
            MATCH (c:Candidate {id: $candidateId})

            SET
                c.name = $name,
                c.phone = $phone

            RETURN c
            """;

        try (Session session = driver.session()) {

            session.run(
                    cypher,
                    Map.of(
                            "candidateId", candidateId,
                            "name", name,
                            "phone", phone
                    )
            ).consume();
        }
    }

    public Map<String, Object> getProfile(
            String candidateId) {

        String cypher = """
            MATCH (c:Candidate {id: $candidateId})

            OPTIONAL MATCH (c)-[:HAS_SKILL]->(s:Skill)

            OPTIONAL MATCH (c)-[:HAS_RESUME]->(r:Resume)

            OPTIONAL MATCH (c)-[:SUBMITTED]->(a:Application)

            RETURN
                c.id AS candidateId,
                c.name AS name,
                c.email AS email,
                c.phone AS phone,
                collect(DISTINCT s.name) AS skills,
                count(DISTINCT r) AS resumeCount,
                count(DISTINCT a) AS applicationCount
            """;

        try (Session session = driver.session()) {

            return session.run(
                    cypher,
                    Map.of(
                            "candidateId",
                            candidateId
                    )
            ).single().asMap();
        }
    }
}