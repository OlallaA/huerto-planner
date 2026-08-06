package com.olalla.plantplan.weather;

import com.olalla.plantplan.dto.WeatherForecastData;

public interface WeatherProvider {

    WeatherForecastData fetchForecast(double latitude, double longitude, int forecastDays);
}
