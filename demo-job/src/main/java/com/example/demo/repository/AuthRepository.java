package com.example.demo.repository;

import java.util.Map;
import java.util.UUID;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.springframework.stereotype.Repository;

@Repository
public class AuthRepository {

    private final Driver driver;

    public AuthRepository(Driver driver) {
        this.driver = driver;
    }


    /*
     * ============================================================
     * CREATE USER + CANDIDATE
     * ============================================================
     */

    public String createUser(
            String name,
            String email,
            String phone,
            String passwordHash) {

        String candidateId =
                "U-" +
                UUID.randomUUID()
                    .toString()
                    .substring(0, 8)
                    .toUpperCase();


        String cypher = """
            CREATE (c:Candidate {
                id: $candidateId,
                name: $name,
                email: $email,
                phone: $phone
            })

            CREATE (u:User {
                email: $email,
                passwordHash: $passwordHash,
                candidateId: $candidateId
            })

            CREATE (u)-[:ACCOUNT_FOR]->(c)

            RETURN c.id AS candidateId
            """;


        try (Session session = driver.session()) {

            return session.executeWrite(tx -> {

                Record record =
                    tx.run(
                        cypher,
                        Map.of(
                            "candidateId", candidateId,
                            "name", name,
                            "email", email,
                            "phone", phone,
                            "passwordHash", passwordHash
                        )
                    ).single();

                return record
                    .get("candidateId")
                    .asString();
            });
        }
    }


    /*
     * ============================================================
     * CHECK EMAIL
     * ============================================================
     */

    public boolean userExists(String email) {

        String cypher = """
            MATCH (u:User {email: $email})
            RETURN count(u) AS count
            """;


        try (Session session = driver.session()) {

            Record record =
                session.run(
                    cypher,
                    Map.of("email", email)
                ).single();

            return record
                .get("count")
                .asInt() > 0;
        }
    }


    /*
     * ============================================================
     * FIND USER FOR LOGIN
     * ============================================================
     */

    public Record findUser(String email) {

        String cypher = """
            MATCH (u:User {email: $email})

            RETURN
                u.email AS email,
                u.passwordHash AS passwordHash,
                u.candidateId AS candidateId
            """;


        try (Session session = driver.session()) {

            return session.run(
                cypher,
                Map.of("email", email)
            ).single();
        }
    }
}