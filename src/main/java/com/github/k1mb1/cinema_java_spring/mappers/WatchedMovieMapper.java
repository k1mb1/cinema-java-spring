package com.github.k1mb1.cinema_java_spring.mappers;

import com.github.k1mb1.cinema_java_spring.models.movie.MovieEntity;
import com.github.k1mb1.cinema_java_spring.models.user.UserEntity;
import com.github.k1mb1.cinema_java_spring.models.watchedmovie.WatchedMovieEntity;
import com.github.k1mb1.cinema_java_spring.models.watchedmovie.WatchedMovieRequestDto;
import com.github.k1mb1.cinema_java_spring.models.watchedmovie.WatchedMovieResponseDto;
import lombok.NonNull;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface WatchedMovieMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "movie.id", target = "movieId")
    WatchedMovieResponseDto toDto(WatchedMovieEntity watchedMovie);

    @Mapping(target = "user", source = "userId", qualifiedByName = "mapIdToUser")
    @Mapping(target = "movie", source = "movieId", qualifiedByName = "mapIdToMovie")
    @Mapping(target = "createAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updateAt", ignore = true)
    @Mapping(target = "watchedAt", ignore = true)
    WatchedMovieEntity toEntity(WatchedMovieRequestDto watchedMovieRequestDto);

    @Named("mapIdToUser")
    default UserEntity mapIdToUser(@NonNull Integer id) {
        return UserEntity.builder().id(id).build();
    }

    @Named("mapIdToMovie")
    default MovieEntity mapIdToMovie(@NonNull Integer id) {
        return MovieEntity.builder().id(id).build();
    }
}
