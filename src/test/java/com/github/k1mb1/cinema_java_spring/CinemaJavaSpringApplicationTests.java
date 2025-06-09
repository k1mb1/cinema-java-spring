package com.github.k1mb1.cinema_java_spring;

import com.github.k1mb1.cinema_java_spring.utils.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class CinemaJavaSpringApplicationTests {

    @Test
    void contextLoads() {
    }

}
