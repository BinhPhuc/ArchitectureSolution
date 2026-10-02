package com.architecture.solution.controller;

import com.architecture.solution.dto.common.ApiResponse;
import com.architecture.solution.dto.common.ErrorResponse;
import com.architecture.solution.dto.auth.request.LoginRequest;
import com.architecture.solution.dto.auth.request.RefreshTokenRequest;
import com.architecture.solution.dto.auth.request.RegisterRequest;
import com.architecture.solution.dto.auth.response.LoginResponse;
import com.architecture.solution.dto.auth.response.TokenResponse;
import com.architecture.solution.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication Controller")
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "Register a new user", description = "Register a new user with username" +
            " and password")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description =
            "User registered")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description =
            "Invalid request, passwords do not match, ADMIN role requested, or username/email " +
                    "already exists",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("Registering user: {}", registerRequest.getUsername());
        authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(
                null, "User registered successfully"));
    }

    @Operation(summary = "Login user", description = "Login with username and password")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
            description = "Login successful")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
            description = "Missing username or password",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401",
            description = "Invalid username or password",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
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
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
            description = "Token refreshed")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
            description = "Missing, invalid or non-refresh token",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
            description = "Refresh token not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
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

    @Operation(summary = "Logout user", description = "Logout user by invalidating refresh token")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
            description = "Logout successful")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
            description = "Missing, invalid or non-refresh token",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
            description = "Refresh token not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PutMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("Logging out user");
        authService.logout(request);
        return ResponseEntity.ok(ApiResponse.success(null, "User logged out successfully"));
    }
}
