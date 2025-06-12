package com.github.k1mb1.cinema_java_spring.services;

import com.github.k1mb1.cinema_java_spring.dtos.country.CountryRequestDto;
import com.github.k1mb1.cinema_java_spring.dtos.country.CountryResponseDto;
import com.github.k1mb1.cinema_java_spring.entities.Country;
import com.github.k1mb1.cinema_java_spring.errors.NotFoundException;
import com.github.k1mb1.cinema_java_spring.mappers.CountryMapper;
import com.github.k1mb1.cinema_java_spring.repositories.CountryRepository;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CountryServiceTest {

    @Mock
    CountryRepository countryRepository;

    @Mock
    CountryMapper countryMapper;

    @InjectMocks
    CountryService countryService;

    Country country;
    CountryRequestDto countryRequestDto;
    CountryResponseDto countryResponseDto;

    static final int VALID_ID = 1;
    static final int INVALID_ID = 99;

    @BeforeEach
    void setUp() {
        countryRequestDto = CountryRequestDto.builder()
                .name("USA")
                .build();

        country = Country.builder()
                .id(VALID_ID)
                .name(countryRequestDto.getName())
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();

        countryResponseDto = CountryResponseDto.builder()
                .id(country.getId())
                .name(country.getName())
                .createAt(country.getCreateAt())
                .updateAt(country.getUpdateAt())
                .build();
    }

    @Test
    void createCountry_ShouldReturnCountryResponseDto() {
        when(countryMapper.toEntity(countryRequestDto)).thenReturn(country);
        when(countryRepository.save(country)).thenReturn(country);
        when(countryMapper.toDto(country)).thenReturn(countryResponseDto);

        val result = countryService.createCountry(countryRequestDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(countryResponseDto.getId());
        assertThat(result.getName()).isEqualTo(countryResponseDto.getName());

        verify(countryRepository).save(country);
    }

    @Test
    void getCountryById_WithValidId_ShouldReturnCountryResponseDto() {
        when(countryRepository.findById(VALID_ID)).thenReturn(Optional.of(country));
        when(countryMapper.toDto(country)).thenReturn(countryResponseDto);

        val result = countryService.getCountryById(VALID_ID);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(countryResponseDto.getId());
        assertThat(result.getName()).isEqualTo(countryResponseDto.getName());

        verify(countryRepository).findById(VALID_ID);
    }

    @Test
    void getCountryById_WithInvalidId_ShouldThrowEntityNotFoundException() {
        when(countryRepository.findById(INVALID_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> countryService.getCountryById(INVALID_ID))
                .isExactlyInstanceOf(NotFoundException.class);

        verify(countryRepository).findById(INVALID_ID);
    }

    @Test
    void getAllCountries_ShouldReturnListOfCountryResponseDto() {
        when(countryRepository.findAll()).thenReturn(List.of(country));
        when(countryMapper.toDto(country)).thenReturn(countryResponseDto);

        val result = countryService.getAllCountries();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(countryResponseDto.getId());

        verify(countryRepository).findAll();
    }

    @Test
    void updateCountry_WithValidId_ShouldReturnUpdatedCountryResponseDto() {
        val updateRequestDto = CountryRequestDto.builder()
                .name("Updated Country")
                .build();

        val updatedCountry = Country.builder()
                .id(country.getId())
                .name(countryRequestDto.getName())
                .createAt(country.getCreateAt())
                .updateAt(LocalDateTime.now())
                .build();

        val updatedResponseDto = CountryResponseDto.builder()
                .id(updatedCountry.getId())
                .name(updatedCountry.getName())
                .createAt(updatedCountry.getCreateAt())
                .updateAt(updatedCountry.getUpdateAt())
                .build();

        when(countryRepository.findById(country.getId())).thenReturn(Optional.of(country));
        doNothing().when(countryMapper).partialUpdate(updateRequestDto, country);
        when(countryRepository.save(country)).thenReturn(updatedCountry);
        when(countryMapper.toDto(updatedCountry)).thenReturn(updatedResponseDto);

        val result = countryService.updateCountry(country.getId(), updateRequestDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(country.getId());
        assertThat(result.getName()).isEqualTo(updatedCountry.getName());

        verify(countryMapper).partialUpdate(updateRequestDto, country);
        verify(countryRepository).save(country);
    }

    @Test
    void updateCountry_WithInvalidId_ShouldThrowEntityNotFoundException() {
        when(countryRepository.findById(INVALID_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> countryService.updateCountry(INVALID_ID, countryRequestDto))
                .isExactlyInstanceOf(NotFoundException.class);

        verify(countryRepository).findById(INVALID_ID);
        verify(countryRepository, never()).save(any(Country.class));
    }

    @Test
    void deleteCountry_WithValidId_ShouldDeleteCountry() {
        when(countryRepository.existsById(VALID_ID)).thenReturn(true);
        doNothing().when(countryRepository).deleteById(VALID_ID);

        countryService.deleteCountry(VALID_ID);

        verify(countryRepository).deleteById(VALID_ID);
    }

    @Test
    void deleteCountry_WithInvalidId_ShouldThrowEntityNotFoundException() {
        when(countryRepository.existsById(INVALID_ID)).thenReturn(false);

        assertThatThrownBy(() -> countryService.deleteCountry(INVALID_ID))
                .isExactlyInstanceOf(NotFoundException.class);

        verify(countryRepository, never()).deleteById(INVALID_ID);
    }
}
