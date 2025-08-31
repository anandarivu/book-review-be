package com.bookreview.service;

import com.bookreview.model.Book;
import com.bookreview.model.User;
import com.bookreview.repository.BookRepository;
import com.bookreview.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FavoriteServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookRepository bookRepository;
    @InjectMocks
    private FavoriteService favoriteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddFavoriteSuccess() {
        String userId = "user1";
        UUID bookId = UUID.randomUUID();
        User user = User.builder().userId(userId).favorites(new HashSet<>()).build();
        Book book = Book.builder().id(bookId).build();
        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(userRepository.save(user)).thenReturn(user);
        Set<Book> result = favoriteService.addFavorite(userId, bookId);
        assertTrue(result.contains(book));
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testRemoveFavoriteSuccess() {
        String userId = "user1";
        UUID bookId = UUID.randomUUID();
        Book book = Book.builder().id(bookId).build();
        Set<Book> favorites = new HashSet<>();
        favorites.add(book);
        User user = User.builder().userId(userId).favorites(favorites).build();
        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(userRepository.save(user)).thenReturn(user);
        Set<Book> result = favoriteService.removeFavorite(userId, bookId);
        assertFalse(result.contains(book));
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testGetFavoritesSuccess() {
        String userId = "user1";
        Book book = Book.builder().id(UUID.randomUUID()).build();
        Set<Book> favorites = new HashSet<>();
        favorites.add(book);
        User user = User.builder().userId(userId).favorites(favorites).build();
        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));
        Set<Book> result = favoriteService.getFavorites(userId);
        assertTrue(result.contains(book));
    }
}
