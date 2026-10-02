package com.project.agriculture.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    // RestClient is Spring's modern, synchronous HTTP client (the successor to RestTemplate).
    // Setting the base URL here means every call in WeatherService only needs to
    // specify the path, not the full domain.
    @Bean
    public RestClient weatherRestClient() {
        return RestClient.builder()
                .baseUrl("https://api.openweathermap.org")
                .build();
    }
}
