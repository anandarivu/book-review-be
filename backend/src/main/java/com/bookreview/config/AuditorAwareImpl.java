package com.bookreview.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

@Configuration
public class AuditorAwareImpl {
    @Bean
    public AuditorAware<String> auditorAware() {
        // TODO: Replace with actual user context (e.g., from SecurityContext)
        return () -> Optional.of("system");
    }
}
