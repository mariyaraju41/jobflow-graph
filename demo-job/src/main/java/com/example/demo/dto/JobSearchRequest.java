package com.example.demo.dto;

import java.util.List;

public class JobSearchRequest {

    private String keyword;
    private String location;

    private Integer experienceMin;
    private Integer experienceMax;

    private Integer postedWithinDays;

    private List<String> skills;

    private boolean useResumeSkills = true;


    public JobSearchRequest() {
    }


    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    public Integer getExperienceMin() {
        return experienceMin;
    }

    public void setExperienceMin(Integer experienceMin) {
        this.experienceMin = experienceMin;
    }


    public Integer getExperienceMax() {
        return experienceMax;
    }

    public void setExperienceMax(Integer experienceMax) {
        this.experienceMax = experienceMax;
    }


    public Integer getPostedWithinDays() {
        return postedWithinDays;
    }

    public void setPostedWithinDays(Integer postedWithinDays) {
        this.postedWithinDays = postedWithinDays;
    }


    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }


    public boolean isUseResumeSkills() {
        return useResumeSkills;
    }

    public void setUseResumeSkills(boolean useResumeSkills) {
        this.useResumeSkills = useResumeSkills;
    }
}