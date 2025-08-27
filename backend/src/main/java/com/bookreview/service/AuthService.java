package com.bookreview.service;

import com.bookreview.dto.AuthResponse;
import com.bookreview.dto.LoginRequest;
import com.bookreview.model.User;
import com.bookreview.repository.UserRepository;
import com.bookreview.util.JwtUtil;
import com.bookreview.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordUtil passwordUtil;
    @Autowired
    private JwtUtil jwtUtil;

    public AuthResponse login(LoginRequest request) {
    User user = userRepository.findByUserId(request.getUserId())
        .orElseThrow(() -> new RuntimeException("Invalid credentials"));
    if (!passwordUtil.matches(request.getPassword(), user.getPassword())) {
        throw new RuntimeException("Invalid credentials");
    }
    String token = jwtUtil.generateToken(user.getUserId(), user.getId(),
        user.getRoles().stream().map(r -> r.getName()).collect(java.util.stream.Collectors.toSet()));
    return new AuthResponse(token);
    }
}
