package com.github.k1mb1.cinema_java_spring.services;

import com.github.k1mb1.cinema_java_spring.models.user.UserEntity;
import com.github.k1mb1.cinema_java_spring.models.user.UserRequestDto;
import com.github.k1mb1.cinema_java_spring.models.user.UserResponseDto;
import com.github.k1mb1.cinema_java_spring.errors.NotFoundException;
import com.github.k1mb1.cinema_java_spring.mappers.UserMapper;
import com.github.k1mb1.cinema_java_spring.repositories.UserRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    UserMapper userMapper;

    @InjectMocks
    UserService userService;

    UserEntity userEntity;
    UserRequestDto userRequestDto;
    UserResponseDto userResponseDto;

    static final int VALID_ID = 1;
    static final int INVALID_ID = 99;

    @BeforeEach
    void setUp() {
        userRequestDto = UserRequestDto.builder()
                .username("testUser")
                .build();

        userEntity = UserEntity.builder()
                .id(VALID_ID)
                .username(userRequestDto.getUsername())
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();

        userResponseDto = UserResponseDto.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .createAt(userEntity.getCreateAt())
                .updateAt(userEntity.getUpdateAt())
                .build();
    }

    @Test
    void createUser_ShouldReturnUserResponseDto() {
        when(userMapper.toEntity(userRequestDto)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(userMapper.toDto(userEntity)).thenReturn(userResponseDto);

        val result = userService.createUser(userRequestDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userResponseDto.getId());
        assertThat(result.getUsername()).isEqualTo(userResponseDto.getUsername());

        verify(userRepository).save(userEntity);
    }

    @Test
    void getUserById_WithValidId_ShouldReturnUserResponseDto() {
        when(userRepository.findById(VALID_ID)).thenReturn(Optional.of(userEntity));
        when(userMapper.toDto(userEntity)).thenReturn(userResponseDto);

        val result = userService.getUserById(VALID_ID);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userResponseDto.getId());
        assertThat(result.getUsername()).isEqualTo(userResponseDto.getUsername());

        verify(userRepository).findById(VALID_ID);
    }

    @Test
    void getUserById_WithInvalidId_ShouldThrowEntityNotFoundException() {
        when(userRepository.findById(INVALID_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(INVALID_ID))
                .isExactlyInstanceOf(NotFoundException.class);

        verify(userRepository).findById(INVALID_ID);
    }

    @Test
    void getAllUsers_ShouldReturnListOfUserResponseDto() {
        when(userRepository.findAll()).thenReturn(List.of(userEntity));
        when(userMapper.toDto(userEntity)).thenReturn(userResponseDto);

//        val result = userService.getAllUsers();

//        assertThat(result).isNotNull();
//        assertThat(result).hasSize(1);
//        assertThat(result.getFirst().getId()).isEqualTo(userResponseDto.getId());

        verify(userRepository).findAll();
    }

    @Test
    void updateUser_WithValidId_ShouldReturnUpdatedUserResponseDto() {
        val updateRequestDto = UserRequestDto.builder()
                .username("updatedUser")
                .build();

        val updatedUser = UserEntity.builder()
                .id(userEntity.getId())
                .username(userRequestDto.getUsername())
                .createAt(userEntity.getCreateAt())
                .updateAt(LocalDateTime.now())
                .watchedMovies(userEntity.getWatchedMovies())
                .build();

        val updatedResponseDto = UserResponseDto.builder()
                .id(updatedUser.getId())
                .username(updatedUser.getUsername())
                .createAt(updatedUser.getCreateAt())
                .updateAt(updatedUser.getUpdateAt())
                .build();

        when(userRepository.findById(userEntity.getId())).thenReturn(Optional.of(userEntity));
        doNothing().when(userMapper).partialUpdate(updateRequestDto, userEntity);
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(userMapper.toDto(userEntity)).thenReturn(updatedResponseDto);

        val result = userService.updateUser(userEntity.getId(), updateRequestDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userEntity.getId());
        assertThat(result.getUsername()).isEqualTo(updatedResponseDto.getUsername());

        verify(userMapper).partialUpdate(updateRequestDto, userEntity);
        verify(userRepository).save(userEntity);
    }

    @Test
    void updateUser_WithInvalidId_ShouldThrowEntityNotFoundException() {
        when(userRepository.findById(INVALID_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(INVALID_ID, userRequestDto))
                .isExactlyInstanceOf(NotFoundException.class);

        verify(userRepository).findById(INVALID_ID);
    }

    @Test
    void deleteUser_WithValidId_ShouldDeleteUser() {
        when(userRepository.existsById(VALID_ID)).thenReturn(true);
        doNothing().when(userRepository).deleteById(VALID_ID);

        userService.deleteUser(VALID_ID);

        verify(userRepository).deleteById(VALID_ID);
    }

    @Test
    void deleteUser_WithInvalidId_ShouldThrowEntityNotFoundException() {
        when(userRepository.existsById(INVALID_ID)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteUser(INVALID_ID))
                .isExactlyInstanceOf(NotFoundException.class);

        verify(userRepository, never()).deleteById(INVALID_ID);
    }
}
