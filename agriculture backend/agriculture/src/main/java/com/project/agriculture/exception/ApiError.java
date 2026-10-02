package com.project.agriculture.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

// Consistent JSON error shape returned to the frontend for every failure
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {
    private int status;
    private String message;
    private LocalDateTime timestamp;
    private Map<String, String> fieldErrors; // only populated for validation errors
}
