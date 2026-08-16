package com.example.demo.service;

import org.neo4j.driver.Record;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.repository.AuthRepository;

@Service
public class AuthService {

    private final AuthRepository authRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;


    public AuthService(
            AuthRepository authRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.authRepository =
                authRepository;

        this.passwordEncoder =
                passwordEncoder;

        this.jwtService =
                jwtService;
    }


    /*
     * ============================================================
     * REGISTER
     * ============================================================
     */

    public String register(
            RegisterRequest request) {


        String email =
                request.getEmail()
                       .trim()
                       .toLowerCase();


        /*
         * Check duplicate email
         */

        if (authRepository.userExists(email)) {

            throw new RuntimeException(
                "An account with this email already exists."
            );
        }


        /*
         * Hash password
         */

        String passwordHash =
                passwordEncoder.encode(
                    request.getPassword()
                );


        /*
         * Create User + Candidate
         */

        String candidateId =
                authRepository.createUser(
                    request.getName(),
                    email,
                    request.getPhone(),
                    passwordHash
                );


        return candidateId;
    }


    /*
     * ============================================================
     * LOGIN
     * ============================================================
     */

    public LoginResponse login(
            LoginRequest request) {


        Record user;


        try {

            user =
                authRepository.findUser(
                    request.getEmail()
                        .trim()
                        .toLowerCase()
                );

        } catch (Exception e) {

            throw new RuntimeException(
                "Invalid email or password"
            );
        }


        String passwordHash =
                user.get("passwordHash")
                    .asString();


        boolean valid =
                passwordEncoder.matches(
                    request.getPassword(),
                    passwordHash
                );


        if (!valid) {

            throw new RuntimeException(
                "Invalid email or password"
            );
        }


        String email =
                user.get("email")
                    .asString();


        String candidateId =
                user.get("candidateId")
                    .asString();


        /*
         * Generate JWT
         */

        String token =
                jwtService.generateToken(
                    email,
                    candidateId
                );


        return new LoginResponse(
            token,
            candidateId
        );
    }
}