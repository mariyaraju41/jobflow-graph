package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;


    public AuthController(
            AuthService authService) {

        this.authService =
                authService;
    }


    /*
     * ============================================================
     * REGISTER
     * ============================================================
     */

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request) {

        try {

            String candidateId =
                    authService.register(request);


            return ResponseEntity.ok(
                java.util.Map.of(
                    "message",
                    "User registered successfully",

                    "candidateId",
                    candidateId
                )
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                .badRequest()
                .body(
                    java.util.Map.of(
                        "message",
                        e.getMessage()
                    )
                );
        }
    }


    /*
     * ============================================================
     * LOGIN
     * ============================================================
     */

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request) {

        try {

            return ResponseEntity.ok(
                authService.login(request)
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                .status(401)
                .body(
                    java.util.Map.of(
                        "message",
                        e.getMessage()
                    )
                );
        }
    }
}