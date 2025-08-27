package com.bookreview.controller;

import com.bookreview.dto.ReviewRequest;
import com.bookreview.service.ReviewService;
import com.bookreview.util.JwtUtil;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private JwtUtil jwtUtil;

    // Only authenticated users can create reviews
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<com.bookreview.dto.ReviewDto> createReview(@RequestBody ReviewRequest request,
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String userId = jwtUtil.getUserId(token);
        com.bookreview.dto.ReviewDto review = reviewService.createReview(request, userId);
        return ResponseEntity.ok(review);
    }

        @GetMapping("/user/me")
        @PreAuthorize("isAuthenticated()")
        public ResponseEntity<Page<com.bookreview.dto.ReviewDto>> getReviewsForCurrentUser(
                @RequestHeader("Authorization") String authHeader,
                @RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "20") int size,
                @RequestParam(defaultValue = "date") String sortBy) {
            String token = authHeader.replace("Bearer ", "");
            String userId = jwtUtil.getUserId(token);
            Page<com.bookreview.dto.ReviewDto> reviews = reviewService.getReviewsForUser(userId, page, size, sortBy);
            return ResponseEntity.ok(reviews);
        }
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<com.bookreview.dto.ReviewDto> updateReview(@PathVariable UUID id, @RequestBody ReviewRequest request,
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String userId = jwtUtil.getUserId(token);
        com.bookreview.dto.ReviewDto review = reviewService.updateReview(id, request, userId);
        return ResponseEntity.ok(review);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteReview(@PathVariable java.util.UUID id,
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String userId = jwtUtil.getUserId(token);
        reviewService.deleteReview(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<com.bookreview.dto.ReviewDto> getReview(@PathVariable java.util.UUID id) {
        return reviewService.getReview(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<Page<com.bookreview.dto.ReviewDto>> getReviewsForBook(
            @PathVariable java.util.UUID bookId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "date") String sortBy) {
        Page<com.bookreview.dto.ReviewDto> reviews = reviewService.getReviewsForBook(bookId, page, size, sortBy);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/book/{bookId}/average-rating")
    public ResponseEntity<Double> getAverageRating(@PathVariable java.util.UUID bookId) {
        Double avg = reviewService.getAverageRating(bookId);
        return ResponseEntity.ok(avg);
    }
}