package ru.ssau.tk.faible.coplatebackend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.ssau.tk.faible.coplatebackend.dto.UserLoginRequest;
import ru.ssau.tk.faible.coplatebackend.dto.UserRequest;
import ru.ssau.tk.faible.coplatebackend.dto.UserResponse;
import ru.ssau.tk.faible.coplatebackend.service.UserService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;

    // POST /api/auth/register - регистрация пользователя
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody UserRequest request) {

        // Регистрируем пользователя
        UserResponse userResponse = userService.saveUser(request);;

        // Возвращаем ResponseEntity со статусом 201 CREATED
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    @GetMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody UserLoginRequest request) {

        // TODO: Переделать на использование полученного JWT (??) токена
        UserResponse userResponse = userService.login(request);

        return ResponseEntity.status(HttpStatus.OK).body(userResponse);
    }
}
