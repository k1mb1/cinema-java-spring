package com.github.k1mb1.cinema_java_spring.models.country;

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
public class CountryRequestDto {

    @NotBlank
    @Size(min = 2, max = 100)
    String name;
}
