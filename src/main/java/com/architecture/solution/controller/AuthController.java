package com.architecture.solution.controller;

import com.architecture.solution.dto.common.ApiResponse;
import com.architecture.solution.dto.request.LoginRequest;
import com.architecture.solution.dto.request.RefreshTokenRequest;
import com.architecture.solution.dto.request.RegisterRequest;
import com.architecture.solution.dto.response.LoginResponse;
import com.architecture.solution.dto.response.TokenResponse;
import com.architecture.solution.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication Controller")
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "Register a new user", description = "Register a new user with username and password")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("Registering user: {}", registerRequest.getUsername());
        authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(
                null, "User registered successfully"));
    }

    @Operation(summary = "Login user", description = "Login with username and password")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Logging in user: {}", loginRequest.getUsername());
        LoginResponse loginResponse = authService.login(loginRequest);
        if (loginResponse == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.success(
                    null, "Invalid username or password"));
        }
        return ResponseEntity.ok(ApiResponse.success(loginResponse));
    }

    @Operation(summary = "Refresh token", description = "Refresh access token using refresh token")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        log.info("Refreshing token");
        TokenResponse tokenResponse = authService.refreshToken(refreshTokenRequest);
        if (tokenResponse == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.success(
                    null, "Invalid refresh token"));
        }
        return ResponseEntity.ok(ApiResponse.success(tokenResponse));
    }
}
