package com.bookreview.service;

import com.bookreview.dto.UserProfileResponse;
import com.bookreview.model.User;
import com.bookreview.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {
    @Autowired
    private UserRepository userRepository;

    public UserProfileResponse getProfile(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserProfileResponse profile = new UserProfileResponse();
        profile.setUserId(user.getUserId());
        profile.setEmail(user.getEmail());
        profile.setCreatedAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null);
        profile.setLastModifiedAt(user.getLastModifiedAt() != null ? user.getLastModifiedAt().toString() : null);
        profile.setCreatedBy(user.getCreatedBy());
        profile.setLastModifiedBy(user.getLastModifiedBy());
        profile.setRoles(user.getRoles().stream().map(r -> r.getName()).collect(java.util.stream.Collectors.toSet()));
        return profile;
    }
}
