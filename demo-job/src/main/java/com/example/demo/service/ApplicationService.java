package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.dto.ApplicationResponse;
import com.example.demo.dto.ApplicationTrackerResponse;
import com.example.demo.dto.JobMatchResponse;
import com.example.demo.repository.ApplicationRepository;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobMatchingService jobMatchingService;


    public ApplicationService(
            ApplicationRepository applicationRepository,
            JobMatchingService jobMatchingService) {

        this.applicationRepository =
                applicationRepository;

        this.jobMatchingService =
                jobMatchingService;
    }


    /*
     * ============================================================
     * APPLY
     * ============================================================
     */

    public ApplicationResponse apply(
            String candidateId,
            String jobId) {


        JobMatchResponse match =
                jobMatchingService.getJobMatch(
                        candidateId,
                        jobId
                );


        /*
         * Candidate does not have a matching skill
         * for this job.
         */

        if (match == null) {

            throw new RuntimeException(
                    "This job does not match your resume skills."
            );
        }


        /*
         * Create application.
         */

        return applicationRepository.createApplication(
                candidateId,
                jobId,
                match.getMatchPercentage(),
                match.getDecision()
        );
    }


    /*
     * ============================================================
     * APPLICATION TRACKER
     * ============================================================
     */

    public List<ApplicationTrackerResponse> getApplications(
            String candidateId) {

        return applicationRepository
                .findApplications(candidateId);
    }
}