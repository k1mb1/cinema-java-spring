package com.github.k1mb1.cinema_java_spring.models.movie;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieRequestDto {

    @NotBlank(message = "Title cannot be blank")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    String title;

    String description;

    @NotNull(message = "Year cannot be null")
    @Positive(message = "Year must be positive")
    Integer year;

    LocalDate releaseDate;

    @PositiveOrZero(message = "World gross must be positive or zero")
    Long worldGross;

    @PositiveOrZero(message = "Budget must be positive or zero")
    Long budget;

    String ageRating;

    @NotNull(message = "Duration cannot be null")
    @Positive(message = "Duration must be positive")
    @Max(value = 1000, message = "Duration cannot exceed 1000 minutes")
    Integer durationMinutes;

    @Builder.Default
    @NotEmpty(message = "At least one genre must be selected")
    Set<@Positive(message = "Genre ID must be positive") Integer> genreIds = new HashSet<>();

    @Builder.Default
    @NotEmpty(message = "At least one country must be selected")
    Set<@Positive(message = "Country ID must be positive") Integer> countryIds = new HashSet<>();
}
