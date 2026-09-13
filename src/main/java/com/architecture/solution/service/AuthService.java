package com.architecture.solution.service;

import com.architecture.solution.dto.request.LoginRequest;
import com.architecture.solution.dto.request.RegisterRequest;

import java.util.Optional;

public interface AuthService {
    void register(RegisterRequest registerRequest);
    Optional<String> login(LoginRequest loginRequest);
}
