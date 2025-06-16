package com.github.k1mb1.cinema_java_spring.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.k1mb1.cinema_java_spring.models.movie.MovieRequestDto;
import com.github.k1mb1.cinema_java_spring.models.movie.MovieResponseDto;
import com.github.k1mb1.cinema_java_spring.utils.IntegrationTest;
import com.github.k1mb1.cinema_java_spring.utils.IntegrationTestUtils;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static com.github.k1mb1.cinema_java_spring.errors.ErrorMessages.MOVIE_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@IntegrationTest
public class MovieControllerTest {

    static final String BASE_URL = "/api/v1/movies";
    static final int INVALID_ID = 99999;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    IntegrationTestUtils utils;

    MovieRequestDto request;

    @BeforeEach
    public void setUp() {
        request = MovieRequestDto.builder()
                .title("Inception")
                .description("A mind-bending thriller")
                .year(2010)
                .worldGross(829895144L)
                .budget(160000000L)
                .ageRating("12+")
                .durationMinutes(148)
                .releaseDate(LocalDate.of(2023, 1, 1))
                .build();
    }

    @Test
    public void createMovie_ShouldReturnMovieResponseDto() throws Exception {
        val response = utils.perform(
                post(BASE_URL).content(objectMapper.writeValueAsString(request)),
                HttpStatus.CREATED,
                MovieResponseDto.class
        );

        assertThat(response.getId()).isNotNull();
        assertMovieResponse(response, request);
    }

    @Test
    public void getMovieById_WithValidId_ShouldReturnMovieResponseDto() throws Exception {
        val createdMovie = utils.perform(
                post(BASE_URL).content(objectMapper.writeValueAsString(request)),
                HttpStatus.CREATED,
                MovieResponseDto.class
        );

        val response = utils.perform(
                get(BASE_URL + "/" + createdMovie.getId()),
                HttpStatus.OK,
                MovieResponseDto.class
        );

        assertThat(response.getId()).isEqualTo(createdMovie.getId());
        assertMovieResponse(response, request);
    }

    @Test
    public void getMovieById_WithInvalidId_ShouldReturn404NotFound() throws Exception {
        utils.expectError(
                get(BASE_URL + "/" + INVALID_ID),
                HttpStatus.NOT_FOUND,
                MOVIE_NOT_FOUND, INVALID_ID
        );
    }

    @Test
    public void getAllMovies_ShouldReturnListOfMovieResponseDto() throws Exception {
        utils.perform(
                post(BASE_URL).content(objectMapper.writeValueAsString(request)),
                HttpStatus.CREATED
        );

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("content.length()").value(equalTo(1)));
    }

    @Test
    public void updateMovie_WithValidId_ShouldReturnUpdatedMovieResponseDto() throws Exception {
        val createdMovie = utils.perform(
                post(BASE_URL).content(objectMapper.writeValueAsString(request)),
                HttpStatus.CREATED,
                MovieResponseDto.class
        );

        val updateRequest = MovieRequestDto.builder()
                .worldGross(1829895144L)
                .durationMinutes(200)
                .build();
        val response = utils.perform(
                put(BASE_URL + "/" + createdMovie.getId()).content(objectMapper.writeValueAsString(updateRequest)),
                HttpStatus.OK,
                MovieResponseDto.class
        );

        assertThat(response)
                .extracting(
                        MovieResponseDto::getTitle,
                        MovieResponseDto::getDescription,
                        MovieResponseDto::getWorldGross,
                        MovieResponseDto::getDurationMinutes,
                        MovieResponseDto::getReleaseDate,
                        MovieResponseDto::getYear,
                        MovieResponseDto::getAgeRating,
                        MovieResponseDto::getBudget)
                .containsExactly(
                        createdMovie.getTitle(),
                        createdMovie.getDescription(),
                        updateRequest.getWorldGross(),
                        updateRequest.getDurationMinutes(),
                        createdMovie.getReleaseDate(),
                        createdMovie.getYear(),
                        createdMovie.getAgeRating(),
                        createdMovie.getBudget()
                );
    }

    @Test
    public void updateMovie_WithInvalidId_ShouldReturn404NotFound() throws Exception {
        utils.expectError(
                put(BASE_URL + "/" + INVALID_ID)
                        .content(objectMapper.writeValueAsString(request)),
                HttpStatus.NOT_FOUND,
                MOVIE_NOT_FOUND, INVALID_ID
        );
    }

    @Test
    public void deleteMovie_WithValidId_ShouldDeleteMovie() throws Exception {
        val createdMovie = utils.perform(
                post(BASE_URL)
                        .content(objectMapper.writeValueAsString(request)),
                HttpStatus.CREATED,
                MovieResponseDto.class
        );

        utils.perform(delete(BASE_URL + "/" + createdMovie.getId()), HttpStatus.NO_CONTENT);

        utils.expectError(
                get(BASE_URL + "/" + createdMovie.getId()),
                HttpStatus.NOT_FOUND,
                MOVIE_NOT_FOUND, createdMovie.getId()
        );
    }

    @Test
    public void deleteMovie_WithInvalidId_ShouldReturn404NotFound() throws Exception {
        utils.expectError(
                delete(BASE_URL + "/" + INVALID_ID),
                HttpStatus.NOT_FOUND,
                MOVIE_NOT_FOUND, INVALID_ID
        );
    }

    private void assertMovieResponse(MovieResponseDto actual, MovieRequestDto expected) {
        assertThat(actual).isNotNull()
                .extracting(
                        MovieResponseDto::getTitle,
                        MovieResponseDto::getDescription,
                        MovieResponseDto::getWorldGross,
                        MovieResponseDto::getDurationMinutes,
                        MovieResponseDto::getReleaseDate,
                        MovieResponseDto::getYear,
                        MovieResponseDto::getAgeRating,
                        MovieResponseDto::getBudget)
                .containsExactly(
                        expected.getTitle(),
                        expected.getDescription(),
                        expected.getWorldGross(),
                        expected.getDurationMinutes(),
                        expected.getReleaseDate(),
                        expected.getYear(),
                        expected.getAgeRating(),
                        expected.getBudget()
                );
    }
}
