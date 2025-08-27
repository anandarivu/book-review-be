package com.bookreview.dto;

import lombok.Data;

@Data
public class AdminSignupRequest {
    private String userId;
    private String email;
    private String password;
    private String secret;
}
