package com.bookreview.service;

import com.bookreview.dto.BookRequest;
import com.bookreview.model.Book;
import com.bookreview.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookServiceTest {
    private final com.bookreview.dto.BookDto dummyDto;

    {
        dummyDto = new com.bookreview.dto.BookDto();
        dummyDto.setId(java.util.UUID.randomUUID());
        dummyDto.setTitle("Title");
        dummyDto.setAuthor("Author");
        dummyDto.setDescription("Desc");
        dummyDto.setCoverImageUrl("http://img.com");
        dummyDto.setGenres(Set.of("Fiction"));
        dummyDto.setPublishedYear(2020);
    }
    @Mock
    private BookRepository bookRepository;
    @Mock
    private com.bookreview.mapper.BookMapper bookMapper;
    @Mock
    private ReviewService reviewService;
    @InjectMocks
    private BookService bookService;

    public BookServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createBook_shouldValidateAndSaveBook() {
    BookRequest request = new BookRequest();
    request.setTitle("Title");
    request.setAuthor("Author");
    request.setDescription("Desc");
    request.setCoverImageUrl("http://img.com");
    request.setGenres(Set.of("Fiction"));
    request.setPublishedYear(2020);

    Book book = Book.builder()
        .id(java.util.UUID.randomUUID())
        .title("Title")
        .author("Author")
        .description("Desc")
        .coverImageUrl("http://img.com")
        .genres(Set.of("Fiction"))
        .publishedYear(2020)
        .build();
    when(bookRepository.save(any(Book.class))).thenReturn(book);

    com.bookreview.dto.BookDto dummyDto = new com.bookreview.dto.BookDto();
    dummyDto.setId(book.getId());
    dummyDto.setTitle(book.getTitle());
    dummyDto.setAuthor(book.getAuthor());
    dummyDto.setDescription(book.getDescription());
    dummyDto.setCoverImageUrl(book.getCoverImageUrl());
    dummyDto.setGenres(book.getGenres());
    dummyDto.setPublishedYear(book.getPublishedYear());
    when(bookMapper.toDto(any(Book.class))).thenReturn(dummyDto);
    com.bookreview.dto.BookDto created = bookService.createBook(request);
    assertNotNull(created.getId());
    assertEquals("Title", created.getTitle());
    assertEquals("Author", created.getAuthor());
    assertEquals("Desc", created.getDescription());
    assertEquals("http://img.com", created.getCoverImageUrl());
    assertEquals(Set.of("Fiction"), created.getGenres());
    assertEquals(2020, created.getPublishedYear());
    }

    @Test
    void createBook_shouldThrowOnInvalidRequest() {
    BookRequest request = new BookRequest();
    request.setTitle("");
    request.setAuthor("");
    request.setDescription("");
    request.setCoverImageUrl("");
    request.setGenres(Set.of());
    request.setPublishedYear(null);
    assertThrows(RuntimeException.class, () -> bookService.createBook(request));
    }

    @Test
    void updateBook_shouldUpdateExistingBook() {
    BookRequest request = new BookRequest();
    request.setTitle("Title");
    request.setAuthor("Author");
    request.setDescription("Desc");
    request.setCoverImageUrl("http://img.com");
    request.setGenres(Set.of("Fiction"));
    request.setPublishedYear(2020);
    java.util.UUID uuid = java.util.UUID.randomUUID();
    Book book = Book.builder().id(uuid).build();
    when(bookRepository.findById(any())).thenReturn(Optional.of(book));
    when(bookRepository.save(any(Book.class))).thenReturn(book);
    when(bookMapper.toDto(any(Book.class))).thenReturn(dummyDto);
    com.bookreview.dto.BookDto updated = bookService.updateBook(uuid, request);
    assertNotNull(updated.getId());
    }

    @Test
    void updateBook_shouldThrowIfNotFound() {
        BookRequest request = new BookRequest();
        java.util.UUID uuid = java.util.UUID.randomUUID();
        when(bookRepository.findById(uuid)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> bookService.updateBook(uuid, request));
    }

    @Test
    void deleteBook_shouldDeleteBook() {
    java.util.UUID uuid = java.util.UUID.randomUUID();
    doNothing().when(bookRepository).deleteById(uuid);
    bookService.deleteBook(uuid);
    verify(bookRepository, times(1)).deleteById(uuid);
    }

    @Test
    void getBook_shouldReturnBookIfExists() {
    java.util.UUID uuid = java.util.UUID.randomUUID();
    Book book = Book.builder().id(uuid).build();
    when(bookRepository.findById(uuid)).thenReturn(Optional.of(book));
    when(bookMapper.toDto(any(Book.class))).thenReturn(dummyDto);
    when(reviewService.getAverageRating(any())).thenReturn(4.5);
    Optional<com.bookreview.dto.BookDto> found = bookService.getBook(uuid);
    assertTrue(found.isPresent());
    }

    @Test
    void getBook_shouldReturnEmptyIfNotFound() {
    java.util.UUID uuid = java.util.UUID.randomUUID();
    when(bookRepository.findById(uuid)).thenReturn(Optional.empty());
    Optional<com.bookreview.dto.BookDto> found = bookService.getBook(uuid);
    assertFalse(found.isPresent());
    }

    @Test
    void searchBooks_shouldReturnBooks() {
        when(bookRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());
        org.springframework.data.domain.Page<com.bookreview.dto.BookDto> page = bookService.searchBooks(null, 0, 20, "title");
        assertNotNull(page);
    }

    @Test
    void getTopRatedBooks_shouldReturnSortedBooks() {
        when(bookRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());
        org.springframework.data.domain.Page<com.bookreview.dto.BookDto> page = bookService.getTopRatedBooks(0, 20);
        assertNotNull(page);
    }
}
