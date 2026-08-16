package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.demo.dto.ProfileResponse;
import com.example.demo.repository.ProfileRepository;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;

    public ProfileService(
            ProfileRepository profileRepository) {

        this.profileRepository =
                profileRepository;
    }

    public ProfileResponse getProfile(
            String candidateId) {

        Map<String, Object> data =
                profileRepository.getProfile(
                        candidateId
                );

        String id =
                stringValue(
                        data.get("candidateId")
                );

        String name =
                stringValue(
                        data.get("name")
                );

        String email =
                stringValue(
                        data.get("email")
                );

        String phone =
                stringValue(
                        data.get("phone")
                );


        List<String> skills =
                new ArrayList<>();

        Object skillObject =
                data.get("skills");

        if (skillObject instanceof List<?> list) {

            for (Object skill : list) {

                if (skill != null &&
                        !skill.toString()
                                .isBlank()) {

                    skills.add(
                            skill.toString()
                    );
                }
            }
        }


        int resumeCount =
                numberValue(
                        data.get("resumeCount")
                );

        int applicationCount =
                numberValue(
                        data.get("applicationCount")
                );


        return new ProfileResponse(
                id,
                name,
                email,
                phone,
                skills,
                resumeCount,
                applicationCount
        );
    }
    public void updateProfile(
            String candidateId,
            String name,
            String phone) {

        profileRepository.updateProfile(
                candidateId,
                name,
                phone
        );
    }

    private String stringValue(
            Object value) {

        if (value == null) {
            return "";
        }

        return value.toString();
    }


    private int numberValue(
            Object value) {

        if (value == null) {
            return 0;
        }

        if (value instanceof Number number) {
            return number.intValue();
        }

        return Integer.parseInt(
                value.toString()
        );
    }
}