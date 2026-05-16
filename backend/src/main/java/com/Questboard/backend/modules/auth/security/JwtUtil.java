// package com.Questboard.backend.modules.auth.security;

// import io.jsonwebtoken.Claims;
// import io.jsonwebtoken.ExpiredJwtException;
// import io.jsonwebtoken.Jwts;
// import io.jsonwebtoken.SignatureAlgorithm;
// import io.jsonwebtoken.io.Decoders;
// import io.jsonwebtoken.security.Keys;
// import jakarta.servlet.http.Cookie;
// import jakarta.servlet.http.HttpServletRequest;

// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.http.ResponseCookie;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.stereotype.Service;

// import javax.crypto.SecretKey;
// import java.util.Date;
// import java.util.HashMap;
// import java.util.Map;
// import java.util.UUID;
// import java.util.function.Function;

// @Service
// public class JwtUtil {

//     @Value("${jwt.secret}")
//     private String secretKey;

//     public String generateToken(UUID userId, long expiryMinutes, String tokenType) {

//     Map<String, Object> claims = new HashMap<>();
//     claims.put("type", tokenType);

//     return Jwts.builder()
//             .setClaims(claims)
//             .setSubject(userId.toString())
//             .setIssuedAt(new Date(System.currentTimeMillis()))
//             .setExpiration(
//                     new Date(System.currentTimeMillis() + expiryMinutes * 60 * 1000)
//             )
//             .signWith(getKey(), SignatureAlgorithm.HS256)
//             .compact();
// }

//     private SecretKey getKey() {
//         byte[] keyBytes = Decoders.BASE64.decode(secretKey);
//         return Keys.hmacShaKeyFor(keyBytes);
//     }

//     public String extractUserName(String token) {
//         return extractClaim(token, Claims::getSubject);
//     }

//     private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
//         final Claims claims = extractAllClaims(token);
//         return claimResolver.apply(claims);
//     }

//     private Claims extractAllClaims(String token) {
//         return Jwts.parserBuilder()
//                 .setSigningKey(getKey())
//                 .setAllowedClockSkewSeconds(60)
//                 .build()
//                 .parseClaimsJws(token)
//                 .getBody();
//     }

//     public boolean validateToken(String token, UserDetails userDetails) {
//         final String userName = extractUserName(token);
//         return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
//     }

//     private boolean isTokenExpired(String token) {
//         return extractExpiration(token).before(new Date());
//     }

//     private Date extractExpiration(String token) {
//         return extractClaim(token, Claims::getExpiration);
//     }

//     public String extractTokenFromCookies(HttpServletRequest request) {
//         if (request.getCookies() != null) {
//             for (Cookie cookie : request.getCookies()) {
//                 if ("access_token".equals(cookie.getName())) {
//                     return cookie.getValue();
//                 }
//             }
//         }
//         return null;
//     }

//     public boolean willExpireSoon(String token, int thresholdMinutes) {
//         try {
//             Claims claims = Jwts.parserBuilder()
//                     .setSigningKey(getKey())
//                     .build()
//                     .parseClaimsJws(token)
//                     .getBody();

//             Date expirationDate = claims.getExpiration();
//             long currentTimeMillis = System.currentTimeMillis();
//             long thresholdMillis = thresholdMinutes * 60 * 1000L;

//             return expirationDate.getTime() - currentTimeMillis <= thresholdMillis;
//         } catch (Exception e) {
//             return true;
//         }
//     }

//     public String extractUsernameEvenIfExpired(String token) {
//         try {
//             return extractUserName(token);
//         } catch (ExpiredJwtException e) {
//             return e.getClaims().getSubject();
//         }
//     }

//     public String extractUsernameFromCookie(HttpServletRequest request) {
//         if (request.getCookies() != null) {
//             for (Cookie cookie : request.getCookies()) {
//                 if ("username".equals(cookie.getName())) {
//                     return cookie.getValue();
//                 }
//             }
//         }
//         return null;
//     }

//     public ResponseCookie createAuthCookie(String token) {
//         return ResponseCookie.from("AUTH_TOKEN", token)
//                 .httpOnly(true)
//                 .secure(true)
//                 .path("/")
//                 .sameSite("Strict")
//                 .maxAge(15 * 60)
//                 .build();
//     }

// }

package com.Questboard.backend.modules.auth.security;

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
            String provider) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("provider", provider);
        claims.put("role", role);
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

    // public boolean validateToken(String token) {
    // try {
    // return !isExpired(token);
    // } catch (Exception e) {
    // return false;
    // }
    // }

    public boolean isTokenValid(String token) {
        try {
            return extractExpiration(token).after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isAccessToken(String token) {
        return "access".equals(extractTokenType(token));
    }

    public boolean isRefreshToken(String token) {
        return "refresh".equals(extractTokenType(token));
    }

    public boolean validateTokenType(String token, String expectedType) {
        return expectedType.equals(extractTokenType(token));
    }

    // private boolean isExpired(String token) {
    //     return extractExpiration(token).before(new Date());
    // }

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