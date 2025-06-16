package com.github.k1mb1.cinema_java_spring.models.watchedmovie;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WatchedMovieRequestDto {

    @NotNull
    Integer userId;

    @NotNull
    Integer movieId;
}
