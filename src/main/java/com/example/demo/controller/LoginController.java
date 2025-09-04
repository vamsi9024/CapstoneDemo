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

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
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

    @Operation(summary = "Login and get JWT token")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody User req, HttpServletResponse response) {
        log.info("Login attempt for username='{}'", req.getUsername());

        return userService.findByUsername(req.getUsername())
                .map(user -> {
                    if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
                        log.warn("Wrong password for username='{}'", req.getUsername());
                        throw new InvalidCredentialsException("Invalid username or password");
                    }

                    String accessToken = tokenService.generateToken(user.getUsername());
                    String refreshToken = tokenService.generateRefreshToken(user.getUsername());

                    // 🔹 Put refresh token inside an HttpOnly cookie
                    ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                            .httpOnly(true)          // not accessible from JS
                            .secure(true)            // send only over HTTPS
                            .path("/")               // valid for all API endpoints
                            .sameSite("Strict")      // or "Lax" for frontend compatibility
                            .maxAge(7 * 24 * 60 * 60) // 7 days
                            .build();

                    response.addHeader("Set-Cookie", refreshCookie.toString());

                    log.info("Login successful for username='{}'", user.getUsername());

                    // 🔹 Only return access token in response body
                    return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));

                })
                .orElseThrow(() -> {
                    log.warn("User '{}' not found", req.getUsername());
                    return new InvalidCredentialsException("User not registered");
                });
    }





}

