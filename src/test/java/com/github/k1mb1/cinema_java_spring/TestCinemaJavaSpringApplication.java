package com.github.k1mb1.cinema_java_spring;

import com.github.k1mb1.cinema_java_spring.utils.TestcontainersConfiguration;
import org.springframework.boot.SpringApplication;

public class TestCinemaJavaSpringApplication {
    public static void main(String[] args) {
        SpringApplication.from(CinemaJavaSpringApplication::main).with(TestcontainersConfiguration.class).run(args);
    }
}
