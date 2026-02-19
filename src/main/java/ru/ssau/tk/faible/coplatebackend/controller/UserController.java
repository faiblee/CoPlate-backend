package ru.ssau.tk.faible.coplatebackend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ssau.tk.faible.coplatebackend.dto.UserRequest;
import ru.ssau.tk.faible.coplatebackend.dto.UserResponse;
import ru.ssau.tk.faible.coplatebackend.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Slf4j
public class UserController {

    private final UserService userService;

    // POST /api/users/register - регистрация пользователя
    @PostMapping("/register")
    public ResponseEntity<UserResponse> saveUser(@RequestBody UserRequest request) {

        // Регистрируем пользователя
        UserResponse userResponse = userService.register(request);

        // Возвращаем ResponseEntity со статусом 201 CREATED
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }
}
