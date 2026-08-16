package com.example.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.InternetJobResponse;
import com.example.demo.service.JoobleJobService;

@RestController
@RequestMapping("/api/job-search")
public class JobSearchController {

    private final JoobleJobService joobleJobService;

    public JobSearchController(
            JoobleJobService joobleJobService) {

        this.joobleJobService = joobleJobService;
    }

    @GetMapping("/internet/{candidateId}")
    public List<InternetJobResponse> searchInternetJobs(

            @PathVariable String candidateId,

            @RequestParam(
                    defaultValue = "Java Developer"
            )
            String keywords,

            @RequestParam(
                    defaultValue = ""
            )
            String location,

            @RequestParam(
                    required = false
            )
            Integer minSalary,

            @RequestParam(
                    required = false
            )
            Integer dateRangeDays) {

        return joobleJobService.searchJobs(
                candidateId,
                keywords,
                location,
                minSalary,
                dateRangeDays
        );
    }
}