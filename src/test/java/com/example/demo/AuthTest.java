package com.example.demo;

import com.example.demo.model.User;

import com.example.demo.repository.UserRepository;
import com.example.demo.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    private TokenService tokenService;


    @BeforeEach
    void setup() {
        userRepository.deleteAll(); // clean DB before each test
    }

    @Test
    @DisplayName("Register Successful")
    void testRegisterUser_Success() throws Exception {
        String json = "{\"username\":\"Priyal\",\"password\":\"Password@123\"}"; // must meet validation rules

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated()) // 201 CREATED
        .andExpect(jsonPath("$.message").value(Matchers.startsWith("New user created successfully")));
    }

    @Test
    void testRegisterUser_AlreadyExists() throws Exception {
        String json = "{\"username\":\"Priyal\",\"password\":\"Password@123\"}";

        // First time -> success
        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        // Second time -> conflict
        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(Matchers.startsWith("User already exists")));

    }



    @Test
    void testLoginFailure_UserNotRegistered() throws Exception {
        String json = "{\"username\":\"UnknownUser\",\"password\":\"SomePass\"}";

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("User not registered"));
    }

    @Test
    void testLoginFailure_InvalidPassword() throws Exception {
        // Create a user with encoded password
        User user = new User();
        user.setUsername("Priyal");
        user.setPassword(passwordEncoder.encode("CorrectPass"));
        userRepository.save(user);

        // Try login with wrong password
        String json = "{\"username\":\"Priyal\", \"password\":\"WrongPass\"}";

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }

    @Test
    void testLoginSuccess() throws Exception {
        // Create a user with encoded password
        User user = new User();
        user.setUsername("Priyal");
        user.setPassword(passwordEncoder.encode("CorrectPass"));
        userRepository.save(user);

        String json = "{\"username\":\"Priyal\", \"password\":\"CorrectPass\"}";

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }
    void testLogout_Success() throws Exception {
        String token = tokenService.generateToken("Priyal");

        mockMvc.perform(post("/api/logout")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logged out successfully"));
    }

    @Test
    void testLogout_NoToken() throws Exception {
        mockMvc.perform(post("/api/logout"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Missing or invalid Authorization header"));
    }

}
