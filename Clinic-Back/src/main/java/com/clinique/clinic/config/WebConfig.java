package com.clinique.clinic.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Apply to all endpoints
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:4200") // or "*" to allow all
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Authorization") // if you need to expose headers
                .allowCredentials(true);         // if you use cookies/auth headers
    }
}


