package com.github.k1mb1.cinema_java_spring.utils;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class UnitTestUtils {
    public static <T extends Throwable> void assertExceptionWithMessage(
            Runnable action,
            Class<T> exceptionClass,
            String expectedSubstring
    ) {
        assertThatThrownBy(action::run)
                .isExactlyInstanceOf(exceptionClass)
                .hasMessageContaining(expectedSubstring);
    }
}
