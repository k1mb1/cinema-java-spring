package com.github.k1mb1.cinema_java_spring.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {

    Integer id;
    String username;
    LocalDateTime createAt;
    LocalDateTime updateAt;
}
