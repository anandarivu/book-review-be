package com.bookreview.controller;

import com.bookreview.model.Book;
import com.bookreview.model.Review;
import com.bookreview.repository.BookRepository;
import com.bookreview.repository.ReviewRepository;
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
class ReviewControllerIT {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    void createReview_shouldReturnForbiddenIfNotAuthenticated() throws Exception {
    String uuid = java.util.UUID.randomUUID().toString();
    String json = "{" +
        "\"title\": \"Review\"," +
        "\"reviewText\": \"Text\"," +
        "\"rating\": 5," +
        "\"bookId\": \"" + uuid + "\"}";
    ResultActions result = mockMvc.perform(post("/api/reviews")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json));
    result.andExpect(status().isForbidden());
    }

    @Test
    void getReview_shouldReturnNotFoundIfMissing() throws Exception {
        String uuid = java.util.UUID.randomUUID().toString();
        ResultActions result = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/reviews/" + uuid));
        result.andExpect(status().isNotFound());
    }

    @Test
    void getReviewsForBook_shouldReturnOk() throws Exception {
        String uuid = java.util.UUID.randomUUID().toString();
        ResultActions result = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/reviews/book/" + uuid));
        result.andExpect(status().isOk());
    }

    @Test
    void getReviewsForUser_shouldReturnForbiddenIfNotAuthenticated() throws Exception {
        ResultActions result = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/reviews/user/me"));
        result.andExpect(status().isForbidden());
    }

    @Test
    void getAverageRating_shouldReturnOk() throws Exception {
        String uuid = java.util.UUID.randomUUID().toString();
        ResultActions result = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/reviews/book/" + uuid + "/average-rating"));
        result.andExpect(status().isOk());
    }
}
