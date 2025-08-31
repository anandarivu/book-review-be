package com.bookreview.service;

import com.bookreview.dto.SignupRequest;
import com.bookreview.dto.UserDto;
import com.bookreview.model.Role;
import com.bookreview.model.User;
import com.bookreview.repository.RoleRepository;
import com.bookreview.repository.UserRepository;
import com.bookreview.util.PasswordUtil;
import com.bookreview.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Optional;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {
    @Mock
    private UserMapper userMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordUtil passwordUtil;
    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSignupSuccess() {
        SignupRequest request = new SignupRequest();
        request.setUserId("user1");
        request.setEmail("user1@example.com");
        request.setPassword("password");
        when(userRepository.findByUserId("user1")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("user1@example.com")).thenReturn(Optional.empty());
        Role role = Role.builder().name("USER").build();
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(role));
        when(passwordUtil.hashPassword("password")).thenReturn("hashedPassword");
        User user = User.builder().userId("user1").email("user1@example.com").password("hashedPassword").roles(Collections.singleton(role)).build();
        when(userRepository.save(any(User.class))).thenReturn(user);
        UserDto dto = new UserDto();
        when(userMapper.toDto(user)).thenReturn(dto);
        UserDto result = userService.signup(request);
        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testSignupDuplicateUserIdOrEmail() {
        SignupRequest request = new SignupRequest();
        request.setUserId("user1");
        request.setEmail("user1@example.com");
        request.setPassword("password");
        when(userRepository.findByUserId("user1")).thenReturn(Optional.of(new User()));
        when(userRepository.findByEmail("user1@example.com")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userService.signup(request));
    }
}
