package com.example.demo.service;


import com.example.demo.model.User;
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.PostConstruct;
//import java.io.InputStream;
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class UserService {
//
//    private List<User> users;
//
//    @PostConstruct
//    public void loadUsers() {
//        ObjectMapper mapper = new ObjectMapper();
//        TypeReference<List<User>> typeRef = new TypeReference<>() {};
//        InputStream inputStream = getClass().getResourceAsStream("/users.json");
//
//        try {
//            users = mapper.readValue(inputStream, typeRef);
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to load users from JSON", e);
//        }
//    }
//
//    public Optional<User> findByUsername(String username) {
//        return users.stream()
//                .filter(user -> user.getUsername().equals(username))
//                .findFirst();
//    }
//}
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

    // Save a new user (registration, seeding, etc.)
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    // Find user by username
    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(userRepository.findByUsername(username));

    }
    public List<User> getUsers()
    {
        return userRepository.findAll();
    }

}