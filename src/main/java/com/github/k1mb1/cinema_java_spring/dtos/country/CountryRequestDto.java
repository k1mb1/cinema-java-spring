package com.github.k1mb1.cinema_java_spring.dtos.country;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CountryRequestDto {

    @NotBlank(message = "Country name cannot be blank")
    String name;
}
