package com.olalla.plantplan.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "plantplan.weather")
public record WeatherProperties(
        String baseUrl,
        int forecastDays
) {
}
