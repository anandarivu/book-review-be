package com.bookreview.controller;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProfileControllerIT {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;

    @Test
    void getProfile_shouldReturnForbiddenIfNotAuthenticated() throws Exception {
        ResultActions result = mockMvc.perform(get("/api/profile"));
        result.andExpect(status().isForbidden());
    }

    @Test
    void getFavorites_shouldReturnForbiddenIfNotAuthenticated() throws Exception {
        ResultActions result = mockMvc.perform(get("/api/profile/favorites"));
        result.andExpect(status().isForbidden());
    }

    @Test
    void addFavorite_shouldReturnForbiddenIfNotAuthenticated() throws Exception {
        String uuid = java.util.UUID.randomUUID().toString();
        ResultActions result = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/profile/favorites/" + uuid));
        result.andExpect(status().isForbidden());
    }

    @Test
    void removeFavorite_shouldReturnForbiddenIfNotAuthenticated() throws Exception {
        String uuid = java.util.UUID.randomUUID().toString();
        ResultActions result = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/profile/favorites/" + uuid));
        result.andExpect(status().isForbidden());
    }
}
