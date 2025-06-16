package com.github.k1mb1.cinema_java_spring.mappers;

import com.github.k1mb1.cinema_java_spring.models.country.CountryEntity;
import com.github.k1mb1.cinema_java_spring.models.genre.GenreEntity;
import com.github.k1mb1.cinema_java_spring.models.movie.MovieEntity;
import com.github.k1mb1.cinema_java_spring.models.movie.MovieRequestDto;
import com.github.k1mb1.cinema_java_spring.models.movie.MovieResponseDto;
import lombok.NonNull;
import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING, uses = {GenreMapper.class, CountryMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MovieMapper {

    MovieResponseDto toDto(MovieEntity movieEntity);

    @Mapping(target = "genres", source = "genreIds")
    @Mapping(target = "countries", source = "countryIds")
    @Mapping(target = "watchedMovies", ignore = true)
    @Mapping(target = "createAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updateAt", ignore = true)
    MovieEntity toEntity(MovieRequestDto movieRequestDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createAt", ignore = true)
    @Mapping(target = "updateAt", ignore = true)
    @Mapping(target = "watchedMovies", ignore = true)
    @Mapping(target = "genres", source = "genreIds")
    @Mapping(target = "countries", source = "countryIds")
    void partialUpdate(MovieRequestDto movieRequestDto, @MappingTarget MovieEntity movie);

    default Set<GenreEntity> mapGenreIds(Set<Integer> genreIds) {
        return genreIds.stream()
                .map(this::mapIdToGenre)
                .collect(Collectors.toSet());
    }

    default Set<CountryEntity> mapCountryIds(Set<Integer> countryIds) {
        return countryIds.stream()
                .map(this::mapIdToCountry)
                .collect(Collectors.toSet());
    }

    default GenreEntity mapIdToGenre(@NonNull Integer id) {
        return GenreEntity.builder().id(id).build();
    }

    default CountryEntity mapIdToCountry(@NonNull Integer id) {
        return CountryEntity.builder().id(id).build();
    }

}
