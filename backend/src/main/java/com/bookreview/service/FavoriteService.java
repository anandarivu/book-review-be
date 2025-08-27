package com.bookreview.service;

import com.bookreview.model.Book;
import com.bookreview.model.User;
import com.bookreview.repository.BookRepository;
import com.bookreview.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
public class FavoriteService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookRepository bookRepository;

    @Transactional
    public Set<Book> addFavorite(String userId, UUID bookId) {
        User user = userRepository.findByUserId(userId).orElseThrow();
        Book book = bookRepository.findById(bookId).orElseThrow();
        user.getFavorites().add(book);
        userRepository.save(user);
        return user.getFavorites();
    }

    @Transactional
    public Set<Book> removeFavorite(String userId, UUID bookId) {
        User user = userRepository.findByUserId(userId).orElseThrow();
        Book book = bookRepository.findById(bookId).orElseThrow();
        user.getFavorites().remove(book);
        userRepository.save(user);
        return user.getFavorites();
    }

    public Set<Book> getFavorites(String userId) {
        User user = userRepository.findByUserId(userId).orElseThrow();
        return user.getFavorites();
    }
}
