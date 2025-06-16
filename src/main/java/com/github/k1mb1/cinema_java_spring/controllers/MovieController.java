package com.github.k1mb1.cinema_java_spring.controllers;

import com.github.k1mb1.cinema_java_spring.models.movie.MovieFilter;
import com.github.k1mb1.cinema_java_spring.models.movie.MovieRequestDto;
import com.github.k1mb1.cinema_java_spring.models.movie.MovieResponseDto;
import com.github.k1mb1.cinema_java_spring.services.MovieService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/v1/movies")
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
    public ResponseEntity<Page<MovieResponseDto>> getAllMovies(
            @ParameterObject @ModelAttribute MovieFilter filter,
            @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.status(OK).body(movieService.getAllMovies(filter, pageable));
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
