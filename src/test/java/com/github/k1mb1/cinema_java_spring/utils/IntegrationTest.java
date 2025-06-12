package com.github.k1mb1.cinema_java_spring.utils;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Составная аннотация для интеграционного тестирования Spring Boot приложений.
 * <p>
 * Применяется к тестовым классам, которым требуется полная интеграция
 * с базой данных, веб-контекстом и внешними зависимостями.
 *
 * @see TestcontainersConfiguration TestcontainersConfiguration
 * @see TestUtilsConfig TestUtilsConfig
 * @see SpringBootTest SpringBootTest
 * @see AutoConfigureMockMvc AutoConfigureMockMvc
 * @see Transactional Transactional
 */
@Import({TestcontainersConfiguration.class, TestUtilsConfig.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface IntegrationTest {
}