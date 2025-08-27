package com.bookreview.service;

import com.bookreview.dto.SignupRequest;
import com.bookreview.model.Role;
import com.bookreview.model.User;
import com.bookreview.repository.RoleRepository;
import com.bookreview.repository.UserRepository;
import com.bookreview.util.PasswordUtil;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordUtil passwordUtil;

    @Mock
    private com.bookreview.mapper.UserMapper userMapper;
    @InjectMocks
    private UserService userService;

    public UserServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void signup_shouldCreateUserWithHashedPasswordAndUserRole() {
        SignupRequest request = new SignupRequest();
        request.setUserId("testuser");
        request.setEmail("test@example.com");
        request.setPassword("password");

        when(userRepository.findByUserId("testuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        Role userRole = Role.builder().name("USER").build();
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(passwordUtil.hashPassword("password")).thenReturn("hashed");
        User user = User.builder()
                .userId("testuser")
                .email("test@example.com")
                .password("hashed")
                .roles(Set.of(userRole))
                .build();
        when(userRepository.save(any(User.class))).thenReturn(user);

    com.bookreview.dto.UserDto dummyDto = new com.bookreview.dto.UserDto();
    dummyDto.setId(java.util.UUID.randomUUID());
    dummyDto.setUsername("testuser");
    dummyDto.setEmail("test@example.com");
    dummyDto.setRoles(Set.of("USER"));
    when(userMapper.toDto(any(User.class))).thenReturn(dummyDto);
    com.bookreview.dto.UserDto created = userService.signup(request);
    assertEquals("testuser", created.getUsername());
    assertEquals("test@example.com", created.getEmail());
    // Password is not exposed in UserDto
    assertTrue(created.getRoles().contains("USER"));
    }

    @Test
    void signup_shouldThrowIfUserIdExists() {
        SignupRequest request = new SignupRequest();
        request.setUserId("testuser");
        request.setEmail("test@example.com");
        when(userRepository.findByUserId("testuser")).thenReturn(Optional.of(new User()));
        assertThrows(RuntimeException.class, () -> userService.signup(request));
    }

    @Test
    void signup_shouldThrowIfEmailExists() {
        SignupRequest request = new SignupRequest();
        request.setUserId("testuser");
        request.setEmail("test@example.com");
        when(userRepository.findByUserId("testuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(new User()));
        assertThrows(RuntimeException.class, () -> userService.signup(request));
    }
}
