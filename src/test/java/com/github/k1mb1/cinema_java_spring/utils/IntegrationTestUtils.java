package com.github.k1mb1.cinema_java_spring.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.k1mb1.cinema_java_spring.config.Error;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Утилитный класс для интеграционных тестов с использованием MockMvc.
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
            MockHttpServletRequestBuilder request,
            HttpStatus status,
            Class<T> clazz
    ) throws Exception {
        MvcResult result = mockMvc.perform(request.contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(status.value()))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        return objectMapper.readValue(content, clazz);
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
            MockHttpServletRequestBuilder request,
            HttpStatus expectedStatus
    ) throws Exception {
        val result = mockMvc.perform(request.contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(expectedStatus.value()))
                .andReturn();

        val content = result.getResponse().getContentAsString();
        val error = objectMapper.readValue(content, Error.class);

        assertThat(error).isNotNull();
        assertThat(error.status()).isEqualTo(expectedStatus);
        assertThat(error.code()).isEqualTo(expectedStatus.value());
        assertThat(error.timestamp()).isNotNull();

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
            MockHttpServletRequestBuilder request,
            HttpStatus expectedStatus
    ) throws Exception {
        mockMvc.perform(request.contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(expectedStatus.value()));
    }
}