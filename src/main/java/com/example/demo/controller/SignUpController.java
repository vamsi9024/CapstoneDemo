package com.example.demo.controller;

import com.example.demo.model.AuthResponse;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;

import java.util.List;
import java.util.Map;

@RequestMapping("/api")
@Tag(name = "New User – SignUp")

@RestController
public class SignUpController {

    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(LogoutController.class);

    public SignUpController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @RateLimiter(name = "loginLimiter", fallbackMethod = "rateLimitFallback")
    public ResponseEntity<Map<String,String>> register(@Valid @RequestBody User user) {
        String response = String.valueOf(userService.saveUser(user));

        if (response.startsWith("User already exists")) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT) // 409
                    .body(Map.of("message",response));
        }

        return ResponseEntity
                .status(HttpStatus.CREATED) // 201
                .body(Map.of("message",response));
    }
    public ResponseEntity<Map<String,String>> rateLimitFallback(User req,  RequestNotPermitted ex) {
        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS) // 429
                .body(Map.of("error", "Too many failed signup attempts. Please try again later."));
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return userService.getUsers();
    }
}
