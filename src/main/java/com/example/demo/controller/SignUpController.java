package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api")
@Tag(name = "New User – SignUp")
@RestController
public class SignUpController {

    private final UserService userService;

    public SignUpController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signin")
    public ResponseEntity<String> signin(@RequestBody User user) {
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

    @GetMapping("/users")
    public List<User> getUsers() {
        return userService.getUsers();
    }
}
