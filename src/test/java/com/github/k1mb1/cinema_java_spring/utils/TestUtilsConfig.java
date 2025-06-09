package com.github.k1mb1.cinema_java_spring.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;

@TestConfiguration
public class TestUtilsConfig {

    @Bean
    public IntegrationTestUtils integrationTestUtils(ObjectMapper objectMapper, MockMvc mockMvc) {
        return new IntegrationTestUtils(objectMapper, mockMvc);
    }
}
