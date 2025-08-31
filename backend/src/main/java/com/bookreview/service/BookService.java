package com.bookreview.service;

import com.bookreview.dto.BookRequest;
import com.bookreview.model.Book;
import com.bookreview.repository.BookRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
public class BookService {
    @Autowired
    private com.bookreview.util.JwtUtil jwtUtil;

    @Autowired
    private com.bookreview.service.FavoriteService favoriteService;

    @Autowired
    private com.bookreview.repository.UserRepository userRepository;

    public com.bookreview.util.JwtUtil getJwtUtil() {
        return jwtUtil;
    }

    public Page<com.bookreview.dto.BookDto> getRecommendations(String userId, int page, int size) {
        // Get user's favorite books
        var favorites = favoriteService.getFavorites(userId);
        java.util.Set<String> genres = new java.util.HashSet<>();
        for (var book : favorites) {
            if (book.getGenres() != null) genres.addAll(book.getGenres());
        }
        // // If no favorites, get genres from user profile (if available)
        // if (genres.isEmpty()) {
        //     var userOpt = userRepository.findByUserId(userId);
        //     if (userOpt.isPresent()) {
        //         var user = userOpt.get();
        //         for (var book : user.getFavorites()) {
        //             if (book.getGenres() != null) genres.addAll(book.getGenres());
        //         }
        //     }
        // }
        // Find books matching these genres, excluding already favorited books
        var allBooks = bookRepository.findAll();
        var recommended = allBooks.stream()
            .filter(book -> genres.stream().anyMatch(g -> book.getGenres().contains(g)))
            .filter(book -> !favorites.contains(book))
            .map(bookMapper::toDto)
            .toList();
        int total = recommended.size();
        int fromIndex = Math.min(page * size, total);
        int toIndex = Math.min(fromIndex + size, total);
        var pagedBooks = recommended.subList(fromIndex, toIndex);
        Pageable pageable = PageRequest.of(page, size);
        return new org.springframework.data.domain.PageImpl<>(pagedBooks, pageable, total);
    }
    @Autowired
    private ReviewService reviewService;
    @Autowired
    com.bookreview.mapper.BookMapper bookMapper;

    public Page<com.bookreview.dto.BookDto> getTopRatedBooks(int page, int size) {
        // Fetch all books
        var allBooks = bookRepository.findAll();
        // Only include books that have at least one review
        var reviewedBooks = allBooks.stream()
                .filter(book -> {
                    Double avg = reviewService.getAverageRating(book.getId());
                    return avg != null;
                })
                .sorted((b1, b2) -> {
                    Double r1 = reviewService.getAverageRating(b1.getId());
                    Double r2 = reviewService.getAverageRating(b2.getId());
                    return Double.compare(r2 != null ? r2 : 0, r1 != null ? r1 : 0);
                })
                .map(book -> {
                    var dto = bookMapper.toDto(book);
                    dto.setAverageRating(reviewService.getAverageRating(book.getId()));
                    dto.setReviewCount((int) reviewService.getReviewsForBook(book.getId(), 0, Integer.MAX_VALUE, "date").getTotalElements());
                    return dto;
                })
                .toList();
        int total = reviewedBooks.size();
        int fromIndex = Math.min(page * size, total);
        int toIndex = Math.min(fromIndex + size, total);
        var pagedBooks = reviewedBooks.subList(fromIndex, toIndex);
        Pageable pageable = PageRequest.of(page, size);
        return new org.springframework.data.domain.PageImpl<>(pagedBooks, pageable, total);
    }

    @Autowired
    private BookRepository bookRepository;

    @Transactional
    public com.bookreview.dto.BookDto createBook(BookRequest request) {
        validateBookRequest(request);
        Book book = Book.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .description(request.getDescription())
                .coverImageUrl(request.getCoverImageUrl())
                .genres(request.getGenres())
                .publishedYear(request.getPublishedYear())
                .build();
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Transactional
    public com.bookreview.dto.BookDto updateBook(java.util.UUID id, BookRequest request) {
        validateBookRequest(request);
        Book book = bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Book not found"));
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setDescription(request.getDescription());
        book.setCoverImageUrl(request.getCoverImageUrl());
        book.setGenres(request.getGenres());
        book.setPublishedYear(request.getPublishedYear());
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Transactional
    public void deleteBook(java.util.UUID id) {
        // Delete all reviews for this book first using ReviewService
        reviewService.deleteReviewsForBook(id);
        bookRepository.deleteById(id);
    }

    public Optional<com.bookreview.dto.BookDto> getBook(java.util.UUID id) {
        return bookRepository.findById(id).map(book -> {
            var dto = bookMapper.toDto(book);
            dto.setAverageRating(reviewService.getAverageRating(book.getId()));
            dto.setReviewCount((int) reviewService.getReviewsForBook(book.getId(), 0, Integer.MAX_VALUE, "date").getTotalElements());
            return dto;
        });
    }

    public Page<com.bookreview.dto.BookDto> searchBooks(String search, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Book> books;
        if (StringUtils.hasText(search)) {
            books = bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(search, search,
                    pageable);
        } else {
            books = bookRepository.findAll(pageable);
        }
        return books.map(book -> {
            var dto = bookMapper.toDto(book);
            dto.setAverageRating(reviewService.getAverageRating(book.getId()));
            dto.setReviewCount((int) reviewService.getReviewsForBook(book.getId(), 0, Integer.MAX_VALUE, "date").getTotalElements());
            return dto;
        });
    }

    private void validateBookRequest(BookRequest request) {
        if (!StringUtils.hasText(request.getTitle()) ||
                !StringUtils.hasText(request.getAuthor()) ||
                !StringUtils.hasText(request.getDescription()) ||
                !StringUtils.hasText(request.getCoverImageUrl()) ||
                request.getPublishedYear() == null) {
            throw new RuntimeException("All fields must be non-empty");
        }
        if (request.getTitle().length() > 128 || request.getTitle().length() < 1) {
            throw new RuntimeException("Title length must be 1-128 characters");
        }
        if (request.getAuthor().length() > 64 || request.getAuthor().length() < 1) {
            throw new RuntimeException("Author length must be 1-64 characters");
        }
        if (request.getDescription().length() > 4096 || request.getDescription().length() < 1) {
            throw new RuntimeException("Description length must be 1-512 characters");
        }
        if (request.getCoverImageUrl().length() > 256 || request.getCoverImageUrl().length() < 1) {
            throw new RuntimeException("Cover image URL length must be 1-256 characters");
        }
        if (request.getGenres() == null || request.getGenres().isEmpty()) {
            throw new RuntimeException("At least one genre must be provided");
        }
    }
}
