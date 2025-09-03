package com.example.demo.controller;
//
//import com.example.demo.model.AuthRequest;
//import com.example.demo.model.AuthResponse;
//import com.example.demo.model.User;
//import com.example.demo.service.TokenService;
//import com.example.demo.service.UserService;
//import io.github.resilience4j.ratelimiter.RequestNotPermitted;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.validation.Valid;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import java.util.Map;
//import org.springframework.web.bind.annotation.*;
//import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
//
//
//
//@RestController
//@RequestMapping("/api")
public class AuthController {
//
//    private final TokenService tokenService;
//    private final UserService userService;
//
//    public AuthController(TokenService tokenService, UserService userService) {
//        this.tokenService = tokenService;
//        this.userService = userService;
//    }
//
//    @RateLimiter(name = "authRateLimiter", fallbackMethod = "rateLimitFallback")
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody AuthRequest req) {
//        return userService.findByUsername(req.getUsername())
//                .filter(user -> user.getPassword().equals(req.getPassword()))
//                .map(user -> {
//                    String token = tokenService.generateToken(user.getUsername());
//                    return ResponseEntity.ok(new AuthResponse(token));
//                })
//                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                        .body((AuthResponse) Map.of("error", "Invalid credentials")));
//    }
//
//    // Fallback method for rate limiter
//    public ResponseEntity<?> rateLimitFallback(AuthRequest req, RequestNotPermitted ex) {
//        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
//                .body(Map.of("error", "Too many login attempts. Please try again later."));
//    }
//
//    @GetMapping("/auth")
//    public ResponseEntity<?> auth(@RequestHeader("Authorization") String authHeader) {
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            return ResponseEntity.badRequest().body("Missing or invalid Authorization header");
//        }
//
//        String token = authHeader.substring(7); // remove "Bearer " prefix
//
//        if (!tokenService.validateToken(token)) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
//        }
//
//        String username = tokenService.getUsername(token);
//        return ResponseEntity.ok("Authenticated as: " + username);
//    }
//
//    @PostMapping("/logout")
//    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            return ResponseEntity.badRequest().body(Map.of("error", "Missing or invalid Authorization header"));
//        }
//
//        String token = authHeader.substring(7);
//
//        if (!tokenService.validateToken(token)) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid or expired token"));
//        }
//
//        tokenService.invalidateToken(token);
//        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
//    }
}
//
//
//
//
//
