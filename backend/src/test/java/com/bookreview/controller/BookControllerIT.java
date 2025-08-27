package com.bookreview.controller;

import com.bookreview.dto.BookRequest;
import com.bookreview.model.Book;
import com.bookreview.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BookControllerIT {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BookRepository bookRepository;

    @Test
    void createBook_shouldReturnForbiddenForNonAdmin() throws Exception {
    String json = "{" +
        "\"title\": \"Integration Book\"," +
        "\"author\": \"Author\"," +
        "\"description\": \"Desc\"," +
        "\"coverImageUrl\": \"http://img.com\"," +
        "\"genres\": [\"Fiction\"]," +
        "\"publishedYear\": 2022}";
        ResultActions result = mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
        result.andExpect(status().isForbidden());
    }

    @Test
    void getBooks_shouldReturnBooks() throws Exception {
        ResultActions result = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/books"));
        result.andExpect(status().isOk());
    }

    @Test
    void getBook_shouldReturnNotFoundIfMissing() throws Exception {
        String uuid = java.util.UUID.randomUUID().toString();
        ResultActions result = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/books/" + uuid));
        result.andExpect(status().isNotFound());
    }

    @Test
    void getAverageRating_shouldReturnOk() throws Exception {
        String uuid = java.util.UUID.randomUUID().toString();
        ResultActions result = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/books/" + uuid + "/average-rating"));
        result.andExpect(status().isOk());
    }

    @Test
    void getTopRatedBooks_shouldReturnOk() throws Exception {
        ResultActions result = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/books/top-rated"));
        result.andExpect(status().isOk());
    }
}
