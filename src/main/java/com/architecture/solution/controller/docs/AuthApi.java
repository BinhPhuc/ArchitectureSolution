package com.architecture.solution.controller.docs;

import com.architecture.solution.dto.auth.request.LoginRequest;
import com.architecture.solution.dto.auth.request.RefreshTokenRequest;
import com.architecture.solution.dto.auth.request.RegisterRequest;
import com.architecture.solution.dto.auth.response.LoginResponse;
import com.architecture.solution.dto.auth.response.TokenResponse;
import com.architecture.solution.dto.common.ApiResponse;
import com.architecture.solution.dto.common.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Authentication Controller")
public interface AuthApi {

    @Operation(
            summary = "Register a new user",
            description = "Register a new user with username and password"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "User registered"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid request, passwords do not match, ADMIN role requested, " +
                    "or username/email already exists",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    ResponseEntity<ApiResponse<String>> register(RegisterRequest registerRequest);

    @Operation(
            summary = "Login user",
            description = "Login with username and password"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Login successful"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Missing username or password",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Invalid username or password",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    ResponseEntity<ApiResponse<LoginResponse>> login(LoginRequest loginRequest);

    @Operation(
            summary = "Refresh token",
            description = "Refresh access token using refresh token"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Token refreshed"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Missing, invalid or non-refresh token",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Refresh token not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    ResponseEntity<ApiResponse<TokenResponse>> refreshToken(RefreshTokenRequest refreshTokenRequest);

    @Operation(
            summary = "Logout user",
            description = "Logout user by invalidating refresh token"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Logout successful"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Missing, invalid or non-refresh token",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Refresh token not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    ResponseEntity<ApiResponse<String>> logout(RefreshTokenRequest request);
}
