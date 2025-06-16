package com.github.k1mb1.cinema_java_spring.models.genre;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenreResponseDto {

    Integer id;
    String name;
    LocalDateTime createAt;
    LocalDateTime updateAt;
}
