package com.example.winter_olympics.service;

import com.example.winter_olympics.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey key;
    private final long expirationMillis;

    public JwtService(
            @Value("${app.jwt.secret:}") String secret,
            @Value("${app.jwt.expiration-ms:86400000}") long expirationMillis
    ) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException(
                    "JWT_SECRET must be configured before the application can start"
            );
        }
        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length < 32) {
            throw new IllegalStateException(
                    "JWT_SECRET must contain at least 32 bytes for HS256"
            );
        }
        if (expirationMillis <= 0) {
            throw new IllegalStateException("JWT_EXPIRATION_MS must be greater than zero");
        }
        this.key = Keys.hmacShaKeyFor(secretBytes);
        this.expirationMillis = expirationMillis;
    }

    public String generateToken(User user) {

        return Jwts.builder()
                .subject(user.getUsername())
                .claim("role", user.getRole().name())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusMillis(expirationMillis)))
                .signWith(key)
                .compact();
    }

    public String extractUsername(String token) {

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
