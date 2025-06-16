package com.github.k1mb1.cinema_java_spring.services;

import com.github.k1mb1.cinema_java_spring.errors.NotFoundException;
import com.github.k1mb1.cinema_java_spring.mappers.MovieMapper;
import com.github.k1mb1.cinema_java_spring.models.country.CountryEntity;
import com.github.k1mb1.cinema_java_spring.models.genre.GenreEntity;
import com.github.k1mb1.cinema_java_spring.models.movie.MovieEntity;
import com.github.k1mb1.cinema_java_spring.models.movie.MovieRequestDto;
import com.github.k1mb1.cinema_java_spring.models.movie.MovieResponseDto;
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

    MovieEntity movieEntity;
    MovieRequestDto movieRequestDto;
    MovieResponseDto movieResponseDto;
    GenreEntity genreEntity;
    CountryEntity countryEntity;

    static final int VALID_ID = 1;
    static final int INVALID_ID = 99;

    @BeforeEach
    void setUp() {
        genreEntity = GenreEntity.builder()
                .id(VALID_ID)
                .name("Action")
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();

        countryEntity = CountryEntity.builder()
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
                .genreIds(Set.of(genreEntity.getId()))
                .countryIds(Set.of(countryEntity.getId()))
                .build();

        movieEntity = MovieEntity.builder()
                .id(VALID_ID)
                .title(movieRequestDto.getTitle())
                .description(movieRequestDto.getDescription())
                .year(movieRequestDto.getYear())
                .releaseDate(movieRequestDto.getReleaseDate())
                .ageRating(movieRequestDto.getAgeRating())
                .budget(movieRequestDto.getBudget())
                .worldGross(movieRequestDto.getWorldGross())
                .durationMinutes(movieRequestDto.getDurationMinutes())
                .genres(Set.of(genreEntity))
                .countries(Set.of(countryEntity))
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();

        movieResponseDto = MovieResponseDto.builder()
                .id(movieEntity.getId())
                .title(movieEntity.getTitle())
                .description(movieEntity.getDescription())
                .year(movieEntity.getYear())
                .releaseDate(movieEntity.getReleaseDate())
                .ageRating(movieEntity.getAgeRating())
                .budget(movieEntity.getBudget())
                .worldGross(movieEntity.getBudget())
                .durationMinutes(movieEntity.getDurationMinutes())
                .createAt(movieEntity.getCreateAt())
                .updateAt(movieEntity.getUpdateAt())
                .build();
    }

    @Test
    void createMovie_ShouldReturnMovieResponseDto() {
        when(movieMapper.toEntity(movieRequestDto)).thenReturn(movieEntity);
        when(genreRepository.findAllById(any())).thenReturn(List.of(genreEntity));
        when(countryRepository.findAllById(any())).thenReturn(List.of(countryEntity));
        when(movieRepository.save(any(MovieEntity.class))).thenReturn(movieEntity);
        when(movieMapper.toDto(movieEntity)).thenReturn(movieResponseDto);

        val result = movieService.createMovie(movieRequestDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(movieResponseDto.getId());
        assertThat(result.getTitle()).isEqualTo(movieResponseDto.getTitle());

        verify(movieRepository).save(any(MovieEntity.class));
    }

    @Test
    void getMovieById_WithValidId_ShouldReturnMovieResponseDto() {
        when(movieRepository.findById(VALID_ID)).thenReturn(Optional.of(movieEntity));
        when(movieMapper.toDto(movieEntity)).thenReturn(movieResponseDto);

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
        when(movieRepository.findAll()).thenReturn(List.of(movieEntity));
        when(movieMapper.toDto(movieEntity)).thenReturn(movieResponseDto);

//        val result = movieService.getAllMovies();
//
//        assertThat(result).isNotNull();
//        assertThat(result).hasSize(1);
//        assertThat(result.getFirst().getId()).isEqualTo(movieResponseDto.getId());
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

        val updatedMovie = MovieEntity.builder()
                .id(movieEntity.getId())
                .title(updateRequestDto.getTitle())
                .description(updateRequestDto.getDescription())
                .year(updateRequestDto.getYear())
                .releaseDate(updateRequestDto.getReleaseDate())
                .genres(Set.of(genreEntity))
                .countries(Set.of(countryEntity))
                .createAt(movieEntity.getCreateAt())
                .updateAt(LocalDateTime.now())
                .watchedMovies(movieEntity.getWatchedMovies())
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

        when(movieRepository.findById(movieEntity.getId())).thenReturn(Optional.of(movieEntity));
        doNothing().when(movieMapper).partialUpdate(updateRequestDto, movieEntity);
        when(genreRepository.findAllById(any())).thenReturn(List.of(genreEntity));
        when(countryRepository.findAllById(any())).thenReturn(List.of(countryEntity));
        when(movieRepository.save(movieEntity)).thenReturn(updatedMovie);
        when(movieMapper.toDto(updatedMovie)).thenReturn(updatedResponseDto);

        val result = movieService.updateMovie(movieEntity.getId(), updateRequestDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(movieEntity.getId());
        assertThat(result.getTitle()).isEqualTo(updatedResponseDto.getTitle());

        verify(movieMapper).partialUpdate(updateRequestDto, movieEntity);
        verify(movieRepository).save(movieEntity);
    }

    @Test
    void updateMovie_WithInvalidId_ShouldThrowEntityNotFoundException() {
        when(movieRepository.findById(INVALID_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> movieService.updateMovie(INVALID_ID, movieRequestDto))
                .isExactlyInstanceOf(NotFoundException.class);

        verify(movieRepository).findById(INVALID_ID);
        verify(movieRepository, never()).save(any(MovieEntity.class));
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
