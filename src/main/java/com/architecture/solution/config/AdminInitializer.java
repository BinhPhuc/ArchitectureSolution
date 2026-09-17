package com.architecture.solution.config;

import com.architecture.solution.entity.Role;
import com.architecture.solution.entity.User;
import com.architecture.solution.entity.UserRole;
import com.architecture.solution.enums.RoleName;
import com.architecture.solution.exception.ResourceNotFoundException;
import com.architecture.solution.repository.RoleRepository;
import com.architecture.solution.repository.UserRepository;
import com.architecture.solution.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(AdminProperties.class)
public class AdminInitializer implements ApplicationRunner {
    private final AdminProperties adminProperties;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Role adminRole = roleRepository.findByName(RoleName.ADMIN)
                .orElseThrow(() -> new ResourceNotFoundException("Role ADMIN is missing in the database"));
        if (userRoleRepository.existsByRoleIdAndIsDeletedFalse(adminRole.getId())) {
            log.debug("An admin account already exists, skipping default admin creation");
            return;
        }
        if (!StringUtils.hasText(adminProperties.password())) {
            log.warn("No admin account exists and app.admin.password is not set, skipping default admin creation");
            return;
        }
        if (!StringUtils.hasText(adminProperties.email()) || !StringUtils.hasText(adminProperties.username())) {
            throw new IllegalStateException("app.admin.email and app.admin.username must not be blank");
        }
        User admin = userRepository.save(User.builder()
                .email(adminProperties.email())
                .username(adminProperties.username())
                .passwordHash(passwordEncoder.encode(adminProperties.password()))
                .displayedName(adminProperties.displayedName())
                .build());
        userRoleRepository.save(UserRole.builder()
                .userId(admin.getId())
                .roleId(adminRole.getId())
                .build());
        log.info("Default admin account '{}' created", admin.getUsername());
    }
}
