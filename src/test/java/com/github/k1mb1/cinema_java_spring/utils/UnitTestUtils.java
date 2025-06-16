package com.github.k1mb1.cinema_java_spring.utils;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Утилитарный класс для unit-тестов
 */
public class UnitTestUtils {
    /**
     * Проверяет, что при выполнении действия выбрасывается исключение указанного типа с ожидаемым сообщением.
     *
     * @param action         действие, которое должно выбросить исключение
     * @param exceptionClass класс ожидаемого исключения
     * @param expectedString ожидаемое сообщение исключения (с поддержкой форматирования)
     * @param expectedArgs   аргументы для форматирования сообщения
     * @param <T>            тип ожидаемого исключения
     */
    public static <T extends Throwable> void assertExceptionWithMessage(
            Runnable action,
            Class<T> exceptionClass,
            String expectedString,
            Object... expectedArgs
    ) {
        assertThatThrownBy(action::run)
                .isExactlyInstanceOf(exceptionClass)
                .hasMessage(expectedString, expectedArgs);
    }
}
