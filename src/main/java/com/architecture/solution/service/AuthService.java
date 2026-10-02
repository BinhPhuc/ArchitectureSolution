package com.architecture.solution.service;

import com.architecture.solution.dto.auth.request.LoginRequest;
import com.architecture.solution.dto.auth.request.RefreshTokenRequest;
import com.architecture.solution.dto.auth.request.RegisterRequest;
import com.architecture.solution.dto.auth.response.LoginResponse;
import com.architecture.solution.dto.auth.response.TokenResponse;

import java.util.Optional;

public interface AuthService {
    void register(RegisterRequest registerRequest);
    LoginResponse login(LoginRequest loginRequest);
    TokenResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
    void logout(RefreshTokenRequest request);
}
