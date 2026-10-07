package com.architecture.solution.filter;

import com.architecture.solution.enums.AuthErrorType;
import com.architecture.solution.enums.TokenType;
import com.architecture.solution.util.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (!authenticate(request, authHeader.substring(7))) {
                rejectToken(request, AuthErrorType.TOKEN_INVALID, "Token type or subject " +
                        "mismatch");
            }
        } catch (ExpiredJwtException ex) {
            rejectToken(request, AuthErrorType.TOKEN_EXPIRED, ex.getMessage());
        } catch (JwtException | IllegalArgumentException | UsernameNotFoundException ex) {
            rejectToken(request, AuthErrorType.TOKEN_INVALID, ex.getMessage());
        }

        filterChain.doFilter(request, response);
    }


    private boolean authenticate(HttpServletRequest request, String token) {
        final Claims claims = jwtUtil.extractAllClaims(token);
        if (!TokenType.ACCESS.name().equals(jwtUtil.extractType(claims))) {
            return false;
        }

        final String username = jwtUtil.extractUsername(claims);
        if (username == null) {
            return false;
        }
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            return true;
        }

        UserDetails user = userDetailsService.loadUserByUsername(username);
        if (!jwtUtil.validateToken(claims, user.getUsername())) {
            return false;
        }

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
        return true;
    }

    private void rejectToken(HttpServletRequest request, AuthErrorType errorType, String reason) {
        log.debug("Rejected JWT for {}: {}", request.getRequestURI(), reason);
        SecurityContextHolder.clearContext();
        request.setAttribute(AuthErrorType.REQUEST_ATTRIBUTE, errorType);
    }
}
