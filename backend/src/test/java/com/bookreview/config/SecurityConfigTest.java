package com.bookreview.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.JwtDecoder;

public class SecurityConfigTest {
    private final SecurityConfig securityConfig = new SecurityConfig();


    @Test
    void jwtDecoder_shouldNotBeNull() {
        JwtDecoder decoder = securityConfig.jwtDecoder();
    org.junit.jupiter.api.Assertions.assertNotNull(decoder);
    }

}
