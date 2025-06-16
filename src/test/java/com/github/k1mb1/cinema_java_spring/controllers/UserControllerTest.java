package com.github.k1mb1.cinema_java_spring.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.k1mb1.cinema_java_spring.models.user.UserEntity;
import com.github.k1mb1.cinema_java_spring.models.user.UserRequestDto;
import com.github.k1mb1.cinema_java_spring.models.user.UserResponseDto;
import com.github.k1mb1.cinema_java_spring.errors.Error;
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
public class UserControllerTest {

    final String baseUrl = "/api/v1/users";
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    IntegrationTestUtils utils;

    @Test
    public void testCreateUser() throws Exception {
        val request = createSampleUserRequest("john.doe@example.com");

        val response = utils.perform(
                post(baseUrl).content(objectMapper.writeValueAsString(request)),
                HttpStatus.CREATED,
                UserResponseDto.class
        );

        assertThat(response.getUsername()).isEqualTo(request.getUsername());
        assertThat(response.getId()).isNotNull();
    }

    @Test
    public void testGetUserById() throws Exception {
        val request = createSampleUserRequest("jane.doe@example.com");
        val createdUser = utils.perform(
                post(baseUrl).content(objectMapper.writeValueAsString(request)),
                HttpStatus.CREATED,
                UserResponseDto.class
        );

        val response = utils.perform(
                get(baseUrl + "/" + createdUser.getId()),
                HttpStatus.OK,
                UserResponseDto.class
        );

        assertThat(response.getId()).isEqualTo(createdUser.getId());
        assertThat(response.getUsername()).isEqualTo(request.getUsername());
    }

    @Test
    public void testGetUserById_NotFound() throws Exception {
        var error = utils.expectError(get(baseUrl + "/99999"), HttpStatus.NOT_FOUND);

        assertThat(error.status()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testGetAllUsers() throws Exception {
        val request1 = UserEntity.builder().username("user1@example.com").build();
        val request2 = UserEntity.builder().username("user2@example.com").build();

        utils.perform(
                post(baseUrl).content(objectMapper.writeValueAsString(request1)),
                HttpStatus.CREATED
        );
        utils.perform(
                post(baseUrl).content(objectMapper.writeValueAsString(request2)),
                HttpStatus.CREATED
        );

        mockMvc.perform(get(baseUrl))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(equalTo(2)));
    }

    @Test
    public void testUpdateUser() throws Exception {
        val createRequest = UserEntity.builder().username("user@example.com").build();
        val createdUser = utils.perform(
                post(baseUrl).content(objectMapper.writeValueAsString(createRequest)),
                HttpStatus.CREATED,
                UserResponseDto.class
        );

        val updateRequest = UserEntity.builder().username("delete.test@example.com").build();
        val response = utils.perform(
                put(baseUrl + "/" + createdUser.getId()).content(objectMapper.writeValueAsString(updateRequest)),
                HttpStatus.OK,
                UserResponseDto.class
        );

        assertThat(response.getId()).isEqualTo(createdUser.getId());
        assertThat(response.getUsername()).isEqualTo(updateRequest.getUsername());
    }

    @Test
    public void testUpdateUser_NotFound() throws Exception {
        val updateRequest = UserEntity.builder().username("user@example.com").build();

        utils.expectError(
                put(baseUrl + "/99999")
                        .content(objectMapper.writeValueAsString(updateRequest)),
                HttpStatus.NOT_FOUND
        );
    }

    @Test
    public void testDeleteUser() throws Exception {
        val request = UserEntity.builder().username("delete.test@example.com").build();
        val createdUser = utils.perform(
                post(baseUrl)
                        .content(objectMapper.writeValueAsString(request)),
                HttpStatus.CREATED,
                UserResponseDto.class
        );

        utils.perform(delete(baseUrl + "/" + createdUser.getId()), HttpStatus.NO_CONTENT);

        val error = utils.expectError(get(baseUrl + "/" + createdUser.getId()), HttpStatus.NOT_FOUND);

        assertThat(error.status()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testDeleteUser_NotFound() throws Exception {
        Error error = utils.expectError(delete(baseUrl + "/99999"), HttpStatus.NOT_FOUND);

        assertThat(error).isNotNull();
        assertThat(error.status()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    // Helper method to create user request objects
    UserRequestDto createSampleUserRequest(String username) {
        UserRequestDto request = new UserRequestDto();
        request.setUsername(username);
        return request;
    }
}
