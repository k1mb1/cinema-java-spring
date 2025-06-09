package com.github.k1mb1.cinema_java_spring.config;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record Error(
        int code,
        String message,
        HttpStatus status,
        LocalDateTime timestamp,
        String details
) {
    public Error {
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Error message cannot be null or blank");
        }
        if (status == null) {
            throw new IllegalArgumentException("HTTP status cannot be null");
        }
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
        code = status.value();
    }

    public static Error of(String message, HttpStatus status) {
        return new Error(0, message, status, LocalDateTime.now(), null);
    }

    public static Error of(String message, HttpStatus status, String details) {
        return new Error(0, message, status, LocalDateTime.now(), details);
    }
}
