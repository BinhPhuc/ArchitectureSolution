package com.architecture.solution.service.impl;

import com.architecture.solution.dto.request.RefreshTokenRequest;
import com.architecture.solution.dto.response.LoginResponse;
import com.architecture.solution.dto.response.TokenResponse;
import com.architecture.solution.enums.TokenType;
import com.architecture.solution.exception.ResourceNotFoundException;
import com.architecture.solution.util.JwtUtil;
import com.architecture.solution.dto.request.LoginRequest;
import com.architecture.solution.dto.request.RegisterRequest;
import com.architecture.solution.entity.User;
import com.architecture.solution.exception.InvalidArgumentException;
import com.architecture.solution.exception.ResourceExistsException;
import com.architecture.solution.repository.UserRepository;
import com.architecture.solution.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void register(RegisterRequest registerRequest) {
        String password = registerRequest.getPassword();
        String retypePassword = registerRequest.getRetypePassword();
        if (!password.equals(retypePassword)) {
            throw new InvalidArgumentException("Passwords do not match");
        }
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new ResourceExistsException("User with username " + registerRequest.getUsername() + " already exists");
        }
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new ResourceExistsException("User with email " + registerRequest.getEmail() + " already exists");
        }
        String encodedPassword = passwordEncoder.encode(password);
        User user = User.builder()
                .username(registerRequest.getUsername())
                .passwordHash(encodedPassword)
                .email(registerRequest.getEmail())
                .displayedName(registerRequest.getDisplayedName())
                .build();
        userRepository.save(user);
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user, null);
        Instant refreshTokenExpiry = jwtUtil.extractExpiration(refreshToken).toInstant();

        user.setRefreshToken(refreshToken);
        user.setRefreshTokenExpiry(refreshTokenExpiry);
        userRepository.save(user);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public TokenResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new InvalidArgumentException("Refresh token is required");
        }
        if (!TokenType.REFRESH.name().equals(jwtUtil.extractType(refreshToken))) {
            throw new InvalidArgumentException("Invalid token type");
        }
        User user = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid refresh token"));
        if (!jwtUtil.validateToken(refreshToken, user)) {
            throw new InvalidArgumentException("Invalid refresh token");
        }
        String newAccessToken = jwtUtil.generateAccessToken(user);
        String newRefreshToken = jwtUtil.generateRefreshToken(user, user.getRefreshTokenExpiry());
        user.setRefreshToken(newRefreshToken);
        userRepository.save(user);
        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }
}
