package com.example.demo.controller;


import com.example.demo.exception.GlobalExceptionHandler;
import com.example.demo.exception.InvalidCredentialsException;
import com.example.demo.model.AuthRequest;
import com.example.demo.model.AuthResponse;
import com.example.demo.model.User;
import com.example.demo.service.TokenService;
import com.example.demo.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.web.ErrorResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")

@Tag(name = "Authentication – Login")
public class LoginController {

    private final UserService userService;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    public LoginController(UserService userService,
                           TokenService tokenService , PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
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
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody User req) {
        log.info("Login attempt for username='{}'", req.getUsername());

        return userService.findByUsername(req.getUsername())
                .filter(user -> {
                    boolean valid = passwordEncoder.matches(req.getPassword(), user.getPassword());
                    if (!valid) {
                        log.warn("Invalid credentials for username='{}'", req.getUsername());
                    }
                    return valid;
                })
                .map(user -> {
                    String accessToken = tokenService.generateToken(user.getUsername());
                    String refreshToken = tokenService.generateRefreshToken(user.getUsername());
                    log.info("Login successful for username='{}'", user.getUsername());
                    return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
                })
                .orElseThrow(() ->
                        new InvalidCredentialsException("User not registered")
                );
    }




}

