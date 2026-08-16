package com.example.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.ApplicationRequest;
import com.example.demo.dto.ApplicationResponse;
import com.example.demo.dto.ApplicationTrackerResponse;
import com.example.demo.service.ApplicationService;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(
            ApplicationService applicationService) {

        this.applicationService = applicationService;
    }

    @PostMapping
    public ApplicationResponse apply(
            @RequestBody ApplicationRequest request) {

        return applicationService.apply(
                request.getCandidateId(),
                request.getJobId()
        );
    }

    @GetMapping("/{candidateId}")
    public List<ApplicationTrackerResponse> getApplications(
            @PathVariable String candidateId) {

        return applicationService.getApplications(candidateId);
    }
}