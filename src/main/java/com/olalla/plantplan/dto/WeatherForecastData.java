package com.olalla.plantplan.dto;

import java.util.List;

public record WeatherForecastData(
        double latitude,
        double longitude,
        String timezone,
        CurrentWeatherResponse current,
        List<DailyWeatherResponse> daily
) {
}
