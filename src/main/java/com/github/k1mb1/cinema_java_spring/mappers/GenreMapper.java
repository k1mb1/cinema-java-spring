package com.github.k1mb1.cinema_java_spring.mappers;

import com.github.k1mb1.cinema_java_spring.dtos.genre.GenreRequestDto;
import com.github.k1mb1.cinema_java_spring.dtos.genre.GenreResponseDto;
import com.github.k1mb1.cinema_java_spring.entities.Genre;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface GenreMapper {

    GenreResponseDto toDto(Genre genre);

    @Mapping(target = "movies", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createAt", ignore = true)
    @Mapping(target = "updateAt", ignore = true)
    Genre toEntity(GenreRequestDto genreRequestDto);
}
