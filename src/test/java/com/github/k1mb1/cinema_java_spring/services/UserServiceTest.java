package com.github.k1mb1.cinema_java_spring.services;

import com.github.k1mb1.cinema_java_spring.config.NotFoundException;
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
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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

    @BeforeEach
    void setUp() {
        // Setup test data
        userRequestDto = UserRequestDto.builder()
                .username("testUser")
                .build();

        user = User.builder()
                .id(1)
                .username("testUser")
                .watchedMovies(Set.of())
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();

        userResponseDto = UserResponseDto.builder()
                .id(1)
                .username("testUser")
                .createAt(user.getCreateAt())
                .updateAt(user.getUpdateAt())
                .build();
    }

    @Test
    void createUser_ShouldReturnUserResponseDto() {
        // Arrange
        when(userMapper.toEntity(userRequestDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userResponseDto);

        // Act
        val result = userService.createUser(userRequestDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userResponseDto.getId());
        assertThat(result.getUsername()).isEqualTo(userResponseDto.getUsername());
        verify(userRepository).save(user);
        verify(userMapper).toDto(user);
    }

    @Test
    void getUserById_WithValidId_ShouldReturnUserResponseDto() {
        // Arrange
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userResponseDto);

        // Act
        val result = userService.getUserById(1);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userResponseDto.getId());
        assertThat(result.getUsername()).isEqualTo(userResponseDto.getUsername());
        verify(userRepository).findById(1);
    }

    @Test
    void getUserById_WithInvalidId_ShouldThrowEntityNotFoundException() {
        // Arrange
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.getUserById(99))
                .isExactlyInstanceOf(NotFoundException.class);
        verify(userRepository).findById(99);
    }

    @Test
    void getAllUsers_ShouldReturnListOfUserResponseDto() {
        // Arrange
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toDto(user)).thenReturn(userResponseDto);

        // Act
        val result = userService.getAllUsers();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(userResponseDto.getId());
        verify(userRepository).findAll();
    }

    @Test
    void updateUser_WithValidId_ShouldReturnUpdatedUserResponseDto() {
        // Arrange
        val updateRequestDto = UserRequestDto.builder()
                .username("updatedUser")
                .build();

        val updatedUser = User.builder()
                .id(1)
                .username("updatedUser")
                .createAt(user.getCreateAt())
                .updateAt(LocalDateTime.now())
                .watchedMovies(user.getWatchedMovies())
                .build();

        val updatedResponseDto = UserResponseDto.builder()
                .id(1)
                .username("updatedUser")
                .createAt(updatedUser.getCreateAt())
                .updateAt(updatedUser.getUpdateAt())
                .build();

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userMapper.toEntity(updateRequestDto)).thenReturn(updatedUser);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.toDto(updatedUser)).thenReturn(updatedResponseDto);

        // Act
        val result = userService.updateUser(1, updateRequestDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(updatedResponseDto.getId());
        assertThat(result.getUsername()).isEqualTo("updatedUser");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_WithInvalidId_ShouldThrowEntityNotFoundException() {
        // Arrange
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.updateUser(99, userRequestDto))
                .isExactlyInstanceOf(NotFoundException.class);
        verify(userRepository).findById(99);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_WithValidId_ShouldDeleteUser() {
        // Arrange
        when(userRepository.existsById(1)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1);

        // Act
        userService.deleteUser(1);

        // Assert
        verify(userRepository).deleteById(1);
    }

    @Test
    void deleteUser_WithInvalidId_ShouldThrowEntityNotFoundException() {
        // Arrange
        when(userRepository.existsById(99)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> userService.deleteUser(99))
                .isExactlyInstanceOf(NotFoundException.class);
        verify(userRepository, never()).deleteById(99);
    }
}
