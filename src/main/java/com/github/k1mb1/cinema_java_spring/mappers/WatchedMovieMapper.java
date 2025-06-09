package com.github.k1mb1.cinema_java_spring.mappers;

import com.github.k1mb1.cinema_java_spring.dtos.watchedmovie.WatchedMovieRequestDto;
import com.github.k1mb1.cinema_java_spring.dtos.watchedmovie.WatchedMovieResponseDto;
import com.github.k1mb1.cinema_java_spring.entities.Movie;
import com.github.k1mb1.cinema_java_spring.entities.User;
import com.github.k1mb1.cinema_java_spring.entities.WatchedMovie;
import lombok.NonNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface WatchedMovieMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "movie.id", target = "movieId")
    WatchedMovieResponseDto toDto(WatchedMovie watchedMovie);

    @Mapping(target = "user", source = "userId", qualifiedByName = "mapIdToUser")
    @Mapping(target = "movie", source = "movieId", qualifiedByName = "mapIdToMovie")
    @Mapping(target = "createAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updateAt", ignore = true)
    @Mapping(target = "watchedAt", ignore = true)
    WatchedMovie toEntity(WatchedMovieRequestDto watchedMovieRequestDto);

    @Named("mapIdToUser")
    default User mapIdToUser(@NonNull Integer id) {
        return User.builder().id(id).build();
    }

    @Named("mapIdToMovie")
    default Movie mapIdToMovie(@NonNull Integer id) {
        return Movie.builder().id(id).build();
    }
}
