package com.bookreview.controller;

import com.bookreview.model.Book;
import com.bookreview.dto.UserProfileResponse;
import com.bookreview.service.FavoriteService;
import com.bookreview.service.ProfileService;
import com.bookreview.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    @Autowired
    private ProfileService profileService;
    @Autowired
    private FavoriteService favoriteService;
    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileResponse> getProfile(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String userId = jwtUtil.getUserId(token);
        UserProfileResponse profile = profileService.getProfile(userId);
        return ResponseEntity.ok(profile);
    }

    @Autowired
    private com.bookreview.mapper.BookMapper bookMapper;

    @GetMapping("/favorites")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Set<com.bookreview.dto.BookDto>> getFavorites(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String userId = jwtUtil.getUserId(token);
        Set<Book> favorites = favoriteService.getFavorites(userId);
        Set<com.bookreview.dto.BookDto> favoriteDtos = favorites.stream()
            .map(bookMapper::toDto)
            .collect(java.util.stream.Collectors.toSet());
        return ResponseEntity.ok(favoriteDtos);
    }

    @PostMapping("/favorites/{bookId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Set<com.bookreview.dto.BookDto>> addFavorite(@PathVariable UUID bookId, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String userId = jwtUtil.getUserId(token);
        Set<Book> favorites = favoriteService.addFavorite(userId, bookId);
        Set<com.bookreview.dto.BookDto> favoriteDtos = favorites.stream()
            .map(bookMapper::toDto)
            .collect(java.util.stream.Collectors.toSet());
        return ResponseEntity.ok(favoriteDtos);
    }

    @DeleteMapping("/favorites/{bookId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Set<com.bookreview.dto.BookDto>> removeFavorite(@PathVariable UUID bookId, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String userId = jwtUtil.getUserId(token);
        Set<Book> favorites = favoriteService.removeFavorite(userId, bookId);
        Set<com.bookreview.dto.BookDto> favoriteDtos = favorites.stream()
            .map(bookMapper::toDto)
            .collect(java.util.stream.Collectors.toSet());
        return ResponseEntity.ok(favoriteDtos);
    }
}
