package com.bookreview.dto;

import lombok.Data;
import java.util.UUID;
import java.util.Set;

@Data
public class UserDto {
    private UUID id;
    private String username;
    private String email;
    private Set<String> roles;
}
