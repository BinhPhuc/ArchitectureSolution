package com.architecture.solution.service;

import com.architecture.solution.dto.request.UserLoginRequest;
import com.architecture.solution.dto.request.UserRegisterRequest;

import java.util.Optional;

public interface AuthService {
    void register(UserRegisterRequest userRegisterRequest);
    Optional<String> login(UserLoginRequest userLoginRequest);
}
