package com.github.k1mb1.cinema_java_spring.mappers;

import com.github.k1mb1.cinema_java_spring.dtos.movie.MovieRequestDto;
import com.github.k1mb1.cinema_java_spring.dtos.movie.MovieResponseDto;
import com.github.k1mb1.cinema_java_spring.entities.Country;
import com.github.k1mb1.cinema_java_spring.entities.Genre;
import com.github.k1mb1.cinema_java_spring.entities.Movie;
import lombok.NonNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING, uses = {GenreMapper.class, CountryMapper.class})
public interface MovieMapper {

    MovieResponseDto toDto(Movie movie);

    @Mapping(target = "genres", ignore = true)
    @Mapping(target = "countries", ignore = true)
    @Mapping(target = "watchedMovies", ignore = true)
    Movie toEntity(MovieRequestDto movieRequestDto);

    default Genre mapIdToGenre(@NonNull Integer id) {
        return Genre.builder().id(id).build();
    }

    default Country mapIdToCountry(@NonNull Integer id) {
        return Country.builder().id(id).build();
    }
}
