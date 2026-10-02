package com.project.agriculture.controller;

import com.project.agriculture.dto.WeatherResponse;
import com.project.agriculture.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
// No @PreAuthorize needed — SecurityConfig already permits all of /api/weather/**,
// since general weather info isn't sensitive and there's no reason to force a login for it.
public class WeatherController {

    private final WeatherService weatherService;

    // GET /api/weather?location=Nashik
    @GetMapping
    public ResponseEntity<WeatherResponse> getWeather(@RequestParam String location) {
        return ResponseEntity.ok(weatherService.getWeather(location));
    }
}
