package com.github.k1mb1.cinema_java_spring.services;

import com.github.k1mb1.cinema_java_spring.errors.NotFoundException;
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

    static final int VALID_ID = 1;
    static final int INVALID_ID = 99;

    @BeforeEach
    void setUp() {
        genre = Genre.builder()
                .id(VALID_ID)
                .name("Action")
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();

        country = Country.builder()
                .id(VALID_ID)
                .name("USA")
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();

        movieRequestDto = MovieRequestDto.builder()
                .title("Test Movie")
                .description("Test Description")
                .year(2023)
                .releaseDate(LocalDate.of(2023, 1, 1))
                .ageRating("18+")
                .budget(1000000L)
                .worldGross(5000000L)
                .durationMinutes(120)
                .genreIds(Set.of(genre.getId()))
                .countryIds(Set.of(country.getId()))
                .build();

        movie = Movie.builder()
                .id(VALID_ID)
                .title(movieRequestDto.getTitle())
                .description(movieRequestDto.getDescription())
                .year(movieRequestDto.getYear())
                .releaseDate(movieRequestDto.getReleaseDate())
                .ageRating(movieRequestDto.getAgeRating())
                .budget(movieRequestDto.getBudget())
                .worldGross(movieRequestDto.getWorldGross())
                .durationMinutes(movieRequestDto.getDurationMinutes())
                .genres(Set.of(genre))
                .countries(Set.of(country))
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();

        movieResponseDto = MovieResponseDto.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .description(movie.getDescription())
                .year(movie.getYear())
                .releaseDate(movie.getReleaseDate())
                .ageRating(movie.getAgeRating())
                .budget(movie.getBudget())
                .worldGross(movie.getBudget())
                .durationMinutes(movie.getDurationMinutes())
                .createAt(movie.getCreateAt())
                .updateAt(movie.getUpdateAt())
                .build();
    }

    @Test
    void createMovie_ShouldReturnMovieResponseDto() {
        when(movieMapper.toEntity(movieRequestDto)).thenReturn(movie);
        when(genreRepository.findAllById(any())).thenReturn(List.of(genre));
        when(countryRepository.findAllById(any())).thenReturn(List.of(country));
        when(movieRepository.save(any(Movie.class))).thenReturn(movie);
        when(movieMapper.toDto(movie)).thenReturn(movieResponseDto);

        val result = movieService.createMovie(movieRequestDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(movieResponseDto.getId());
        assertThat(result.getTitle()).isEqualTo(movieResponseDto.getTitle());

        verify(movieRepository).save(any(Movie.class));
    }

    @Test
    void getMovieById_WithValidId_ShouldReturnMovieResponseDto() {
        when(movieRepository.findById(VALID_ID)).thenReturn(Optional.of(movie));
        when(movieMapper.toDto(movie)).thenReturn(movieResponseDto);

        val result = movieService.getMovieById(VALID_ID);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(movieResponseDto.getId());
        assertThat(result.getTitle()).isEqualTo(movieResponseDto.getTitle());

        verify(movieRepository).findById(VALID_ID);
    }

    @Test
    void getMovieById_WithInvalidId_ShouldThrowEntityNotFoundException() {
        when(movieRepository.findById(INVALID_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> movieService.getMovieById(INVALID_ID))
                .isExactlyInstanceOf(NotFoundException.class);

        verify(movieRepository).findById(INVALID_ID);
    }

    @Test
    void getAllMovies_ShouldReturnListOfMovieResponseDto() {
        when(movieRepository.findAll()).thenReturn(List.of(movie));
        when(movieMapper.toDto(movie)).thenReturn(movieResponseDto);

        val result = movieService.getAllMovies();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(movieResponseDto.getId());
        verify(movieRepository).findAll();
    }

    @Test
    void updateMovie_WithValidId_ShouldReturnUpdatedMovieResponseDto() {
        val updateRequestDto = MovieRequestDto.builder()
                .title("Updated Movie")
                .description("Updated Description")
                .year(2024)
                .releaseDate(LocalDate.of(2024, 1, 1))
                .genreIds(Set.of(VALID_ID))
                .countryIds(Set.of(VALID_ID))
                .build();

        val updatedMovie = Movie.builder()
                .id(movie.getId())
                .title(updateRequestDto.getTitle())
                .description(updateRequestDto.getDescription())
                .year(updateRequestDto.getYear())
                .releaseDate(updateRequestDto.getReleaseDate())
                .genres(Set.of(genre))
                .countries(Set.of(country))
                .createAt(movie.getCreateAt())
                .updateAt(LocalDateTime.now())
                .watchedMovies(movie.getWatchedMovies())
                .build();

        val updatedResponseDto = MovieResponseDto.builder()
                .id(updatedMovie.getId())
                .title(updatedMovie.getTitle())
                .description(updatedMovie.getDescription())
                .year(updatedMovie.getYear())
                .releaseDate(updatedMovie.getReleaseDate())
                .createAt(updatedMovie.getCreateAt())
                .updateAt(updatedMovie.getUpdateAt())
                .build();

        when(movieRepository.findById(movie.getId())).thenReturn(Optional.of(movie));
        doNothing().when(movieMapper).partialUpdate(updateRequestDto, movie);
        when(genreRepository.findAllById(any())).thenReturn(List.of(genre));
        when(countryRepository.findAllById(any())).thenReturn(List.of(country));
        when(movieRepository.save(movie)).thenReturn(updatedMovie);
        when(movieMapper.toDto(updatedMovie)).thenReturn(updatedResponseDto);

        val result = movieService.updateMovie(movie.getId(), updateRequestDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(movie.getId());
        assertThat(result.getTitle()).isEqualTo(updatedResponseDto.getTitle());

        verify(movieMapper).partialUpdate(updateRequestDto, movie);
        verify(movieRepository).save(movie);
    }

    @Test
    void updateMovie_WithInvalidId_ShouldThrowEntityNotFoundException() {
        when(movieRepository.findById(INVALID_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> movieService.updateMovie(INVALID_ID, movieRequestDto))
                .isExactlyInstanceOf(NotFoundException.class);

        verify(movieRepository).findById(INVALID_ID);
        verify(movieRepository, never()).save(any(Movie.class));
    }

    @Test
    void deleteMovie_WithValidId_ShouldDeleteMovie() {
        when(movieRepository.existsById(VALID_ID)).thenReturn(true);
        doNothing().when(movieRepository).deleteById(VALID_ID);

        movieService.deleteMovie(VALID_ID);

        verify(movieRepository).deleteById(VALID_ID);
    }

    @Test
    void deleteMovie_WithInvalidId_ShouldThrowEntityNotFoundException() {
        when(movieRepository.existsById(INVALID_ID)).thenReturn(false);

        assertThatThrownBy(() -> movieService.deleteMovie(INVALID_ID))
                .isExactlyInstanceOf(NotFoundException.class);

        verify(movieRepository, never()).deleteById(INVALID_ID);
    }
}
