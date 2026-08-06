package com.olalla.plantplan.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.olalla.plantplan.dto.CurrentWeatherResponse;
import com.olalla.plantplan.dto.DailyWeatherResponse;
import com.olalla.plantplan.dto.WeatherForecastData;
import com.olalla.plantplan.exception.WeatherProviderException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Component
public class OpenMeteoWeatherProvider implements WeatherProvider {

    private static final String CURRENT_VARS =
            "temperature_2m,relative_humidity_2m,precipitation,weather_code,wind_speed_10m";
    private static final String DAILY_VARS =
            "weather_code,temperature_2m_max,temperature_2m_min,precipitation_sum,precipitation_probability_max";

    private final RestClient weatherRestClient;

    public OpenMeteoWeatherProvider(RestClient weatherRestClient) {
        this.weatherRestClient = weatherRestClient;
    }

    @Override
    public WeatherForecastData fetchForecast(double latitude, double longitude, int forecastDays) {
        try {
            OpenMeteoApiResponse response = weatherRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1/forecast")
                            .queryParam("latitude", latitude)
                            .queryParam("longitude", longitude)
                            .queryParam("current", CURRENT_VARS)
                            .queryParam("daily", DAILY_VARS)
                            .queryParam("timezone", "auto")
                            .queryParam("forecast_days", forecastDays)
                            .build())
                    .retrieve()
                    .body(OpenMeteoApiResponse.class);

            if (response == null || response.current() == null || response.daily() == null) {
                throw new WeatherProviderException("La API meteorologica devolvio una respuesta incompleta");
            }

            return mapResponse(response);
        } catch (WeatherProviderException exception) {
            throw exception;
        } catch (RestClientException exception) {
            throw new WeatherProviderException(
                    "No se pudo obtener la informacion meteorologica. Intentalo de nuevo mas tarde",
                    exception
            );
        }
    }

    private WeatherForecastData mapResponse(OpenMeteoApiResponse response) {
        Current current = response.current();
        CurrentWeatherResponse currentWeather = new CurrentWeatherResponse(
                parseDateTime(current.time()),
                current.temperature2m(),
                current.relativeHumidity2m(),
                current.precipitation(),
                current.weatherCode(),
                WmoWeatherCodes.describe(current.weatherCode()),
                current.windSpeed10m()
        );

        Daily daily = response.daily();
        List<DailyWeatherResponse> days = new ArrayList<>();
        List<LocalDate> dates = daily.time() == null ? List.of() : daily.time();

        for (int i = 0; i < dates.size(); i++) {
            Integer code = valueAt(daily.weatherCode(), i);
            days.add(new DailyWeatherResponse(
                    dates.get(i),
                    code,
                    WmoWeatherCodes.describe(code),
                    valueAt(daily.temperature2mMax(), i),
                    valueAt(daily.temperature2mMin(), i),
                    valueAt(daily.precipitationSum(), i),
                    valueAt(daily.precipitationProbabilityMax(), i)
            ));
        }

        return new WeatherForecastData(
                response.latitude(),
                response.longitude(),
                response.timezone(),
                currentWeather,
                days
        );
    }

    private static <T> T valueAt(List<T> values, int index) {
        if (values == null || index >= values.size()) {
            return null;
        }
        return values.get(index);
    }

    private static LocalDateTime parseDateTime(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException ignored) {
            return LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record OpenMeteoApiResponse(
            double latitude,
            double longitude,
            String timezone,
            Current current,
            Daily daily
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record Current(
            String time,
            @JsonProperty("temperature_2m") Double temperature2m,
            @JsonProperty("relative_humidity_2m") Integer relativeHumidity2m,
            Double precipitation,
            @JsonProperty("weather_code") Integer weatherCode,
            @JsonProperty("wind_speed_10m") Double windSpeed10m
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record Daily(
            List<LocalDate> time,
            @JsonProperty("weather_code") List<Integer> weatherCode,
            @JsonProperty("temperature_2m_max") List<Double> temperature2mMax,
            @JsonProperty("temperature_2m_min") List<Double> temperature2mMin,
            @JsonProperty("precipitation_sum") List<Double> precipitationSum,
            @JsonProperty("precipitation_probability_max") List<Integer> precipitationProbabilityMax
    ) {
    }
}
