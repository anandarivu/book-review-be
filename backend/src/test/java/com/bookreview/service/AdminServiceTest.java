package com.bookreview.service;

import com.bookreview.dto.AdminSignupRequest;
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

class AdminServiceTest {
    @Mock
    private UserMapper userMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordUtil passwordUtil;
    @InjectMocks
    private AdminService adminService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateAdminSuccess() {
        AdminSignupRequest request = new AdminSignupRequest();
        request.setUserId("admin1");
        request.setEmail("admin1@example.com");
        request.setPassword("password");
        when(userRepository.findByUserId("admin1")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("admin1@example.com")).thenReturn(Optional.empty());
        Role role = Role.builder().name("ADMIN").build();
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(role));
        when(passwordUtil.hashPassword("password")).thenReturn("hashedPassword");
        User user = User.builder().userId("admin1").email("admin1@example.com").password("hashedPassword").roles(Collections.singleton(role)).build();
        when(userRepository.save(any(User.class))).thenReturn(user);
        UserDto dto = new UserDto();
        when(userMapper.toDto(user)).thenReturn(dto);
        UserDto result = adminService.createAdmin(request);
        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateAdminDuplicateUserIdOrEmail() {
        AdminSignupRequest request = new AdminSignupRequest();
        request.setUserId("admin1");
        request.setEmail("admin1@example.com");
        request.setPassword("password");
        when(userRepository.findByUserId("admin1")).thenReturn(Optional.of(new User()));
        when(userRepository.findByEmail("admin1@example.com")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> adminService.createAdmin(request));
    }

        @Test
        void testCreateAdminRoleNotFound() {
            AdminSignupRequest request = new AdminSignupRequest();
            request.setUserId("admin2");
            request.setEmail("admin2@example.com");
            request.setPassword("password");
            when(userRepository.findByUserId("admin2")).thenReturn(Optional.empty());
            when(userRepository.findByEmail("admin2@example.com")).thenReturn(Optional.empty());
            when(roleRepository.findByName("ADMIN")).thenReturn(Optional.empty());
            Role newRole = Role.builder().name("ADMIN").build();
            when(roleRepository.save(any(Role.class))).thenReturn(newRole);
            when(passwordUtil.hashPassword("password")).thenReturn("hashedPassword");
            User user = User.builder().userId("admin2").email("admin2@example.com").password("hashedPassword").roles(Collections.singleton(newRole)).build();
            when(userRepository.save(any(User.class))).thenReturn(user);
            UserDto dto = new UserDto();
            when(userMapper.toDto(user)).thenReturn(dto);
        UserDto result = adminService.createAdmin(request);
            assertNotNull(result);
            verify(roleRepository, times(1)).save(any(Role.class));
        }

        @Test
        void testCreateAdminPasswordHashingFailure() {
            AdminSignupRequest request = new AdminSignupRequest();
            request.setUserId("admin3");
            request.setEmail("admin3@example.com");
            request.setPassword("password");
            when(userRepository.findByUserId("admin3")).thenReturn(Optional.empty());
            when(userRepository.findByEmail("admin3@example.com")).thenReturn(Optional.empty());
            Role role = Role.builder().name("ADMIN").build();
            when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(role));
            when(passwordUtil.hashPassword("password")).thenThrow(new RuntimeException("Hashing failed"));
            assertThrows(RuntimeException.class, () -> adminService.createAdmin(request));
        }

        @Test
        void testCreateAdminWithoutSecretSuccess() {
            AdminSignupRequest request = new AdminSignupRequest();
            request.setUserId("admin4");
            request.setEmail("admin4@example.com");
            request.setPassword("password");
            when(userRepository.findByUserId("admin4")).thenReturn(Optional.empty());
            when(userRepository.findByEmail("admin4@example.com")).thenReturn(Optional.empty());
            Role role = Role.builder().name("ADMIN").build();
            when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(role));
            when(passwordUtil.hashPassword("password")).thenReturn("hashedPassword");
            User user = User.builder().userId("admin4").email("admin4@example.com").password("hashedPassword").roles(Collections.singleton(role)).build();
            when(userRepository.save(any(User.class))).thenReturn(user);
            UserDto dto = new UserDto();
            when(userMapper.toDto(user)).thenReturn(dto);
            UserDto result = adminService.createAdminWithoutSecret(request);
            assertNotNull(result);
            verify(userRepository, times(1)).save(any(User.class));
        }

        @Test
        void testCreateAdminWithoutSecretDuplicateUserIdOrEmail() {
            AdminSignupRequest request = new AdminSignupRequest();
            request.setUserId("admin5");
            request.setEmail("admin5@example.com");
            request.setPassword("password");
            when(userRepository.findByUserId("admin5")).thenReturn(Optional.of(new User()));
            when(userRepository.findByEmail("admin5@example.com")).thenReturn(Optional.empty());
            assertThrows(RuntimeException.class, () -> adminService.createAdminWithoutSecret(request));
        }
}
