package com.github.k1mb1.cinema_java_spring.models.genre;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenreRequestDto {

    @NotBlank
    @Size(min = 2, max = 50)
    String name;
}
