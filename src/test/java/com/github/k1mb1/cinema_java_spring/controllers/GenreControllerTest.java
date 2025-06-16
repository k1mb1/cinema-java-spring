package com.github.k1mb1.cinema_java_spring.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.k1mb1.cinema_java_spring.models.genre.GenreRequestDto;
import com.github.k1mb1.cinema_java_spring.models.genre.GenreResponseDto;
import com.github.k1mb1.cinema_java_spring.utils.IntegrationTest;
import com.github.k1mb1.cinema_java_spring.utils.IntegrationTestUtils;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@IntegrationTest
public class GenreControllerTest {

    static final String BASE_URL = "/api/v1/genres";
    static final int INVALID_ID = 99999;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    IntegrationTestUtils utils;

    GenreRequestDto request;

    @BeforeEach
    public void setUp() {
        request = new GenreRequestDto("Action");
    }

    @Test
    public void createGenre_ShouldReturnGenreResponseDto() throws Exception {
        val response = utils.perform(
                post(BASE_URL).content(objectMapper.writeValueAsString(request)),
                HttpStatus.CREATED,
                GenreResponseDto.class
        );

        assertThat(response.getId()).isNotNull();
        assertThat(response.getName()).isEqualTo(request.getName());
    }

    @Test
    public void getGenreById_WithValidId_ShouldReturnGenreResponseDto() throws Exception {
        val createdGenre = utils.perform(
                post(BASE_URL).content(objectMapper.writeValueAsString(request)),
                HttpStatus.CREATED,
                GenreResponseDto.class
        );

        val response = utils.perform(
                get(BASE_URL + "/" + createdGenre.getId()),
                HttpStatus.OK,
                GenreResponseDto.class
        );

        assertThat(response.getId()).isEqualTo(createdGenre.getId());
        assertThat(response.getName()).isEqualTo(request.getName());
    }

    @Test
    public void getGenreById_WithInvalidId_ShouldReturn404NotFound() throws Exception {
        utils.expectError(get(BASE_URL + "/" + INVALID_ID), HttpStatus.NOT_FOUND);
    }

    @Test
    public void getAllGenres_ShouldReturnPageOfGenreResponseDto() throws Exception {
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
    public void updateGenre_WithValidId_ShouldReturnUpdatedGenreResponseDto() throws Exception {
        val createdGenre = utils.perform(
                post(BASE_URL).content(objectMapper.writeValueAsString(request)),
                HttpStatus.CREATED,
                GenreResponseDto.class
        );

        val updateRequest = new GenreRequestDto("Science Fiction");
        val response = utils.perform(
                put(BASE_URL + "/" + createdGenre.getId()).content(objectMapper.writeValueAsString(updateRequest)),
                HttpStatus.OK,
                GenreResponseDto.class
        );

        assertThat(response.getId()).isEqualTo(createdGenre.getId());
        assertThat(response.getName()).isEqualTo(updateRequest.getName());
    }

    @Test
    public void updateGenre_WithInvalidId_ShouldReturn404NotFound() throws Exception {
        utils.expectError(
                put(BASE_URL + "/" + INVALID_ID)
                        .content(objectMapper.writeValueAsString(request)),
                HttpStatus.NOT_FOUND
        );
    }

    @Test
    public void deleteGenre_WithValidId_ShouldDeleteGenre() throws Exception {
        val createdGenre = utils.perform(
                post(BASE_URL)
                        .content(objectMapper.writeValueAsString(request)),
                HttpStatus.CREATED,
                GenreResponseDto.class
        );
        utils.perform(delete(BASE_URL + "/" + createdGenre.getId()), HttpStatus.NO_CONTENT);

        utils.expectError(get(BASE_URL + "/" + createdGenre.getId()), HttpStatus.NOT_FOUND);
    }

    @Test
    public void deleteGenre_WithInvalidId_ShouldReturn404NotFound() throws Exception {
        utils.expectError(delete(BASE_URL + "/" + INVALID_ID), HttpStatus.NOT_FOUND);
    }
}
