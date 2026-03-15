package ru.ssau.tk.faible.coplatebackend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk.faible.coplatebackend.dto.UserPutRequest;
import ru.ssau.tk.faible.coplatebackend.dto.UserRequest;
import ru.ssau.tk.faible.coplatebackend.dto.UserResponse;
import ru.ssau.tk.faible.coplatebackend.entity.UserDetailsImplementation;
import ru.ssau.tk.faible.coplatebackend.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Slf4j
public class UserController {

    private final UserService userService;

    // /api/users/{id} - получение пользователя по id
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImplementation currentUser
    ) {

        log.debug("Получен запрос на получение пользователя по id = {}", id);
        UserResponse userResponse = userService.findById(id, currentUser);

        return ResponseEntity.status(HttpStatus.OK).body(userResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> changeUserById(
            @PathVariable Long id,
            @RequestBody UserPutRequest userRequest,
            @AuthenticationPrincipal UserDetailsImplementation currentUser
    ) {

        UserResponse userResponse = userService.updateUser(userRequest, id, currentUser);

        return ResponseEntity.status(HttpStatus.OK).body(userResponse);
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImplementation currentUser) {
        userService.deleteUser(id, currentUser);
    }
}
