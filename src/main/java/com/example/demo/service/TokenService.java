package com.example.demo.service;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
public class TokenService {

    private final SecretKey key;
    private final Set<String> validTokens = Collections.synchronizedSet(new HashSet<>());

    public TokenService() {
        this.key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    // Generate a new token for the given username and store it in-memory
    public String generateToken(String username) {
        String jwt = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600_000)) // 1 hour expiry
                .signWith(key)
                .compact();

        validTokens.add(jwt);
        return jwt;
    }

    // Validate token signature and check if token is stored (not logged out)
    public boolean isTokenValid(String token) {
        if (!validTokens.contains(token)) {
            return false;
        }

        try {
            Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);  // throws if invalid
            return true;
        } catch (JwtException e) {
            // invalid token
            return false;
        }
    }

    // Remove token from in-memory store (logout)
    public boolean invalidateToken(String token) {
        return validTokens.remove(token);
    }

    // Decode and return claims from token
    public Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token) {
        // Check if token is in the in-memory set of valid tokens
        if (!validTokens.contains(token)) {
            return false; // token is logged out or never issued
        }

        try {
            Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token); // will throw exception if invalid

            return true; // token is valid and not expired
        } catch (JwtException | IllegalArgumentException e) {
            // Token is invalid (expired, malformed, bad signature, etc.)
            return false;
        }

    }
    public String getUsername(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getSubject(); // "sub" field is usually the username
        } catch (JwtException e) {
            return null; // or throw a custom exception if preferred
        }
    }


}


