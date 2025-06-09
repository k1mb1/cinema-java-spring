package com.github.k1mb1.cinema_java_spring.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.k1mb1.cinema_java_spring.config.Error;
import com.github.k1mb1.cinema_java_spring.dtos.genre.GenreRequestDto;
import com.github.k1mb1.cinema_java_spring.dtos.genre.GenreResponseDto;
import com.github.k1mb1.cinema_java_spring.utils.IntegrationTest;
import com.github.k1mb1.cinema_java_spring.utils.IntegrationTestUtils;
import lombok.val;
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

    final String baseUrl = "/api/genres";
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    IntegrationTestUtils utils;

    @Test
    public void testCreateGenre() throws Exception {
        val request = new GenreRequestDto("Action");

        val response = utils.perform(
                post(baseUrl).content(objectMapper.writeValueAsString(request)),
                HttpStatus.CREATED,
                GenreResponseDto.class
        );

        assertThat(response.getName()).isEqualTo(request.getName());
        assertThat(response.getId()).isNotNull();
    }

    @Test
    public void testGetGenreById() throws Exception {
        val request = new GenreRequestDto("Comedy");
        val createdGenre = utils.perform(
                post(baseUrl).content(objectMapper.writeValueAsString(request)),
                HttpStatus.CREATED,
                GenreResponseDto.class
        );

        val response = utils.perform(
                get(baseUrl + "/" + createdGenre.getId()),
                HttpStatus.OK,
                GenreResponseDto.class
        );

        assertThat(response.getId()).isEqualTo(createdGenre.getId());
        assertThat(response.getName()).isEqualTo(request.getName());
    }

    @Test
    public void testGetGenreById_NotFound() throws Exception {
        var error = utils.expectError(get(baseUrl + "/99999"), HttpStatus.NOT_FOUND);
        assertThat(error).isNotNull();
        assertThat(error.status()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testGetAllGenres() throws Exception {
        utils.perform(
                post(baseUrl).content(objectMapper.writeValueAsString(new GenreRequestDto("Drama"))),
                HttpStatus.CREATED
        );
        utils.perform(
                post(baseUrl).content(objectMapper.writeValueAsString(new GenreRequestDto("Horror"))),
                HttpStatus.CREATED
        );

        mockMvc.perform(get(baseUrl))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(equalTo(2)));
    }

    @Test
    public void testUpdateGenre() throws Exception {
        val createRequest = new GenreRequestDto("Sci-Fi");
        val createdGenre = utils.perform(
                post(baseUrl).content(objectMapper.writeValueAsString(createRequest)),
                HttpStatus.CREATED,
                GenreResponseDto.class
        );

        val updateRequest = new GenreRequestDto("Science Fiction");
        val response = utils.perform(
                put(baseUrl + "/" + createdGenre.getId()).content(objectMapper.writeValueAsString(updateRequest)),
                HttpStatus.OK,
                GenreResponseDto.class
        );

        assertThat(response.getId()).isEqualTo(createdGenre.getId());
        assertThat(response.getName()).isEqualTo(updateRequest.getName());
    }

    @Test
    public void testUpdateGenre_NotFound() throws Exception {
        GenreRequestDto updateRequest = new GenreRequestDto("Non-existent");

        val error = utils.expectError(
                put(baseUrl + "/99999")
                        .content(objectMapper.writeValueAsString(updateRequest)),
                HttpStatus.NOT_FOUND
        );

        assertThat(error).isNotNull();
        assertThat(error.status()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testDeleteGenre() throws Exception {
        // Create genre first
        val request = new GenreRequestDto("Thriller");
        val createdGenre = utils.perform(
                post(baseUrl)
                        .content(objectMapper.writeValueAsString(request)),
                HttpStatus.CREATED,
                GenreResponseDto.class
        );
        // Delete genre
        utils.perform(delete(baseUrl + "/" + createdGenre.getId()), HttpStatus.NO_CONTENT);

        // Verify deletion
        utils.expectError(get(baseUrl + "/" + createdGenre.getId()), HttpStatus.NOT_FOUND);
    }

    @Test
    public void testDeleteGenre_NotFound() throws Exception {
        Error error = utils.expectError(delete(baseUrl + "/99999"), HttpStatus.NOT_FOUND);

        assertThat(error).isNotNull();
        assertThat(error.status()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
