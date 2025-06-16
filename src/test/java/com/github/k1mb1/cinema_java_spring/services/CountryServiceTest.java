package com.github.k1mb1.cinema_java_spring.services;

import com.github.k1mb1.cinema_java_spring.errors.NotFoundException;
import com.github.k1mb1.cinema_java_spring.mappers.CountryMapper;
import com.github.k1mb1.cinema_java_spring.models.country.CountryEntity;
import com.github.k1mb1.cinema_java_spring.models.country.CountryRequestDto;
import com.github.k1mb1.cinema_java_spring.models.country.CountryResponseDto;
import com.github.k1mb1.cinema_java_spring.repositories.CountryRepository;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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

    CountryEntity countryEntity;
    CountryRequestDto countryRequestDto;
    CountryResponseDto countryResponseDto;

    static final int VALID_ID = 1;
    static final int INVALID_ID = 99;

    @BeforeEach
    void setUp() {
        countryRequestDto = CountryRequestDto.builder()
                .name("USA")
                .build();

        countryEntity = CountryEntity.builder()
                .id(VALID_ID)
                .name(countryRequestDto.getName())
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();

        countryResponseDto = CountryResponseDto.builder()
                .id(countryEntity.getId())
                .name(countryEntity.getName())
                .createAt(countryEntity.getCreateAt())
                .updateAt(countryEntity.getUpdateAt())
                .build();
    }

    @Test
    void createCountry_ShouldReturnCountryResponseDto() {
        when(countryMapper.toEntity(countryRequestDto)).thenReturn(countryEntity);
        when(countryRepository.save(countryEntity)).thenReturn(countryEntity);
        when(countryMapper.toDto(countryEntity)).thenReturn(countryResponseDto);

        val result = countryService.createCountry(countryRequestDto);

        assertCountryResponse(countryResponseDto, result);

        verify(countryRepository).save(countryEntity);
    }

    @Test
    void getCountryById_WithValidId_ShouldReturnCountryResponseDto() {
        when(countryRepository.findById(VALID_ID)).thenReturn(Optional.of(countryEntity));
        when(countryMapper.toDto(countryEntity)).thenReturn(countryResponseDto);

        val result = countryService.getCountryById(VALID_ID);

        assertCountryResponse(countryResponseDto, result);

        verify(countryRepository).findById(VALID_ID);
    }

    @Test
    void getCountryById_WithInvalidId_ShouldThrowNotFoundException() {
        when(countryRepository.findById(INVALID_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> countryService.getCountryById(INVALID_ID))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasMessageContaining(String.valueOf(INVALID_ID));

        verify(countryRepository).findById(INVALID_ID);
    }

    @Test
    void getAllCountries_ShouldReturnPageOfCountryResponseDto() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CountryEntity> countryPage = new PageImpl<>(List.of(countryEntity), pageable, 1);

        when(countryRepository.findAll(pageable)).thenReturn(countryPage);
        when(countryMapper.toDto(countryEntity)).thenReturn(countryResponseDto);

        val result = countryService.getAllCountries(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);
        assertCountryResponse(countryResponseDto, result.getContent().getFirst());

        verify(countryRepository).findAll(pageable);
    }

    @Test
    void updateCountry_WithValidId_ShouldReturnUpdatedCountryResponseDto() {
        val updateRequestDto = CountryRequestDto.builder()
                .name("Updated Country")
                .build();

        val updatedCountry = CountryEntity.builder()
                .id(countryEntity.getId())
                .name(updateRequestDto.getName())
                .createAt(countryEntity.getCreateAt())
                .updateAt(LocalDateTime.now())
                .build();

        val updatedResponseDto = CountryResponseDto.builder()
                .id(updatedCountry.getId())
                .name(updatedCountry.getName())
                .createAt(updatedCountry.getCreateAt())
                .updateAt(updatedCountry.getUpdateAt())
                .build();

        when(countryRepository.findById(VALID_ID)).thenReturn(Optional.of(countryEntity));
        doNothing().when(countryMapper).partialUpdate(updateRequestDto, countryEntity);
        when(countryRepository.save(countryEntity)).thenReturn(updatedCountry);
        when(countryMapper.toDto(updatedCountry)).thenReturn(updatedResponseDto);

        val result = countryService.updateCountry(VALID_ID, updateRequestDto);

        assertCountryResponse(updatedResponseDto, result);

        verify(countryRepository).findById(VALID_ID);
        verify(countryMapper).partialUpdate(updateRequestDto, countryEntity);
        verify(countryRepository).save(countryEntity);
    }

    @Test
    void updateCountry_WithInvalidId_ShouldThrowNotFoundException() {
        when(countryRepository.findById(INVALID_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> countryService.updateCountry(INVALID_ID, countryRequestDto))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasMessageContaining(String.valueOf(INVALID_ID));

        verify(countryRepository).findById(INVALID_ID);
        verify(countryRepository, never()).save(any(CountryEntity.class));
    }

    @Test
    void deleteCountry_WithValidId_ShouldDeleteCountry() {
        when(countryRepository.existsById(VALID_ID)).thenReturn(true);
        doNothing().when(countryRepository).deleteById(VALID_ID);

        countryService.deleteCountry(VALID_ID);

        verify(countryRepository).deleteById(VALID_ID);
    }

    @Test
    void deleteCountry_WithInvalidId_ShouldThrowNotFoundException() {
        when(countryRepository.existsById(INVALID_ID)).thenReturn(false);

        assertThatThrownBy(() -> countryService.deleteCountry(INVALID_ID))
                .isExactlyInstanceOf(NotFoundException.class);

        verify(countryRepository, never()).deleteById(INVALID_ID);
    }

    @Test
    void getCountryEntityById_WithValidId_ShouldReturnCountryEntity() {
        when(countryRepository.findById(VALID_ID)).thenReturn(Optional.of(countryEntity));

        val result = countryService.getCountryEntityById(VALID_ID);

        assertThat(result).isNotNull()
                .extracting(CountryEntity::getId, CountryEntity::getName)
                .containsExactly(countryEntity.getId(), countryEntity.getName());

        verify(countryRepository).findById(VALID_ID);
    }

    @Test
    void getCountryEntityById_WithInvalidId_ShouldThrowNotFoundException() {
        when(countryRepository.findById(INVALID_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> countryService.getCountryEntityById(INVALID_ID))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasMessageContaining(String.valueOf(INVALID_ID));

        verify(countryRepository).findById(INVALID_ID);
    }

    @Test
    void getCountriesByIds_ShouldReturnListOfCountryEntities() {
        List<Integer> ids = List.of(VALID_ID);
        when(countryRepository.findAllById(ids)).thenReturn(List.of(countryEntity));

        val result = countryService.getCountriesByIds(ids);

        assertThat(result).isNotNull()
                .hasSize(1)
                .first()
                .extracting(CountryEntity::getId, CountryEntity::getName)
                .containsExactly(VALID_ID, countryEntity.getName());

        verify(countryRepository).findAllById(ids);
    }

    private void assertCountryResponse(CountryResponseDto expected, CountryResponseDto actual) {
        assertThat(actual).isNotNull()
                .extracting(
                        CountryResponseDto::getId,
                        CountryResponseDto::getName
                )
                .containsExactly(
                        expected.getId(),
                        expected.getName()
                );
    }
}
