package com.bookreview.service;

import com.bookreview.model.User;
import com.bookreview.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfileServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private ProfileService profileService;

    public ProfileServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getProfile_shouldReturnUserProfile() {
    java.util.Set<com.bookreview.model.Role> roles = new java.util.HashSet<>();
    roles.add(com.bookreview.model.Role.builder().name("USER").build());
    User user = User.builder().userId("user1").email("test@example.com").roles(roles).build();
        when(userRepository.findByUserId("user1")).thenReturn(Optional.of(user));
        var profile = profileService.getProfile("user1");
        assertEquals("user1", profile.getUserId());
        assertEquals("test@example.com", profile.getEmail());
    }

    @Test
    void getProfile_shouldThrowIfUserNotFound() {
        when(userRepository.findByUserId("user1")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> profileService.getProfile("user1"));
    }
}
