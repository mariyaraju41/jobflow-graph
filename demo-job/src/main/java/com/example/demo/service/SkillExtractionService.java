package com.example.demo.service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.stereotype.Service;

@Service
public class SkillExtractionService {

    /*
     * Canonical skill names.
     *
     * Different names such as:
     *
     * Apache Kafka -> Kafka
     * ReactJS      -> React
     * React.js     -> React
     * Springboot    -> Spring Boot
     *
     * will all become one skill.
     */
    private static final List<String> KNOWN_SKILLS = List.of(

            // Java
            "Java",
            "Java 8",
            "Java 11",
            "Java 17",
            "Java 21",
            "J2EE",

            // Spring
            "Spring Boot",
            "Spring MVC",
            "Spring Security",
            "Spring Data JPA",
            "Spring",
            "Hibernate",
            "JPA",

            // Backend
            "REST API",
            "REST",
            "Microservices",
            "SQL",
            "MySQL",
            "PostgreSQL",
            "Oracle",

            // Messaging
            "Kafka",
            "RabbitMQ",

            // Cloud
            "AWS",
            "Azure",
            "GCP",

            // DevOps
            "Docker",
            "Kubernetes",
            "Jenkins",
            "CI/CD",

            // Frontend
            "React",
            "Angular",
            "JavaScript",
            "TypeScript",
            "HTML",
            "CSS",

            // Testing
            "JUnit",
            "Mockito",

            // Build
            "Maven",
            "Gradle",

            // Version Control
            "Git",
            "GitHub",
            "Bitbucket"
    );


    public List<String> extractSkills(String text) {

        if (text == null || text.isBlank()) {
            return List.of();
        }


        /*
         * Normalize the job text before searching.
         */
        String normalizedText =
                normalizeText(text);


        Set<String> detected =
                new LinkedHashSet<>();


        for (String skill : KNOWN_SKILLS) {

            String normalizedSkill =
                    normalizeText(skill);


            if (containsSkill(
                    normalizedText,
                    normalizedSkill)) {

                detected.add(skill);
            }
        }


        /*
         * Remove redundant parent skills.
         *
         * Example:
         *
         * Spring Boot
         * Spring
         *
         * We keep both only when both are
         * genuinely mentioned in the job.
         */
        return new ArrayList<>(detected);
    }


    /*
     * Normalize different spellings / aliases.
     */
    private String normalizeText(String text) {

        String value =
                text.toLowerCase(Locale.ROOT);


        /*
         * Kafka aliases
         */
        value = value.replace(
                "apache kafka",
                "kafka"
        );


        /*
         * React aliases
         */
        value = value.replace(
                "reactjs",
                "react"
        );

        value = value.replace(
                "react.js",
                "react"
        );


        /*
         * Spring Boot aliases
         */
        value = value.replace(
                "springboot",
                "spring boot"
        );

        value = value.replace(
                "spring-boot",
                "spring boot"
        );


        /*
         * JavaScript aliases
         */
        value = value.replace(
                "java script",
                "javascript"
        );


        /*
         * REST API aliases
         */
        value = value.replace(
                "restful api",
                "rest api"
        );


        return value;
    }


    private boolean containsSkill(
            String text,
            String skill) {

        /*
         * Word-boundary matching.
         *
         * This prevents:
         *
         * Java
         * from accidentally matching
         * Javascript.
         */

        String pattern =
                "(^|\\W)"
                + java.util.regex.Pattern.quote(skill)
                + "($|\\W)";


        return text.matches(
                "(?s).*"
                + pattern
                + ".*"
        );
    }
}