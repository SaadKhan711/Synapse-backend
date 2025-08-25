package com.synapse.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // <-- Import this
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Security configuration for the gateway.
 * This version includes a rule to permit CORS preflight requests.
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
            .authorizeExchange(exchange -> exchange
                // !! --- THIS IS THE FIX --- !!
                // Allow all OPTIONS requests. This is required for CORS preflight checks.
                .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                
                // Any other request must be authenticated.
                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt());
            
        // Disable CSRF protection for stateless APIs
        http.csrf(ServerHttpSecurity.CsrfSpec::disable);

        return http.build();
    }
}
