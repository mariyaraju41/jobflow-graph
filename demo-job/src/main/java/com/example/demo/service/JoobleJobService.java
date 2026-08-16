package com.example.demo.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import com.example.demo.dto.InternetJobResponse;
import com.example.demo.dto.JoobleJob;
import com.example.demo.dto.JoobleResponse;
import com.example.demo.repository.JobRepository;

@Service
public class JoobleJobService {

    private static final int AUTO_PREPARE_THRESHOLD = 80;

    private final RestClient restClient;
    private final JobRepository jobRepository;
    private final SkillExtractionService skillExtractionService;

    @Value("${jooble.api.key}")
    private String apiKey;


    public JoobleJobService(
            JobRepository jobRepository,
            SkillExtractionService skillExtractionService) {

        this.jobRepository = jobRepository;
        this.skillExtractionService = skillExtractionService;

        this.restClient = RestClient
                .builder()
                .baseUrl("https://in.jooble.org")
                .build();
    }


    // =====================================================
    // SEARCH INTERNET JOBS
    // =====================================================

    public List<InternetJobResponse> searchJobs(
            String candidateId,
            String keywords,
            String location,
            Integer minSalary,
            Integer dateRangeDays) {

        List<String> candidateSkills =
                jobRepository.findCandidateSkills(candidateId);


        Map<String, Object> request = Map.of(
                "keywords",
                keywords == null
                        ? "Java Developer"
                        : keywords,

                "location",
                location == null
                        ? ""
                        : location,

                "salary",
                minSalary == null
                        ? 0
                        : minSalary,

                "page",
                "1",

                "ResultOnPage",
                "20",

                "companysearch",
                "false"
        );


        try {

            JoobleResponse response =
                    restClient.post()
                            .uri("/api/" + apiKey)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(request)
                            .retrieve()
                            .body(JoobleResponse.class);


            if (response == null ||
                    response.getJobs() == null) {

                return List.of();
            }


            return response.getJobs()
                    .stream()

                    // DATE FILTER
                    .filter(job ->
                            matchesDateFilter(
                                    job,
                                    dateRangeDays
                            ))

                    // SKILL MATCHING
                    .map(job ->
                            calculateMatch(
                                    job,
                                    candidateSkills
                            ))

                    // HIGHEST MATCH FIRST
                    .sorted(
                            (a, b) ->
                                    Integer.compare(
                                            b.getMatchPercentage(),
                                            a.getMatchPercentage()
                                    )
                    )

                    .toList();


        } catch (HttpClientErrorException e) {

            System.out.println(
                    "Jooble API Error: "
                    + e.getStatusCode()
            );

            System.out.println(
                    "Jooble Response: "
                    + e.getResponseBodyAsString()
            );

            throw new RuntimeException(
                    "Jooble API rejected the request."
            );
        }
    }


    // =====================================================
    // DATE FILTER
    // =====================================================

    private boolean matchesDateFilter(
            JoobleJob job,
            Integer dateRangeDays) {

        // No date filter
        if (dateRangeDays == null ||
                dateRangeDays <= 0) {

            return true;
        }


        if (job.getUpdated() == null ||
                job.getUpdated().isBlank()) {

            return false;
        }


        try {

            String dateValue =
                    job.getUpdated();


            /*
             * Example:
             *
             * 2026-08-13T00:00:00.0000000
             *
             * We normalize the fractional
             * seconds before parsing.
             */

            if (dateValue.contains(".")) {

                String[] parts =
                        dateValue.split("\\.");

                String base =
                        parts[0];

                String fraction =
                        parts[1]
                                .replaceAll(
                                        "[^0-9].*",
                                        ""
                                );


                // Java supports maximum 9 digits
                if (fraction.length() > 9) {

                    fraction =
                            fraction.substring(
                                    0,
                                    9
                            );
                }


                while (fraction.length() < 9) {

                    fraction += "0";
                }


                dateValue =
                        base
                        + "."
                        + fraction;
            }


            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern(
                            "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS"
                    );


            LocalDateTime updatedDate =
                    LocalDateTime.parse(
                            dateValue,
                            formatter
                    );


            LocalDateTime now =
                    LocalDateTime.now();


            long hours =
                    Duration.between(
                            updatedDate,
                            now
                    ).toHours();


            return hours >= 0 &&
                    hours <= dateRangeDays * 24L;


        } catch (Exception e) {

            System.out.println(
                    "Unable to parse Jooble date: "
                    + job.getUpdated()
            );

            return false;
        }
    }


    // =====================================================
    // SKILL MATCHING
    // =====================================================

    private InternetJobResponse calculateMatch(
            JoobleJob job,
            List<String> candidateSkills) {


        /*
         * Combine job title + Jooble snippet.
         */
        String jobText =
                safe(job.getTitle())
                + " "
                + safe(job.getSnippet());


        /*
         * Extract technical skills
         * from this particular job.
         */
        List<String> requiredSkills =
                skillExtractionService.extractSkills(
                        jobText
                );


        /*
         * Convert candidate skills to lowercase
         * for case-insensitive comparison.
         */
        Set<String> candidateSkillSet =
                candidateSkills.stream()
                        .map(skill ->
                                skill.toLowerCase(
                                        Locale.ROOT
                                )
                        )
                        .collect(
                                Collectors.toSet()
                        );


        /*
         * Skills candidate has.
         */
        List<String> matchedSkills =
                requiredSkills.stream()
                        .filter(skill ->
                                candidateSkillSet.contains(
                                        skill.toLowerCase(
                                                Locale.ROOT
                                        )
                                )
                        )
                        .distinct()
                        .toList();


        /*
         * Skills candidate is missing.
         */
        List<String> missingSkills =
                requiredSkills.stream()
                        .filter(skill ->
                                !candidateSkillSet.contains(
                                        skill.toLowerCase(
                                                Locale.ROOT
                                        )
                                )
                        )
                        .distinct()
                        .toList();


        /*
         * Number of skills required by this job.
         */
        int requiredSkillCount =
                requiredSkills.size();


        /*
         * Number of required skills
         * candidate actually has.
         */
        int matchedSkillCount =
                matchedSkills.size();


        /*
         * Correct formula:
         *
         * matched required skills
         * ----------------------- × 100
         * total required skills
         */
        int percentage;


        if (requiredSkillCount == 0) {

            percentage = 0;

        } else {

            percentage =
                    (int) Math.round(
                            matchedSkillCount
                            * 100.0
                            / requiredSkillCount
                    );
        }


        // =================================================
        // DECISION
        // =================================================

        String decision;

        String recommendation;


        if (percentage >=
                AUTO_PREPARE_THRESHOLD) {

            decision =
                    "AUTO_PREPARE";

            recommendation =
                    "Strong match. Application can be prepared automatically.";

        } else {

            decision =
                    "MANUAL_REVIEW";

            recommendation =
                    "Review skill gaps before applying.";
        }


        // =================================================
        // RESPONSE
        // =================================================

        return new InternetJobResponse(

                String.valueOf(
                        job.getId()
                ),

                safe(job.getTitle()),

                safe(job.getCompany()),

                safe(job.getLocation()),

                safe(job.getSalary()),

                safe(job.getSource()),

                safe(job.getLink()),

                safe(job.getType()),

                safe(job.getUpdated()),

                matchedSkills,

                missingSkills,

                requiredSkillCount,

                matchedSkillCount,

                percentage,

                decision,

                recommendation
        );
    }


    // =====================================================
    // NULL-SAFE STRING
    // =====================================================

    private String safe(String value) {

        return value == null
                ? ""
                : value;
    }
}