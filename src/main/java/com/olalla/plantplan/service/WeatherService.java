package com.olalla.plantplan.service;

import com.olalla.plantplan.config.WeatherProperties;
import com.olalla.plantplan.dto.DailyWeatherResponse;
import com.olalla.plantplan.dto.GardenWeatherResponse;
import com.olalla.plantplan.dto.WeatherForecastData;
import com.olalla.plantplan.entity.Garden;
import com.olalla.plantplan.exception.ForbiddenException;
import com.olalla.plantplan.exception.ResourceNotFoundException;
import com.olalla.plantplan.repository.GardenRepository;
import com.olalla.plantplan.weather.WeatherProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WeatherService {

    private final GardenRepository gardenRepository;
    private final WeatherProvider weatherProvider;
    private final WeatherProperties weatherProperties;

    public WeatherService(
            GardenRepository gardenRepository,
            WeatherProvider weatherProvider,
            WeatherProperties weatherProperties
    ) {
        this.gardenRepository = gardenRepository;
        this.weatherProvider = weatherProvider;
        this.weatherProperties = weatherProperties;
    }

    @Transactional(readOnly = true)
    public GardenWeatherResponse getGardenWeather(Long gardenId, Long userId) {
        Garden garden = gardenRepository.findById(gardenId)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un huerto con id " + gardenId));

        if (!garden.getUser().getId().equals(userId)) {
            throw new ForbiddenException("No puedes acceder al huerto con id " + gardenId);
        }

        if (garden.getLatitude() == null || garden.getLongitude() == null) {
            throw new IllegalArgumentException(
                    "El huerto con id " + gardenId + " no tiene coordenadas. Anade latitud y longitud para consultar el tiempo"
            );
        }

        WeatherForecastData forecast = weatherProvider.fetchForecast(
                garden.getLatitude(),
                garden.getLongitude(),
                weatherProperties.forecastDays()
        );

        return new GardenWeatherResponse(
                garden.getId(),
                garden.getName(),
                garden.getLatitude(),
                garden.getLongitude(),
                forecast.timezone(),
                forecast.current(),
                forecast.daily(),
                buildWateringHint(forecast.daily())
        );
    }

    private String buildWateringHint(List<DailyWeatherResponse> daily) {
        if (daily == null || daily.isEmpty()) {
            return "No hay suficientes datos para recomendar el riego";
        }

        DailyWeatherResponse today = daily.getFirst();
        Double precipitation = today.precipitationSumMm();
        Integer probability = today.precipitationProbabilityMaxPercent();
        Double maxTemp = today.temperatureMaxC();

        double rainMm = precipitation == null ? 0.0 : precipitation;
        int rainProbability = probability == null ? 0 : probability;
        double temperature = maxTemp == null ? 0.0 : maxTemp;

        if (rainMm >= 5.0 || rainProbability >= 70) {
            return "Hoy se espera lluvia relevante. Puedes retrasar el riego";
        }

        if (temperature >= 30.0 && rainMm < 1.0) {
            return "Dia caluroso y seco. Conviene regar, preferiblemente al atardecer";
        }

        if (rainMm >= 1.0 || rainProbability >= 40) {
            return "Hay probabilidad de precipitacion. Revisa el riego antes de regar";
        }

        return "Condiciones estables. Sigue la frecuencia de riego habitual de tus cultivos";
    }
}
