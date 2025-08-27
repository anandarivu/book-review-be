package com.bookreview.util;

import java.security.Key;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
    // Use a secure random key for production; here is a sample 256-bit key
    private static final String SECRET = "i-like-marvel-movies-until-endgame"; // Must be 32+ chars
    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());
    private final long EXPIRATION = 30 * 60 * 1000; // 30 minutes

    public String generateToken(String userId, java.util.UUID uuid, Set<String> roles) {
        return Jwts.builder()
                .setSubject(userId)
                .claim("roles", roles)
                .claim("userId", userId)
                .claim("uuid", uuid != null ? uuid.toString() : null)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(key)
                .compact();
    }

    public String getUserId(String token) {
        Claims claims = Jwts.parser().setSigningKey(key).build().parseSignedClaims(token).getPayload();
        return claims.getSubject();
    }

    public Set<String> getRoles(String token) {
    	Claims claims = Jwts.parser().setSigningKey(key).build().parseSignedClaims(token).getPayload();
        Object roles = claims.get("roles");
        if (roles instanceof Set) {
            return (Set<String>) roles;
        } else if (roles instanceof java.util.List) {
            return ((java.util.List<?>) roles).stream().map(Object::toString).collect(Collectors.toSet());
        }
        return Set.of();
    }
}
