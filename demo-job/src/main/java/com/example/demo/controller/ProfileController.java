package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.ProfileResponse;
import com.example.demo.dto.ProfileUpdateRequest;
import com.example.demo.service.ProfileService;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(
            ProfileService profileService) {

        this.profileService =
                profileService;
    }


    @GetMapping("/{candidateId}")
    public ProfileResponse getProfile(
            @PathVariable String candidateId) {

        return profileService.getProfile(
                candidateId
        );
    }


    @PutMapping("/{candidateId}")
    public ProfileResponse updateProfile(

            @PathVariable String candidateId,

            @RequestBody ProfileUpdateRequest request) {


        profileService.updateProfile(
                candidateId,
                request.getName(),
                request.getPhone()
        );


        // Return updated profile

        return profileService.getProfile(
                candidateId
        );
    }
}