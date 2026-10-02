package com.project.agriculture.exception;

// Thrown whenever a lookup by ID fails (crop, fertilizer, disease not found, etc.)
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
