package com.example.demo.controller;


import com.example.demo.exception.GlobalExceptionHandler;
import com.example.demo.exception.InvalidTokenException;
import com.example.demo.exception.MissingAuthorizationHeaderException;
import com.example.demo.service.TokenService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "Authentication – Logout")
public class LogoutController {

    private final TokenService tokenService;
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    public LogoutController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

//    @Operation(summary = "Logout and invalidate token")
//    @PostMapping("/logout")
//    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            return ResponseEntity
//                    .badRequest()
//                    .body(new AuthResponse("Missing or invalid Authorization header"));
//        }
//        String token = authHeader.substring(7);
//        if (!tokenService.validateToken(token)) {
//            return ResponseEntity
//                    .status(HttpStatus.UNAUTHORIZED)
//                    .body(new ErrorResponse("Invalid or expired token"));
//        }
//        tokenService.invalidateToken(token);
//        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
//    }

    @Operation(summary = "Logout and invalidate token")
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        log.info("Logout attempt");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header");
            throw new MissingAuthorizationHeaderException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        if (!tokenService.validateToken(token)) {
            log.warn("Invalid or expired token");
            throw new InvalidTokenException("Invalid or expired token");
        }

        tokenService.invalidateToken(token);
        log.info("Logout successful, token invalidated");
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }
}

