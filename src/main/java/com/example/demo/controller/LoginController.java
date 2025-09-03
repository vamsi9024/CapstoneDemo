package com.example.demo.controller;


import com.example.demo.exception.GlobalExceptionHandler;
import com.example.demo.exception.InvalidCredentialsException;
import com.example.demo.model.AuthResponse;
import com.example.demo.model.User;
import com.example.demo.service.TokenService;
import com.example.demo.service.UserService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;

import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "Authentication – Login")
public class LoginController {

    private final UserService userService;
    private final TokenService tokenService;
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    public LoginController(UserService userService,
                           TokenService tokenService) {
        this.userService = userService;
        this.tokenService = tokenService;
    }

    @Operation(summary = "Login and get JWT token")
    @PostMapping("/login")
//    @CircuitBreaker(name = "loginService", fallbackMethod = "loginFallback")
    @RateLimiter(name = "loginLimiter", fallbackMethod = "rateLimitFallback") // Add RateLimiter
    public ResponseEntity<?> login(@RequestBody User req) {
        log.info("Login attempt for username='{}'", req.getUsername());

        return userService.findByUsername(req.getUsername())
                .filter(user -> {
                    boolean valid = user.getPassword().equals(req.getPassword());
                    if (!valid) {
                        log.warn("Invalid credentials for username='{}'", req.getUsername());
                    }
                    return valid;
                })
                .map(user -> {
                    String token = tokenService.generateToken(user.getUsername());
                    log.info("Login successful for username='{}'", user.getUsername());
                    return ResponseEntity.ok(new AuthResponse(token));
                })
                .orElseThrow(() ->
                        new InvalidCredentialsException("Invalid username or password")
                );
    }

    // --- RateLimiter fallback (too many login attempts) ---
    public ResponseEntity<?> rateLimitFallback(User req, RequestNotPermitted ex) {
        log.warn("Rate limiter triggered for username='{}'", req.getUsername());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(Map.of("error", "Too many failed login attempts. Please try again later."));
    }

//    public ResponseEntity<?> loginFallback(User req, Throwable t) {
//        log.error("Circuit breaker fallback triggered for login username='{}'", req.getUsername(), t);
//        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
//                .body(Map.of("error", "Login service temporarily unavailable. Please try again later."));
//    }
}
