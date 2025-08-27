package com.bookreview.dto;

import lombok.Data;

@Data
public class UserProfileResponse {
    private String userId;
    private String email;
    private String createdAt;
    private String lastModifiedAt;
    private String createdBy;
    private String lastModifiedBy;
    private java.util.Set<String> roles;
}
