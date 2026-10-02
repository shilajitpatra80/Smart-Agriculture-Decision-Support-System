package com.project.agriculture.exception;

// Thrown when trying to create something that violates a uniqueness rule
// (e.g. registering with an email that's already taken)
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
