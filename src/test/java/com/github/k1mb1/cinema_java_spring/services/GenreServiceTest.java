package com.github.k1mb1.cinema_java_spring.services;

import com.github.k1mb1.cinema_java_spring.errors.NotFoundException;
import com.github.k1mb1.cinema_java_spring.mappers.GenreMapper;
import com.github.k1mb1.cinema_java_spring.models.genre.GenreEntity;
import com.github.k1mb1.cinema_java_spring.models.genre.GenreRequestDto;
import com.github.k1mb1.cinema_java_spring.models.genre.GenreResponseDto;
import com.github.k1mb1.cinema_java_spring.repositories.GenreRepository;
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

import static com.github.k1mb1.cinema_java_spring.errors.ErrorMessages.GENRE_NOT_FOUND;
import static com.github.k1mb1.cinema_java_spring.utils.UnitTestUtils.assertExceptionWithMessage;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenreServiceTest {

    @Mock
    GenreRepository genreRepository;

    @Mock
    GenreMapper genreMapper;

    @InjectMocks
    GenreService genreService;

    GenreEntity genreEntity;
    GenreRequestDto genreRequestDto;
    GenreResponseDto genreResponseDto;

    static final int VALID_ID = 1;
    static final int INVALID_ID = 99;

    @BeforeEach
    void setUp() {
        genreRequestDto = GenreRequestDto.builder()
                .name("Action")
                .build();

        genreEntity = GenreEntity.builder()
                .id(VALID_ID)
                .name(genreRequestDto.getName())
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();

        genreResponseDto = GenreResponseDto.builder()
                .id(genreEntity.getId())
                .name(genreEntity.getName())
                .createAt(genreEntity.getCreateAt())
                .updateAt(genreEntity.getUpdateAt())
                .build();
    }

    @Test
    void createGenre_ShouldReturnGenreResponseDto() {
        when(genreMapper.toEntity(genreRequestDto)).thenReturn(genreEntity);
        when(genreRepository.save(genreEntity)).thenReturn(genreEntity);
        when(genreMapper.toDto(genreEntity)).thenReturn(genreResponseDto);

        val result = genreService.createGenre(genreRequestDto);

        assertGenreResponse(genreResponseDto, result);

        verify(genreRepository).save(genreEntity);
    }

    @Test
    void getGenreById_WithValidId_ShouldReturnGenreResponseDto() {
        when(genreRepository.findById(VALID_ID)).thenReturn(Optional.of(genreEntity));
        when(genreMapper.toDto(genreEntity)).thenReturn(genreResponseDto);

        val result = genreService.getGenreById(VALID_ID);

        assertGenreResponse(genreResponseDto, result);

        verify(genreRepository).findById(VALID_ID);
    }

    @Test
    void getGenreById_WithInvalidId_ShouldThrowEntityNotFoundException() {
        when(genreRepository.findById(INVALID_ID)).thenReturn(Optional.empty());

        assertExceptionWithMessage(
                () -> genreService.getGenreById(INVALID_ID),
                NotFoundException.class,
                GENRE_NOT_FOUND, INVALID_ID
        );

        verify(genreRepository).findById(INVALID_ID);
    }

    @Test
    void getAllGenres_ShouldReturnPageOfGenreResponseDto() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<GenreEntity> genrePage = new PageImpl<>(List.of(genreEntity), pageable, 1);

        when(genreRepository.findAll(pageable)).thenReturn(genrePage);
        when(genreMapper.toDto(genreEntity)).thenReturn(genreResponseDto);

        val result = genreService.getAllGenres(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);
        assertGenreResponse(genreResponseDto, result.getContent().getFirst());

        verify(genreRepository).findAll(pageable);
    }

    @Test
    void updateGenre_WithValidId_ShouldReturnUpdatedGenreResponseDto() {
        val updateRequestDto = GenreRequestDto.builder()
                .name("Updated Genre")
                .build();

        val updatedGenre = GenreEntity.builder()
                .id(genreEntity.getId())
                .name(updateRequestDto.getName())
                .createAt(genreEntity.getCreateAt())
                .updateAt(LocalDateTime.now())
                .build();

        val updatedResponseDto = GenreResponseDto.builder()
                .id(updatedGenre.getId())
                .name(updatedGenre.getName())
                .createAt(updatedGenre.getCreateAt())
                .updateAt(updatedGenre.getUpdateAt())
                .build();

        when(genreRepository.findById(genreEntity.getId())).thenReturn(Optional.of(genreEntity));
        doNothing().when(genreMapper).partialUpdate(updateRequestDto, genreEntity);
        when(genreRepository.save(genreEntity)).thenReturn(updatedGenre);
        when(genreMapper.toDto(updatedGenre)).thenReturn(updatedResponseDto);

        val result = genreService.updateGenre(genreEntity.getId(), updateRequestDto);

        assertGenreResponse(updatedResponseDto, result);

        verify(genreMapper).partialUpdate(updateRequestDto, genreEntity);
        verify(genreRepository).save(genreEntity);
    }

    @Test
    void updateGenre_WithInvalidId_ShouldThrowEntityNotFoundException() {
        when(genreRepository.findById(INVALID_ID)).thenReturn(Optional.empty());

        assertExceptionWithMessage(
                () -> genreService.updateGenre(INVALID_ID, genreRequestDto),
                NotFoundException.class,
                GENRE_NOT_FOUND, INVALID_ID
        );

        verify(genreRepository).findById(INVALID_ID);
        verify(genreRepository, never()).save(any(GenreEntity.class));
    }

    @Test
    void deleteGenre_WithValidId_ShouldDeleteGenre() {
        when(genreRepository.existsById(VALID_ID)).thenReturn(true);
        doNothing().when(genreRepository).deleteById(VALID_ID);

        genreService.deleteGenre(VALID_ID);

        verify(genreRepository).deleteById(VALID_ID);
    }

    @Test
    void deleteGenre_WithInvalidId_ShouldThrowEntityNotFoundException() {
        when(genreRepository.existsById(INVALID_ID)).thenReturn(false);

        assertExceptionWithMessage(
                () -> genreService.deleteGenre(INVALID_ID),
                NotFoundException.class,
                GENRE_NOT_FOUND, INVALID_ID
        );

        verify(genreRepository, never()).deleteById(INVALID_ID);
    }

    @Test
    void getGenreEntityById_WithValidId_ShouldReturnGenreEntity() {
        when(genreRepository.findById(VALID_ID)).thenReturn(Optional.of(genreEntity));

        val result = genreService.findGenreById(VALID_ID);

        assertThat(result).isNotNull()
                .extracting(GenreEntity::getId, GenreEntity::getName)
                .containsExactly(genreEntity.getId(), genreEntity.getName());

        verify(genreRepository).findById(VALID_ID);
    }

    @Test
    void getGenreEntityById_WithInvalidId_ShouldThrowNotFoundException() {
        when(genreRepository.findById(INVALID_ID)).thenReturn(Optional.empty());

        assertExceptionWithMessage(
                () -> genreService.findGenreById(INVALID_ID),
                NotFoundException.class,
                GENRE_NOT_FOUND, INVALID_ID
        );

        verify(genreRepository).findById(INVALID_ID);
    }

    @Test
    void getGenresByIds_ShouldReturnListOfGenreEntities() {
        List<Integer> ids = List.of(VALID_ID);
        when(genreRepository.findAllById(ids)).thenReturn(List.of(genreEntity));

        val result = genreService.findGenresByIds(ids);

        assertThat(result).isNotNull()
                .hasSize(1)
                .first()
                .extracting(GenreEntity::getId, GenreEntity::getName)
                .containsExactly(VALID_ID, genreEntity.getName());

        verify(genreRepository).findAllById(ids);
    }

    private void assertGenreResponse(GenreResponseDto expected, GenreResponseDto actual) {
        assertThat(actual).isNotNull()
                .extracting(
                        GenreResponseDto::getId,
                        GenreResponseDto::getName
                )
                .containsExactly(
                        expected.getId(),
                        expected.getName()
                );
    }
}
