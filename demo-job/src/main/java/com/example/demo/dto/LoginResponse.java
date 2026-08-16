
package com.example.demo.dto;

public class LoginResponse {

    private String token;
    private String candidateId;

    public LoginResponse(
            String token,
            String candidateId) {

        this.token = token;
        this.candidateId = candidateId;
    }

    public String getToken() {
        return token;
    }

    public String getCandidateId() {
        return candidateId;
    }
}