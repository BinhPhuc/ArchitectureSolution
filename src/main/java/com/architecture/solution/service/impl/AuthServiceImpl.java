package com.architecture.solution.service.impl;

import com.architecture.solution.exception.ResourceNotFoundException;
import com.architecture.solution.util.JwtUtil;
import com.architecture.solution.dto.request.UserLoginRequest;
import com.architecture.solution.dto.request.UserRegisterRequest;
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
    public void register(UserRegisterRequest userRegisterRequest) {
        if (userRepository.existsByUsername(userRegisterRequest.getUsername())) {
            throw new ResourceExistsException("User with username " + userRegisterRequest.getUsername() + " already exists");
        }
        String password = userRegisterRequest.getPassword();
        String retypePassword = userRegisterRequest.getRetypePassword();
        if (!password.equals(retypePassword)) {
            throw new InvalidArgumentException("Passwords do not match");
        }
        String encodedPassword = passwordEncoder.encode(password);
        User user = User.builder()
                .username(userRegisterRequest.getUsername())
                .password(encodedPassword)
                .build();
        userRepository.save(user);
    }

    @Override
    public Optional<String> login(UserLoginRequest userLoginRequest) {
        User user = userRepository.findByUsername(userLoginRequest.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with username: " + userLoginRequest.getUsername()));
        if (!passwordEncoder.matches(userLoginRequest.getPassword(), user.getPassword())) {
            throw new InvalidArgumentException("Invalid username or password");
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userLoginRequest.getUsername(),
                        userLoginRequest.getPassword()
                )
        );
        String token = jwtUtil.generateToken(user);
        return Optional.of(token);
    }
}
