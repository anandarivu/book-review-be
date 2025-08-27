package com.bookreview.service;

import com.bookreview.dto.ReviewRequest;
import com.bookreview.model.Book;
import com.bookreview.model.Review;
import com.bookreview.repository.BookRepository;
import com.bookreview.repository.ReviewRepository;
import com.bookreview.util.XssSanitizer;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReviewServiceTest {
    private final com.bookreview.dto.ReviewDto dummyDto;

    {
        dummyDto = new com.bookreview.dto.ReviewDto();
        dummyDto.setId(java.util.UUID.randomUUID());
        dummyDto.setBookId(java.util.UUID.randomUUID());
        dummyDto.setUserId("user1");
        dummyDto.setTitle("Sanitized");
        dummyDto.setReviewText("Sanitized");
        dummyDto.setRating(5);
    }
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private XssSanitizer xssSanitizer;
    @Mock
    private com.bookreview.mapper.ReviewMapper reviewMapper;
    @InjectMocks
    private ReviewService reviewService;

    public ReviewServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createReview_shouldSanitizeAndSaveReview() {
        UUID bookUuid = UUID.randomUUID();
        ReviewRequest request = new ReviewRequest();
        request.setTitle("<b>Title</b>");
        request.setReviewText("<script>bad()</script>Good");
        request.setRating(5);
        request.setBookId(bookUuid);
        Book book = Book.builder().id(bookUuid).build();
        when(bookRepository.findById(bookUuid)).thenReturn(Optional.of(book));
        when(xssSanitizer.sanitize(anyString())).thenReturn("Sanitized");
        Review review = Review.builder()
                .title("Sanitized")
                .reviewText("Sanitized")
                .rating(5)
                .date(LocalDate.now())
                .book(book)
                .userId("user1")
                .deleted(false)
                .build();
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

    com.bookreview.dto.ReviewDto dummyDto = new com.bookreview.dto.ReviewDto();
    dummyDto.setId(review.getId());
    dummyDto.setBookId(book.getId());
    // Set userId to match expected value
    dummyDto.setUserId("user1");
    dummyDto.setTitle(review.getTitle());
    dummyDto.setReviewText(review.getReviewText());
    dummyDto.setRating(review.getRating());
    when(reviewMapper.toDto(any(Review.class))).thenReturn(dummyDto);
    com.bookreview.dto.ReviewDto created = reviewService.createReview(request, "user1");
    assertEquals("Sanitized", created.getTitle());
    assertEquals("Sanitized", created.getReviewText());
    assertEquals(5, created.getRating());
    assertEquals("user1", created.getUserId());
    }

    @Test
    void updateReview_shouldUpdateOwnReview() {
        UUID bookUuid = UUID.randomUUID();
        UUID reviewUuid = UUID.randomUUID();
        ReviewRequest request = new ReviewRequest();
        request.setTitle("Title");
        request.setReviewText("Text");
        request.setRating(4);
        request.setBookId(bookUuid);
        Book book = Book.builder().id(bookUuid).title("Book Title").build();
        Review review = Review.builder()
            .id(reviewUuid)
            .userId("user1")
            .title("Old Title")
            .reviewText("Old Text")
            .rating(3)
            .date(LocalDate.now())
            .book(book)
            .deleted(false)
            .build();
        when(reviewRepository.findById(reviewUuid)).thenReturn(Optional.of(review));
        when(xssSanitizer.sanitize(anyString())).thenReturn("Sanitized");
        when(reviewRepository.save(any(Review.class))).thenReturn(review);
    when(reviewMapper.toDto(any(Review.class))).thenReturn(dummyDto);
    com.bookreview.dto.ReviewDto updated = reviewService.updateReview(reviewUuid, request, "user1");
    assertEquals("Sanitized", updated.getTitle());
    }

    @Test
    void updateReview_shouldThrowIfNotOwner() {
        UUID reviewUuid = UUID.randomUUID();
        ReviewRequest request = new ReviewRequest();
        Review review = Review.builder().id(reviewUuid).userId("other").build();
        when(reviewRepository.findById(reviewUuid)).thenReturn(Optional.of(review));
        assertThrows(RuntimeException.class, () -> reviewService.updateReview(reviewUuid, request, "user1"));
    }

    @Test
    void deleteReview_shouldSoftDeleteOwnReview() {
        UUID reviewUuid = UUID.randomUUID();
        Review review = Review.builder().id(reviewUuid).userId("user1").deleted(false).build();
        when(reviewRepository.findById(reviewUuid)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);
        reviewService.deleteReview(reviewUuid, "user1");
        assertTrue(review.getDeleted());
    }

    @Test
    void deleteReview_shouldThrowIfNotOwner() {
        UUID reviewUuid = UUID.randomUUID();
        Review review = Review.builder().id(reviewUuid).userId("other").deleted(false).build();
        when(reviewRepository.findById(reviewUuid)).thenReturn(Optional.of(review));
        assertThrows(RuntimeException.class, () -> reviewService.deleteReview(reviewUuid, "user1"));
    }

    @Test
    void getReview_shouldReturnIfNotDeleted() {
        UUID reviewUuid = UUID.randomUUID();
        Review review = Review.builder().id(reviewUuid).deleted(false).build();
        when(reviewRepository.findById(reviewUuid)).thenReturn(Optional.of(review));
    when(reviewMapper.toDto(any(Review.class))).thenReturn(dummyDto);
    Optional<com.bookreview.dto.ReviewDto> found = reviewService.getReview(reviewUuid);
    assertTrue(found.isPresent());
    }

    @Test
    void getReview_shouldReturnEmptyIfDeleted() {
        UUID reviewUuid = UUID.randomUUID();
        Review review = Review.builder().id(reviewUuid).deleted(true).build();
        when(reviewRepository.findById(reviewUuid)).thenReturn(Optional.of(review));
    Optional<com.bookreview.dto.ReviewDto> found = reviewService.getReview(reviewUuid);
    assertFalse(found.isPresent());
    }

    @Test
    void getReviewsForBook_shouldReturnPage() {
        UUID bookUuid = UUID.randomUUID();
        when(reviewRepository.findByBookIdAndDeletedFalse(eq(bookUuid), any())).thenReturn(Page.empty());
    when(reviewMapper.toDto(any(Review.class))).thenReturn(dummyDto);
    org.springframework.data.domain.Page<com.bookreview.dto.ReviewDto> page = reviewService.getReviewsForBook(bookUuid, 0, 20, "date");
    assertNotNull(page);
    }

    @Test
    void getReviewsForUser_shouldReturnPage() {
        when(reviewRepository.findByUserIdAndDeletedFalse(anyString(), any())).thenReturn(Page.empty());
    when(reviewMapper.toDto(any(Review.class))).thenReturn(dummyDto);
    org.springframework.data.domain.Page<com.bookreview.dto.ReviewDto> page = reviewService.getReviewsForUser("user1", 0, 20, "date");
    assertNotNull(page);
    }

    @Test
    void getAverageRating_shouldReturnValue() {
        UUID bookUuid = UUID.randomUUID();
        when(reviewRepository.findAverageRatingByBookIdAndDeletedFalse(eq(bookUuid))).thenReturn(4.5);
        Double avg = reviewService.getAverageRating(bookUuid);
        assertEquals(4.5, avg);
    }
}
