package com.architecture.solution.service.impl;

import com.architecture.solution.dto.user.response.UserResponse;
import com.architecture.solution.entity.User;
import com.architecture.solution.repository.UserRepository;
import com.architecture.solution.service.UserService;
import com.architecture.solution.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserResponse getUserProfile() {
        User user = SecurityUtils.getUser();

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .displayedName(user.getDisplayedName())
                .build();
    }
}
