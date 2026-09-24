package com.architecture.solution.service.impl;

import com.architecture.solution.dto.request.RefreshTokenRequest;
import com.architecture.solution.dto.response.LoginResponse;
import com.architecture.solution.dto.response.TokenResponse;
import com.architecture.solution.entity.*;
import com.architecture.solution.enums.RoleName;
import com.architecture.solution.enums.TokenType;
import com.architecture.solution.exception.ResourceNotFoundException;
import com.architecture.solution.repository.*;
import com.architecture.solution.util.JwtUtil;
import com.architecture.solution.dto.request.LoginRequest;
import com.architecture.solution.dto.request.RegisterRequest;
import com.architecture.solution.exception.InvalidArgumentException;
import com.architecture.solution.exception.ResourceExistsException;
import com.architecture.solution.security.CustomUserDetails;
import com.architecture.solution.service.AuthService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final CandidateRepository candidateRepository;
    private final RecruiterRepository recruiterRepository;

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void register(RegisterRequest registerRequest) {
        Set<RoleName> roleNames = registerRequest.getRoles();
        if (roleNames == null || roleNames.isEmpty()) {
            throw new InvalidArgumentException("Roles are required");
        }

        String password = registerRequest.getPassword();
        String retypePassword = registerRequest.getRetypePassword();
        if (!password.equals(retypePassword)) {
            throw new InvalidArgumentException("Passwords do not match");
        }
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new ResourceExistsException("User with username " + registerRequest.getUsername() + " already exists");
        }
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new ResourceExistsException("User with email " + registerRequest.getEmail() +
                    " already exists");
        }
        String encodedPassword = passwordEncoder.encode(password);
        userRepository.save(User.builder()
                .username(registerRequest.getUsername())
                .passwordHash(encodedPassword)
                .email(registerRequest.getEmail())
                .displayedName(registerRequest.getDisplayedName())
                .build());

        User user = userRepository.findByUsername(registerRequest.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found after " +
                        "registration"));

        for (RoleName roleName : roleNames) {
            if (roleName == RoleName.ADMIN) {
                throw new InvalidArgumentException("Cannot register as ADMIN");
            }
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
            UserRole userRole = UserRole.builder()
                    .userId(user.getId())
                    .roleId(role.getId())
                    .build();
            userRoleRepository.save(userRole);

            if (roleName == RoleName.CANDIDATE) {
                Candidate candidate = Candidate.builder()
                        .userId(user.getId())
                        .build();
                candidateRepository.save(candidate);
            } else if (roleName == RoleName.RECRUITER) {
                recruiterRepository.save(Recruiter.builder()
                        .companyName(registerRequest.getDisplayedName())
                        .userId(user.getId())
                        .build());
            }
        }
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        User user = ((CustomUserDetails) authentication.getPrincipal()).getUser();

        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user, null);

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        Claims claims = jwtUtil.extractAllClaims(accessToken);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresAt(jwtUtil.extractExpiration(claims).toInstant())
                .username(user.getUsername())
                .roles(userRoleRepository.findRoleNamesByUserId(user.getId()))
                .build();
    }

    @Override
    public TokenResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new InvalidArgumentException("Refresh token is required");
        }
        Claims claims = jwtUtil.extractAllClaims(refreshToken);
        if (!TokenType.REFRESH.name().equals(jwtUtil.extractType(claims))) {
            throw new InvalidArgumentException("Invalid token type");
        }
        User user = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid refresh token"));
        if (!jwtUtil.validateToken(claims, user.getUsername())) {
            throw new InvalidArgumentException("Invalid refresh token");
        }
        String newAccessToken = jwtUtil.generateAccessToken(user);
        String newRefreshToken = jwtUtil.generateRefreshToken(user,
                jwtUtil.extractExpiration(claims).toInstant());
        user.setRefreshToken(newRefreshToken);
        userRepository.save(user);
        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .expiresAt(jwtUtil.extractExpiration(jwtUtil.extractAllClaims(newAccessToken)).toInstant())
                .build();
    }

    @Override
    public void logout(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new InvalidArgumentException("Refresh token is required");
        }
        Claims claims = jwtUtil.extractAllClaims(refreshToken);
        if (!TokenType.REFRESH.name().equals(jwtUtil.extractType(claims))) {
            throw new InvalidArgumentException("Invalid token type");
        }
        User user = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid refresh token"));
        if (!jwtUtil.validateToken(claims, user.getUsername())) {
            throw new InvalidArgumentException("Invalid refresh token");
        }
        user.setRefreshToken(null);
        userRepository.save(user);
    }
}
