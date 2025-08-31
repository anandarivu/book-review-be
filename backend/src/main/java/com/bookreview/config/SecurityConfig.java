package com.bookreview.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter jwtAuthenticationConverter() {
        org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter converter = new org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            java.util.Collection<org.springframework.security.core.GrantedAuthority> authorities = new java.util.ArrayList<>();
            Object rolesObj = jwt.getClaim("roles");
            if (rolesObj instanceof java.util.Collection<?>) {
                for (Object role : (java.util.Collection<?>) rolesObj) {
                    authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + role));
                }
            }
            return authorities;
        });
        return converter;
    }
    @Bean
    public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
    org.springframework.web.cors.CorsConfiguration configuration = new org.springframework.web.cors.CorsConfiguration();
        configuration.setAllowedOrigins(java.util.List.of(
            "http://localhost:5173",
            "http://book-review-frontend-anand-2d097b30.s3-website.ap-south-1.amazonaws.com/"
        ));
    configuration.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(java.util.List.of("*"));
    configuration.setAllowCredentials(true);
        org.springframework.web.cors.UrlBasedCorsConfigurationSource source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    private static final String JWT_SECRET = "i-like-marvel-movies-until-endgame";

    @Bean
    public JwtDecoder jwtDecoder() {
        byte[] secretBytes = JWT_SECRET.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        return NimbusJwtDecoder.withSecretKey(new javax.crypto.spec.SecretKeySpec(secretBytes, "HmacSHA256")).build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .cors().and()
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/api/auth/**", "/swagger-ui.html", "/v3/api-docs/**", "/swagger-ui/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/books/**").hasRole("ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/books/**").hasRole("ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/books/**").hasRole("ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/books/**").authenticated()
                .anyRequest().authenticated()
            );
    http.oauth2ResourceServer().jwt().jwtAuthenticationConverter(jwtAuthenticationConverter());
        return http.build();
    }
}
