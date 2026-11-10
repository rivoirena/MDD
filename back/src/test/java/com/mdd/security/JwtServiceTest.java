package com.mdd.security;

import com.mdd.models.User;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String SECRET = "test-secret-key-at-least-32-characters-long";
    private static final long EXPIRATION_MS = 3_600_000L;

    private JwtService jwtService;
    private User user;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET, EXPIRATION_MS);

        user = new User();
        user.setId(1L);
        user.setEmail("john@example.com");
    }

    @Test
    void generateToken_shouldProduceValidToken() {
        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertTrue(jwtService.isTokenValid(token));
    }

    @Test
    void extractEmail_shouldReturnEmailOfUser() {
        String token = jwtService.generateToken(user);

        assertEquals("john@example.com", jwtService.extractEmail(token));
    }

    @Test
    void isTokenValid_shouldReturnFalse_whenTokenIsExpired() {
        JwtService shortLivedService = new JwtService(SECRET, -1000L);
        String token = shortLivedService.generateToken(user);

        assertFalse(jwtService.isTokenValid(token));
    }

    @Test
    void isTokenValid_shouldReturnFalse_whenSignedWithAnotherKey() {
        JwtService otherService =
                new JwtService("another-secret-key-at-least-32-characters-long", EXPIRATION_MS);
        String token = otherService.generateToken(user);

        assertFalse(jwtService.isTokenValid(token));
    }

    @Test
    void isTokenValid_shouldReturnFalse_whenTokenIsMalformed() {
        assertFalse(jwtService.isTokenValid("not-a-jwt"));
    }

    @Test
    void isTokenValid_shouldReturnFalse_whenTokenIsEmpty() {
        assertFalse(jwtService.isTokenValid(""));
    }

    @Test
    void isTokenValid_shouldReturnFalse_whenTokenIsNull() {
        assertFalse(jwtService.isTokenValid(null));
    }

    @Test
    void isTokenValid_shouldReturnFalse_whenTokenIsUnsigned() {
        String unsigned = Jwts.builder()
                .subject("john@example.com")
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .compact();

        assertFalse(jwtService.isTokenValid(unsigned));
    }

    @Test
    void extractEmail_shouldThrow_whenTokenIsInvalid() {
        assertThrows(JwtException.class, () -> jwtService.extractEmail("not-a-jwt"));
    }

    @Test
    void generateToken_shouldContainIdClaim() {
        String token = jwtService.generateToken(user);

        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        Number id = Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token)
                .getPayload()
                .get("id", Number.class);

        assertEquals(1L, id.longValue());
    }
}