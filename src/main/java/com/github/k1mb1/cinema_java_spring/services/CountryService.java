package com.github.k1mb1.cinema_java_spring.services;

import com.github.k1mb1.cinema_java_spring.dtos.country.CountryRequestDto;
import com.github.k1mb1.cinema_java_spring.dtos.country.CountryResponseDto;
import com.github.k1mb1.cinema_java_spring.errors.NotFoundException;
import com.github.k1mb1.cinema_java_spring.mappers.CountryMapper;
import com.github.k1mb1.cinema_java_spring.repositories.CountryRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.github.k1mb1.cinema_java_spring.errors.ErrorMessages.COUNTRY_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
@FieldDefaults(makeFinal = true)
public class CountryService {

    CountryRepository countryRepository;
    CountryMapper countryMapper;

    public CountryResponseDto createCountry(@NonNull CountryRequestDto countryRequestDto) {
        return countryMapper.toDto(
                countryRepository.save(countryMapper.toEntity(countryRequestDto))
        );
    }

    @Transactional(readOnly = true)
    public CountryResponseDto getCountryById(@NonNull Integer id) {
        return countryMapper.toDto(
                countryRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException(COUNTRY_NOT_FOUND.formatted(id)))
        );
    }

    @Transactional(readOnly = true)
    public List<CountryResponseDto> getAllCountries() {
        return countryRepository.findAll().stream()
                .map(countryMapper::toDto)
                .toList();
    }

    public CountryResponseDto updateCountry(@NonNull Integer id, @NonNull CountryRequestDto countryRequestDto) {
        val existingCountry = countryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(COUNTRY_NOT_FOUND.formatted(id)));

        countryMapper.partialUpdate(countryRequestDto, existingCountry);

        return countryMapper.toDto(countryRepository.save(existingCountry));
    }

    public void deleteCountry(@NonNull Integer id) {
        if (!countryRepository.existsById(id)) {
            throw new NotFoundException(COUNTRY_NOT_FOUND.formatted(id));
        }
        countryRepository.deleteById(id);
    }
}
