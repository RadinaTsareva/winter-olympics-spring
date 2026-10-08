package com.example.winter_olympics;

import com.example.winter_olympics.entity.Role;
import com.example.winter_olympics.entity.User;
import com.example.winter_olympics.service.JwtService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtServiceTests {

    private static final String SECRET = "test-only-secret-with-32-bytes-minimum";

    @Test
    void generatedTokenContainsSubjectRoleAndConfiguredExpiration() {
        long expiration = 60_000;
        JwtService jwtService = new JwtService(SECRET, expiration);
        User user = new User("admin", "not-used", Role.ADMIN);

        String token = jwtService.generateToken(user);
        var claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals("admin", jwtService.extractUsername(token));
        assertEquals("ADMIN", claims.get("role"));
        assertEquals(expiration, claims.getExpiration().getTime() - claims.getIssuedAt().getTime(), 1_000);
    }

    @Test
    void invalidAndExpiredTokensAreRejected() {
        JwtService jwtService = new JwtService(SECRET, 60_000);
        String expiredToken = Jwts.builder()
                .subject("admin")
                .issuedAt(Date.from(Instant.now().minusSeconds(20)))
                .expiration(Date.from(Instant.now().minusSeconds(10)))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
                .compact();

        assertThrows(RuntimeException.class, () -> jwtService.extractUsername("not-a-token"));
        assertThrows(RuntimeException.class, () -> jwtService.extractUsername(expiredToken));
    }

    @Test
    void unsafeSecretAndExpirationFailFast() {
        assertThrows(IllegalStateException.class, () -> new JwtService("short", 60_000));
        assertThrows(IllegalStateException.class, () -> new JwtService(SECRET, 0));
    }
}
