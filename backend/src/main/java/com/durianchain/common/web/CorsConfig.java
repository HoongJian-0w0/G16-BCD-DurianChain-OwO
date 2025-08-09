package com.durianchain.common.web;

import com.durianchain.interceptors.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
                .addMapping("/**")
                .allowedOrigins("http://localhost:5174", "http://127.0.0.1:5174")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
        // .allowCredentials(true)
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        // Public API pages
                        "/user/page",
                        "/farm/page",
                        "/batch/page",
                        "/batch",
                        "/batch/txhash/*",
                        "/durian/page",
                        "/trader-agency/page",
                        "/logistics-company/page",
                        "/durian-variety/page",
                        "/durian/scan/*",
                        "/durian/by-batch/*",

                        // Auth endpoints
                        "/auth/login",
                        "/auth/register",

                        // Swagger / API docs
                        "/swagger-ui/**",
                        "/v3/api-docs/**",

                        // Error handling
                        "/error"
                );
    }
}
