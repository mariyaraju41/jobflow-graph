package com.example.demo.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

    // ============================================================
    // PASSWORD ENCODER
    // ============================================================

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ============================================================
    // CORS
    // ============================================================

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of(
                        "https://jobflow-graph-63gt.vercel.app",
                        "http://localhost:5173"
                )
        );

        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "PATCH",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                List.of("*")
        );

        configuration.setExposedHeaders(
                List.of("Authorization")
        );

        configuration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }

    // ============================================================
    // SPRING SECURITY
    // ============================================================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                // Enable CORS
                .cors(cors -> cors
                        .configurationSource(
                                corsConfigurationSource()
                        )
                )

                // REST API
                .csrf(csrf ->
                        csrf.disable()
                )

                .authorizeHttpRequests(auth -> auth

                        // ------------------------------------------------
                        // CORS preflight
                        // ------------------------------------------------
                        .requestMatchers(
                                HttpMethod.OPTIONS,
                                "/**"
                        )
                        .permitAll()

                        // ------------------------------------------------
                        // Public authentication endpoints
                        // ------------------------------------------------
                        .requestMatchers(
                                "/api/auth/register",
                                "/api/auth/login"
                        )
                        .permitAll()

                        // ------------------------------------------------
                        // Database test endpoint
                        // ------------------------------------------------
                        .requestMatchers(
                                "/api/database/**"
                        )
                        .permitAll()

                        // ------------------------------------------------
                        // Everything else
                        // ------------------------------------------------
                        .anyRequest()
                        .authenticated()
                );

        return http.build();
    }
}