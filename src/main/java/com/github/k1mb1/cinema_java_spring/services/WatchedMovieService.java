package com.github.k1mb1.cinema_java_spring.services;

import com.github.k1mb1.cinema_java_spring.models.watchedmovie.WatchedMovieRequestDto;
import com.github.k1mb1.cinema_java_spring.models.watchedmovie.WatchedMovieResponseDto;
import com.github.k1mb1.cinema_java_spring.errors.NotFoundException;
import com.github.k1mb1.cinema_java_spring.mappers.WatchedMovieMapper;
import com.github.k1mb1.cinema_java_spring.repositories.MovieRepository;
import com.github.k1mb1.cinema_java_spring.repositories.UserRepository;
import com.github.k1mb1.cinema_java_spring.repositories.WatchedMovieRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@FieldDefaults(makeFinal = true)
public class WatchedMovieService {

    WatchedMovieRepository watchedMovieRepository;
    UserRepository userRepository;
    MovieRepository movieRepository;
    WatchedMovieMapper watchedMovieMapper;

    public WatchedMovieResponseDto markMovieAsWatched(@NonNull WatchedMovieRequestDto watchedMovieRequestDto) {
        val user = userRepository.findById(watchedMovieRequestDto.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + watchedMovieRequestDto.getUserId()));

        val movie = movieRepository.findById(watchedMovieRequestDto.getMovieId())
                .orElseThrow(() -> new NotFoundException("Movie not found with ID: " + watchedMovieRequestDto.getMovieId()));

        val watchedMovie = watchedMovieMapper.toEntity(watchedMovieRequestDto);
        val savedWatchedMovie = watchedMovieRepository.save(watchedMovie);

        return watchedMovieMapper.toDto(savedWatchedMovie);
    }

    @Transactional(readOnly = true)
    public WatchedMovieResponseDto getWatchedMovieById(@NonNull Integer id) {
        return watchedMovieMapper.toDto(
                watchedMovieRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Watched movie record not found with ID: " + id))
        );
    }

    @Transactional(readOnly = true)
    public List<WatchedMovieResponseDto> getAllWatchedMovies() {
        return watchedMovieRepository.findAll().stream()
                .map(watchedMovieMapper::toDto)
                .toList();
    }

    public void deleteWatchedMovie(@NonNull Integer id) {
        if (!watchedMovieRepository.existsById(id)) {
            throw new NotFoundException("Watched movie record not found with ID: " + id);
        }
        watchedMovieRepository.deleteById(id);
    }
}
