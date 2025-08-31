package com.bookreview.service;

import com.bookreview.dto.UserProfileResponse;
import com.bookreview.model.User;
import com.bookreview.model.Role;
import com.bookreview.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfileServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private ProfileService profileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetProfileSuccess() {
    User user = User.builder()
        .userId("user1")
        .email("user1@example.com")
        .roles(Set.of(Role.builder().name("USER").build()))
        .build();
    user.setCreatedAt(Instant.now());
    user.setLastModifiedAt(Instant.now());
    user.setCreatedBy("admin");
    user.setLastModifiedBy("admin");
        when(userRepository.findByUserId("user1")).thenReturn(Optional.of(user));
        UserProfileResponse response = profileService.getProfile("user1");
        assertNotNull(response);
        assertEquals("user1", response.getUserId());
        assertEquals("user1@example.com", response.getEmail());
        assertTrue(response.getRoles().contains("USER"));
    }

    @Test
    void testGetProfileUserNotFound() {
        when(userRepository.findByUserId("user1")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> profileService.getProfile("user1"));
    }
}
