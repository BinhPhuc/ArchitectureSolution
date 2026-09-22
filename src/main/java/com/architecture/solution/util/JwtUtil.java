package com.architecture.solution.util;

import com.architecture.solution.entity.User;
import com.architecture.solution.enums.TokenType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
    @Value("${jwt.secretKey}")
    private String secretKey;

    private static final long ACCESS_TOKEN_VALIDITY = 5 * 60 * 1000; // 5 minutes
    private static final long REFRESH_TOKEN_VALIDITY = 7 * 24 * 60 * 60 * 1000; // 7 days

    public String generateAccessToken(User user) {
        Map<String, String> claims = new HashMap<>();
        claims.put("type", TokenType.ACCESS.toString());
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(User user, Instant refreshTokenExpirationDate) {
        Map<String, String> claims = new HashMap<>();
        claims.put("type", TokenType.REFRESH.toString());
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(refreshTokenExpirationDate == null ?
                        new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY) :
                        Date.from(refreshTokenExpirationDate))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignInKey() {
        byte[] bytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(bytes);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUsername(Claims claims) {
        return claims.getSubject();
    }

    public Date extractExpiration(Claims claims) {
        return claims.getExpiration();
    }

    public String extractType(Claims claims) {
        return claims.get("type", String.class);
    }

    public boolean isTokenExpired(Claims claims) {
        Date expirationDate = claims.getExpiration();
        return expirationDate.before(new Date());
    }

    public boolean validateToken(Claims claims, UserDetails userDetails) {
        String username = extractUsername(claims);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(claims);
    }
}
