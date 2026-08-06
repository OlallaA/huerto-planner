package com.olalla.plantplan.dto;

import java.time.LocalDateTime;

public record CurrentWeatherResponse(
        LocalDateTime time,
        Double temperatureC,
        Integer relativeHumidityPercent,
        Double precipitationMm,
        Integer weatherCode,
        String weatherDescription,
        Double windSpeedKmh
) {
}
