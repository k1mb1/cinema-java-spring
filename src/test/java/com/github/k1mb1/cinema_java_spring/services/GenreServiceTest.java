package com.github.k1mb1.cinema_java_spring.services;

import com.github.k1mb1.cinema_java_spring.config.NotFoundException;
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
import java.util.HashSet;
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

    @BeforeEach
    void setUp() {
        // Setup test data
        genreRequestDto = GenreRequestDto.builder()
                .name("Action")
                .build();

        genre = Genre.builder()
                .id(1)
                .name("Action")
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .movies(new HashSet<>())
                .build();

        genreResponseDto = GenreResponseDto.builder()
                .id(1)
                .name("Action")
                .createAt(genre.getCreateAt())
                .updateAt(genre.getUpdateAt())
                .build();
    }

    @Test
    void createGenre_ShouldReturnGenreResponseDto() {
        // Arrange
        when(genreMapper.toEntity(genreRequestDto)).thenReturn(genre);
        when(genreRepository.save(genre)).thenReturn(genre);
        when(genreMapper.toDto(genre)).thenReturn(genreResponseDto);

        // Act
        val result = genreService.createGenre(genreRequestDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(genreResponseDto.getId());
        assertThat(result.getName()).isEqualTo(genreResponseDto.getName());
        verify(genreRepository).save(genre);
    }

    @Test
    void getGenreById_WithValidId_ShouldReturnGenreResponseDto() {
        // Arrange
        when(genreRepository.findById(1)).thenReturn(Optional.of(genre));
        when(genreMapper.toDto(genre)).thenReturn(genreResponseDto);

        // Act
        val result = genreService.getGenreById(1);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(genreResponseDto.getId());
        assertThat(result.getName()).isEqualTo(genreResponseDto.getName());
        verify(genreRepository).findById(1);
    }

    @Test
    void getGenreById_WithInvalidId_ShouldThrowEntityNotFoundException() {
        // Arrange
        when(genreRepository.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> genreService.getGenreById(99))
                .isExactlyInstanceOf(NotFoundException.class);
        verify(genreRepository).findById(99);
    }

    @Test
    void getAllGenres_ShouldReturnListOfGenreResponseDto() {
        // Arrange
        when(genreRepository.findAll()).thenReturn(List.of(genre));
        when(genreMapper.toDto(genre)).thenReturn(genreResponseDto);

        // Act
        val result = genreService.getAllGenres();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(genreResponseDto.getId());
        verify(genreRepository).findAll();
    }

    @Test
    void updateGenre_WithValidId_ShouldReturnUpdatedGenreResponseDto() {
        // Arrange
        val updateRequestDto = GenreRequestDto.builder()
                .name("Updated Genre")
                .build();

        val updatedGenre = Genre.builder()
                .id(1)
                .name("Updated Genre")
                .createAt(genre.getCreateAt())
                .updateAt(LocalDateTime.now())
                .movies(genre.getMovies())
                .build();

        val updatedResponseDto = GenreResponseDto.builder()
                .id(1)
                .name("Updated Genre")
                .createAt(updatedGenre.getCreateAt())
                .updateAt(updatedGenre.getUpdateAt())
                .build();

        when(genreRepository.findById(1)).thenReturn(Optional.of(genre));
        when(genreMapper.toEntity(updateRequestDto)).thenReturn(updatedGenre);
        when(genreRepository.save(any(Genre.class))).thenReturn(updatedGenre);
        when(genreMapper.toDto(updatedGenre)).thenReturn(updatedResponseDto);

        // Act
        val result = genreService.updateGenre(1, updateRequestDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(updatedResponseDto.getId());
        assertThat(result.getName()).isEqualTo("Updated Genre");
        verify(genreRepository).save(any(Genre.class));
    }

    @Test
    void updateGenre_WithInvalidId_ShouldThrowEntityNotFoundException() {
        // Arrange
        when(genreRepository.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> genreService.updateGenre(99, genreRequestDto))
                .isExactlyInstanceOf(NotFoundException.class);
        verify(genreRepository).findById(99);
        verify(genreRepository, never()).save(any(Genre.class));
    }

    @Test
    void deleteGenre_WithValidId_ShouldDeleteGenre() {
        // Arrange
        when(genreRepository.existsById(1)).thenReturn(true);
        doNothing().when(genreRepository).deleteById(1);

        // Act
        genreService.deleteGenre(1);

        // Assert
        verify(genreRepository).deleteById(1);
    }

    @Test
    void deleteGenre_WithInvalidId_ShouldThrowEntityNotFoundException() {
        // Arrange
        when(genreRepository.existsById(99)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> genreService.deleteGenre(99))
                .isExactlyInstanceOf(NotFoundException.class);
        verify(genreRepository, never()).deleteById(99);
    }
}
