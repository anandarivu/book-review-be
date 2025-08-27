package com.bookreview.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class XssControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should sanitize XSS in review content")
    void shouldSanitizeXssInReviewContent() throws Exception {
        String maliciousContent = "<script>alert('xss')</script>Great book!";
        String reviewJson = String.format("{\"bookId\":1,\"content\":\"%s\",\"rating\":5}", maliciousContent);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reviewJson))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").value("Great book!"));
    }

    @Test
    @DisplayName("Should allow safe review content")
    void shouldAllowSafeReviewContent() throws Exception {
        String safeContent = "Excellent read!";
        String reviewJson = String.format("{\"bookId\":1,\"content\":\"%s\",\"rating\":5}", safeContent);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reviewJson))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").value(safeContent));
    }
}
