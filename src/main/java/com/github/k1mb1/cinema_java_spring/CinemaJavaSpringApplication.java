package com.github.k1mb1.cinema_java_spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class CinemaJavaSpringApplication {
    public static void main(String[] args) {
        SpringApplication.run(CinemaJavaSpringApplication.class, args);
    }
}
