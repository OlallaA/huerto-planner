package com.olalla.plantplan.dto;

import java.util.List;

public record GardenWeatherResponse(
        Long gardenId,
        String gardenName,
        Double latitude,
        Double longitude,
        String timezone,
        CurrentWeatherResponse current,
        List<DailyWeatherResponse> daily,
        String wateringHint
) {
}
