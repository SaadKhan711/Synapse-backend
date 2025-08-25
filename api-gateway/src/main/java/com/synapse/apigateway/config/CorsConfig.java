package com.synapse.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Collections;

/**
 * Configures Cross-Origin Resource Sharing (CORS) for the API Gateway.
 * This allows the frontend application running on a different origin (e.g., localhost:5173)
 * to make requests to the gateway.
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        
        // Allow requests from our frontend's origin
        corsConfig.setAllowedOrigins(Collections.singletonList("http://localhost:5173"));
        
        // Allow all HTTP methods (GET, POST, etc.)
        corsConfig.addAllowedMethod("*");
        
        // Allow all headers
        corsConfig.addAllowedHeader("*");
        
        // Allow credentials (like cookies, if we were using them)
        corsConfig.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig); // Apply this configuration to all paths

        return new CorsWebFilter(source);
    }
}
