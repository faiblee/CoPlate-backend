package ru.ssau.tk.faible.coplatebackend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk.faible.coplatebackend.dto.AuthResponse;
import ru.ssau.tk.faible.coplatebackend.dto.UserLoginRequest;
import ru.ssau.tk.faible.coplatebackend.dto.UserRequest;
import ru.ssau.tk.faible.coplatebackend.dto.UserResponse;
import ru.ssau.tk.faible.coplatebackend.entity.UserDetailsImplementation;
import ru.ssau.tk.faible.coplatebackend.jwt.JwtCore;
import ru.ssau.tk.faible.coplatebackend.service.UserService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtCore jwtCore;

    // POST /api/auth/register - регистрация пользователя
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody UserRequest request) {
        log.debug("Получен запрос на регистрацию пользователя с username={}", request.getUsername());
        // Регистрируем пользователя
        UserResponse userResponse = userService.saveUser(request);
        log.info("Пользователь с username={} успешно добавлен в базу данных с id={}", userResponse.getUsername(), userResponse.getId());
        String token = jwtCore.generateToken(userResponse.getId(), userResponse.getUsername());
        log.debug("Токен успешно сгенерирован");
        AuthResponse authResponse = new AuthResponse(token, userResponse.getId(), userResponse.getUsername(), userResponse.getName());
        // Возвращаем ResponseEntity со статусом 201 CREATED
        return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody UserLoginRequest request, @AuthenticationPrincipal UserDetailsImplementation currentUser) {

        log.debug("Получен запрос на авторизацию пользователя с username={}", request.getUsername());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        log.info("Пользователь успешно авторизован");

        UserResponse userResponse = userService.findByUsername(request.getUsername(), currentUser);

        log.debug("Пользователь успешно найден в бд");
        String token = jwtCore.generateToken(userResponse.getId(), request.getUsername());
        AuthResponse authResponse = new AuthResponse(token, userResponse.getId(), request.getUsername(), userResponse.getName());

        return ResponseEntity.status(HttpStatus.OK).body(authResponse);
    }
}
