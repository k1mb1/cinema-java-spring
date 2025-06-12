package com.github.k1mb1.cinema_java_spring.controllers;

import com.github.k1mb1.cinema_java_spring.dtos.genre.GenreRequestDto;
import com.github.k1mb1.cinema_java_spring.dtos.genre.GenreResponseDto;
import com.github.k1mb1.cinema_java_spring.services.GenreService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/genres")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true)
public class GenreController {

    GenreService genreService;

    @PostMapping
    public ResponseEntity<GenreResponseDto> createGenre(@NonNull @RequestBody GenreRequestDto genreRequestDto) {
        return ResponseEntity.status(CREATED).body(genreService.createGenre(genreRequestDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenreResponseDto> getGenreById(@NonNull @PathVariable Integer id) {
        return ResponseEntity.status(OK).body(genreService.getGenreById(id));
    }

    @GetMapping
    public ResponseEntity<List<GenreResponseDto>> getAllGenres() {
        return ResponseEntity.status(OK).body(genreService.getAllGenres());
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenreResponseDto> updateGenre(
            @NonNull @PathVariable Integer id,
            @NonNull @RequestBody GenreRequestDto genreRequestDto
    ) {
        return ResponseEntity.status(OK).body(genreService.updateGenre(id, genreRequestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGenre(@NonNull @PathVariable Integer id) {
        genreService.deleteGenre(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
