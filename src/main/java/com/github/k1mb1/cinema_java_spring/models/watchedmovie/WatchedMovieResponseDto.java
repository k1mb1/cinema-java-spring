package com.github.k1mb1.cinema_java_spring.models.watchedmovie;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WatchedMovieResponseDto {

    Integer id;
    Integer userId;
    Integer movieId;
    LocalDateTime watchedAt;
    LocalDateTime createAt;
    LocalDateTime updateAt;
}
