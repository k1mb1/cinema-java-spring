package com.github.k1mb1.cinema_java_spring.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.k1mb1.cinema_java_spring.errors.Error;
import com.github.k1mb1.cinema_java_spring.models.user.UserRequestDto;
import com.github.k1mb1.cinema_java_spring.models.user.UserResponseDto;
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
public class UserControllerTest {

    static final String BASE_URL = "/api/v1/users";
    static final int INVALID_ID = 99999;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    IntegrationTestUtils utils;

    UserRequestDto request;

    @BeforeEach
    public void setUp() {
        request = UserRequestDto.builder().username("john.doe").build();
    }

    @Test
    public void createUser_ShouldReturnUserResponseDto() throws Exception {
        val response = utils.perform(
                post(BASE_URL).content(objectMapper.writeValueAsString(request)),
                HttpStatus.CREATED,
                UserResponseDto.class
        );

        assertThat(response.getUsername()).isEqualTo(request.getUsername());
        assertThat(response.getId()).isNotNull();
    }

    @Test
    public void getUserById_WithValidId_ShouldReturnUserResponseDto() throws Exception {
        val createdUser = utils.perform(
                post(BASE_URL).content(objectMapper.writeValueAsString(request)),
                HttpStatus.CREATED,
                UserResponseDto.class
        );

        val response = utils.perform(
                get(BASE_URL + "/" + createdUser.getId()),
                HttpStatus.OK,
                UserResponseDto.class
        );

        assertThat(response.getId()).isEqualTo(createdUser.getId());
        assertThat(response.getUsername()).isEqualTo(request.getUsername());
    }

    @Test
    public void getUserById_WithInvalidId_ShouldReturn404NotFound() throws Exception {
        val error = utils.expectError(get(BASE_URL + "/" + INVALID_ID), HttpStatus.NOT_FOUND);

        assertThat(error.status()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void getAllUsers_ShouldReturnListOfUserResponseDto() throws Exception {
        utils.perform(
                post(BASE_URL).content(objectMapper.writeValueAsString(request)),
                HttpStatus.CREATED
        );

        val request2 = UserRequestDto.builder().username("user2").build();
        utils.perform(
                post(BASE_URL).content(objectMapper.writeValueAsString(request2)),
                HttpStatus.CREATED
        );

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(equalTo(2)));
    }

    @Test
    public void updateUser_WithValidId_ShouldReturnUpdatedUserResponseDto() throws Exception {
        val createdUser = utils.perform(
                post(BASE_URL).content(objectMapper.writeValueAsString(request)),
                HttpStatus.CREATED,
                UserResponseDto.class
        );

        val updateRequest = new UserRequestDto();
        updateRequest.setUsername("updated.user");

        val response = utils.perform(
                put(BASE_URL + "/" + createdUser.getId()).content(objectMapper.writeValueAsString(updateRequest)),
                HttpStatus.OK,
                UserResponseDto.class
        );

        assertThat(response.getId()).isEqualTo(createdUser.getId());
        assertThat(response.getUsername()).isEqualTo(updateRequest.getUsername());
    }

    @Test
    public void updateUser_WithInvalidId_ShouldReturn404NotFound() throws Exception {
        val updateRequest = UserRequestDto.builder().username("nonexistent").build();

        utils.expectError(
                put(BASE_URL + "/" + INVALID_ID)
                        .content(objectMapper.writeValueAsString(updateRequest)),
                HttpStatus.NOT_FOUND
        );
    }

    @Test
    public void deleteUser_WithValidId_ShouldDeleteUser() throws Exception {
        val createdUser = utils.perform(
                post(BASE_URL)
                        .content(objectMapper.writeValueAsString(request)),
                HttpStatus.CREATED,
                UserResponseDto.class
        );

        utils.perform(delete(BASE_URL + "/" + createdUser.getId()), HttpStatus.NO_CONTENT);

        utils.expectError(get(BASE_URL + "/" + createdUser.getId()), HttpStatus.NOT_FOUND);
    }

    @Test
    public void deleteUser_WithInvalidId_ShouldReturn404NotFound() throws Exception {
        Error error = utils.expectError(delete(BASE_URL + "/" + INVALID_ID), HttpStatus.NOT_FOUND);

        assertThat(error).isNotNull();
        assertThat(error.status()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
