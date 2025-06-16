package com.github.k1mb1.cinema_java_spring.errors;

import lombok.NonNull;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record Error(
        int code,
        String message,
        HttpStatus status,
        LocalDateTime timestamp,
        String details
) {
    public static Error of(@NonNull String message, @NonNull HttpStatus status) {
        return new Error(status.value(), message, status, LocalDateTime.now(), null);
    }

    public static Error of(@NonNull String message, @NonNull HttpStatus status, @NonNull String details) {
        return new Error(status.value(), message, status, LocalDateTime.now(), details);
    }
}