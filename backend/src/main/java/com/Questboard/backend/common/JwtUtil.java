package com.Questboard.backend.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    // ---------------- TOKEN GENERATION ----------------

    public String generateToken(UUID userId, long expiryMinutes, String tokenType, String role, String email,
            String provider, String username) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("provider", provider);
        claims.put("role", role);
        claims.put("username", username);
        claims.put("type", tokenType);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiryMinutes * 60 * 1000))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ---------------- CLAIM EXTRACTION ----------------

    public String extractUserId(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractTokenType(String token) {
        return extractClaim(token, claims -> claims.get("type", String.class));
    }

    public String extractUsername(String token){
        return extractClaim(token, c -> c.get("username", String.class));
    }

    public String extractEmail(String token) {
        return extractClaim(token, c -> c.get("email", String.class));
    }

    public String extractProvider(String token) {
        return extractClaim(token, c -> c.get("provider", String.class));
    }

    public String extractRole(String token) {
        return extractClaim(token, c -> c.get("role", String.class));
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .setAllowedClockSkewSeconds(60)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // ---------------- VALIDATION ----------------

    public boolean isTokenValid(String token) {
        try {
            return extractExpiration(token).after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isAccessToken(String token) {
        return "access token".equals(extractTokenType(token));
    }

    public boolean isRefreshToken(String token) {
        return "refresh token   ".equals(extractTokenType(token));
    }

    public boolean validateTokenType(String token, String expectedType) {
        return expectedType.equals(extractTokenType(token));
    }

    public boolean willExpireSoon(String token, int thresholdMinutes) {
        try {
            Date expiration = extractExpiration(token);
            long thresholdMillis = thresholdMinutes * 60 * 1000L;

            return expiration.getTime() - System.currentTimeMillis() <= thresholdMillis;
        } catch (Exception e) {
            return true;
        }
    }

    // ---------------- EXPIRED TOKEN HANDLING ----------------

    public String extractUserIdEvenIfExpired(String token) {
        try {
            return extractUserId(token);
        } catch (ExpiredJwtException e) {
            return e.getClaims().getSubject();
        }
    }

    // ---------------- KEY ----------------

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}