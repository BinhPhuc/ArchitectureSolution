package com.architecture.solution.util;

import com.architecture.solution.entity.User;
import com.architecture.solution.exception.ResourceNotFoundException;
import com.architecture.solution.exception.UnauthorizedException;
import com.architecture.solution.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    private SecurityUtils() {
    }

    public static User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("User is not authenticated");
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof CustomUserDetails)) {
            throw new UnauthorizedException("User details are not available");
        }
        User user = ((CustomUserDetails) principal).getUser();
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        return user;
    }

    public static String getUserId() {
        return getUser().getId();
    }
}
