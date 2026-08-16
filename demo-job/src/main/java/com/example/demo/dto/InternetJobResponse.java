package com.example.demo.dto;

import java.util.List;

public class InternetJobResponse {

    private String jobId;
    private String job;
    private String company;
    private String location;
    private String salary;
    private String source;
    private String link;
    private String jobType;
    private String updated;

    private List<String> matchedSkills;
    private List<String> missingSkills;

    private int requiredSkillCount;
    private int matchedSkillCount;
    private int matchPercentage;

    private String decision;
    private String recommendation;


    public InternetJobResponse(
            String jobId,
            String job,
            String company,
            String location,
            String salary,
            String source,
            String link,
            String jobType,
            String updated,
            List<String> matchedSkills,
            List<String> missingSkills,
            int requiredSkillCount,
            int matchedSkillCount,
            int matchPercentage,
            String decision,
            String recommendation) {

        this.jobId = jobId;
        this.job = job;
        this.company = company;
        this.location = location;
        this.salary = salary;
        this.source = source;
        this.link = link;
        this.jobType = jobType;
        this.updated = updated;

        this.matchedSkills = matchedSkills;
        this.missingSkills = missingSkills;

        this.requiredSkillCount = requiredSkillCount;
        this.matchedSkillCount = matchedSkillCount;
        this.matchPercentage = matchPercentage;

        this.decision = decision;
        this.recommendation = recommendation;
    }


    public String getJobId() {
        return jobId;
    }


    public String getJob() {
        return job;
    }


    public String getCompany() {
        return company;
    }


    public String getLocation() {
        return location;
    }


    public String getSalary() {
        return salary;
    }


    public String getSource() {
        return source;
    }


    public String getLink() {
        return link;
    }


    public String getJobType() {
        return jobType;
    }


    public String getUpdated() {
        return updated;
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
}