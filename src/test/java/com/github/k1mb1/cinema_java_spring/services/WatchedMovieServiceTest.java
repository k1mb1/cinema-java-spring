package com.github.k1mb1.cinema_java_spring.services;

import com.github.k1mb1.cinema_java_spring.config.NotFoundException;
import com.github.k1mb1.cinema_java_spring.dtos.watchedmovie.WatchedMovieRequestDto;
import com.github.k1mb1.cinema_java_spring.dtos.watchedmovie.WatchedMovieResponseDto;
import com.github.k1mb1.cinema_java_spring.entities.Movie;
import com.github.k1mb1.cinema_java_spring.entities.User;
import com.github.k1mb1.cinema_java_spring.entities.WatchedMovie;
import com.github.k1mb1.cinema_java_spring.mappers.WatchedMovieMapper;
import com.github.k1mb1.cinema_java_spring.repositories.MovieRepository;
import com.github.k1mb1.cinema_java_spring.repositories.UserRepository;
import com.github.k1mb1.cinema_java_spring.repositories.WatchedMovieRepository;
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
class WatchedMovieServiceTest {

    @Mock
    WatchedMovieRepository watchedMovieRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    MovieRepository movieRepository;

    @Mock
    WatchedMovieMapper watchedMovieMapper;

    @InjectMocks
    WatchedMovieService watchedMovieService;

    User user;
    Movie movie;
    WatchedMovie watchedMovie;
    WatchedMovieRequestDto watchedMovieRequestDto;
    WatchedMovieResponseDto watchedMovieResponseDto;

    @BeforeEach
    void setUp() {
        // Setup test data
        user = User.builder()
                .id(1)
                .username("testUser")
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();

        movie = Movie.builder()
                .id(1)
                .title("Test Movie")
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();

        watchedMovieRequestDto = WatchedMovieRequestDto.builder()
                .userId(1)
                .movieId(1)
                .build();

        watchedMovie = WatchedMovie.builder()
                .id(1)
                .user(user)
                .movie(movie)
                .watchedAt(LocalDateTime.now())
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();

        watchedMovieResponseDto = WatchedMovieResponseDto.builder()
                .id(1)
                .userId(1)
                .movieId(1)
                .watchedAt(watchedMovie.getWatchedAt())
                .createAt(watchedMovie.getCreateAt())
                .updateAt(watchedMovie.getUpdateAt())
                .build();
    }

    @Test
    void markMovieAsWatched_ShouldReturnWatchedMovieResponseDto() {
        // Arrange
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(movieRepository.findById(1)).thenReturn(Optional.of(movie));
        when(watchedMovieMapper.toEntity(watchedMovieRequestDto)).thenReturn(watchedMovie);
        when(watchedMovieRepository.save(any(WatchedMovie.class))).thenReturn(watchedMovie);
        when(watchedMovieMapper.toDto(watchedMovie)).thenReturn(watchedMovieResponseDto);

        // Act
        val result = watchedMovieService.markMovieAsWatched(watchedMovieRequestDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(watchedMovieResponseDto.getId());
        assertThat(result.getUserId()).isEqualTo(watchedMovieResponseDto.getUserId());
        assertThat(result.getMovieId()).isEqualTo(watchedMovieResponseDto.getMovieId());
        verify(watchedMovieRepository).save(any(WatchedMovie.class));
    }

    @Test
    void markMovieAsWatched_WithInvalidUserId_ShouldThrowEntityNotFoundException() {
        // Arrange
        when(userRepository.findById(99)).thenReturn(Optional.empty());
        watchedMovieRequestDto.setUserId(99);

        // Act & Assert
        assertThatThrownBy(() -> watchedMovieService.markMovieAsWatched(watchedMovieRequestDto))
                .isExactlyInstanceOf(NotFoundException.class);
        verify(userRepository).findById(99);
        verify(watchedMovieRepository, never()).save(any(WatchedMovie.class));
    }

    @Test
    void markMovieAsWatched_WithInvalidMovieId_ShouldThrowEntityNotFoundException() {
        // Arrange
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(movieRepository.findById(99)).thenReturn(Optional.empty());
        watchedMovieRequestDto.setMovieId(99);

        // Act & Assert
        assertThatThrownBy(() -> watchedMovieService.markMovieAsWatched(watchedMovieRequestDto))
                .isExactlyInstanceOf(NotFoundException.class);
        verify(movieRepository).findById(99);
        verify(watchedMovieRepository, never()).save(any(WatchedMovie.class));
    }

    @Test
    void getWatchedMovieById_WithValidId_ShouldReturnWatchedMovieResponseDto() {
        // Arrange
        when(watchedMovieRepository.findById(1)).thenReturn(Optional.of(watchedMovie));
        when(watchedMovieMapper.toDto(watchedMovie)).thenReturn(watchedMovieResponseDto);

        // Act
        val result = watchedMovieService.getWatchedMovieById(1);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(watchedMovieResponseDto.getId());
        assertThat(result.getUserId()).isEqualTo(watchedMovieResponseDto.getUserId());
        assertThat(result.getMovieId()).isEqualTo(watchedMovieResponseDto.getMovieId());
        verify(watchedMovieRepository).findById(1);
    }

    @Test
    void getWatchedMovieById_WithInvalidId_ShouldThrowEntityNotFoundException() {
        // Arrange
        when(watchedMovieRepository.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> watchedMovieService.getWatchedMovieById(99))
                .isExactlyInstanceOf(NotFoundException.class);
        verify(watchedMovieRepository).findById(99);
    }

    @Test
    void getAllWatchedMovies_ShouldReturnListOfWatchedMovieResponseDto() {
        // Arrange
        when(watchedMovieRepository.findAll()).thenReturn(List.of(watchedMovie));
        when(watchedMovieMapper.toDto(watchedMovie)).thenReturn(watchedMovieResponseDto);

        // Act
        val result = watchedMovieService.getAllWatchedMovies();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(watchedMovieResponseDto.getId());
        verify(watchedMovieRepository).findAll();
    }

    @Test
    void deleteWatchedMovie_WithValidId_ShouldDeleteWatchedMovie() {
        // Arrange
        when(watchedMovieRepository.existsById(1)).thenReturn(true);
        doNothing().when(watchedMovieRepository).deleteById(1);

        // Act
        watchedMovieService.deleteWatchedMovie(1);

        // Assert
        verify(watchedMovieRepository).deleteById(1);
    }

    @Test
    void deleteWatchedMovie_WithInvalidId_ShouldThrowEntityNotFoundException() {
        // Arrange
        when(watchedMovieRepository.existsById(99)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> watchedMovieService.deleteWatchedMovie(99))
                .isExactlyInstanceOf(NotFoundException.class);
        verify(watchedMovieRepository, never()).deleteById(99);
    }
}
