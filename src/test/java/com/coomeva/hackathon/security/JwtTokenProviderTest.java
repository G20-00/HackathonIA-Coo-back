package com.coomeva.hackathon.security;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private UserDetails userDetails;
    private String testSecret = "ThisIsAVeryLongSecretKeyForJWTTokenGenerationAndValidation1234567890";
    private Long testExpiration = 86400000L; // 24 hours in milliseconds

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();

        // Set private fields using ReflectionTestUtils
        ReflectionTestUtils.setField(jwtTokenProvider, "secret", testSecret);
        ReflectionTestUtils.setField(jwtTokenProvider, "expiration", testExpiration);

        userDetails = User.builder()
                .username("test@example.com")
                .password("password123")
                .authorities(new ArrayList<>())
                .build();
    }

    @Test
    void generateToken_Success() {
        String token = jwtTokenProvider.generateToken(userDetails);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3); // JWT has 3 parts separated by dots
    }

    @Test
    void extractUsername_Success() {
        String token = jwtTokenProvider.generateToken(userDetails);

        String username = jwtTokenProvider.extractUsername(token);

        assertEquals("test@example.com", username);
    }

    @Test
    void extractExpiration_Success() {
        String token = jwtTokenProvider.generateToken(userDetails);

        Date expiration = jwtTokenProvider.extractExpiration(token);

        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void validateToken_ValidToken_Success() {
        String token = jwtTokenProvider.generateToken(userDetails);

        Boolean isValid = jwtTokenProvider.validateToken(token, userDetails);

        assertTrue(isValid);
    }

    @Test
    void validateToken_WrongUser_ReturnsFalse() {
        String token = jwtTokenProvider.generateToken(userDetails);

        UserDetails differentUser = User.builder()
                .username("different@example.com")
                .password("password123")
                .authorities(new ArrayList<>())
                .build();

        Boolean isValid = jwtTokenProvider.validateToken(token, differentUser);

        assertFalse(isValid);
    }

    @Test
    void extractExpiration_ValidToken_ReturnsExpirationDate() {
        String token = jwtTokenProvider.generateToken(userDetails);

        Date expiration = jwtTokenProvider.extractExpiration(token);

        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
        // Token should expire within the configured time period
        long expectedExpirationTime = System.currentTimeMillis() + testExpiration;
        long actualExpirationTime = expiration.getTime();
        // Allow 1 second tolerance
        assertTrue(Math.abs(expectedExpirationTime - actualExpirationTime) < 1000);
    }

    @Test
    void extractUsername_InvalidToken_ThrowsException() {
        String invalidToken = "invalid.token.here";

        assertThrows(Exception.class, () -> jwtTokenProvider.extractUsername(invalidToken));
    }

    @Test
    void extractExpiration_InvalidToken_ThrowsException() {
        String invalidToken = "invalid.token.here";

        assertThrows(Exception.class, () -> jwtTokenProvider.extractExpiration(invalidToken));
    }

    @Test
    void generateToken_DifferentUsers_GenerateDifferentTokens() {
        String token1 = jwtTokenProvider.generateToken(userDetails);

        UserDetails userDetails2 = User.builder()
                .username("another@example.com")
                .password("password456")
                .authorities(new ArrayList<>())
                .build();

        String token2 = jwtTokenProvider.generateToken(userDetails2);

        assertNotEquals(token1, token2);
    }

    @Test
    void extractUsername_FromMultipleTokens_Success() {
        UserDetails user1 = User.builder()
                .username("user1@example.com")
                .password("password")
                .authorities(new ArrayList<>())
                .build();

        UserDetails user2 = User.builder()
                .username("user2@example.com")
                .password("password")
                .authorities(new ArrayList<>())
                .build();

        String token1 = jwtTokenProvider.generateToken(user1);
        String token2 = jwtTokenProvider.generateToken(user2);

        assertEquals("user1@example.com", jwtTokenProvider.extractUsername(token1));
        assertEquals("user2@example.com", jwtTokenProvider.extractUsername(token2));
    }

    @Test
    void validateToken_TokenGeneratedWithDifferentSecret_ThrowsException() {
        String token = jwtTokenProvider.generateToken(userDetails);

        // Change secret
        ReflectionTestUtils.setField(jwtTokenProvider, "secret",
                "DifferentSecretKeyForJWTTokenGenerationAndValidation0987654321");

        assertThrows(Exception.class, () -> jwtTokenProvider.validateToken(token, userDetails));
    }
}
