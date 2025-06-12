package com.github.k1mb1.cinema_java_spring.mappers;

import com.github.k1mb1.cinema_java_spring.dtos.movie.MovieRequestDto;
import com.github.k1mb1.cinema_java_spring.dtos.movie.MovieResponseDto;
import com.github.k1mb1.cinema_java_spring.entities.Country;
import com.github.k1mb1.cinema_java_spring.entities.Genre;
import com.github.k1mb1.cinema_java_spring.entities.Movie;
import lombok.NonNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;
import java.util.stream.Collectors;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING, uses = {GenreMapper.class, CountryMapper.class})
public interface MovieMapper {

    MovieResponseDto toDto(Movie movie);

    @Mapping(target = "genres", source = "genreIds")
    @Mapping(target = "countries", source = "countryIds")
    @Mapping(target = "watchedMovies", ignore = true)
    @Mapping(target = "createAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updateAt", ignore = true)
    Movie toEntity(MovieRequestDto movieRequestDto);

    default Set<Genre> mapGenreIds(Set<Integer> genreIds) {
        return genreIds.stream()
                .map(this::mapIdToGenre)
                .collect(Collectors.toSet());
    }

    default Set<Country> mapCountryIds(Set<Integer> countryIds) {
        return countryIds.stream()
                .map(this::mapIdToCountry)
                .collect(Collectors.toSet());
    }

    default Genre mapIdToGenre(@NonNull Integer id) {
        return Genre.builder().id(id).build();
    }

    default Country mapIdToCountry(@NonNull Integer id) {
        return Country.builder().id(id).build();
    }
}
