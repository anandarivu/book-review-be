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
public class PerformanceControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should return paginated books quickly")
    void shouldReturnPaginatedBooksQuickly() throws Exception {
        long start = System.currentTimeMillis();
        mockMvc.perform(MockMvcRequestBuilders.get("/api/books?page=0&size=10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray());
        long duration = System.currentTimeMillis() - start;
        assert(duration < 1000); // Should respond within 1 second
    }

    @Test
    @DisplayName("Should return sorted books quickly")
    void shouldReturnSortedBooksQuickly() throws Exception {
        long start = System.currentTimeMillis();
        mockMvc.perform(MockMvcRequestBuilders.get("/api/books?sort=title,asc"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray());
        long duration = System.currentTimeMillis() - start;
        assert(duration < 1000);
    }

    @Test
    @DisplayName("Should return search results quickly")
    void shouldReturnSearchResultsQuickly() throws Exception {
        long start = System.currentTimeMillis();
        mockMvc.perform(MockMvcRequestBuilders.get("/api/books?search=java"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray());
        long duration = System.currentTimeMillis() - start;
        assert(duration < 1000);
    }
}
