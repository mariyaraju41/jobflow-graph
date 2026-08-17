package com.example.demo.config;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;

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
    // JWT ENCODER
    // ============================================================

    @Bean
    public JwtEncoder jwtEncoder() {

        try {

            KeyPairGenerator keyPairGenerator =
                    KeyPairGenerator.getInstance("RSA");

            keyPairGenerator.initialize(2048);

            KeyPair keyPair =
                    keyPairGenerator.generateKeyPair();

            RSAPublicKey publicKey =
                    (RSAPublicKey) keyPair.getPublic();

            RSAPrivateKey privateKey =
                    (RSAPrivateKey) keyPair.getPrivate();


            RSAKey rsaKey =
                    new RSAKey.Builder(publicKey)

                            .privateKey(privateKey)

                            /*
                             * Explicitly mark this as a
                             * signing key.
                             */
                            .keyUse(
                                    KeyUse.SIGNATURE
                            )

                            /*
                             * Explicitly tell Nimbus
                             * which signing algorithm
                             * this key supports.
                             */
                            .algorithm(
                                    JWSAlgorithm.RS256
                            )

                            .keyID(
                                    "jobflow-rsa-key"
                            )

                            .build();


            JWKSet jwkSet =
                    new JWKSet(rsaKey);


            ImmutableJWKSet<com.nimbusds.jose.proc.SecurityContext>
                    jwkSource =
                    new ImmutableJWKSet<>(
                            jwkSet
                    );


            return new NimbusJwtEncoder(
                    jwkSource
            );

        } catch (Exception e) {

            throw new IllegalStateException(
                    "Failed to create JWT encoder",
                    e
            );
        }
    }


    // ============================================================
    // CORS
    // ============================================================

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();


        configuration.setAllowedOriginPatterns(
                List.of(
                        "https://*.vercel.app",
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
                List.of(
                        "Authorization"
                )
        );


        configuration.setAllowCredentials(
                false
        );


        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();


        source.registerCorsConfiguration(
                "/**",
                configuration
        );


        return source;
    }


    // ============================================================
    // SECURITY
    // ============================================================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http

                .cors(cors ->
                        cors.configurationSource(
                                corsConfigurationSource()
                        )
                )

                .csrf(csrf ->
                        csrf.disable()
                )

                .authorizeHttpRequests(auth ->
                        auth

                        .requestMatchers(
                                HttpMethod.OPTIONS,
                                "/**"
                        )
                        .permitAll()

                        .requestMatchers(
                                "/",
                                "/api/health"
                        )
                        .permitAll()

                        .requestMatchers(
                                "/api/auth/register",
                                "/api/auth/login"
                        )
                        .permitAll()

                        .requestMatchers(
                                "/api/database/**"
                        )
                        .permitAll()

                        .anyRequest()
                        .authenticated()
                );


        return http.build();
    }
}