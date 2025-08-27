package com.bookreview.controller;

import com.bookreview.dto.SignupRequest;
import com.bookreview.model.User;
import com.bookreview.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIT {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;

    @Test
    void signup_shouldCreateUser() throws Exception {
        String json = "{" +
                "\"userId\": \"integrationuser\"," +
                "\"email\": \"integration@example.com\"," +
                "\"password\": \"Password@123\"}";
        ResultActions result = mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is("integrationuser")))
                .andExpect(jsonPath("$.email", is("integration@example.com")));
        userRepository.deleteAll();
    }

    @Test
    void login_shouldReturnToken() throws Exception {
        // First, signup
        String signupJson = "{" +
                "\"userId\": \"loginuser\"," +
                "\"email\": \"login@example.com\"," +
                "\"password\": \"Password@123\"}";
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(signupJson));
        String loginJson = "{" +
                "\"userId\": \"loginuser\"," +
                "\"password\": \"Password@123\"}";
        ResultActions result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson));
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.token", not(emptyOrNullString())));
        userRepository.deleteAll();
    }

    @Test
    void logout_shouldReturnSuccessMessage() throws Exception {
        ResultActions result = mockMvc.perform(post("/api/auth/logout"));
        result.andExpect(status().isOk())
                .andExpect(content().string(containsString("Logged out successfully")));
    }

    @Test
    void adminSignup_shouldCreateAdminWithSecret() throws Exception {
        String json = "{" +
                "\"userId\": \"adminuser\"," +
                "\"email\": \"admin@example.com\"," +
                "\"password\": \"Password@123\"," +
                "\"secret\": \"changeme\"}";
        ResultActions result = mockMvc.perform(post("/api/auth/admin/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is("adminuser")))
                .andExpect(jsonPath("$.email", is("admin@example.com")));
        userRepository.deleteAll();
    }

    @Test
    void adminSignup_shouldFailWithWrongSecret() throws Exception {
        String json = "{" +
                "\"userId\": \"adminuser\"," +
                "\"email\": \"admin@example.com\"," +
                "\"password\": \"Password@123\"," +
                "\"secret\": \"wrongsecret\"}";
        ResultActions result = mockMvc.perform(post("/api/auth/admin/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
        result.andExpect(status().is4xxClientError());
    }
}
