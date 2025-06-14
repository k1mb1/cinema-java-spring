package com.github.k1mb1.cinema_java_spring.services;

import com.github.k1mb1.cinema_java_spring.errors.NotFoundException;
import com.github.k1mb1.cinema_java_spring.dtos.user.UserRequestDto;
import com.github.k1mb1.cinema_java_spring.dtos.user.UserResponseDto;
import com.github.k1mb1.cinema_java_spring.entities.User;
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

    User user;
    UserRequestDto userRequestDto;
    UserResponseDto userResponseDto;

    static final int VALID_ID = 1;
    static final int INVALID_ID = 99;

    @BeforeEach
    void setUp() {
        userRequestDto = UserRequestDto.builder()
                .username("testUser")
                .build();

        user = User.builder()
                .id(VALID_ID)
                .username(userRequestDto.getUsername())
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();

        userResponseDto = UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .createAt(user.getCreateAt())
                .updateAt(user.getUpdateAt())
                .build();
    }

    @Test
    void createUser_ShouldReturnUserResponseDto() {
        when(userMapper.toEntity(userRequestDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userResponseDto);

        val result = userService.createUser(userRequestDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userResponseDto.getId());
        assertThat(result.getUsername()).isEqualTo(userResponseDto.getUsername());
        
        verify(userRepository).save(user);
    }

    @Test
    void getUserById_WithValidId_ShouldReturnUserResponseDto() {
        when(userRepository.findById(VALID_ID)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userResponseDto);

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
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toDto(user)).thenReturn(userResponseDto);

        val result = userService.getAllUsers();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(userResponseDto.getId());

        verify(userRepository).findAll();
    }

    @Test
    void updateUser_WithValidId_ShouldReturnUpdatedUserResponseDto() {
        val updateRequestDto = UserRequestDto.builder()
                .username("updatedUser")
                .build();

        val updatedUser = User.builder()
                .id(user.getId())
                .username(userRequestDto.getUsername())
                .createAt(user.getCreateAt())
                .updateAt(LocalDateTime.now())
                .watchedMovies(user.getWatchedMovies())
                .build();

        val updatedResponseDto = UserResponseDto.builder()
                .id(updatedUser.getId())
                .username(updatedUser.getUsername())
                .createAt(updatedUser.getCreateAt())
                .updateAt(updatedUser.getUpdateAt())
                .build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        doNothing().when(userMapper).partialUpdate(updateRequestDto, user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(updatedResponseDto);

        val result = userService.updateUser(user.getId(), updateRequestDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(user.getId());
        assertThat(result.getUsername()).isEqualTo(updatedResponseDto.getUsername());

        verify(userMapper).partialUpdate(updateRequestDto, user);
        verify(userRepository).save(user);
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
