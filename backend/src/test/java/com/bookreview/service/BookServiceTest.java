package com.bookreview.service;

import com.bookreview.dto.BookRequest;
import com.bookreview.dto.BookDto;
import com.bookreview.model.Book;
import com.bookreview.repository.BookRepository;
import com.bookreview.mapper.BookMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookServiceTest {
    @Test
    void testGetTopRatedBooks() {
        Book book1 = Book.builder().id(UUID.randomUUID()).title("Book1").genres(java.util.Set.of("Fiction")).build();
        Book book2 = Book.builder().id(UUID.randomUUID()).title("Book2").genres(java.util.Set.of("Drama")).build();
        when(bookRepository.findAll()).thenReturn(java.util.List.of(book1, book2));
        when(reviewService.getAverageRating(book1.getId())).thenReturn(4.5);
        when(reviewService.getAverageRating(book2.getId())).thenReturn(null);
        BookDto dto1 = new BookDto();
        when(bookMapper.toDto(book1)).thenReturn(dto1);
        when(reviewService.getReviewsForBook(eq(book1.getId()), anyInt(), anyInt(), anyString())).thenReturn(new org.springframework.data.domain.PageImpl<>(java.util.Collections.emptyList()));
        var result = bookService.getTopRatedBooks(0, 10);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testGetRecommendations() throws Exception {
        String userId = "user1";
        Book book1 = Book.builder().id(UUID.randomUUID()).title("Book1").genres(java.util.Set.of("Fiction")).build();
        when(bookRepository.findAll()).thenReturn(java.util.List.of(book1));
        com.bookreview.model.User user = com.bookreview.model.User.builder().userId(userId).favorites(java.util.Set.of(book1)).build();
        com.bookreview.service.FavoriteService favoriteService = mock(com.bookreview.service.FavoriteService.class);
        when(favoriteService.getFavorites(userId)).thenReturn(java.util.Set.of(book1));
        BookService service = new BookService();
        java.lang.reflect.Field favField = BookService.class.getDeclaredField("favoriteService");
        favField.setAccessible(true);
        favField.set(service, favoriteService);
        java.lang.reflect.Field repoField = BookService.class.getDeclaredField("bookRepository");
        repoField.setAccessible(true);
        repoField.set(service, bookRepository);
        java.lang.reflect.Field mapperField = BookService.class.getDeclaredField("bookMapper");
        mapperField.setAccessible(true);
        mapperField.set(service, bookMapper);
        var result = service.getRecommendations(userId, 0, 10);
        assertNotNull(result);
    }

    @Test
    void testValidateBookRequestInvalidAuthor() throws Exception {
        BookRequest request = new BookRequest();
        request.setTitle("Title");
        request.setAuthor("");
        request.setDescription("Description");
        request.setCoverImageUrl("http://image.url");
        request.setGenres(java.util.Set.of("Fiction"));
        request.setPublishedYear(2020);
        java.lang.reflect.Method m = BookService.class.getDeclaredMethod("validateBookRequest", BookRequest.class);
        m.setAccessible(true);
        Exception ex = assertThrows(java.lang.reflect.InvocationTargetException.class, () -> m.invoke(bookService, request));
        assertTrue(ex.getCause() instanceof RuntimeException);
    }

    @Test
    void testValidateBookRequestInvalidGenres() throws Exception {
        BookRequest request = new BookRequest();
        request.setTitle("Title");
        request.setAuthor("Author");
        request.setDescription("Description");
        request.setCoverImageUrl("http://image.url");
        request.setGenres(java.util.Set.of());
        request.setPublishedYear(2020);
        java.lang.reflect.Method m = BookService.class.getDeclaredMethod("validateBookRequest", BookRequest.class);
        m.setAccessible(true);
        Exception ex = assertThrows(java.lang.reflect.InvocationTargetException.class, () -> m.invoke(bookService, request));
        assertTrue(ex.getCause() instanceof RuntimeException);
    }

    @Test
    void testDeleteBookAlsoDeletesReviews() {
        UUID id = UUID.randomUUID();
        doNothing().when(bookRepository).deleteById(id);
        doNothing().when(reviewService).deleteReviewsForBook(id);
        bookService.deleteBook(id);
        verify(reviewService, times(1)).deleteReviewsForBook(id);
        verify(bookRepository, times(1)).deleteById(id);
    }

    @Test
    void testGetBookNotFound() {
        UUID id = UUID.randomUUID();
        when(bookRepository.findById(id)).thenReturn(Optional.empty());
        Optional<BookDto> result = bookService.getBook(id);
        assertTrue(result.isEmpty());
    }
    @Test
    void testUpdateBookSuccess() {
        UUID id = UUID.randomUUID();
        BookRequest request = new BookRequest();
        request.setTitle("Updated Book");
        request.setAuthor("Author");
        request.setDescription("Description");
        request.setCoverImageUrl("http://image.url");
        request.setGenres(java.util.Set.of("Fiction"));
        request.setPublishedYear(2021);
        Book book = Book.builder().title("Old Book").author("Author").description("Old Description").coverImageUrl("http://old.url").genres(java.util.Set.of("Fiction")).publishedYear(2020).build();
        when(bookRepository.findById(id)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        BookDto dto = new BookDto();
        when(bookMapper.toDto(book)).thenReturn(dto);
        BookDto result = bookService.updateBook(id, request);
        assertNotNull(result);
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void testUpdateBookNotFound() {
        UUID id = UUID.randomUUID();
        BookRequest request = new BookRequest();
        request.setTitle("Updated Book");
        request.setAuthor("Author");
        request.setDescription("Description");
        request.setCoverImageUrl("http://image.url");
        request.setGenres(java.util.Set.of("Fiction"));
        request.setPublishedYear(2021);
        when(bookRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> bookService.updateBook(id, request));
    }

    @Test
    void testSearchBooksWithSearch() {
        String search = "Test";
        int page = 0, size = 10;
        String sortBy = "title";
        Book book = Book.builder().title("Test Book").author("Author").description("Description").coverImageUrl("http://image.url").genres(java.util.Set.of("Fiction")).publishedYear(2020).build();
        org.springframework.data.domain.Page<Book> bookPage = new org.springframework.data.domain.PageImpl<>(java.util.List.of(book));
        when(bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(eq(search), eq(search), any())).thenReturn(bookPage);
        BookDto dto = new BookDto();
        when(bookMapper.toDto(book)).thenReturn(dto);
        when(reviewService.getAverageRating(any())).thenReturn(4.5);
        when(reviewService.getReviewsForBook(any(), anyInt(), anyInt(), anyString())).thenReturn(new org.springframework.data.domain.PageImpl<>(java.util.Collections.emptyList()));
        var result = bookService.searchBooks(search, page, size, sortBy);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testSearchBooksWithoutSearch() {
        int page = 0, size = 10;
        String sortBy = "title";
        Book book = Book.builder().title("Test Book").author("Author").description("Description").coverImageUrl("http://image.url").genres(java.util.Set.of("Fiction")).publishedYear(2020).build();
        org.springframework.data.domain.Page<Book> bookPage = new org.springframework.data.domain.PageImpl<>(java.util.List.of(book));
        when(bookRepository.findAll(any(org.springframework.data.domain.Pageable.class))).thenReturn(bookPage);
        BookDto dto = new BookDto();
        when(bookMapper.toDto(book)).thenReturn(dto);
        when(reviewService.getAverageRating(any())).thenReturn(4.5);
        when(reviewService.getReviewsForBook(any(), anyInt(), anyInt(), anyString())).thenReturn(new org.springframework.data.domain.PageImpl<>(java.util.Collections.emptyList()));
        var result = bookService.searchBooks(null, page, size, sortBy);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testValidateBookRequestInvalidTitle() throws Exception {
        BookRequest request = new BookRequest();
        request.setTitle("");
        request.setAuthor("Author");
        request.setDescription("Description");
        request.setCoverImageUrl("http://image.url");
        request.setGenres(java.util.Set.of("Fiction"));
        request.setPublishedYear(2020);
        java.lang.reflect.Method m = BookService.class.getDeclaredMethod("validateBookRequest", BookRequest.class);
        m.setAccessible(true);
        Exception ex = assertThrows(java.lang.reflect.InvocationTargetException.class, () -> m.invoke(bookService, request));
        assertTrue(ex.getCause() instanceof RuntimeException);
    }
    @Mock
    private ReviewService reviewService;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookMapper bookMapper;
    @InjectMocks
    private BookService bookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateBook() {
        BookRequest request = new BookRequest();
        request.setTitle("Test Book");
        request.setAuthor("Author");
        request.setDescription("Description");
        request.setCoverImageUrl("http://image.url");
    request.setGenres(java.util.Set.of("Fiction"));
        request.setPublishedYear(2020);
    Book book = Book.builder().title("Test Book").author("Author").description("Description").coverImageUrl("http://image.url").genres(java.util.Set.of("Fiction")).publishedYear(2020).build();
        BookDto dto = new BookDto();
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(dto);
        BookDto result = bookService.createBook(request);
        assertNotNull(result);
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void testGetBook() {
        UUID id = UUID.randomUUID();
        Book book = Book.builder().title("Test Book").build();
        BookDto dto = new BookDto();
        when(bookRepository.findById(id)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(dto);
    org.springframework.data.domain.Page<com.bookreview.dto.ReviewDto> emptyReviewPage = new org.springframework.data.domain.PageImpl<>(java.util.Collections.emptyList());
    when(reviewService.getReviewsForBook(any(), anyInt(), anyInt(), anyString())).thenReturn(emptyReviewPage);
    Optional<BookDto> result = bookService.getBook(id);
    assertTrue(result.isPresent());
    verify(bookRepository, times(1)).findById(id);
    }

    @Test
    void testDeleteBook() {
        UUID id = UUID.randomUUID();
        doNothing().when(bookRepository).deleteById(id);
        bookService.deleteBook(id);
        verify(bookRepository, times(1)).deleteById(id);
    }
}
