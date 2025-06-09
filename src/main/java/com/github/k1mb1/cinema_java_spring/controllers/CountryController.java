package com.github.k1mb1.cinema_java_spring.controllers;

import com.github.k1mb1.cinema_java_spring.dtos.country.CountryRequestDto;
import com.github.k1mb1.cinema_java_spring.dtos.country.CountryResponseDto;
import com.github.k1mb1.cinema_java_spring.services.CountryService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/countries")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true)
public class CountryController {

    CountryService countryService;

    @PostMapping
    public ResponseEntity<CountryResponseDto> createCountry(@NonNull @RequestBody CountryRequestDto countryRequestDto) {
        return ResponseEntity.status(CREATED).body(countryService.createCountry(countryRequestDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CountryResponseDto> getCountryById(@NonNull @PathVariable Integer id) {
        return ResponseEntity.status(OK).body(countryService.getCountryById(id));
    }

    @GetMapping
    public ResponseEntity<List<CountryResponseDto>> getAllCountries() {
        return ResponseEntity.status(OK).body(countryService.getAllCountries());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CountryResponseDto> updateCountry(@NonNull @PathVariable Integer id, @NonNull @RequestBody CountryRequestDto countryRequestDto) {
        return ResponseEntity.status(OK).body(countryService.updateCountry(id, countryRequestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCountry(@NonNull @PathVariable Integer id) {
        countryService.deleteCountry(id);
        return ResponseEntity.status(NO_CONTENT).build();
    }
}
