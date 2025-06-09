package com.github.k1mb1.cinema_java_spring.services;

import com.github.k1mb1.cinema_java_spring.config.NotFoundException;
import com.github.k1mb1.cinema_java_spring.dtos.country.CountryRequestDto;
import com.github.k1mb1.cinema_java_spring.dtos.country.CountryResponseDto;
import com.github.k1mb1.cinema_java_spring.entities.Country;
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
import java.util.HashSet;
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

    @BeforeEach
    void setUp() {
        // Setup test data
        countryRequestDto = CountryRequestDto.builder()
                .name("USA")
                .build();

        country = Country.builder()
                .id(1)
                .name("USA")
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .movies(new HashSet<>())
                .build();

        countryResponseDto = CountryResponseDto.builder()
                .id(1)
                .name("USA")
                .createAt(country.getCreateAt())
                .updateAt(country.getUpdateAt())
                .build();
    }

    @Test
    void createCountry_ShouldReturnCountryResponseDto() {
        // Arrange
        when(countryMapper.toEntity(countryRequestDto)).thenReturn(country);
        when(countryRepository.save(country)).thenReturn(country);
        when(countryMapper.toDto(country)).thenReturn(countryResponseDto);

        // Act
        val result = countryService.createCountry(countryRequestDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(countryResponseDto.getId());
        assertThat(result.getName()).isEqualTo(countryResponseDto.getName());
        verify(countryRepository).save(country);
    }

    @Test
    void getCountryById_WithValidId_ShouldReturnCountryResponseDto() {
        // Arrange
        when(countryRepository.findById(1)).thenReturn(Optional.of(country));
        when(countryMapper.toDto(country)).thenReturn(countryResponseDto);

        // Act
        val result = countryService.getCountryById(1);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(countryResponseDto.getId());
        assertThat(result.getName()).isEqualTo(countryResponseDto.getName());
        verify(countryRepository).findById(1);
    }

    @Test
    void getCountryById_WithInvalidId_ShouldThrowEntityNotFoundException() {
        // Arrange
        when(countryRepository.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> countryService.getCountryById(99))
                .isExactlyInstanceOf(NotFoundException.class);
        verify(countryRepository).findById(99);
    }

    @Test
    void getAllCountries_ShouldReturnListOfCountryResponseDto() {
        // Arrange
        when(countryRepository.findAll()).thenReturn(List.of(country));
        when(countryMapper.toDto(country)).thenReturn(countryResponseDto);

        // Act
        val result = countryService.getAllCountries();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(countryResponseDto.getId());
        verify(countryRepository).findAll();
    }

    @Test
    void updateCountry_WithValidId_ShouldReturnUpdatedCountryResponseDto() {
        // Arrange
        val updateRequestDto = CountryRequestDto.builder()
                .name("Updated Country")
                .build();

        val updatedCountry = Country.builder()
                .id(1)
                .name("Updated Country")
                .createAt(country.getCreateAt())
                .updateAt(LocalDateTime.now())
                .movies(country.getMovies())
                .build();

        val updatedResponseDto = CountryResponseDto.builder()
                .id(1)
                .name("Updated Country")
                .createAt(updatedCountry.getCreateAt())
                .updateAt(updatedCountry.getUpdateAt())
                .build();

        when(countryRepository.findById(1)).thenReturn(Optional.of(country));
        when(countryMapper.toEntity(updateRequestDto)).thenReturn(updatedCountry);
        when(countryRepository.save(any(Country.class))).thenReturn(updatedCountry);
        when(countryMapper.toDto(updatedCountry)).thenReturn(updatedResponseDto);

        // Act
        val result = countryService.updateCountry(1, updateRequestDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(updatedResponseDto.getId());
        assertThat(result.getName()).isEqualTo("Updated Country");
        verify(countryRepository).save(any(Country.class));
    }

    @Test
    void updateCountry_WithInvalidId_ShouldThrowEntityNotFoundException() {
        // Arrange
        when(countryRepository.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> countryService.updateCountry(99, countryRequestDto))
                .isExactlyInstanceOf(NotFoundException.class);
        verify(countryRepository).findById(99);
        verify(countryRepository, never()).save(any(Country.class));
    }

    @Test
    void deleteCountry_WithValidId_ShouldDeleteCountry() {
        // Arrange
        when(countryRepository.existsById(1)).thenReturn(true);
        doNothing().when(countryRepository).deleteById(1);

        // Act
        countryService.deleteCountry(1);

        // Assert
        verify(countryRepository).deleteById(1);
    }

    @Test
    void deleteCountry_WithInvalidId_ShouldThrowEntityNotFoundException() {
        // Arrange
        when(countryRepository.existsById(99)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> countryService.deleteCountry(99))
                .isExactlyInstanceOf(NotFoundException.class);
        verify(countryRepository, never()).deleteById(99);
    }
}
