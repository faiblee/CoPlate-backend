package ru.ssau.tk.faible.coplatebackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.ssau.tk.faible.coplatebackend.dto.UserLoginRequest;
import ru.ssau.tk.faible.coplatebackend.dto.UserPutRequest;
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

    public UserResponse updateUser(UserPutRequest userRequest, Long id) {
        // TODO: Проверка прав
        User updated_user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        if (userRequest.getUsername() != null) { // если передан новый username
            updated_user.setUsername(userRequest.getUsername());
        }
        if (userRequest.getName() != null) { // если передано новое имя
            updated_user.setName(userRequest.getName());
        }
        if (userRequest.getNew_password() != null && userRequest.getOld_password() != null) { // если переданы пароли
            String old_password = userRequest.getOld_password();
            String new_password = userRequest.getNew_password();
            // TODO: Переделать на сравнение хэшей
            if (old_password.equals(updated_user.getPasswordHash())) { // если введен верный старый пароль
                // TODO: Переделать на добавление хэшированного пароля
                updated_user.setPasswordHash(new_password);
            }
            else {
                throw new InvalidPasswordException();
            }
        }

        User saved_user = userRepository.save(updated_user);

        return new UserResponse(saved_user.getId(), saved_user.getUsername(), saved_user.getName());
    }

    public void deleteUser(Long id) {
        // TODO: Проверка прав
        userRepository.deleteById(id);
    }
}
