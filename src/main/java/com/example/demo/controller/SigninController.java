package com.example.demo.controller;

import com.example.demo.model.AuthResponse;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api")
@Tag(name = "New User – SignIn")
@RestController
public class SigninController {

    private final UserService userService;

    public SigninController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signin")
    public ResponseEntity<String> signin(@RequestBody User user) {
        System.out.println(user);
        userService.saveUser(user);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("New user created successfully");
    }

    @GetMapping("/users")
    public List<User> getUsers(){
        return userService.getUsers();
    }
}
