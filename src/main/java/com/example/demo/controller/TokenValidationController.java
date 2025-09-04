package com.example.demo.controller;

import com.example.demo.exception.ApiException;
import com.example.demo.exception.GlobalExceptionHandler;
import com.example.demo.service.TokenService;
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
    @GetMapping("/auth")
    public ResponseEntity<String> auth(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        logger.info("Token validation request received");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Validation failed – missing or invalid Authorization header");
            throw new ApiException(HttpStatus.BAD_REQUEST, "Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        if (!tokenService.validateToken(token)) {
            logger.warn("Validation failed – invalid or expired token");
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }

        String username = tokenService.getUsername(token);
        logger.info("Token valid for user: {}", username);
        return ResponseEntity.ok("Authenticated as: " + username);
    }
}
