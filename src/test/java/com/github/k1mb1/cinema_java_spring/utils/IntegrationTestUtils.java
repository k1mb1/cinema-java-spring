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
     * Выполняет HTTP-запрос через MockMvc, проверяет ожидаемый статус ответа
     * и преобразует тело ответа в объект указанного типа.
     *
     * @param request подготовленный MockMvc-запрос
     * @param status  ожидаемый HTTP-статус ответа
     * @param clazz   класс, в который будет десериализован JSON-ответ
     * @param <T>     тип возвращаемого объекта
     * @return десериализованный объект из тела ответа
     * @throws Exception если возникает ошибка при выполнении запроса или десериализации ответа
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
     * Проверяет, что HTTP-ответ содержит объект {@link Error} с ожидаемым статусом и сообщением.
     *
     * @param request         подготовленный HTTP-запрос
     * @param expectedStatus  ожидаемый HTTP-статус ответа
     * @param expectedMessage ожидаемое сообщение об ошибке (с поддержкой форматирования)
     * @param expectedArgs    аргументы для форматирования сообщения
     * @throws Exception если возникает ошибка при выполнении запроса или десериализации ответа
     */
    public void expectError(
            @NonNull MockHttpServletRequestBuilder request,
            @NonNull HttpStatus expectedStatus,
            @NonNull String expectedMessage,
            @NonNull Object... expectedArgs
    ) throws Exception {
        val result = mockMvc.perform(request.contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        val error = objectMapper.readValue(result.getResponse().getContentAsString(), Error.class);

        assertThat(error).isNotNull();
        assertThat(error.status()).isEqualTo(expectedStatus);
        assertThat(error.code()).isEqualTo(expectedStatus.value());
        assertThat(error.timestamp()).isNotNull();
        assertThat(error.message()).isEqualTo(expectedMessage, expectedArgs);
    }

    /**
     * Выполняет указанный HTTP-запрос через MockMvc и проверяет соответствие статуса ответа ожидаемому.
     *
     * @param request        подготовленный MockMvc-запрос
     * @param expectedStatus ожидаемый HTTP-статус ответа
     * @throws Exception если возникает ошибка при выполнении запроса или проверке статуса
     */
    public void perform(
            @NonNull MockHttpServletRequestBuilder request,
            @NonNull HttpStatus expectedStatus
    ) throws Exception {
        mockMvc.perform(request.contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(expectedStatus.value()));
    }
}