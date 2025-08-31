package com.bookreview.service;

import com.bookreview.dto.ReviewRequest;
import com.bookreview.dto.ReviewDto;
import com.bookreview.model.Book;
import com.bookreview.model.Review;
import com.bookreview.repository.BookRepository;
import com.bookreview.repository.ReviewRepository;
import com.bookreview.util.XssSanitizer;
import com.bookreview.mapper.ReviewMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReviewServiceTest {
    @Test
    void testUpdateReviewSuccess() {
        UUID reviewId = UUID.randomUUID();
        ReviewRequest request = new ReviewRequest();
        request.setTitle("Updated Title");
        request.setReviewText("Updated Text");
        request.setRating(4);
        request.setBookId(UUID.randomUUID());
        Review review = Review.builder().id(reviewId).userId("user1").deleted(false).build();
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(xssSanitizer.sanitize(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);
        ReviewDto dto = new ReviewDto();
        when(reviewMapper.toDto(review)).thenReturn(dto);
        ReviewDto result = reviewService.updateReview(reviewId, request, "user1");
        assertNotNull(result);
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void testUpdateReviewNotAuthorized() {
        UUID reviewId = UUID.randomUUID();
        ReviewRequest request = new ReviewRequest();
        request.setTitle("Updated Title");
        request.setReviewText("Updated Text");
        request.setRating(4);
        request.setBookId(UUID.randomUUID());
        Review review = Review.builder().id(reviewId).userId("user1").deleted(false).build();
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        assertThrows(RuntimeException.class, () -> reviewService.updateReview(reviewId, request, "otherUser"));
    }

    @Test
    void testUpdateReviewNotFound() {
        UUID reviewId = UUID.randomUUID();
        ReviewRequest request = new ReviewRequest();
        request.setTitle("Updated Title");
        request.setReviewText("Updated Text");
        request.setRating(4);
        request.setBookId(UUID.randomUUID());
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> reviewService.updateReview(reviewId, request, "user1"));
    }

    @Test
    void testGetReviewFound() {
        UUID reviewId = UUID.randomUUID();
        Review review = Review.builder().id(reviewId).userId("user1").deleted(false).build();
        ReviewDto dto = new ReviewDto();
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(reviewMapper.toDto(review)).thenReturn(dto);
        Optional<ReviewDto> result = reviewService.getReview(reviewId);
        assertTrue(result.isPresent());
    }

    @Test
    void testGetReviewDeleted() {
        UUID reviewId = UUID.randomUUID();
        Review review = Review.builder().id(reviewId).userId("user1").deleted(true).build();
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        Optional<ReviewDto> result = reviewService.getReview(reviewId);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetReviewNotFound() {
        UUID reviewId = UUID.randomUUID();
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());
        Optional<ReviewDto> result = reviewService.getReview(reviewId);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetReviewsForBook() {
        UUID bookId = UUID.randomUUID();
        Review review = Review.builder().id(UUID.randomUUID()).userId("user1").deleted(false).build();
        ReviewDto dto = new ReviewDto();
        org.springframework.data.domain.Page<Review> reviewPage = new org.springframework.data.domain.PageImpl<>(java.util.List.of(review));
        when(reviewRepository.findByBookIdAndDeletedFalse(eq(bookId), any())).thenReturn(reviewPage);
        when(reviewMapper.toDto(review)).thenReturn(dto);
        var result = reviewService.getReviewsForBook(bookId, 0, 10, "date");
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testGetReviewsForUser() {
        String userId = "user1";
        Review review = Review.builder().id(UUID.randomUUID()).userId(userId).deleted(false).build();
        ReviewDto dto = new ReviewDto();
        org.springframework.data.domain.Page<Review> reviewPage = new org.springframework.data.domain.PageImpl<>(java.util.List.of(review));
        when(reviewRepository.findByUserIdAndDeletedFalse(eq(userId), any())).thenReturn(reviewPage);
        when(reviewMapper.toDto(review)).thenReturn(dto);
        var result = reviewService.getReviewsForUser(userId, 0, 10, "date");
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testGetAverageRating() {
        UUID bookId = UUID.randomUUID();
        when(reviewRepository.findAverageRatingByBookIdAndDeletedFalse(bookId)).thenReturn(4.0);
        Double avg = reviewService.getAverageRating(bookId);
        assertEquals(4.0, avg);
    }

    @Test
    void testValidateReviewRequestInvalidTitle() throws Exception {
        ReviewRequest request = new ReviewRequest();
        request.setTitle("");
        request.setReviewText("Text");
        request.setRating(5);
        request.setBookId(UUID.randomUUID());
        java.lang.reflect.Method m = ReviewService.class.getDeclaredMethod("validateReviewRequest", ReviewRequest.class);
        m.setAccessible(true);
        Exception ex = assertThrows(java.lang.reflect.InvocationTargetException.class, () -> m.invoke(reviewService, request));
        assertTrue(ex.getCause() instanceof RuntimeException);
    }

    @Test
    void testDeleteReviewsForBook() {
        UUID bookId = UUID.randomUUID();
        Review review = Review.builder().id(UUID.randomUUID()).userId("user1").deleted(false).build();
        org.springframework.data.domain.Page<Review> reviewPage = new org.springframework.data.domain.PageImpl<>(java.util.List.of(review));
        when(reviewRepository.findByBookIdAndDeletedFalse(eq(bookId), any())).thenReturn(reviewPage);
        doNothing().when(reviewRepository).deleteAll(any());
        reviewService.deleteReviewsForBook(bookId);
        verify(reviewRepository, times(1)).deleteAll(any());
    }
    @Mock
    private ReviewMapper reviewMapper;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private XssSanitizer xssSanitizer;
    @InjectMocks
    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateReviewSuccess() {
        ReviewRequest request = new ReviewRequest();
        UUID bookId = UUID.randomUUID();
        request.setBookId(bookId);
        request.setTitle("Title");
        request.setReviewText("Text");
        request.setRating(5);
        Book book = Book.builder().id(bookId).build();
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(xssSanitizer.sanitize(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
        Review review = Review.builder().title("Title").reviewText("Text").rating(5).book(book).userId("user1").deleted(false).build();
        when(reviewRepository.save(any(Review.class))).thenReturn(review);
        ReviewDto dto = new ReviewDto();
        when(reviewMapper.toDto(review)).thenReturn(dto);
        ReviewDto result = reviewService.createReview(request, "user1");
        assertNotNull(result);
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void testCreateReviewBookNotFound() {
        ReviewRequest request = new ReviewRequest();
        UUID bookId = UUID.randomUUID();
        request.setBookId(bookId);
        request.setTitle("Title");
        request.setReviewText("Text");
        request.setRating(5);
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> reviewService.createReview(request, "user1"));
    }

    @Test
    void testDeleteReviewNotAuthorized() {
        UUID reviewId = UUID.randomUUID();
        Review review = Review.builder().id(reviewId).userId("user1").deleted(false).build();
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        assertThrows(RuntimeException.class, () -> reviewService.deleteReview(reviewId, "otherUser"));
    }

    @Test
    void testDeleteReviewSuccess() {
        UUID reviewId = UUID.randomUUID();
        Review review = Review.builder().id(reviewId).userId("user1").deleted(false).build();
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);
        reviewService.deleteReview(reviewId, "user1");
        verify(reviewRepository, times(1)).save(any(Review.class));
    }
}
