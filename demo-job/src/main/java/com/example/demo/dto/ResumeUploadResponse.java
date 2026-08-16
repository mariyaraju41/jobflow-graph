package com.example.demo.dto;

import java.util.List;

public class ResumeUploadResponse {

    private String resumeId;
    private String candidateId;
    private String fileName;
    private String email;
    private String phone;
    private List<String> skills;

    public ResumeUploadResponse(
            String resumeId,
            String candidateId,
            String fileName,
            String email,
            String phone,
            List<String> skills) {

        this.resumeId = resumeId;
        this.candidateId = candidateId;
        this.fileName = fileName;
        this.email = email;
        this.phone = phone;
        this.skills = skills;
    }

    public String getResumeId() {
        return resumeId;
    }

    public String getCandidateId() {
        return candidateId;
    }

    public String getFileName() {
        return fileName;
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
}