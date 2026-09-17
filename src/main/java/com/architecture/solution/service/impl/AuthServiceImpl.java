package com.architecture.solution.service.impl;

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

import java.util.Optional;

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
        String encodedPassword = passwordEncoder.encode(password);
        User user = User.builder()
                .username(registerRequest.getUsername())
                .passwordHash(encodedPassword)
                .build();
        userRepository.save(user);
    }

    // TODO: return LoginResponse that has both accessToken & refreshToken
    @Override
    public Optional<String> login(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        String token = jwtUtil.generateToken(user);
        return Optional.of(token);
    }
}
