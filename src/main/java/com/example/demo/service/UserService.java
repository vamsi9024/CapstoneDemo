package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String saveUser(User user) {
        Optional<User> existingUser = Optional.ofNullable(userRepository.findByUsername(user.getUsername()));

        if (existingUser.isPresent()) {
            return "User already exists with username: " + user.getUsername();
        }

        userRepository.save(user);
        return "New user created successfully";
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }
}
