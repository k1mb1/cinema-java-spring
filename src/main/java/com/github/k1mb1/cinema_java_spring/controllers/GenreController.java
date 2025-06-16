package com.github.k1mb1.cinema_java_spring.controllers;

import com.github.k1mb1.cinema_java_spring.models.genre.GenreRequestDto;
import com.github.k1mb1.cinema_java_spring.models.genre.GenreResponseDto;
import com.github.k1mb1.cinema_java_spring.services.GenreService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1/genres")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true)
public class GenreController {

    GenreService genreService;

    @PostMapping
    public ResponseEntity<GenreResponseDto> createGenre(
            @Valid @NonNull @RequestBody GenreRequestDto genreRequestDto
    ) {
        return ResponseEntity.status(CREATED).body(genreService.createGenre(genreRequestDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenreResponseDto> getGenreById(@NonNull @PathVariable Integer id) {
        return ResponseEntity.status(OK).body(genreService.getGenreById(id));
    }

    @GetMapping
    public ResponseEntity<Page<GenreResponseDto>> getAllGenres(
            @NonNull @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.status(OK).body(genreService.getAllGenres(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenreResponseDto> updateGenre(
            @NonNull @PathVariable Integer id,
            @Valid @NonNull @RequestBody GenreRequestDto genreRequestDto
    ) {
        return ResponseEntity.status(OK).body(genreService.updateGenre(id, genreRequestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGenre(@NonNull @PathVariable Integer id) {
        genreService.deleteGenre(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
