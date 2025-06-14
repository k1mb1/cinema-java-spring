package com.github.k1mb1.cinema_java_spring.services;

import com.github.k1mb1.cinema_java_spring.dtos.movie.MovieRequestDto;
import com.github.k1mb1.cinema_java_spring.dtos.movie.MovieResponseDto;
import com.github.k1mb1.cinema_java_spring.entities.Country;
import com.github.k1mb1.cinema_java_spring.entities.Genre;
import com.github.k1mb1.cinema_java_spring.errors.NotFoundException;
import com.github.k1mb1.cinema_java_spring.mappers.MovieMapper;
import com.github.k1mb1.cinema_java_spring.repositories.CountryRepository;
import com.github.k1mb1.cinema_java_spring.repositories.GenreRepository;
import com.github.k1mb1.cinema_java_spring.repositories.MovieRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.github.k1mb1.cinema_java_spring.errors.ErrorMessages.MOVIE_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
@FieldDefaults(makeFinal = true)
public class MovieService {

    MovieRepository movieRepository;
    GenreRepository genreRepository;
    CountryRepository countryRepository;
    MovieMapper movieMapper;

    public MovieResponseDto createMovie(@NonNull MovieRequestDto movieRequestDto) {
        val movie = movieMapper.toEntity(movieRequestDto);
        System.out.println(movieRequestDto);
        System.out.println(movie.getGenres());

        // Set genres if provided
        if (movieRequestDto.getGenreIds() != null && !movieRequestDto.getGenreIds().isEmpty()) {
            Set<Genre> genres = new HashSet<>(genreRepository.findAllById(movieRequestDto.getGenreIds()));
            movie.setGenres(genres);
        }

        // Set countries if provided
        if (movieRequestDto.getCountryIds() != null && !movieRequestDto.getCountryIds().isEmpty()) {
            Set<Country> countries = new HashSet<>(countryRepository.findAllById(movieRequestDto.getCountryIds()));
            movie.setCountries(countries);
        }

        val savedMovie = movieRepository.save(movie);
        return movieMapper.toDto(savedMovie);
    }

    @Transactional(readOnly = true)
    public MovieResponseDto getMovieById(@NonNull Integer id) {
        return movieMapper.toDto(
                movieRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException(MOVIE_NOT_FOUND.formatted(id)))
        );
    }

    @Transactional(readOnly = true)
    public List<MovieResponseDto> getAllMovies() {
        return movieRepository.findAll().stream()
                .map(movieMapper::toDto)
                .toList();
    }

    public MovieResponseDto updateMovie(@NonNull Integer id, @NonNull MovieRequestDto movieRequestDto) {
        val existingMovie = movieRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(MOVIE_NOT_FOUND.formatted(id)));

        movieMapper.partialUpdate(movieRequestDto, existingMovie);

        // Update genres if provided
        if (movieRequestDto.getGenreIds() != null && !movieRequestDto.getGenreIds().isEmpty()) {
            Set<Genre> genres = new HashSet<>(genreRepository.findAllById(movieRequestDto.getGenreIds()));
            existingMovie.setGenres(genres);
        }

        // Update countries if provided
        if (movieRequestDto.getCountryIds() != null && !movieRequestDto.getCountryIds().isEmpty()) {
            Set<Country> countries = new HashSet<>(countryRepository.findAllById(movieRequestDto.getCountryIds()));
            existingMovie.setCountries(countries);
        }

        return movieMapper.toDto(movieRepository.save(existingMovie));
    }

    public void deleteMovie(@NonNull Integer id) {
        if (!movieRepository.existsById(id)) {
            throw new NotFoundException(MOVIE_NOT_FOUND.formatted(id));
        }
        movieRepository.deleteById(id);
    }
}
