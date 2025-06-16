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

    @NotBlank(message = "Genre name cannot be blank")
    @Size(min = 2, max = 50, message = "Genre name must be between 2 and 50 characters")
    String name;
}
