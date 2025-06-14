package com.github.k1mb1.cinema_java_spring.services;

import com.github.k1mb1.cinema_java_spring.dtos.genre.GenreRequestDto;
import com.github.k1mb1.cinema_java_spring.dtos.genre.GenreResponseDto;
import com.github.k1mb1.cinema_java_spring.errors.NotFoundException;
import com.github.k1mb1.cinema_java_spring.mappers.GenreMapper;
import com.github.k1mb1.cinema_java_spring.repositories.GenreRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.github.k1mb1.cinema_java_spring.errors.ErrorMessages.GENRE_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
@FieldDefaults(makeFinal = true)
public class GenreService {

    GenreRepository genreRepository;
    GenreMapper genreMapper;

    public GenreResponseDto createGenre(@NonNull GenreRequestDto genreRequestDto) {
        return genreMapper.toDto(
                genreRepository.save(genreMapper.toEntity(genreRequestDto))
        );
    }

    @Transactional(readOnly = true)
    public GenreResponseDto getGenreById(@NonNull Integer id) {
        return genreMapper.toDto(
                genreRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException(GENRE_NOT_FOUND.formatted(id))));
    }

    @Transactional(readOnly = true)
    public List<GenreResponseDto> getAllGenres() {
        return genreRepository.findAll().stream()
                .map(genreMapper::toDto)
                .toList();
    }

    public GenreResponseDto updateGenre(@NonNull Integer id, @NonNull GenreRequestDto genreRequestDto) {
        val existingGenre = genreRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(GENRE_NOT_FOUND.formatted(id)));

        genreMapper.partialUpdate(genreRequestDto, existingGenre);

        return genreMapper.toDto(genreRepository.save(existingGenre));
    }

    public void deleteGenre(@NonNull Integer id) {
        if (!genreRepository.existsById(id)) {
            throw new NotFoundException(GENRE_NOT_FOUND.formatted(id));
        }
        genreRepository.deleteById(id);
    }
}
