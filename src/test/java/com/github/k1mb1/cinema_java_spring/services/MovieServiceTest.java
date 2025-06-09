package com.github.k1mb1.cinema_java_spring.services;

import com.github.k1mb1.cinema_java_spring.config.NotFoundException;
import com.github.k1mb1.cinema_java_spring.dtos.movie.MovieRequestDto;
import com.github.k1mb1.cinema_java_spring.dtos.movie.MovieResponseDto;
import com.github.k1mb1.cinema_java_spring.entities.Country;
import com.github.k1mb1.cinema_java_spring.entities.Genre;
import com.github.k1mb1.cinema_java_spring.entities.Movie;
import com.github.k1mb1.cinema_java_spring.mappers.MovieMapper;
import com.github.k1mb1.cinema_java_spring.repositories.CountryRepository;
import com.github.k1mb1.cinema_java_spring.repositories.GenreRepository;
import com.github.k1mb1.cinema_java_spring.repositories.MovieRepository;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    MovieRepository movieRepository;

    @Mock
    GenreRepository genreRepository;

    @Mock
    CountryRepository countryRepository;

    @Mock
    MovieMapper movieMapper;

    @InjectMocks
    MovieService movieService;

    Movie movie;
    MovieRequestDto movieRequestDto;
    MovieResponseDto movieResponseDto;
    Genre genre;
    Country country;

    @BeforeEach
    void setUp() {
        // Setup test data
        genre = Genre.builder()
                .id(1)
                .name("Action")
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();

        country = Country.builder()
                .id(1)
                .name("USA")
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();

        movieRequestDto = MovieRequestDto.builder()
                .title("Test Movie")
                .description("Test Description")
                .year(2023)
                .releaseDate(LocalDate.of(2023, 1, 1))
                .ageRating("PG-13")
                .budget(1000000L)
                .worldGross(5000000L)
                .durationMinutes(120)
                .genreIds(Set.of(1))
                .countryIds(Set.of(1))
                .build();

        movie = Movie.builder()
                .id(1)
                .title("Test Movie")
                .description("Test Description")
                .year(2023)
                .releaseDate(LocalDate.of(2023, 1, 1))
                .ageRating("PG-13")
                .budget(1000000L)
                .worldGross(5000000L)
                .durationMinutes(120)
                .genres(new HashSet<>(Set.of(genre)))
                .countries(new HashSet<>(Set.of(country)))
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .watchedMovies(new HashSet<>())
                .build();

        movieResponseDto = MovieResponseDto.builder()
                .id(1)
                .title("Test Movie")
                .description("Test Description")
                .year(2023)
                .releaseDate(LocalDate.of(2023, 1, 1))
                .ageRating("PG-13")
                .budget(1000000L)
                .worldGross(5000000L)
                .durationMinutes(120)
                .createAt(movie.getCreateAt())
                .updateAt(movie.getUpdateAt())
                .build();
    }

    @Test
    void createMovie_ShouldReturnMovieResponseDto() {
        // Arrange
        when(movieMapper.toEntity(movieRequestDto)).thenReturn(movie);
        when(genreRepository.findAllById(any())).thenReturn(List.of(genre));
        when(countryRepository.findAllById(any())).thenReturn(List.of(country));
        when(movieRepository.save(any(Movie.class))).thenReturn(movie);
        when(movieMapper.toDto(movie)).thenReturn(movieResponseDto);

        // Act
        val result = movieService.createMovie(movieRequestDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(movieResponseDto.getId());
        assertThat(result.getTitle()).isEqualTo(movieResponseDto.getTitle());
        verify(movieRepository).save(any(Movie.class));
    }

    @Test
    void getMovieById_WithValidId_ShouldReturnMovieResponseDto() {
        // Arrange
        when(movieRepository.findById(1)).thenReturn(Optional.of(movie));
        when(movieMapper.toDto(movie)).thenReturn(movieResponseDto);

        // Act
        val result = movieService.getMovieById(1);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(movieResponseDto.getId());
        assertThat(result.getTitle()).isEqualTo(movieResponseDto.getTitle());
        verify(movieRepository).findById(1);
    }

    @Test
    void getMovieById_WithInvalidId_ShouldThrowEntityNotFoundException() {
        // Arrange
        when(movieRepository.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> movieService.getMovieById(99))
                .isExactlyInstanceOf(NotFoundException.class);
        verify(movieRepository).findById(99);
    }

    @Test
    void getAllMovies_ShouldReturnListOfMovieResponseDto() {
        // Arrange
        when(movieRepository.findAll()).thenReturn(List.of(movie));
        when(movieMapper.toDto(movie)).thenReturn(movieResponseDto);

        // Act
        val result = movieService.getAllMovies();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(movieResponseDto.getId());
        verify(movieRepository).findAll();
    }

    @Test
    void updateMovie_WithValidId_ShouldReturnUpdatedMovieResponseDto() {
        // Arrange
        val updateRequestDto = MovieRequestDto.builder()
                .title("Updated Movie")
                .description("Updated Description")
                .year(2024)
                .releaseDate(LocalDate.of(2024, 1, 1))
                .genreIds(Set.of(1))
                .countryIds(Set.of(1))
                .build();

        val updatedMovie = Movie.builder()
                .id(1)
                .title("Updated Movie")
                .description("Updated Description")
                .year(2024)
                .releaseDate(LocalDate.of(2024, 1, 1))
                .genres(new HashSet<>(Set.of(genre)))
                .countries(new HashSet<>(Set.of(country)))
                .createAt(movie.getCreateAt())
                .updateAt(LocalDateTime.now())
                .watchedMovies(movie.getWatchedMovies())
                .build();

        val updatedResponseDto = MovieResponseDto.builder()
                .id(1)
                .title("Updated Movie")
                .description("Updated Description")
                .year(2024)
                .releaseDate(LocalDate.of(2024, 1, 1))
                .createAt(updatedMovie.getCreateAt())
                .updateAt(updatedMovie.getUpdateAt())
                .build();

        when(movieRepository.findById(1)).thenReturn(Optional.of(movie));
        when(movieMapper.toEntity(updateRequestDto)).thenReturn(updatedMovie);
        when(genreRepository.findAllById(any())).thenReturn(List.of(genre));
        when(countryRepository.findAllById(any())).thenReturn(List.of(country));
        when(movieRepository.save(any(Movie.class))).thenReturn(updatedMovie);
        when(movieMapper.toDto(updatedMovie)).thenReturn(updatedResponseDto);

        // Act
        val result = movieService.updateMovie(1, updateRequestDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(updatedResponseDto.getId());
        assertThat(result.getTitle()).isEqualTo("Updated Movie");
        verify(movieRepository).save(any(Movie.class));
    }

    @Test
    void updateMovie_WithInvalidId_ShouldThrowEntityNotFoundException() {
        // Arrange
        when(movieRepository.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> movieService.updateMovie(99, movieRequestDto))
                .isExactlyInstanceOf(NotFoundException.class);
        verify(movieRepository).findById(99);
        verify(movieRepository, never()).save(any(Movie.class));
    }

    @Test
    void deleteMovie_WithValidId_ShouldDeleteMovie() {
        // Arrange
        when(movieRepository.existsById(1)).thenReturn(true);
        doNothing().when(movieRepository).deleteById(1);

        // Act
        movieService.deleteMovie(1);

        // Assert
        verify(movieRepository).deleteById(1);
    }

    @Test
    void deleteMovie_WithInvalidId_ShouldThrowEntityNotFoundException() {
        // Arrange
        when(movieRepository.existsById(99)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> movieService.deleteMovie(99))
                .isExactlyInstanceOf(NotFoundException.class);
        verify(movieRepository, never()).deleteById(99);
    }
}
