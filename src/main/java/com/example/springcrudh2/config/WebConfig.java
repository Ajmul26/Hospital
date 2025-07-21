package com.example.springcrudh2.config;

import com.example.springcrudh2.interceptor.SplunkLoggingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration for registering interceptors and other web-related settings
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    private final SplunkLoggingInterceptor splunkLoggingInterceptor;
    
    @Autowired
    public WebConfig(SplunkLoggingInterceptor splunkLoggingInterceptor) {
        this.splunkLoggingInterceptor = splunkLoggingInterceptor;
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(splunkLoggingInterceptor)
                .addPathPatterns("/api/**") // Only intercept API calls
                .excludePathPatterns(
                    "/api/users/health", // Exclude health check to avoid noise
                    "/actuator/**",      // Exclude actuator endpoints
                    "/swagger-ui/**",    // Exclude Swagger UI
                    "/v3/api-docs/**"    // Exclude OpenAPI docs
                );
    }
}