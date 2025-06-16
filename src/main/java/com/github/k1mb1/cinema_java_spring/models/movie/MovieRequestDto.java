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

    @NotBlank
    @Size(min = 1, max = 255)
    String title;

    String description;

    @NotNull
    @PositiveOrZero
    Integer year;

    LocalDate releaseDate;

    @PositiveOrZero
    Long worldGross;

    @PositiveOrZero
    Long budget;

    String ageRating;

    @NotNull
    @PositiveOrZero
    Integer durationMinutes;

    @Builder.Default
    @NotEmpty
    Set<@NotNull Integer> genreIds = new HashSet<>();

    @Builder.Default
    @NotEmpty
    Set<@NotNull Integer> countryIds = new HashSet<>();
}
