package com.bookreview.service;

import com.bookreview.model.Book;
import com.bookreview.model.User;
import com.bookreview.repository.BookRepository;
import com.bookreview.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FavoriteServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookRepository bookRepository;
    @InjectMocks
    private FavoriteService favoriteService;

    public FavoriteServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addFavorite_shouldAddBookToFavorites() {
        java.util.UUID uuid = java.util.UUID.randomUUID();
        User user = User.builder().userId("user1").favorites(new HashSet<>()).build();
        Book book = Book.builder().id(uuid).build();
        when(userRepository.findByUserId("user1")).thenReturn(Optional.of(user));
        when(bookRepository.findById(uuid)).thenReturn(Optional.of(book));
        when(userRepository.save(any(User.class))).thenReturn(user);

        Set<Book> favorites = favoriteService.addFavorite("user1", uuid);
        assertTrue(favorites.contains(book));
    }

    @Test
    void addFavorite_shouldThrowIfUserNotFound() {
        java.util.UUID uuid = java.util.UUID.randomUUID();
        when(userRepository.findByUserId("user1")).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> favoriteService.addFavorite("user1", uuid));
    }

    @Test
    void addFavorite_shouldThrowIfBookNotFound() {
        java.util.UUID uuid = java.util.UUID.randomUUID();
        User user = User.builder().userId("user1").favorites(new HashSet<>()).build();
        when(userRepository.findByUserId("user1")).thenReturn(Optional.of(user));
        when(bookRepository.findById(uuid)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> favoriteService.addFavorite("user1", uuid));
    }

    @Test
    void removeFavorite_shouldRemoveBookFromFavorites() {
        java.util.UUID uuid = java.util.UUID.randomUUID();
        Book book = Book.builder().id(uuid).build();
        Set<Book> favs = new HashSet<>();
        favs.add(book);
        User user = User.builder().userId("user1").favorites(favs).build();
        when(userRepository.findByUserId("user1")).thenReturn(Optional.of(user));
        when(bookRepository.findById(uuid)).thenReturn(Optional.of(book));
        when(userRepository.save(any(User.class))).thenReturn(user);

        Set<Book> favorites = favoriteService.removeFavorite("user1", uuid);
        assertFalse(favorites.contains(book));
    }

    @Test
    void removeFavorite_shouldThrowIfUserNotFound() {
        java.util.UUID uuid = java.util.UUID.randomUUID();
        when(userRepository.findByUserId("user1")).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> favoriteService.removeFavorite("user1", uuid));
    }

    @Test
    void removeFavorite_shouldThrowIfBookNotFound() {
        java.util.UUID uuid = java.util.UUID.randomUUID();
        User user = User.builder().userId("user1").favorites(new HashSet<>()).build();
        when(userRepository.findByUserId("user1")).thenReturn(Optional.of(user));
        when(bookRepository.findById(uuid)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> favoriteService.removeFavorite("user1", uuid));
    }

    @Test
    void getFavorites_shouldReturnFavorites() {
        java.util.UUID uuid = java.util.UUID.randomUUID();
        Book book = Book.builder().id(uuid).build();
        Set<Book> favs = new HashSet<>();
        favs.add(book);
        User user = User.builder().userId("user1").favorites(favs).build();
        when(userRepository.findByUserId("user1")).thenReturn(Optional.of(user));
        Set<Book> favorites = favoriteService.getFavorites("user1");
        assertTrue(favorites.contains(book));
    }

    @Test
    void getFavorites_shouldThrowIfUserNotFound() {
        when(userRepository.findByUserId("user1")).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> favoriteService.getFavorites("user1"));
    }
}
