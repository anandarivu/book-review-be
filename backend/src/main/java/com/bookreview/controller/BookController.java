package com.bookreview.controller;

import com.bookreview.dto.BookDto;
import com.bookreview.dto.BookRequest;
import com.bookreview.service.BookService;
import com.bookreview.service.ReviewService;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
public class BookController {
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private BookService bookService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookDto> createBook(@RequestBody BookRequest request) {
        BookDto book = bookService.createBook(request);
        return ResponseEntity.ok(book);
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookDto> updateBook(@PathVariable java.util.UUID id, @RequestBody BookRequest request) {
        BookDto book = bookService.updateBook(id, request);
        return ResponseEntity.ok(book);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBook(@PathVariable java.util.UUID id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDto> getBook(@PathVariable java.util.UUID id) {
        return bookService.getBook(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/average-rating")
    public ResponseEntity<Double> getAverageRating(@PathVariable UUID id) {
        Double avg = reviewService.getAverageRating(id);
        return ResponseEntity.ok(avg);
    }

    @GetMapping
    public ResponseEntity<Page<BookDto>> searchBooks(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "title") String sortBy) {
        Page<BookDto> books = bookService.searchBooks(search, page, size, sortBy);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/top-rated")
    public ResponseEntity<Page<BookDto>> getTopRatedBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<BookDto> books = bookService.getTopRatedBooks(page, size);
        return ResponseEntity.ok(books);
    }
}
