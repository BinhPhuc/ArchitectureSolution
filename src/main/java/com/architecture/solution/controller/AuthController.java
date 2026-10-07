package com.architecture.solution.controller;

import com.architecture.solution.controller.docs.AuthApi;
import com.architecture.solution.dto.auth.request.LoginRequest;
import com.architecture.solution.dto.auth.request.RefreshTokenRequest;
import com.architecture.solution.dto.auth.request.RegisterRequest;
import com.architecture.solution.dto.auth.response.LoginResponse;
import com.architecture.solution.dto.auth.response.TokenResponse;
import com.architecture.solution.dto.common.ApiResponse;
import com.architecture.solution.service.AuthService;
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
public class AuthController implements AuthApi {
    private final AuthService authService;

    @Override
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(
            @Valid @RequestBody RegisterRequest registerRequest
    ) {
        log.info("Registering user: {}", registerRequest.getUsername());
        authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(
                null, "User registered successfully"));
    }

    @Override
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest loginRequest
    ) {
        log.info("Logging in user: {}", loginRequest.getUsername());
        LoginResponse loginResponse = authService.login(loginRequest);
        if (loginResponse == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.success(
                    null, "Invalid username or password"));
        }
        return ResponseEntity.ok(ApiResponse.success(loginResponse));
    }

    @Override
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest refreshTokenRequest
    ) {
        log.info("Refreshing token");
        TokenResponse tokenResponse = authService.refreshToken(refreshTokenRequest);
        if (tokenResponse == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.success(
                    null, "Invalid refresh token"));
        }
        return ResponseEntity.ok(ApiResponse.success(tokenResponse));
    }

    @Override
    @PutMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        log.info("Logging out user");
        authService.logout(request);
        return ResponseEntity.ok(ApiResponse.success(null, "User logged out successfully"));
    }
}
