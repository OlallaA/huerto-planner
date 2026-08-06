package com.olalla.plantplan.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "plantplan.jwt")
public record JwtProperties(
        String secret,
        long expiration
) {
}