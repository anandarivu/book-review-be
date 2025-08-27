package com.bookreview.service;

import com.bookreview.dto.AdminSignupRequest;
import com.bookreview.model.Role;
import com.bookreview.model.User;
import com.bookreview.repository.RoleRepository;
import com.bookreview.repository.UserRepository;
import com.bookreview.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.util.Collections;

@Service
public class AdminService {
    @Autowired
    private com.bookreview.mapper.UserMapper userMapper;
    @Transactional
    public com.bookreview.dto.UserDto createAdminWithoutSecret(AdminSignupRequest request) {
        if (userRepository.findByUserId(request.getUserId()).isPresent() ||
            userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User ID or email already exists");
        }
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> roleRepository.save(Role.builder().name("ADMIN").build()));
        User user = User.builder()
                .userId(request.getUserId())
                .email(request.getEmail())
                .password(passwordUtil.hashPassword(request.getPassword()))
                .roles(Collections.singleton(adminRole))
                .build();
    return userMapper.toDto(userRepository.save(user));
    }
    @Value("${admin.secret}")
    private String adminSecret;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordUtil passwordUtil;

    @Transactional
    public com.bookreview.dto.UserDto createAdmin(AdminSignupRequest request) {
        if (userRepository.findByUserId(request.getUserId()).isPresent() ||
            userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User ID or email already exists");
        }
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> roleRepository.save(Role.builder().name("ADMIN").build()));
        User user = User.builder()
                .userId(request.getUserId())
                .email(request.getEmail())
                .password(passwordUtil.hashPassword(request.getPassword()))
                .roles(Collections.singleton(adminRole))
                .build();
    return userMapper.toDto(userRepository.save(user));
    }
}
