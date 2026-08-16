package com.example.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.JobMatchResponse;
import com.example.demo.service.JobMatchingService;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobMatchingService jobMatchingService;

    public JobController(
            JobMatchingService jobMatchingService) {

        this.jobMatchingService = jobMatchingService;
    }

    @GetMapping("/matches/{candidateId}")
    public List<JobMatchResponse> getJobMatches(
            @PathVariable String candidateId) {

        return jobMatchingService
                .getJobMatches(candidateId);
    }

    @GetMapping("/matches/{candidateId}/filter")
    public List<JobMatchResponse> getJobMatchesWithFilters(

            @PathVariable String candidateId,

            @RequestParam(required = false)
            String location,

            @RequestParam(required = false)
            Integer minSalary,

            @RequestParam(required = false)
            Integer experience) {

        return jobMatchingService.getJobMatches(
                candidateId,
                location,
                minSalary,
                experience
        );
    }
}