package com.architecture.solution.controller;

import com.architecture.solution.dto.common.ApiResponse;
import com.architecture.solution.dto.request.UserLoginRequest;
import com.architecture.solution.dto.request.UserRegisterRequest;
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

import java.util.Optional;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication Controller")
public class AuthController {
    private final AuthService userService;

    @Operation(summary = "Register a new user", description = "Register a new user with username and password")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody UserRegisterRequest userRegisterRequest) {
        log.info("Registering user: {}", userRegisterRequest.getUsername());
        userService.register(userRegisterRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(
                null, "User registered successfully"));
    }

    @Operation(summary = "Login user", description = "Login with username and password")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@Valid @RequestBody UserLoginRequest userLoginRequest) {
        log.info("Logging in user: {}", userLoginRequest.getUsername());
        Optional<String> token = userService.login(userLoginRequest);
        if (token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.success(
                    null, "Invalid username or password"));
        }
        return ResponseEntity.ok(ApiResponse.success(token.get()));
    }
}
