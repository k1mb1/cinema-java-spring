package com.github.k1mb1.cinema_java_spring.services;

import com.github.k1mb1.cinema_java_spring.errors.NotFoundException;
import com.github.k1mb1.cinema_java_spring.dtos.genre.GenreRequestDto;
import com.github.k1mb1.cinema_java_spring.dtos.genre.GenreResponseDto;
import com.github.k1mb1.cinema_java_spring.entities.Genre;
import com.github.k1mb1.cinema_java_spring.mappers.GenreMapper;
import com.github.k1mb1.cinema_java_spring.repositories.GenreRepository;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenreServiceTest {

    @Mock
    GenreRepository genreRepository;

    @Mock
    GenreMapper genreMapper;

    @InjectMocks
    GenreService genreService;

    Genre genre;
    GenreRequestDto genreRequestDto;
    GenreResponseDto genreResponseDto;

    static final int VALID_ID = 1;
    static final int INVALID_ID = 99;

    @BeforeEach
    void setUp() {
        genreRequestDto = GenreRequestDto.builder()
                .name("Action")
                .build();

        genre = Genre.builder()
                .id(VALID_ID)
                .name(genreRequestDto.getName())
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();

        genreResponseDto = GenreResponseDto.builder()
                .id(genre.getId())
                .name(genre.getName())
                .createAt(genre.getCreateAt())
                .updateAt(genre.getUpdateAt())
                .build();
    }

    @Test
    void createGenre_ShouldReturnGenreResponseDto() {
        when(genreMapper.toEntity(genreRequestDto)).thenReturn(genre);
        when(genreRepository.save(genre)).thenReturn(genre);
        when(genreMapper.toDto(genre)).thenReturn(genreResponseDto);

        val result = genreService.createGenre(genreRequestDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(genreResponseDto.getId());
        assertThat(result.getName()).isEqualTo(genreResponseDto.getName());

        verify(genreRepository).save(genre);
    }

    @Test
    void getGenreById_WithValidId_ShouldReturnGenreResponseDto() {
        when(genreRepository.findById(VALID_ID)).thenReturn(Optional.of(genre));
        when(genreMapper.toDto(genre)).thenReturn(genreResponseDto);

        val result = genreService.getGenreById(VALID_ID);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(genreResponseDto.getId());
        assertThat(result.getName()).isEqualTo(genreResponseDto.getName());

        verify(genreRepository).findById(VALID_ID);
    }

    @Test
    void getGenreById_WithInvalidId_ShouldThrowEntityNotFoundException() {
        when(genreRepository.findById(INVALID_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> genreService.getGenreById(INVALID_ID))
                .isExactlyInstanceOf(NotFoundException.class);

        verify(genreRepository).findById(INVALID_ID);
    }

    @Test
    void getAllGenres_ShouldReturnListOfGenreResponseDto() {
        when(genreRepository.findAll()).thenReturn(List.of(genre));
        when(genreMapper.toDto(genre)).thenReturn(genreResponseDto);

        val result = genreService.getAllGenres();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(genreResponseDto.getId());

        verify(genreRepository).findAll();
    }

    @Test
    void updateGenre_WithValidId_ShouldReturnUpdatedGenreResponseDto() {
        val updateRequestDto = GenreRequestDto.builder()
                .name("Updated Genre")
                .build();

        val updatedGenre = Genre.builder()
                .id(genre.getId())
                .name(updateRequestDto.getName())
                .createAt(genre.getCreateAt())
                .updateAt(LocalDateTime.now())
                .build();

        val updatedResponseDto = GenreResponseDto.builder()
                .id(updatedGenre.getId())
                .name(updatedGenre.getName())
                .createAt(updatedGenre.getCreateAt())
                .updateAt(updatedGenre.getUpdateAt())
                .build();

        when(genreRepository.findById(genre.getId())).thenReturn(Optional.of(genre));
        doNothing().when(genreMapper).partialUpdate(updateRequestDto, genre);
        when(genreRepository.save(genre)).thenReturn(updatedGenre);
        when(genreMapper.toDto(updatedGenre)).thenReturn(updatedResponseDto);

        val result = genreService.updateGenre(genre.getId(), updateRequestDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(genre.getId());
        assertThat(result.getName()).isEqualTo(updatedGenre.getName());

        verify(genreMapper).partialUpdate(updateRequestDto, genre);
        verify(genreRepository).save(genre);
    }

    @Test
    void updateGenre_WithInvalidId_ShouldThrowEntityNotFoundException() {
        when(genreRepository.findById(INVALID_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> genreService.updateGenre(INVALID_ID, genreRequestDto))
                .isExactlyInstanceOf(NotFoundException.class);

        verify(genreRepository).findById(INVALID_ID);
        verify(genreRepository, never()).save(any(Genre.class));
    }

    @Test
    void deleteGenre_WithValidId_ShouldDeleteGenre() {
        when(genreRepository.existsById(VALID_ID)).thenReturn(true);
        doNothing().when(genreRepository).deleteById(VALID_ID);

        genreService.deleteGenre(VALID_ID);

        verify(genreRepository).deleteById(VALID_ID);
    }

    @Test
    void deleteGenre_WithInvalidId_ShouldThrowEntityNotFoundException() {
        when(genreRepository.existsById(INVALID_ID)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> genreService.deleteGenre(INVALID_ID))
                .isExactlyInstanceOf(NotFoundException.class);

        verify(genreRepository, never()).deleteById(INVALID_ID);
    }
}
