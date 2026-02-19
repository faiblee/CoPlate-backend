package ru.ssau.tk.faible.coplatebackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.ssau.tk.faible.coplatebackend.dto.UserLoginRequest;
import ru.ssau.tk.faible.coplatebackend.dto.UserRequest;
import ru.ssau.tk.faible.coplatebackend.dto.UserResponse;
import ru.ssau.tk.faible.coplatebackend.entity.User;
import ru.ssau.tk.faible.coplatebackend.exception.InvalidPasswordException;
import ru.ssau.tk.faible.coplatebackend.exception.UserAlreadyExistsException;
import ru.ssau.tk.faible.coplatebackend.exception.UserNotFoundException;
import ru.ssau.tk.faible.coplatebackend.repository.UserRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponse saveUser(UserRequest userRequest) throws ResponseStatusException {
        // Создаем Entity для добавления в БД
        User userEntity = new User(
                userRequest.getUsername(),
                userRequest.getPassword(),
                userRequest.getName()
        );

        // Сохраняем User в БД
        User savedUser;
        try {
            savedUser = userRepository.save(userEntity);
        } catch (Exception e) {
            throw new UserAlreadyExistsException(userRequest.getUsername());
        }

        // Возвращаем DTO ответа
        return new UserResponse(savedUser.getId(), savedUser.getUsername(), savedUser.getName());
    }

    // TODO: Возвращать не UserResponse, а также JWT (??) токен для последующего использования
    public UserResponse login(UserLoginRequest loginRequest) throws ResponseStatusException {
        User user = userRepository.findByUsername(loginRequest.getUsername()).orElseThrow(() -> new UserNotFoundException(loginRequest.getUsername()));

        // Если пароль введен верно
        // TODO: Заменить на нормальную проверку хэшей с помощью Security
        if (user.getPasswordHash().equals(loginRequest.getPassword())) {
            return new UserResponse(user.getId(), user.getUsername(), user.getName());
        } else {
            throw new InvalidPasswordException();
        }
    }

    public UserResponse findById(Long id) throws ResponseStatusException {

        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));

        return new UserResponse(user.getId(), user.getUsername(), user.getName());

    }
}
