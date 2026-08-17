package com.example.demo.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;

    public JwtService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public String generateToken(
            String email,
            String candidateId) {

        Instant now = Instant.now();


        JwtClaimsSet claims =
                JwtClaimsSet.builder()

                        .subject(email)

                        .claim(
                                "candidateId",
                                candidateId
                        )

                        .issuedAt(now)

                        .expiresAt(
                                now.plus(
                                        24,
                                        ChronoUnit.HOURS
                                )
                        )

                        .build();


        /*
         * SecurityConfig creates an RSA signing key,
         * so the JWT must use RS256.
         */
        JwsHeader header =
                JwsHeader.with(
                        SignatureAlgorithm.RS256
                ).build();


        return jwtEncoder
                .encode(
                        JwtEncoderParameters.from(
                                header,
                                claims
                        )
                )
                .getTokenValue();
    }
}