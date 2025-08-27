package com.bookreview.dto;

import lombok.Data;

@Data
public class SignupRequest {
    private String userId;
    private String email;
    private String password;
}
