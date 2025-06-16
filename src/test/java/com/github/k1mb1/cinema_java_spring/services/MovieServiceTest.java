package com.github.k1mb1.cinema_java_spring.services;

import com.github.k1mb1.cinema_java_spring.errors.NotFoundException;
import com.github.k1mb1.cinema_java_spring.mappers.MovieMapper;
import com.github.k1mb1.cinema_java_spring.models.country.CountryEntity;
import com.github.k1mb1.cinema_java_spring.models.genre.GenreEntity;
import com.github.k1mb1.cinema_java_spring.models.movie.MovieEntity;
import com.github.k1mb1.cinema_java_spring.models.movie.MovieFilter;
import com.github.k1mb1.cinema_java_spring.models.movie.MovieRequestDto;
import com.github.k1mb1.cinema_java_spring.models.movie.MovieResponseDto;
import com.github.k1mb1.cinema_java_spring.repositories.MovieRepository;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.github.k1mb1.cinema_java_spring.errors.ErrorMessages.MOVIE_NOT_FOUND;
import static com.github.k1mb1.cinema_java_spring.utils.UnitTestUtils.assertExceptionWithMessage;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private GenreService genreService;

    @Mock
    private CountryService countryService;

    @Mock
    private MovieMapper movieMapper;

    @InjectMocks
    private MovieService movieService;

    private MovieEntity movieEntity;
    private MovieRequestDto movieRequestDto;
    private MovieResponseDto movieResponseDto;

    private static final int VALID_ID = 1;
    private static final int INVALID_ID = 99;

    @BeforeEach
    void setUp() {
        movieRequestDto = MovieRequestDto.builder()
                .title("Test Movie")
                .description("Test Description")
                .year(2023)
                .releaseDate(LocalDate.of(2023, 1, 1))
                .ageRating("18+")
                .budget(1000000L)
                .worldGross(5000000L)
                .durationMinutes(120)
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
                .worldGross(movieEntity.getWorldGross())
                .durationMinutes(movieEntity.getDurationMinutes())
                .createAt(movieEntity.getCreateAt())
                .updateAt(movieEntity.getUpdateAt())
                .build();
    }

    // СОЗДАНИЕ ФИЛЬМА
    @Test
    void createMovie_ShouldReturnMovieResponseDto() {
        when(movieMapper.toEntity(movieRequestDto)).thenReturn(movieEntity);
        when(movieRepository.save(any(MovieEntity.class))).thenReturn(movieEntity);
        when(movieMapper.toDto(movieEntity)).thenReturn(movieResponseDto);

        val result = movieService.createMovie(movieRequestDto);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(movieResponseDto);

        verify(movieMapper).toEntity(movieRequestDto);
        verify(movieRepository).save(movieEntity);
        verify(movieMapper).toDto(movieEntity);
        verifyNoMoreInteractions(genreService, countryService);
    }

    @Test
    void createMovie_WithGenresAndCountries_ShouldSetRelationships() {
        Set<Integer> genreIds = Set.of(1, 2);
        Set<Integer> countryIds = Set.of(3, 4);

        val requestWithRelations = MovieRequestDto.builder()
                .title("Movie with relations")
                .genreIds(genreIds)
                .countryIds(countryIds)
                .build();

        when(movieMapper.toEntity(requestWithRelations)).thenReturn(movieEntity);
        when(genreService.findGenresByIds(genreIds)).thenReturn(List.of(new GenreEntity(), new GenreEntity()));
        when(countryService.findCountriesByIds(countryIds)).thenReturn(List.of(new CountryEntity(), new CountryEntity()));
        when(movieRepository.save(any(MovieEntity.class))).thenReturn(movieEntity);
        when(movieMapper.toDto(movieEntity)).thenReturn(movieResponseDto);

        movieService.createMovie(requestWithRelations);

        verify(genreService).findGenresByIds(genreIds);
        verify(countryService).findCountriesByIds(countryIds);
        verify(movieRepository).save(movieEntity);
    }

    @Test
    void getMovieById_WithValidId_ShouldReturnMovieResponseDto() {
        when(movieRepository.findWithRelationshipsById(VALID_ID)).thenReturn(Optional.of(movieEntity));
        when(movieMapper.toDto(movieEntity)).thenReturn(movieResponseDto);

        val result = movieService.getMovieById(VALID_ID);

        assertThat(result).isEqualTo(movieResponseDto);
        verify(movieRepository).findWithRelationshipsById(VALID_ID);
    }

    @Test
    void getMovieById_WithInvalidId_ShouldThrowNotFoundException() {
        when(movieRepository.findWithRelationshipsById(INVALID_ID)).thenReturn(Optional.empty());

        assertExceptionWithMessage(
                () -> movieService.getMovieById(INVALID_ID),
                NotFoundException.class,
                MOVIE_NOT_FOUND, INVALID_ID
        );

        verify(movieRepository).findWithRelationshipsById(INVALID_ID);
    }

    @Test
    void getAllMovies_ShouldReturnPageOfMovieResponseDto() {
        Page<MovieEntity> moviePage = new PageImpl<>(List.of(movieEntity));
        MovieFilter filter = mock(MovieFilter.class);
        Pageable pageable = PageRequest.of(0, 10);
        Specification<MovieEntity> specification = mock(Specification.class);

        when(filter.toSpecification()).thenReturn(specification);
        when(movieRepository.findAll(specification, pageable)).thenReturn(moviePage);
        when(movieMapper.toDto(movieEntity)).thenReturn(movieResponseDto);

        Page<MovieResponseDto> result = movieService.getAllMovies(filter, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst()).isEqualTo(movieResponseDto);

        verify(filter).toSpecification();
        verify(movieRepository).findAll(specification, pageable);
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
                .createAt(movieEntity.getCreateAt())
                .updateAt(LocalDateTime.now())
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

        when(movieRepository.findWithRelationshipsById(VALID_ID)).thenReturn(Optional.of(movieEntity));
        when(genreService.findGenresByIds(updateRequestDto.getGenreIds())).thenReturn(List.of(new GenreEntity()));
        when(countryService.findCountriesByIds(updateRequestDto.getCountryIds())).thenReturn(List.of(new CountryEntity()));
        when(movieRepository.save(movieEntity)).thenReturn(updatedMovie);
        when(movieMapper.toDto(updatedMovie)).thenReturn(updatedResponseDto);

        val result = movieService.updateMovie(VALID_ID, updateRequestDto);

        assertThat(result).isEqualTo(updatedResponseDto);

        verify(movieRepository).findWithRelationshipsById(VALID_ID);
        verify(movieMapper).partialUpdate(updateRequestDto, movieEntity);
        verify(genreService).findGenresByIds(updateRequestDto.getGenreIds());
        verify(countryService).findCountriesByIds(updateRequestDto.getCountryIds());
        verify(movieRepository).save(movieEntity);
    }

    @Test
    void updateMovie_WithInvalidId_ShouldThrowNotFoundException() {
        when(movieRepository.findWithRelationshipsById(INVALID_ID)).thenReturn(Optional.empty());

        assertExceptionWithMessage(
                () -> movieService.updateMovie(INVALID_ID, movieRequestDto),
                NotFoundException.class,
                MOVIE_NOT_FOUND, INVALID_ID
        );

        verify(movieRepository).findWithRelationshipsById(INVALID_ID);
        verifyNoMoreInteractions(movieMapper, movieRepository);
    }

    @Test
    void deleteMovie_WithValidId_ShouldDeleteMovie() {
        when(movieRepository.existsById(VALID_ID)).thenReturn(true);
        doNothing().when(movieRepository).deleteById(VALID_ID);

        movieService.deleteMovie(VALID_ID);

        verify(movieRepository).existsById(VALID_ID);
        verify(movieRepository).deleteById(VALID_ID);
    }

    @Test
    void deleteMovie_WithInvalidId_ShouldThrowNotFoundException() {
        when(movieRepository.existsById(INVALID_ID)).thenReturn(false);

        assertExceptionWithMessage(
                () -> movieService.deleteMovie(INVALID_ID),
                NotFoundException.class,
                MOVIE_NOT_FOUND, INVALID_ID
        );

        verify(movieRepository).existsById(INVALID_ID);
        verify(movieRepository, never()).deleteById(INVALID_ID);
    }

    @Test
    void findMovieById_WithValidId_ShouldReturnMovieEntity() {
        when(movieRepository.findWithRelationshipsById(VALID_ID)).thenReturn(Optional.of(movieEntity));

        val result = movieService.findMovieById(VALID_ID);

        assertThat(result).isEqualTo(movieEntity);
        verify(movieRepository).findWithRelationshipsById(VALID_ID);
    }

    @Test
    void findMovieById_WithInvalidId_ShouldThrowNotFoundException() {
        when(movieRepository.findWithRelationshipsById(INVALID_ID)).thenReturn(Optional.empty());

        assertExceptionWithMessage(
                () -> movieService.findMovieById(INVALID_ID),
                NotFoundException.class,
                MOVIE_NOT_FOUND, INVALID_ID
        );

        verify(movieRepository).findWithRelationshipsById(INVALID_ID);
    }
}