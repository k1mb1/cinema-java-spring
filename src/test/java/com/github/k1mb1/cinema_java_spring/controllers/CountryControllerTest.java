package com.github.k1mb1.cinema_java_spring.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.k1mb1.cinema_java_spring.models.country.CountryRequestDto;
import com.github.k1mb1.cinema_java_spring.models.country.CountryResponseDto;
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
public class CountryControllerTest {

    static final String BASE_URL = "/api/v1/countries";
    static final int INVALID_ID = 99999;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    IntegrationTestUtils utils;

    CountryRequestDto request;

    @BeforeEach
    public void setUp() {
        request = new CountryRequestDto("Test Country");
    }

    @Test
    public void createCountry_ShouldReturnCountryResponseDto() throws Exception {

        val response = utils.perform(
                post(BASE_URL).content(objectMapper.writeValueAsString(request)),
                HttpStatus.CREATED,
                CountryResponseDto.class
        );

        assertThat(response.getId()).isNotNull();
        assertThat(response.getName()).isEqualTo(request.getName());
    }

    @Test
    public void getCountryById_WithValidId_ShouldReturnCountryResponseDto() throws Exception {
        val createdCountry = utils.perform(
                post(BASE_URL).content(objectMapper.writeValueAsString(request)),
                HttpStatus.CREATED,
                CountryResponseDto.class
        );

        val response = utils.perform(
                get(BASE_URL + "/" + createdCountry.getId()),
                HttpStatus.OK,
                CountryResponseDto.class
        );

        assertThat(response.getId()).isEqualTo(createdCountry.getId());
        assertThat(response.getName()).isEqualTo(request.getName());
    }

    @Test
    public void getCountryById_WithInvalidId_ShouldReturn404NotFound() throws Exception {
        utils.expectError(get(BASE_URL + "/" + INVALID_ID), HttpStatus.NOT_FOUND);
    }

    @Test
    public void getAllCountries_ShouldReturnPageOfCountryResponseDto() throws Exception {
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
    public void updateCountry_WithValidId_ShouldReturnUpdatedCountryResponseDto() throws Exception {
        val createdCountry = utils.perform(
                post(BASE_URL).content(objectMapper.writeValueAsString(request)),
                HttpStatus.CREATED,
                CountryResponseDto.class
        );

        val updateRequest = new CountryRequestDto("Test Updated");

        val response = utils.perform(
                put(BASE_URL + "/" + createdCountry.getId()).content(objectMapper.writeValueAsString(updateRequest)),
                HttpStatus.OK,
                CountryResponseDto.class
        );

        assertThat(response.getId()).isEqualTo(createdCountry.getId());
        assertThat(response.getName()).isEqualTo(updateRequest.getName());
    }

    @Test
    public void updateCountry_WithInvalidId_ShouldReturn404NotFound() throws Exception {
        CountryRequestDto updateRequest = new CountryRequestDto("Non-existent");

        utils.expectError(
                put(BASE_URL + "/" + INVALID_ID)
                        .content(objectMapper.writeValueAsString(updateRequest)),
                HttpStatus.NOT_FOUND
        );
    }

    @Test
    public void deleteCountry_WithValidId_ShouldDeleteCountry() throws Exception {
        val createdCountry = utils.perform(
                post(BASE_URL)
                        .content(objectMapper.writeValueAsString(request)),
                HttpStatus.CREATED,
                CountryResponseDto.class
        );

        utils.perform(delete(BASE_URL + "/" + createdCountry.getId()), HttpStatus.NO_CONTENT);

        utils.expectError(get(BASE_URL + "/" + createdCountry.getId()), HttpStatus.NOT_FOUND);
    }

    @Test
    public void deleteCountry_WithInvalidId_ShouldReturn404NotFound() throws Exception {
        utils.expectError(delete(BASE_URL + "/" + INVALID_ID), HttpStatus.NOT_FOUND);
    }
}
