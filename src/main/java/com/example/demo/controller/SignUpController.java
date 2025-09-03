package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/api")
@Tag(name = "New User – SignUp")
@RestController
public class SignUpController {

    private final UserService userService;

    public SignUpController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    @RateLimiter(name = "loginLimiter", fallbackMethod = "rateLimitFallback")
    public ResponseEntity<String> signup(@RequestBody User user) {
        String response = userService.saveUser(user);

        if (response.startsWith("User already exists")) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT) // 409
                    .body(response);
        }

        return ResponseEntity
                .status(HttpStatus.CREATED) // 201
                .body(response);
    }

    // --- RateLimiter fallback (too many login attempts) ---
    public ResponseEntity<?> rateLimitFallback(User req, RequestNotPermitted ex) {
//        log.warn("Rate limiter triggered for username='{}'", req.getUsername());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(Map.of("error", "Too many failed signup attempts. Please try again later."));
    }
    @GetMapping("/users")
    public List<User> getUsers() {
        return userService.getUsers();
    }
}
