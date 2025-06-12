package com.github.k1mb1.cinema_java_spring.controllers;

import com.github.k1mb1.cinema_java_spring.dtos.movie.MovieRequestDto;
import com.github.k1mb1.cinema_java_spring.dtos.movie.MovieResponseDto;
import com.github.k1mb1.cinema_java_spring.services.MovieService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true)
public class MovieController {

    MovieService movieService;

    @PostMapping
    public ResponseEntity<MovieResponseDto> createMovie(@NonNull @RequestBody MovieRequestDto movieRequestDto) {
        return ResponseEntity.status(CREATED).body(movieService.createMovie(movieRequestDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieResponseDto> getMovieById(@NonNull @PathVariable Integer id) {
        return ResponseEntity.status(OK).body(movieService.getMovieById(id));
    }

    @GetMapping
    public ResponseEntity<List<MovieResponseDto>> getAllMovies() {
        return ResponseEntity.status(OK).body(movieService.getAllMovies());
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovieResponseDto> updateMovie(
            @NonNull @PathVariable Integer id,
            @NonNull @RequestBody MovieRequestDto movieRequestDto
    ) {
        return ResponseEntity.status(OK).body(movieService.updateMovie(id, movieRequestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@NonNull @PathVariable Integer id) {
        movieService.deleteMovie(id);
        return ResponseEntity.status(NO_CONTENT).build();
    }
}
