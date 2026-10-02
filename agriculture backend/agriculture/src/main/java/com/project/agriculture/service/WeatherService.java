package com.project.agriculture.service;

import com.project.agriculture.dto.OpenWeatherApiResponse;
import com.project.agriculture.dto.WeatherResponse;
import com.project.agriculture.entity.WeatherData;
import com.project.agriculture.exception.WeatherServiceException;
import com.project.agriculture.repository.WeatherDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WeatherService {

    private final WeatherDataRepository weatherDataRepository;
    private final RestClient weatherRestClient;

    @Value("${openweather.api-key}")
    private String apiKey;

    @Transactional
    public WeatherResponse getWeather(String location) {
        LocalDate today = LocalDate.now();

        // Cache-first: only hit the external API once per location per day.
        // This matters both for free-tier rate limits and for response speed.
        Optional<WeatherData> cached = weatherDataRepository.findByLocationAndRecordDate(location, today);
        if (cached.isPresent()) {
            return toResponse(cached.get(), "CACHED");
        }

        OpenWeatherApiResponse apiResponse = fetchFromApi(location);
        WeatherData saved = weatherDataRepository.save(toEntity(location, today, apiResponse));

        return toResponse(saved, "LIVE");
    }

    private OpenWeatherApiResponse fetchFromApi(String location) {
        try {
            return weatherRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/data/2.5/weather")
                            .queryParam("q", location)
                            .queryParam("appid", apiKey)
                            .queryParam("units", "metric") // Celsius, not Kelvin
                            .build())
                    .retrieve()
                    .body(OpenWeatherApiResponse.class);
        } catch (RestClientException e) {
            // Covers: unknown city name, network failure, invalid API key, rate limit hit, etc.
            throw new WeatherServiceException(
                    "Could not fetch weather for '" + location + "'. Check the location name and try again.");
        }
    }

    private WeatherData toEntity(String location, LocalDate date, OpenWeatherApiResponse api) {
        WeatherData entry = new WeatherData();
        entry.setLocation(location);
        entry.setRecordDate(date);
        entry.setTemperature(api.getMain() != null ? BigDecimal.valueOf(api.getMain().getTemp()) : null);
        entry.setHumidity(api.getMain() != null ? BigDecimal.valueOf(api.getMain().getHumidity()) : null);

        BigDecimal rainfall = (api.getRain() != null && api.getRain().getOneHour() != null)
                ? BigDecimal.valueOf(api.getRain().getOneHour())
                : BigDecimal.ZERO; // no "rain" object in the response simply means it isn't raining
        entry.setRainfall(rainfall);

        return entry;
    }

    private WeatherResponse toResponse(WeatherData data, String source) {
        return new WeatherResponse(
                data.getLocation(),
                data.getRecordDate(),
                data.getTemperature(),
                data.getRainfall(),
                data.getHumidity(),
                source
        );
    }
}
