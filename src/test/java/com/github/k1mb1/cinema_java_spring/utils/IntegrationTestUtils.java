package com.github.k1mb1.cinema_java_spring.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.k1mb1.cinema_java_spring.errors.Error;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Утилитарный класс для интеграционных тестов с использованием MockMvc.
 */
@RequiredArgsConstructor
public class IntegrationTestUtils {

    final ObjectMapper objectMapper;
    final MockMvc mockMvc;

    /**
     * Выполняет MockMvc-запрос и преобразует тело ответа в объект указанного класса.
     *
     * @param request подготовленный запрос
     * @param status  ожидаемый HTTP статус ответа
     * @param clazz   класс, в который нужно преобразовать ответ
     * @param <T>     тип возвращаемого объекта
     * @return объект, полученный из JSON-ответа
     * @throws Exception если произошла ошибка при выполнении запроса или десериализации
     */
    public <T> T perform(
            @NonNull MockHttpServletRequestBuilder request,
            @NonNull HttpStatus status,
            @NonNull Class<T> clazz
    ) throws Exception {
        val result = mockMvc.perform(request.contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(status.value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        val object = objectMapper.readValue(result.getResponse().getContentAsString(), clazz);

        assertThat(object).isNotNull();

        return object;
    }


    /**
     * Проверяет, что ответ содержит объект Error с указанным статусом
     *
     * @param request        подготовленный запрос
     * @param expectedStatus ожидаемый HTTP статус
     * @return объект Error из ответа
     * @throws Exception если произошла ошибка при выполнении запроса или десериализации
     */
    public Error expectError(
            @NonNull MockHttpServletRequestBuilder request,
            @NonNull HttpStatus expectedStatus
    ) throws Exception {
        val result = mockMvc.perform(request.contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(expectedStatus.value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        val error = objectMapper.readValue(result.getResponse().getContentAsString(), Error.class);

        assertThat(error).isNotNull();
        assertThat(error.status()).isEqualTo(expectedStatus);
        assertThat(error.code()).isEqualTo(expectedStatus.value());
        assertThat(error.timestamp()).isNotNull();
        assertThat(error.message()).isNotNull();

        return error;
    }

    /**
     * Выполняет MockMvc-запрос и проверяет статус ответа
     *
     * @param request        подготовленный запрос
     * @param expectedStatus ожидаемый HTTP статус
     * @throws Exception если произошла ошибка при выполнении запроса
     */
    public void perform(
            @NonNull MockHttpServletRequestBuilder request,
            @NonNull HttpStatus expectedStatus
    ) throws Exception {
        mockMvc.perform(request.contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(expectedStatus.value()));
    }
}