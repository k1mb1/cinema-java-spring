package com.github.k1mb1.cinema_java_spring.controllers;

import com.github.k1mb1.cinema_java_spring.models.watchedmovie.WatchedMovieRequestDto;
import com.github.k1mb1.cinema_java_spring.models.watchedmovie.WatchedMovieResponseDto;
import com.github.k1mb1.cinema_java_spring.services.WatchedMovieService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/v1/watched-movies")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true)
public class WatchedMovieController {

    WatchedMovieService watchedMovieService;

    @PostMapping
    public ResponseEntity<WatchedMovieResponseDto> markMovieAsWatched(
            @Valid @NonNull @RequestBody WatchedMovieRequestDto watchedMovieRequestDto
    ) {
        return ResponseEntity.status(CREATED).body(watchedMovieService.markMovieAsWatched(watchedMovieRequestDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WatchedMovieResponseDto> getWatchedMovieById(@NonNull @PathVariable Integer id) {
        return ResponseEntity.status(OK).body(watchedMovieService.getWatchedMovieById(id));
    }

    @GetMapping
    public ResponseEntity<List<WatchedMovieResponseDto>> getAllWatchedMovies() {
        return ResponseEntity.status(OK).body(watchedMovieService.getAllWatchedMovies());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWatchedMovie(@NonNull @PathVariable Integer id) {
        watchedMovieService.deleteWatchedMovie(id);
        return ResponseEntity.status(NO_CONTENT).build();
    }
}
