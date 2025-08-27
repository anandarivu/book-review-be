package com.bookreview.service;

import com.bookreview.dto.SignupRequest;
import com.bookreview.model.Role;
import com.bookreview.model.User;
import com.bookreview.repository.RoleRepository;
import com.bookreview.repository.UserRepository;
import com.bookreview.util.PasswordUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserService {
    @Autowired
    private com.bookreview.mapper.UserMapper userMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordUtil passwordUtil;

    @Transactional
    public com.bookreview.dto.UserDto signup(SignupRequest request) {
        if (userRepository.findByUserId(request.getUserId()).isPresent() ||
            userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User ID or email already exists");
        }
        Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> roleRepository.save(Role.builder().name("USER").build()));
        User user = User.builder()
                .userId(request.getUserId())
                .email(request.getEmail())
                .password(passwordUtil.hashPassword(request.getPassword()))
                .roles(Collections.singleton(userRole))
                .build();
        return userMapper.toDto(userRepository.save(user));
    }
}
