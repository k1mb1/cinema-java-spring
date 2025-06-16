package com.github.k1mb1.cinema_java_spring.services;

import com.github.k1mb1.cinema_java_spring.errors.NotFoundException;
import com.github.k1mb1.cinema_java_spring.mappers.CountryMapper;
import com.github.k1mb1.cinema_java_spring.models.country.CountryEntity;
import com.github.k1mb1.cinema_java_spring.models.country.CountryRequestDto;
import com.github.k1mb1.cinema_java_spring.models.country.CountryResponseDto;
import com.github.k1mb1.cinema_java_spring.repositories.CountryRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public CountryResponseDto createCountry(
            @NonNull CountryRequestDto countryRequestDto
    ) {
        return countryMapper.toDto(
                countryRepository.save(countryMapper.toEntity(countryRequestDto))
        );
    }

    @Transactional(readOnly = true)
    public CountryResponseDto getCountryById(@NonNull Integer id) {
        return countryMapper.toDto(getCountryEntityById(id));
    }

    @Transactional(readOnly = true)
    public Page<CountryResponseDto> getAllCountries(
            @NonNull Pageable pageable
    ) {
        return countryRepository.findAll(pageable).map(countryMapper::toDto);
    }

    public CountryResponseDto updateCountry(
            @NonNull Integer id,
            @NonNull CountryRequestDto countryRequestDto
    ) {
        val existingCountry = getCountryEntityById(id);

        countryMapper.partialUpdate(countryRequestDto, existingCountry);

        return countryMapper.toDto(countryRepository.save(existingCountry));
    }

    public void deleteCountry(@NonNull Integer id) {
        if (!countryRepository.existsById(id)) {
            throw new NotFoundException(COUNTRY_NOT_FOUND.formatted(id));
        }
        countryRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public CountryEntity getCountryEntityById(@NonNull Integer id) {
        return countryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(COUNTRY_NOT_FOUND.formatted(id)));
    }

    @Transactional(readOnly = true)
    public List<CountryEntity> getCountriesByIds(@NonNull Iterable<Integer> ids) {
        return countryRepository.findAllById(ids);
    }
}
