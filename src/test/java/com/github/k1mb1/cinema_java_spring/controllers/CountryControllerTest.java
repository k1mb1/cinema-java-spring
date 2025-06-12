package com.github.k1mb1.cinema_java_spring.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.k1mb1.cinema_java_spring.dtos.country.CountryRequestDto;
import com.github.k1mb1.cinema_java_spring.dtos.country.CountryResponseDto;
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
public class CountryControllerTest {

    final String baseUrl = "/api/countries";
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    IntegrationTestUtils utils;

    @Test
    public void testCreateCountry() throws Exception {
        val request = new CountryRequestDto("United States");

        val response = utils.perform(
                post(baseUrl).content(objectMapper.writeValueAsString(request)),
                HttpStatus.CREATED,
                CountryResponseDto.class
        );

        assertThat(response.getName()).isEqualTo(request.getName());
        assertThat(response.getId()).isNotNull();
    }

    @Test
    public void testGetCountryById() throws Exception {
        val request = new CountryRequestDto("Canada");
        val createdCountry = utils.perform(
                post(baseUrl).content(objectMapper.writeValueAsString(request)),
                HttpStatus.CREATED,
                CountryResponseDto.class
        );

        val response = utils.perform(
                get(baseUrl + "/" + createdCountry.getId()),
                HttpStatus.OK,
                CountryResponseDto.class
        );

        assertThat(response.getId()).isEqualTo(createdCountry.getId());
        assertThat(response.getName()).isEqualTo(request.getName());
    }

    @Test
    public void testGetCountryById_NotFound() throws Exception {
        utils.expectError(get(baseUrl + "/99999"), HttpStatus.NOT_FOUND);
    }

    @Test
    public void testGetAllCountries() throws Exception {
        val request1 = new CountryRequestDto("Canada");
        val request2 = new CountryRequestDto("United States");
        utils.perform(
                post(baseUrl).content(objectMapper.writeValueAsString(request1)),
                HttpStatus.CREATED
        );
        utils.perform(
                post(baseUrl).content(objectMapper.writeValueAsString(request2)),
                HttpStatus.CREATED
        );

        mockMvc.perform(get(baseUrl))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(equalTo(2)));
    }

    @Test
    public void testUpdateCountry() throws Exception {
        val createRequest = new CountryRequestDto("Spain");
        val createdCountry = utils.perform(
                post(baseUrl).content(objectMapper.writeValueAsString(createRequest)),
                HttpStatus.CREATED,
                CountryResponseDto.class
        );

        val updateRequest = new CountryRequestDto("Spain Updated");
        val response = utils.perform(
                put(baseUrl + "/" + createdCountry.getId()).content(objectMapper.writeValueAsString(updateRequest)),
                HttpStatus.OK,
                CountryResponseDto.class
        );

        assertThat(response.getId()).isEqualTo(createdCountry.getId());
        assertThat(response.getName()).isEqualTo(updateRequest.getName());
    }

    @Test
    public void testUpdateCountry_NotFound() throws Exception {
        CountryRequestDto updateRequest = new CountryRequestDto("Non-existent");

        utils.expectError(
                put(baseUrl + "/99999")
                        .content(objectMapper.writeValueAsString(updateRequest)),
                HttpStatus.NOT_FOUND
        );
    }

    @Test
    public void testDeleteCountry() throws Exception {
        val request = new CountryRequestDto("Italy");
        val createdCountry = utils.perform(
                post(baseUrl)
                        .content(objectMapper.writeValueAsString(request)),
                HttpStatus.CREATED,
                CountryResponseDto.class
        );
        utils.perform(delete(baseUrl + "/" + createdCountry.getId()), HttpStatus.NO_CONTENT);

        utils.expectError(get(baseUrl + "/" + createdCountry.getId()), HttpStatus.NOT_FOUND);
    }

    @Test
    public void testDeleteCountry_NotFound() throws Exception {
        utils.expectError(delete(baseUrl + "/99999"), HttpStatus.NOT_FOUND);
    }
}
