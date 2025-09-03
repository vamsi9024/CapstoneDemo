package com.example.demo.controller;

import com.example.demo.exception.ApiException;
import com.example.demo.service.TokenService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Tag(name = "Authentication – Token Validation")
public class TokenValidationController {

    private static final Logger logger = LoggerFactory.getLogger(TokenValidationController.class);

    private final TokenService tokenService;

    public TokenValidationController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Operation(summary = "Validate JWT token")
    @CircuitBreaker(name = "tokenValidator", fallbackMethod = "authFallback")
    @GetMapping("/auth")
    public ResponseEntity<String> auth(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        if (!tokenService.validateToken(token)) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }

        String username = tokenService.getUsername(token);
        return ResponseEntity.ok("Authenticated as: " + username);
    }

    public ResponseEntity<String> authFallback(String authHeader, Throwable t) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Authentication service is currently unavailable. Please try again in a few moments.");
    }

}