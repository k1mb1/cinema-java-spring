package com.github.k1mb1.cinema_java_spring.services;

import com.github.k1mb1.cinema_java_spring.errors.NotFoundException;
import com.github.k1mb1.cinema_java_spring.dtos.user.UserRequestDto;
import com.github.k1mb1.cinema_java_spring.dtos.user.UserResponseDto;
import com.github.k1mb1.cinema_java_spring.mappers.UserMapper;
import com.github.k1mb1.cinema_java_spring.repositories.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.github.k1mb1.cinema_java_spring.errors.ErrorMessages.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
@FieldDefaults(makeFinal = true)
public class UserService {

    UserRepository userRepository;
    UserMapper userMapper;

    public UserResponseDto createUser(@NonNull UserRequestDto userRequestDto) {
        return userMapper.toDto(
                userRepository.save(userMapper.toEntity(userRequestDto))
        );
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserById(@NonNull Integer id) {
        return userMapper.toDto(
                userRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.formatted(id)))
        );
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    public UserResponseDto updateUser(@NonNull Integer id, @NonNull UserRequestDto userRequestDto) {
        val existingUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.formatted(id)));

        userMapper.partialUpdate(userRequestDto, existingUser);

        return userMapper.toDto(userRepository.save(existingUser));
    }

    public void deleteUser(@NonNull Integer id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException(USER_NOT_FOUND.formatted(id));
        }
        userRepository.deleteById(id);
    }
}
