package com.github.k1mb1.cinema_java_spring.controllers;

import com.github.k1mb1.cinema_java_spring.models.country.CountryRequestDto;
import com.github.k1mb1.cinema_java_spring.models.country.CountryResponseDto;
import com.github.k1mb1.cinema_java_spring.services.CountryService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/v1/countries")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true)
public class CountryController {

    CountryService countryService;

    @PostMapping
    public ResponseEntity<CountryResponseDto> createCountry(
            @Valid @NonNull @RequestBody CountryRequestDto countryRequestDto
    ) {
        return ResponseEntity.status(CREATED).body(countryService.createCountry(countryRequestDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CountryResponseDto> getCountryById(@NonNull @PathVariable Integer id) {
        return ResponseEntity.status(OK).body(countryService.getCountryById(id));
    }

    @GetMapping
    public ResponseEntity<Page<CountryResponseDto>> getAllCountries(
            @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.status(OK).body(countryService.getAllCountries(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CountryResponseDto> updateCountry(
            @NonNull @PathVariable Integer id,
            @Valid @NonNull @RequestBody CountryRequestDto countryRequestDto
    ) {
        return ResponseEntity.status(OK).body(countryService.updateCountry(id, countryRequestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCountry(@NonNull @PathVariable Integer id) {
        countryService.deleteCountry(id);
        return ResponseEntity.status(NO_CONTENT).build();
    }
}
