package com.project.agriculture.exception;

// Thrown when the external weather API call fails or returns something unusable
public class WeatherServiceException extends RuntimeException {
    public WeatherServiceException(String message) {
        super(message);
    }
}
