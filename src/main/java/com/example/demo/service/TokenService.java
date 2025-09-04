package com.example.demo.service;
import com.example.demo.model.RefreshToken;
import com.example.demo.repository.RefreshTokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.*;

@Service
public class TokenService {

    private final SecretKey key;
    private final Set<String> validTokens = Collections.synchronizedSet(new HashSet<>());
    private final RefreshTokenRepository refreshTokenRepository;
   // Expiry times
    private static final long ACCESS_TOKEN_EXPIRY = 1000 * 60 * 15;   // 15 minutes
    private static final long REFRESH_TOKEN_EXPIRY = 1000L * 60 * 60 * 24 * 7; // 7 days


    public TokenService(RefreshTokenRepository refreshTokenRepository) {
        this.key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        this.refreshTokenRepository = refreshTokenRepository;
    }

    // Generate a new token for the given username and store it in-memory
    public String generateToken(String username) {
        String jwt = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRY))
                .signWith(key)
                .compact();

        validTokens.add(jwt);
        return jwt;
    }
    // generate refresh token and save
    public RefreshToken createRefreshToken(String username) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setUsername(username);
        refreshToken.setExpiryDate(Instant.now().plusSeconds(86400)); // 1 day

        return refreshTokenRepository.save(refreshToken);
    }
    public String generateRefreshToken(String username) {
        String refreshToken = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRY))
                .signWith(key)
                .compact();

        // save in DB for extra security
        storeRefreshToken(username, refreshToken);

        return refreshToken;
    }

    // Store refresh token in DB
    public void storeRefreshToken(String username, String refreshToken) {
        RefreshToken entity = new RefreshToken();
        entity.setUsername(username);
        entity.setToken(refreshToken);
        entity.setExpiryDate(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRY).toInstant());
        refreshTokenRepository.save(entity);
    }

    // Validate refresh token
    public boolean validateRefreshToken(String token) {
        try {
            Jwts.parser().setSigningKey(key).build().parseClaimsJws(token);
            return refreshTokenRepository.findByToken(token).isPresent();
        } catch (JwtException e) {
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



    public String getUsernameFromToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .map(RefreshToken::getUsername)
                .orElse(null);
    }


}



