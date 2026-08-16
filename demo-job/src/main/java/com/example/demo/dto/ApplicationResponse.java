package com.example.demo.dto;

public class ApplicationResponse {

    private String applicationId;
    private String jobId;
    private String jobTitle;
    private int matchPercentage;
    private String decision;
    private String status;
    private String applicationUrl;


    public ApplicationResponse(
            String applicationId,
            String jobId,
            String jobTitle,
            int matchPercentage,
            String decision,
            String status,
            String applicationUrl) {

        this.applicationId = applicationId;
        this.jobId = jobId;
        this.jobTitle = jobTitle;
        this.matchPercentage = matchPercentage;
        this.decision = decision;
        this.status = status;
        this.applicationUrl = applicationUrl;
    }


    public String getApplicationId() {
        return applicationId;
    }


    public String getJobId() {
        return jobId;
    }


    public String getJobTitle() {
        return jobTitle;
    }


    public int getMatchPercentage() {
        return matchPercentage;
    }


    public String getDecision() {
        return decision;
    }


    public String getStatus() {
        return status;
    }


    public String getApplicationUrl() {
        return applicationUrl;
    }
}