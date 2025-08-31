package com.bookreview.service;

import com.bookreview.model.Book;
import com.bookreview.model.Review;
import com.bookreview.model.User;
import com.bookreview.dto.BookDto;
import com.bookreview.repository.BookRepository;
import com.bookreview.repository.UserRepository;
import com.bookreview.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LLMRecommendationServiceTest {
    @Mock
    private org.springframework.web.client.RestTemplate restTemplate;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private BookService bookService;
    @Mock
    private BookRepository bookRepository;
    @InjectMocks
    private LLMRecommendationService llmRecommendationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    llmRecommendationService.setRestTemplate(restTemplate);
    }

    @Test
    void testGetRecommendationsUserNotFound() {
        when(userRepository.findByUserId("user1")).thenReturn(Optional.empty());
        List<BookDto> result = llmRecommendationService.getRecommendations("user1");
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetRecommendationsWithFavoritesAndReviews() {
        Book book1 = Book.builder().id(UUID.randomUUID()).title("Book One").author("Author A").genres(Set.of("Fiction")).build();
        Book book2 = Book.builder().id(UUID.randomUUID()).title("Book Two").author("Author B").genres(Set.of("Drama")).build();
        Set<Book> favorites = Set.of(book1, book2);
        User user = User.builder().userId("user1").favorites(favorites).build();
        when(userRepository.findByUserId("user1")).thenReturn(Optional.of(user));
        Review review = Review.builder().title("Book One").rating(5).build();
        when(reviewRepository.findByUserIdAndDeletedFalse(eq("user1"), any())).thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(review)));
        when(bookRepository.findAll()).thenReturn(List.of(book1, book2));
        // Mock bookService.getBook to return BookDto
        BookDto dto1 = new BookDto();
        when(bookService.getBook(book1.getId())).thenReturn(Optional.of(dto1));
        BookDto dto2 = new BookDto();
        when(bookService.getBook(book2.getId())).thenReturn(Optional.of(dto2));
    // Mock RestTemplate response
    String mockJson = "{\"choices\":[{\"message\":{\"content\":\"[{\\\"id\\\":\\\"" + book1.getId() + "\\\",\\\"title\\\":\\\"Book One\\\",\\\"author\\\":\\\"Author A\\\",\\\"genres\\\":[\\\"Fiction\\\"]}]\"}}]}";
    org.springframework.http.ResponseEntity<String> mockResponse = new org.springframework.http.ResponseEntity<>(mockJson, org.springframework.http.HttpStatus.OK);
    when(restTemplate.postForEntity(anyString(), any(), eq(String.class))).thenReturn(mockResponse);
    List<BookDto> result = llmRecommendationService.getRecommendations("user1");
    assertNotNull(result);
    }
}
