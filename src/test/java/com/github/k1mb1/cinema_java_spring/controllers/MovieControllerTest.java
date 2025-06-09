package com.github.k1mb1.cinema_java_spring.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.k1mb1.cinema_java_spring.config.Error;
import com.github.k1mb1.cinema_java_spring.dtos.movie.MovieRequestDto;
import com.github.k1mb1.cinema_java_spring.dtos.movie.MovieResponseDto;
import com.github.k1mb1.cinema_java_spring.utils.IntegrationTest;
import com.github.k1mb1.cinema_java_spring.utils.IntegrationTestUtils;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@IntegrationTest
public class MovieControllerTest {

    final String baseUrl = "/api/movies";
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    IntegrationTestUtils utils;

    @Test
    public void testCreateMovie() throws Exception {
        val request = createSampleMovieRequest("Inception", "A mind-bending thriller");

        val response = utils.perform(
                post(baseUrl).content(objectMapper.writeValueAsString(request)),
                HttpStatus.CREATED,
                MovieResponseDto.class
        );

        assertThat(response.getTitle()).isEqualTo(request.getTitle());
        assertThat(response.getDescription()).isEqualTo(request.getDescription());
        assertThat(response.getId()).isNotNull();
    }

    @Test
    public void testGetMovieById() throws Exception {
        val request = createSampleMovieRequest("The Godfather", "Crime drama film");
        val createdMovie = utils.perform(
                post(baseUrl).content(objectMapper.writeValueAsString(request)),
                HttpStatus.CREATED,
                MovieResponseDto.class
        );

        val response = utils.perform(
                get(baseUrl + "/" + createdMovie.getId()),
                HttpStatus.OK,
                MovieResponseDto.class
        );

        assertThat(response.getId()).isEqualTo(createdMovie.getId());
        assertThat(response.getTitle()).isEqualTo(request.getTitle());
        assertThat(response.getDescription()).isEqualTo(request.getDescription());
    }

    @Test
    @Rollback
    public void testGetMovieById_NotFound() throws Exception {
        var error = utils.expectError(get(baseUrl + "/99999"), HttpStatus.NOT_FOUND);
        assertThat(error).isNotNull();
        assertThat(error.status()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @Rollback
    public void testGetAllMovies() throws Exception {
        utils.perform(
                post(baseUrl).content(objectMapper.writeValueAsString(
                        createSampleMovieRequest("Pulp Fiction", "Crime black comedy"))),
                HttpStatus.CREATED
        );
        utils.perform(
                post(baseUrl).content(objectMapper.writeValueAsString(
                        createSampleMovieRequest("The Matrix", "Sci-fi action"))),
                HttpStatus.CREATED
        );

        mockMvc.perform(get(baseUrl))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(equalTo(2)));
    }

    @Test
    @Rollback
    public void testUpdateMovie() throws Exception {
        val createRequest = createSampleMovieRequest("Titanic", "Romance disaster film");
        val createdMovie = utils.perform(
                post(baseUrl).content(objectMapper.writeValueAsString(createRequest)),
                HttpStatus.CREATED,
                MovieResponseDto.class
        );

        val updateRequest = createSampleMovieRequest("Titanic (1997)", "Epic romance and disaster film");
        val response = utils.perform(
                put(baseUrl + "/" + createdMovie.getId()).content(objectMapper.writeValueAsString(updateRequest)),
                HttpStatus.OK,
                MovieResponseDto.class
        );

        assertThat(response.getId()).isEqualTo(createdMovie.getId());
        assertThat(response.getTitle()).isEqualTo(updateRequest.getTitle());
        assertThat(response.getDescription()).isEqualTo(updateRequest.getDescription());
    }

    @Test
    @Rollback
    public void testUpdateMovie_NotFound() throws Exception {
        val updateRequest = createSampleMovieRequest("Non-existent Movie", "This movie doesn't exist");

        val error = utils.expectError(
                put(baseUrl + "/99999")
                        .content(objectMapper.writeValueAsString(updateRequest)),
                HttpStatus.NOT_FOUND
        );

        assertThat(error).isNotNull();
        assertThat(error.status()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @Rollback
    public void testDeleteMovie() throws Exception {
        // Create movie first
        val request = createSampleMovieRequest("Avatar", "Sci-fi adventure");
        val createdMovie = utils.perform(
                post(baseUrl)
                        .content(objectMapper.writeValueAsString(request)),
                HttpStatus.CREATED,
                MovieResponseDto.class
        );

        // Delete movie
        utils.perform(delete(baseUrl + "/" + createdMovie.getId()), HttpStatus.NO_CONTENT);

        // Verify deletion
        utils.expectError(get(baseUrl + "/" + createdMovie.getId()), HttpStatus.NOT_FOUND);
    }

    @Test
    @Rollback
    public void testDeleteMovie_NotFound() throws Exception {
        Error error = utils.expectError(delete(baseUrl + "/99999"), HttpStatus.NOT_FOUND);

        assertThat(error).isNotNull();
        assertThat(error.status()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    // Helper method to create movie request objects
    MovieRequestDto createSampleMovieRequest(String title, String description) {
        MovieRequestDto request = new MovieRequestDto();
        request.setTitle(title);
        request.setDescription(description);
        request.setReleaseDate(LocalDate.of(2023, 1, 1));
        request.setGenreIds(Set.of(1));
        request.setCountryIds(Set.of(1));
        return request;
    }
}
