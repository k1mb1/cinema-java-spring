package com.github.k1mb1.cinema_java_spring.dtos.movie;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
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
    String title;

    String description;

    @Positive(message = "Year must be positive")
    Integer year;

    LocalDate releaseDate;

    Long worldGross;

    Long budget;

    String ageRating;

    @Positive(message = "Duration must be positive")
    Integer durationMinutes;

    @Builder.Default
    Set<Integer> genreIds = new HashSet<>();

    @Builder.Default
    Set<Integer> countryIds = new HashSet<>();
}
