package com.synapse.modelservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for the service.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Authorize all HTTP requests. Any request must be authenticated.
            .authorizeHttpRequests(authorize -> authorize
                .anyRequest().authenticated()
            )
            // Configure the app as an OAuth2 Resource Server,
            // which validates JWTs from the configured issuer.
            .oauth2ResourceServer(oauth2 -> oauth2.jwt());
        return http.build();
    }
}