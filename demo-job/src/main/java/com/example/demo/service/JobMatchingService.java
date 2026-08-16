package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.dto.JobMatchResponse;
import com.example.demo.repository.JobRepository;

@Service
public class JobMatchingService {

    private static final int AUTO_PREPARE_THRESHOLD = 80;

    private final JobRepository jobRepository;


    public JobMatchingService(
            JobRepository jobRepository) {

        this.jobRepository = jobRepository;
    }


    // ============================================================
    // GET JOB MATCHES
    // ============================================================

    public List<JobMatchResponse> getJobMatches(
            String candidateId) {

        return jobRepository
                .findJobMatches(candidateId)
                .stream()
                .map(this::applyDecision)
                .toList();
    }


    // ============================================================
    // GET JOB MATCHES WITH FILTERS
    // ============================================================

    public List<JobMatchResponse> getJobMatches(
            String candidateId,
            String location,
            Integer minSalary,
            Integer experience) {

        return jobRepository
                .findJobMatches(
                        candidateId,
                        location,
                        minSalary,
                        experience
                )
                .stream()
                .map(this::applyDecision)
                .toList();
    }


    // ============================================================
    // GET ONE JOB MATCH
    // ============================================================

    public JobMatchResponse getJobMatch(
            String candidateId,
            String jobId) {

        JobMatchResponse match =
                jobRepository.findJobMatch(
                        candidateId,
                        jobId
                );


        if (match == null) {
            return null;
        }


        return applyDecision(match);
    }


    // ============================================================
    // APPLY DECISION
    // ============================================================

    private JobMatchResponse applyDecision(
            JobMatchResponse job) {

        String decision;

        String recommendation;


        if (job.getMatchPercentage()
                >= AUTO_PREPARE_THRESHOLD) {

            decision = "AUTO_PREPARE";

            recommendation =
                    "Strong match. Application can be prepared automatically.";

        } else {

            decision = "MANUAL_REVIEW";

            recommendation =
                    "Review skill gaps before applying.";
        }


        /*
         * IMPORTANT:
         *
         * applicationUrl must be passed through here.
         *
         * Otherwise JobRepository gets the URL,
         * but JobMatchingService removes it when
         * creating the final response.
         */

        return new JobMatchResponse(

                job.getJobId(),

                job.getJob(),

                job.getMatchedSkills(),

                job.getMissingSkills(),

                job.getRequiredSkillCount(),

                job.getMatchedSkillCount(),

                job.getMatchPercentage(),

                decision,

                recommendation,

                job.getApplicationUrl()
        );
    }
}