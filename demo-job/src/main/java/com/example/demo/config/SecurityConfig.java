package com.example.demo.config;

import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.nimbusds.jose.jwk.source.ImmutableSecret;


@Configuration
public class SecurityConfig {


    /*
     * ============================================================
     * JWT SECRET
     * ============================================================
     */

    private static final String JWT_SECRET =
            "JobFlowSuperSecretKeyForJwt2026VeryLongKey";


    /*
     * ============================================================
     * SECURITY FILTER CHAIN
     * ============================================================
     */

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {


        http

            /*
             * ----------------------------------------------------
             * CORS
             * ----------------------------------------------------
             */

            .cors(cors -> cors.configurationSource(
                    corsConfigurationSource()
            ))


            /*
             * ----------------------------------------------------
             * CSRF
             * ----------------------------------------------------
             */

            .csrf(csrf -> csrf.disable())


            /*
             * ----------------------------------------------------
             * STATELESS JWT SESSION
             * ----------------------------------------------------
             */

            .sessionManagement(session ->
                    session.sessionCreationPolicy(
                            SessionCreationPolicy.STATELESS
                    )
            )


            /*
             * ----------------------------------------------------
             * AUTHORIZATION
             * ----------------------------------------------------
             */

            .authorizeHttpRequests(auth -> auth


                    /*
                     * Authentication endpoints
                     */

                    .requestMatchers(
                            "/api/auth/**"
                    )
                    .permitAll()


                    /*
                     * CORS preflight requests
                     */

                    .requestMatchers(
                            HttpMethod.OPTIONS,
                            "/**"
                    )
                    .permitAll()


                    /*
                     * Everything else requires JWT
                     */

                    .anyRequest()
                    .authenticated()
            )


            /*
             * ----------------------------------------------------
             * JWT RESOURCE SERVER
             * ----------------------------------------------------
             */

            .oauth2ResourceServer(oauth2 ->
                    oauth2.jwt(jwt -> {})
            );


        return http.build();
    }


    /*
     * ============================================================
     * CORS CONFIGURATION
     * ============================================================
     */

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {


        CorsConfiguration configuration =
                new CorsConfiguration();


        /*
         * React/Vite development server
         */

        configuration.setAllowedOrigins(
                List.of(
                        "http://localhost:5173"
                )
        );


        /*
         * HTTP methods
         */

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


        /*
         * Request headers
         *
         * Authorization is important because
         * React sends:
         *
         * Authorization: Bearer <JWT>
         */

        configuration.setAllowedHeaders(
                List.of(
                        "Authorization",
                        "Content-Type",
                        "Accept",
                        "Origin",
                        "X-Requested-With"
                )
        );


        /*
         * Response headers that browser is allowed
         * to access.
         */

        configuration.setExposedHeaders(
                List.of(
                        "Authorization"
                )
        );


        /*
         * We are using JWT in Authorization header,
         * not browser cookies.
         *
         * Therefore credentials are not required.
         */

        configuration.setAllowCredentials(false);


        /*
         * Apply CORS configuration to all APIs.
         */

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();


        source.registerCorsConfiguration(
                "/**",
                configuration
        );


        return source;
    }


    /*
     * ============================================================
     * PASSWORD ENCODER
     * ============================================================
     */

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }


    /*
     * ============================================================
     * JWT ENCODER
     * ============================================================
     */

    @Bean
    public JwtEncoder jwtEncoder() {


        SecretKey key =
                new SecretKeySpec(
                        JWT_SECRET.getBytes(
                                StandardCharsets.UTF_8
                        ),
                        "HmacSHA256"
                );


        return new NimbusJwtEncoder(
                new ImmutableSecret<>(key)
        );
    }


    /*
     * ============================================================
     * JWT DECODER
     * ============================================================
     */

    @Bean
    public JwtDecoder jwtDecoder() {


        SecretKey key =
                new SecretKeySpec(
                        JWT_SECRET.getBytes(
                                StandardCharsets.UTF_8
                        ),
                        "HmacSHA256"
                );


        return NimbusJwtDecoder
                .withSecretKey(key)
                .macAlgorithm(
                        MacAlgorithm.HS256
                )
                .build();
    }
}