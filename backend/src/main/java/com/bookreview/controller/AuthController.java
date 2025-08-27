package com.bookreview.controller;

import com.bookreview.dto.SignupRequest;
import com.bookreview.model.User;
import com.bookreview.service.UserService;
import com.bookreview.service.AuthService;
import com.bookreview.service.AdminService;
import com.bookreview.service.ProfileService;
import com.bookreview.dto.LoginRequest;
import com.bookreview.dto.AuthResponse;
import com.bookreview.dto.AdminSignupRequest;
import com.bookreview.dto.UserProfileResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        // For stateless JWT, instruct client to delete token
        return ResponseEntity.ok("Logged out successfully. Please delete your token on client side.");
    }

    @PostMapping("/admin/login")
    public ResponseEntity<AuthResponse> adminLogin(@RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

        @Autowired
        private AdminService adminService;

        @Autowired
        private ProfileService profileService;

    @PostMapping("/signup")
    public ResponseEntity<com.bookreview.dto.UserDto> signup(@RequestBody SignupRequest request) {
        com.bookreview.dto.UserDto user = userService.signup(request);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/admin/signup")
    public ResponseEntity<com.bookreview.dto.UserDto> createAdmin(@RequestBody AdminSignupRequest request) {
        com.bookreview.dto.UserDto user = adminService.createAdmin(request);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<UserProfileResponse> getProfile(@PathVariable String userId) {
        UserProfileResponse profile = profileService.getProfile(userId);
        return ResponseEntity.ok(profile);
    }

    // Login, logout, admin creation, and profile endpoints to be added next
}
