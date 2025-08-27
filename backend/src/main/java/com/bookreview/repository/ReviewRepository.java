package com.bookreview.repository;

import com.bookreview.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    Page<Review> findByBookIdAndDeletedFalse(UUID bookId, Pageable pageable);

    @org.springframework.data.jpa.repository.Query(value = "SELECT AVG(r.rating) FROM reviews r WHERE r.book_id = :bookId AND r.deleted = false", nativeQuery = true)
    Double findAverageRatingByBookIdAndDeletedFalse(UUID bookId);

    Page<Review> findByUserIdAndDeletedFalse(String userId, Pageable pageable);
}
