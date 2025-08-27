package com.bookreview.service;

import com.bookreview.model.Book;
import com.bookreview.model.Review;
import com.bookreview.dto.BookDto;
import com.bookreview.mapper.BookMapper;
import com.bookreview.repository.BookRepository;
import com.bookreview.repository.UserRepository;
import com.bookreview.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LLMRecommendationService {
    @Value("${perplexity.api.key:YOUR_PERPLEXITY_API_KEY}")
    private String apiKey;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    public List<BookDto> getRecommendations(String userId) {
        var userOpt = userRepository.findByUserId(userId);
        if (userOpt.isEmpty()) return Collections.emptyList();
        var user = userOpt.get();
        // Get favorites
        Set<Book> favorites = user.getFavorites();
        // Get reviews
        List<Review> reviews = reviewRepository.findByUserIdAndDeletedFalse(userId, org.springframework.data.domain.Pageable.unpaged()).getContent();
        // Get all available books
        List<Book> allBooks = bookRepository.findAll();
        String availableBooks = allBooks.stream()
            .map(b -> String.format("{title: '%s', author: '%s', genres: %s, id: %s}", b.getTitle(), b.getAuthor(), b.getGenres(), b.getId()))
            .collect(Collectors.joining(", "));
        // Build prompt
        StringBuilder prompt = new StringBuilder();
        prompt.append("Here is a list of available books on our platform: [");
        prompt.append(availableBooks);
        prompt.append("]. Suggest 10 books for a user who likes these books: ");
        prompt.append(favorites.stream().map(Book::getTitle).collect(Collectors.joining(", ")));
        prompt.append(" whose authers are: ");
        prompt.append(favorites.stream().map(Book::getAuthor).collect(Collectors.joining(", ")));
        prompt.append(". The user has reviewed: ");
        prompt.append(reviews.stream().map(r -> r.getTitle() + " (" + r.getRating() + "/5)").collect(Collectors.joining(", ")));
        prompt.append(". Give good weightage to the book's authors, as user may tend to like other novels from same authors");
        prompt.append(". Only recommend books from the available list. Respond with a JSON array of book objects with id, title, author, and genre.");

            // Call Perplexity API
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://api.perplexity.ai/chat/completions";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);
            String body = "{\"model\":\"sonar\",\"messages\":[{\"role\":\"user\",\"content\":\"" + prompt.toString() + "\"}]}";
            HttpEntity<String> entity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            List<BookDto> recommended = new ArrayList<>();
            try {
                String json = response.getBody();
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(json);
                String content = root.path("choices").get(0).path("message").path("content").asText();
                System.out.println("content: " + content);
                int start = content.indexOf("[");
                int end = content.lastIndexOf("]") + 1;
                if (start >= 0 && end > start) {
                    String arr = content.substring(start, end);
                    List<Map<String, Object>> books = mapper.readValue(arr, List.class);
                    for (Map<String, Object> b : books) {
                        bookService.getBook(UUID.fromString((String) b.get("id"))).ifPresent(book -> {
                            recommended.add(book);
                        });
                    }
                }
            } catch (Exception e) {
                // Log and return empty list
            }
            return recommended;
    }
}
