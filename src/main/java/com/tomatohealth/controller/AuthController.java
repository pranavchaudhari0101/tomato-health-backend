package com.tomatohealth.controller;

import com.tomatohealth.dto.auth.AuthResponse;
import com.tomatohealth.dto.auth.LoginRequest;
import com.tomatohealth.dto.auth.RegisterRequest;
import com.tomatohealth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication operations (registration and login).
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User registration and login endpoints")
public class AuthController {

    private final AuthService authService;

    /**
     * Register a new user account.
     *
     * @param request registration details
     * @return JWT token and user info
     */
    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Create a new user account and return a JWT token")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Authenticate a user and return a JWT token.
     *
     * @param request login credentials
     * @return JWT token and user info
     */
    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate user and return a JWT token")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
