package com.github.k1mb1.cinema_java_spring.controllers;

import com.github.k1mb1.cinema_java_spring.models.user.UserRequestDto;
import com.github.k1mb1.cinema_java_spring.models.user.UserResponseDto;
import com.github.k1mb1.cinema_java_spring.services.UserService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true)
public class UserController {

    UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(
            @Valid @NonNull @RequestBody UserRequestDto userRequestDto
    ) {
        return ResponseEntity.status(CREATED).body(userService.createUser(userRequestDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@NonNull @PathVariable Integer id) {
        return ResponseEntity.status(OK).body(userService.getUserById(id));
    }

    @GetMapping
    public ResponseEntity<Page<UserResponseDto>> getAllUsers(
            @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.status(OK).body(userService.getAllUsers(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(
            @NonNull @PathVariable Integer id,
            @Valid @NonNull @RequestBody UserRequestDto userRequestDto
    ) {
        return ResponseEntity.status(OK).body(userService.updateUser(id, userRequestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@NonNull @PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.status(NO_CONTENT).build();
    }
}
