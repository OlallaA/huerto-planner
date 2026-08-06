package com.olalla.plantplan.dto;

import java.time.LocalDate;

public record DailyWeatherResponse(
        LocalDate date,
        Integer weatherCode,
        String weatherDescription,
        Double temperatureMaxC,
        Double temperatureMinC,
        Double precipitationSumMm,
        Integer precipitationProbabilityMaxPercent
) {
}
