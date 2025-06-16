package com.github.k1mb1.cinema_java_spring.services;

import com.github.k1mb1.cinema_java_spring.errors.NotFoundException;
import com.github.k1mb1.cinema_java_spring.mappers.MovieMapper;
import com.github.k1mb1.cinema_java_spring.models.movie.MovieEntity;
import com.github.k1mb1.cinema_java_spring.models.movie.MovieFilter;
import com.github.k1mb1.cinema_java_spring.models.movie.MovieRequestDto;
import com.github.k1mb1.cinema_java_spring.models.movie.MovieResponseDto;
import com.github.k1mb1.cinema_java_spring.repositories.MovieRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

import static com.github.k1mb1.cinema_java_spring.errors.ErrorMessages.MOVIE_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
@FieldDefaults(makeFinal = true)
public class MovieService {

    MovieRepository movieRepository;
    MovieMapper movieMapper;

    GenreService genreService;
    CountryService countryService;

    public MovieResponseDto createMovie(@NonNull MovieRequestDto movieRequestDto) {
        val movie = movieMapper.toEntity(movieRequestDto);

        applyRelationships(movie, movieRequestDto);

        return movieMapper.toDto(movieRepository.save(movie));
    }

    @Transactional(readOnly = true)
    public MovieResponseDto getMovieById(@NonNull Integer id) {
        return movieMapper.toDto(findMovieById(id));
    }

    @Transactional(readOnly = true)
    public Page<MovieResponseDto> getAllMovies(
            MovieFilter filter,
            @NonNull Pageable pageable
    ) {
        return movieRepository.findAll(filter.toSpecification(), pageable)
                .map(movieMapper::toDto);
    }

    public MovieResponseDto updateMovie(
            @NonNull Integer id,
            @NonNull MovieRequestDto movieRequestDto
    ) {
        val existingMovie = findMovieById(id);

        movieMapper.partialUpdate(movieRequestDto, existingMovie);
        applyRelationships(existingMovie, movieRequestDto);

        return movieMapper.toDto(movieRepository.save(existingMovie));
    }

    public void deleteMovie(@NonNull Integer id) {
        if (!movieRepository.existsById(id)) {
            throw new NotFoundException(MOVIE_NOT_FOUND.formatted(id));
        }
        movieRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public MovieEntity findMovieById(@NonNull Integer id) {
        return movieRepository.findWithRelationshipsById(id)
                .orElseThrow(() -> new NotFoundException(MOVIE_NOT_FOUND.formatted(id)));
    }

    private void applyRelationships(@NonNull MovieEntity movie, @NonNull MovieRequestDto movieRequestDto) {
        applyGenres(movie, movieRequestDto.getGenreIds());
        applyCountries(movie, movieRequestDto.getCountryIds());
    }

    private void applyGenres(@NonNull MovieEntity movie, @NonNull Set<Integer> genreIds) {
        if (!genreIds.isEmpty()) {
            movie.setGenres(new HashSet<>(genreService.findGenresByIds(genreIds)));
        }
    }

    private void applyCountries(@NonNull MovieEntity movie, @NonNull Set<Integer> countryIds) {
        if (!countryIds.isEmpty()) {
            movie.setCountries(new HashSet<>(countryService.findCountriesByIds(countryIds)));
        }
    }
}
