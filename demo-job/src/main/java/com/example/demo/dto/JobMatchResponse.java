package com.example.demo.dto;

import java.util.List;

public class JobMatchResponse {

    private String jobId;
    private String job;

    private List<String> matchedSkills;
    private List<String> missingSkills;

    private int requiredSkillCount;
    private int matchedSkillCount;
    private int matchPercentage;

    private String decision;
    private String recommendation;

    private String applicationUrl;


    public JobMatchResponse(
            String jobId,
            String job,
            List<String> matchedSkills,
            List<String> missingSkills,
            int requiredSkillCount,
            int matchedSkillCount,
            int matchPercentage,
            String decision,
            String recommendation,
            String applicationUrl) {

        this.jobId = jobId;
        this.job = job;

        this.matchedSkills = matchedSkills;
        this.missingSkills = missingSkills;

        this.requiredSkillCount = requiredSkillCount;
        this.matchedSkillCount = matchedSkillCount;
        this.matchPercentage = matchPercentage;

        this.decision = decision;
        this.recommendation = recommendation;

        this.applicationUrl = applicationUrl;
    }


    public String getJobId() {
        return jobId;
    }


    public String getJob() {
        return job;
    }


    public List<String> getMatchedSkills() {
        return matchedSkills;
    }


    public List<String> getMissingSkills() {
        return missingSkills;
    }


    public int getRequiredSkillCount() {
        return requiredSkillCount;
    }


    public int getMatchedSkillCount() {
        return matchedSkillCount;
    }


    public int getMatchPercentage() {
        return matchPercentage;
    }


    public String getDecision() {
        return decision;
    }


    public String getRecommendation() {
        return recommendation;
    }


    public String getApplicationUrl() {
        return applicationUrl;
    }
}