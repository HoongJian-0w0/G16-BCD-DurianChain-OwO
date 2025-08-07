package com.durianchain.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PinataConfig {
    @Value("${pinata.jwt}")
    public String jwt;

    @Value("${pinata.base-url}")
    public String baseUrl;
}
