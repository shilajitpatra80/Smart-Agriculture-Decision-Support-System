package com.project.agriculture.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

// Mirrors OpenWeatherMap's /data/2.5/weather response — but we only declare
// the fields we actually use. @JsonIgnoreProperties(ignoreUnknown = true)
// tells Jackson to silently skip everything else in their (much larger) response
// instead of throwing an error, so this stays easy to read.
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenWeatherApiResponse {

    private String name; // city name, as OpenWeatherMap resolved it
    private Main main;
    private Rain rain;   // absent entirely if it isn't currently raining

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Main {
        private Double temp;
        private Double humidity;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Rain {
        @JsonProperty("1h") // OpenWeatherMap's field is literally named "1h" (rain in mm, last hour)
        private Double oneHour;
    }
}
