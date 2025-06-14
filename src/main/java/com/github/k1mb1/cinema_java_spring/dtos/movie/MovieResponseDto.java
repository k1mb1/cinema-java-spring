package com.github.k1mb1.cinema_java_spring.dtos.movie;

import com.github.k1mb1.cinema_java_spring.dtos.country.CountryResponseDto;
import com.github.k1mb1.cinema_java_spring.dtos.genre.GenreResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieResponseDto {

    Integer id;
    String title;
    String description;
    Integer year;
    LocalDate releaseDate;
    Long worldGross;
    Long budget;
    String ageRating;
    Integer durationMinutes;

    @Builder.Default
    Set<GenreResponseDto> genres = new HashSet<>();
    @Builder.Default
    Set<CountryResponseDto> countries = new HashSet<>();

    LocalDateTime createAt;
    LocalDateTime updateAt;
}
