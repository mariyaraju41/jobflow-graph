package com.example.demo.dto;

import java.util.List;

public class JoobleResponse {

    private int totalCount;
    private List<JoobleJob> jobs;

    public JoobleResponse() {
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<JoobleJob> getJobs() {
        return jobs;
    }

    public void setJobs(List<JoobleJob> jobs) {
        this.jobs = jobs;
    }
}