package com.architecture.solution.service.impl;

import com.architecture.solution.entity.User;
import com.architecture.solution.repository.UserRepository;
import com.architecture.solution.repository.UserRoleRepository;
import com.architecture.solution.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        List<GrantedAuthority> authorities =
                userRoleRepository.findRoleNamesByUserId(user.getId()).stream()
                .map(roleName -> (GrantedAuthority) new SimpleGrantedAuthority("ROLE_" + roleName.name()))
                .toList();

        return new CustomUserDetails(user, authorities);
    }
}
