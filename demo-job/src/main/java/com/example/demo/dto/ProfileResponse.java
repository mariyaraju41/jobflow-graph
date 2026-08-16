package com.example.demo.dto;

import java.util.List;

public class ProfileResponse {

    private String candidateId;
    private String name;
    private String email;
    private String phone;

    private List<String> skills;

    private int resumeCount;
    private int applicationCount;

    public ProfileResponse(
            String candidateId,
            String name,
            String email,
            String phone,
            List<String> skills,
            int resumeCount,
            int applicationCount) {

        this.candidateId = candidateId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.skills = skills;
        this.resumeCount = resumeCount;
        this.applicationCount = applicationCount;
    }

    public String getCandidateId() {
        return candidateId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public List<String> getSkills() {
        return skills;
    }

    public int getResumeCount() {
        return resumeCount;
    }

    public int getApplicationCount() {
        return applicationCount;
    }
}