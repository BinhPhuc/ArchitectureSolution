package com.architecture.solution.service.impl;

import com.architecture.solution.dto.auth.request.RefreshTokenRequest;
import com.architecture.solution.dto.auth.response.LoginResponse;
import com.architecture.solution.dto.auth.response.TokenResponse;
import com.architecture.solution.entity.*;
import com.architecture.solution.enums.RoleName;
import com.architecture.solution.enums.TokenType;
import com.architecture.solution.exception.UnauthorizedException;
import com.architecture.solution.repository.*;
import com.architecture.solution.util.JwtUtils;
import com.architecture.solution.dto.auth.request.LoginRequest;
import com.architecture.solution.dto.auth.request.RegisterRequest;
import com.architecture.solution.exception.InvalidArgumentException;
import com.architecture.solution.exception.ResourceExistsException;
import com.architecture.solution.security.CustomUserDetails;
import com.architecture.solution.service.AuthService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final SellerRepository sellerRepository;

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void register(RegisterRequest registerRequest) {
        if (!StringUtils.hasText(registerRequest.getRole())) {
            throw new InvalidArgumentException("Role is required");
        }
        RoleName role = RoleName.valueOf(registerRequest.getRole());

        if (role == RoleName.SELLER && !StringUtils.hasText(registerRequest.getShopName())) {
            throw new InvalidArgumentException("Shop name is required for sellers");
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
            throw new ResourceExistsException("User with email " + registerRequest.getEmail() + " already exists");
        }
        String encodedPassword = passwordEncoder.encode(password);
        User user = userRepository.save(User.builder()
                .username(registerRequest.getUsername())
                .passwordHash(encodedPassword)
                .email(registerRequest.getEmail())
                .displayedName(registerRequest.getUsername())
                .role(role)
                .build());

        if (role == RoleName.CUSTOMER) {
            customerRepository.save(Customer.builder()
                    .userId(user.getId())
                    .phone(registerRequest.getPhone())
                    .shippingAddress(registerRequest.getShippingAddress())
                    .build());
        } else {
            sellerRepository.save(Seller.builder()
                    .shopName(registerRequest.getShopName())
                    .userId(user.getId())
                    .build());
        }
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        Object principal = authentication.getPrincipal();
        if (principal == null) {
            throw new BadCredentialsException("Invalid username or password");
        }

        User user = ((CustomUserDetails) principal).getUser();

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
                .role(user.getRole())
                .build();
    }

    @Override
    @Transactional
    public TokenResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new InvalidArgumentException("Refresh token is required");
        }
        Claims claims = jwtUtil.extractAllClaims(refreshToken);
        if (!TokenType.REFRESH.name().equals(jwtUtil.extractType(claims))) {
            throw new UnauthorizedException("Invalid token type");
        }
        User user = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));
        if (!jwtUtil.validateToken(claims, user.getUsername())) {
            throw new UnauthorizedException("Invalid refresh token");
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
}
