package com.bookreview.service;

import com.bookreview.dto.ReviewRequest;
import com.bookreview.model.Book;
import com.bookreview.model.Review;
import com.bookreview.repository.BookRepository;
import com.bookreview.repository.ReviewRepository;
import com.bookreview.util.XssSanitizer;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
public class ReviewService {
    @Transactional
    public void deleteReviewsForBook(UUID bookId) {
        var reviews = reviewRepository.findByBookIdAndDeletedFalse(bookId, Pageable.unpaged()).getContent();
        reviewRepository.deleteAll(reviews);
    }
    @Autowired
    private com.bookreview.mapper.ReviewMapper reviewMapper;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private XssSanitizer xssSanitizer;

    @Transactional
    public com.bookreview.dto.ReviewDto createReview(ReviewRequest request, String userId) {
    validateReviewRequest(request);
    Book book = bookRepository.findById(request.getBookId())
        .orElseThrow(() -> new RuntimeException("Book not found"));
    Review review = Review.builder()
        .title(xssSanitizer.sanitize(request.getTitle()))
        .reviewText(xssSanitizer.sanitize(request.getReviewText()))
        .rating(request.getRating())
        .date(LocalDate.now())
        .book(book)
        .userId(userId)
        .deleted(false)
        .build();
    return reviewMapper.toDto(reviewRepository.save(review));
    }

    @Transactional
    public com.bookreview.dto.ReviewDto updateReview(java.util.UUID id, ReviewRequest request, String userId) {
        validateReviewRequest(request);
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        if (!review.getUserId().equals(userId)) {
            throw new RuntimeException("Not authorized");
        }
        review.setTitle(xssSanitizer.sanitize(request.getTitle()));
        review.setReviewText(xssSanitizer.sanitize(request.getReviewText()));
        review.setRating(request.getRating());
        return reviewMapper.toDto(reviewRepository.save(review));
    }

    @Transactional
    public void deleteReview(java.util.UUID id, String userId) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        if (!review.getUserId().equals(userId)) {
            throw new RuntimeException("Not authorized");
        }
        review.setDeleted(true);
        reviewRepository.save(review);
    }

    public Optional<com.bookreview.dto.ReviewDto> getReview(UUID id) {
        return reviewRepository.findById(id)
            .filter(r -> !r.getDeleted())
            .map(reviewMapper::toDto);
    }

    public Page<com.bookreview.dto.ReviewDto> getReviewsForBook(java.util.UUID bookId, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return reviewRepository.findByBookIdAndDeletedFalse(bookId, pageable)
            .map(reviewMapper::toDto);
    }

    public Page<com.bookreview.dto.ReviewDto> getReviewsForUser(String userId, int page, int size, String sortBy) {
        Sort sort = Sort.by(Sort.Direction.DESC, "lastModifiedAt");
        Pageable pageable = PageRequest.of(page, size, sort);
        return reviewRepository.findByUserIdAndDeletedFalse(userId, pageable)
            .map(reviewMapper::toDto);
    }

    public Double getAverageRating(UUID bookId) {
        return reviewRepository.findAverageRatingByBookIdAndDeletedFalse(bookId);
    }

    private void validateReviewRequest(ReviewRequest request) {
        if (!StringUtils.hasText(request.getTitle()) ||
                !StringUtils.hasText(request.getReviewText()) ||
                request.getRating() == null ||
                request.getBookId() == null) {
            throw new RuntimeException("All fields must be non-empty");
        }
        if (request.getTitle().length() > 128 || request.getTitle().length() < 1) {
            throw new RuntimeException("Title length must be 1-128 characters");
        }
        if (request.getReviewText().length() > 1024 || request.getReviewText().length() < 1) {
            throw new RuntimeException("Review text length must be 1-1024 characters");
        }
        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }
    }
}
