package com.architecture.solution.service;

import com.architecture.solution.dto.request.LoginRequest;
import com.architecture.solution.dto.request.RefreshTokenRequest;
import com.architecture.solution.dto.request.RegisterRequest;
import com.architecture.solution.dto.response.LoginResponse;
import com.architecture.solution.dto.response.TokenResponse;

import java.util.Optional;

public interface AuthService {
    void register(RegisterRequest registerRequest);
    LoginResponse login(LoginRequest loginRequest);
    TokenResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
}
