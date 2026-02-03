package com.coomeva.hackathon.controller;

import com.coomeva.hackathon.dto.AuthResponse;
import com.coomeva.hackathon.dto.LoginRequest;
import com.coomeva.hackathon.dto.RegisterRequest;
import com.coomeva.hackathon.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setDocumentNumber("123456789");
        registerRequest.setPhone("555-1234");
        registerRequest.setAddress("123 Main St");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        authResponse = new AuthResponse(
                "jwt-token-here",
                "test@example.com",
                "John",
                "Doe",
                "USER"
        );
    }

    @Test
    void register_Success() {
        when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);

        ResponseEntity<AuthResponse> response = authController.register(registerRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("jwt-token-here", response.getBody().getToken());
        assertEquals("test@example.com", response.getBody().getEmail());
        assertEquals("John", response.getBody().getFirstName());
        assertEquals("Doe", response.getBody().getLastName());
        assertEquals("USER", response.getBody().getRole());

        verify(authService).register(any(RegisterRequest.class));
    }

    @Test
    void register_EmailAlreadyExists() {
        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new RuntimeException("Email already registered"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authController.register(registerRequest);
        });

        assertEquals("Email already registered", exception.getMessage());
        verify(authService).register(any(RegisterRequest.class));
    }

    @Test
    void login_Success() {
        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        ResponseEntity<AuthResponse> response = authController.login(loginRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("jwt-token-here", response.getBody().getToken());
        assertEquals("test@example.com", response.getBody().getEmail());
        assertEquals("John", response.getBody().getFirstName());
        assertEquals("Doe", response.getBody().getLastName());
        assertEquals("USER", response.getBody().getRole());

        verify(authService).login(any(LoginRequest.class));
    }

    @Test
    void login_InvalidCredentials() {
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authController.login(loginRequest);
        });

        assertEquals("Invalid credentials", exception.getMessage());
        verify(authService).login(any(LoginRequest.class));
    }
}
