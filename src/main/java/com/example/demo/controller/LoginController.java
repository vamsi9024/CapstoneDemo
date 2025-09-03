package com.example.demo.controller;


import com.example.demo.exception.GlobalExceptionHandler;
import com.example.demo.exception.InvalidCredentialsException;
import com.example.demo.model.AuthRequest;
import com.example.demo.model.AuthResponse;
import com.example.demo.model.User;
import com.example.demo.service.TokenService;
import com.example.demo.service.UserService;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

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

//    @Operation(summary = "Login and get JWT token")
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody AuthRequest req) {
//        return userService.findByUsername(req.getUsername())
//                .filter(user -> user.getPassword().equals(req.getPassword()))
//                .map(user -> {
//                    String token = tokenService.generateToken(user.getUsername());
//                    return ResponseEntity.ok(new AuthResponse(token));
//                })
//                .orElseGet(() ->
//                        ResponseEntity
//                                .status(HttpStatus.UNAUTHORIZED)
//                                .body(new ErrorResponse("Invalid credentials"))
//                );
//    }

    @Operation(summary = "Login and get JWT token")
    @RateLimiter(name = "loginRateLimiter", fallbackMethod = "rateLimitFallback")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User req) {
        log.info("Login attempt for username='{}'", req.getUsername());

        return userService.findByUsername(req.getUsername())
                .filter(user -> user.getPassword().equals(req.getPassword()))
                .map(user -> {
                    String token = tokenService.generateToken(user.getUsername());
                    return ResponseEntity.ok(new AuthResponse(token));
                })
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));
    }

    public ResponseEntity<?> rateLimitFallback(User req, RequestNotPermitted ex) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(Map.of("error", "Too many login attempts. Please try again in a few seconds."));
    }

}

