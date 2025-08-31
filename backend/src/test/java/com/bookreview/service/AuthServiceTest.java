package com.bookreview.service;

import com.bookreview.dto.AuthResponse;
import com.bookreview.dto.LoginRequest;
import com.bookreview.model.User;
import com.bookreview.model.Role;
import com.bookreview.repository.UserRepository;
import com.bookreview.util.JwtUtil;
import com.bookreview.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordUtil passwordUtil;
    @Mock
    private JwtUtil jwtUtil;
    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoginSuccess() {
        LoginRequest request = new LoginRequest();
        request.setUserId("user1");
        request.setPassword("password");
        User user = User.builder().userId("user1").password("hashedPassword").id(UUID.randomUUID()).roles(Set.of(Role.builder().name("USER").build())).build();
        when(userRepository.findByUserId("user1")).thenReturn(Optional.of(user));
        when(passwordUtil.matches("password", "hashedPassword")).thenReturn(true);
        when(jwtUtil.generateToken(eq("user1"), any(UUID.class), anySet())).thenReturn("token");
        AuthResponse response = authService.login(request);
        assertNotNull(response);
        assertEquals("token", response.getToken());
    }

    @Test
    void testLoginInvalidCredentials() {
        LoginRequest request = new LoginRequest();
        request.setUserId("user1");
        request.setPassword("wrong");
        User user = User.builder().userId("user1").password("hashedPassword").id(UUID.randomUUID()).roles(Set.of(Role.builder().name("USER").build())).build();
        when(userRepository.findByUserId("user1")).thenReturn(Optional.of(user));
        when(passwordUtil.matches("wrong", "hashedPassword")).thenReturn(false);
        assertThrows(RuntimeException.class, () -> authService.login(request));
    }

    @Test
    void testLoginUserNotFound() {
        LoginRequest request = new LoginRequest();
        request.setUserId("user1");
        request.setPassword("password");
        when(userRepository.findByUserId("user1")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> authService.login(request));
    }
}
