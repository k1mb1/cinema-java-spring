package com.github.k1mb1.cinema_java_spring.controllers;

import com.github.k1mb1.cinema_java_spring.dtos.user.UserRequestDto;
import com.github.k1mb1.cinema_java_spring.dtos.user.UserResponseDto;
import com.github.k1mb1.cinema_java_spring.services.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true)
public class UserController {

    UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@NonNull @RequestBody UserRequestDto userRequestDto) {
        return ResponseEntity.status(CREATED).body(userService.createUser(userRequestDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@NonNull @PathVariable Integer id) {
        return ResponseEntity.status(OK).body(userService.getUserById(id));
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.status(OK).body(userService.getAllUsers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@NonNull @PathVariable Integer id, @NonNull @RequestBody UserRequestDto userRequestDto) {
        return ResponseEntity.status(OK).body(userService.updateUser(id, userRequestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@NonNull @PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.status(NO_CONTENT).build();
    }
}
