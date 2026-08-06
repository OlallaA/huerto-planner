package com.olalla.plantplan.controller;

import com.olalla.plantplan.dto.GardenWeatherResponse;
import com.olalla.plantplan.security.AuthenticatedUser;
import com.olalla.plantplan.service.WeatherService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/gardens/{gardenId}/weather")
    public GardenWeatherResponse getGardenWeather(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long gardenId
    ) {
        return weatherService.getGardenWeather(gardenId, user.id());
    }
}
