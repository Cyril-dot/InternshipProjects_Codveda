package com.LibraryManagementSystem20.LibraryManagementSystemUpgrade;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Enable CORS for all paths and all origins
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")          // all endpoints
                .allowedOrigins("*")       // allow all origins
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*");
    }

}
