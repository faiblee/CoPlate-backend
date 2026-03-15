package ru.ssau.tk.faible.coplatebackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.ssau.tk.faible.coplatebackend.dto.UserLoginRequest;
import ru.ssau.tk.faible.coplatebackend.dto.UserPutRequest;
import ru.ssau.tk.faible.coplatebackend.dto.UserRequest;
import ru.ssau.tk.faible.coplatebackend.dto.UserResponse;
import ru.ssau.tk.faible.coplatebackend.entity.User;
import ru.ssau.tk.faible.coplatebackend.entity.UserDetailsImplementation;
import ru.ssau.tk.faible.coplatebackend.exception.*;
import ru.ssau.tk.faible.coplatebackend.repository.UserRepository;

import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse saveUser(UserRequest userRequest) throws ResponseStatusException {
        // Создаем Entity для добавления в БД
        User userEntity = new User(
                userRequest.getUsername(),
                passwordEncoder.encode(userRequest.getPassword()),
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

    public UserResponse findById(Long id, UserDetailsImplementation currentUser) throws ResponseStatusException {

        // если пользователь не авторизован
        if (currentUser == null) {
            throw new UnauthorizedException();
        }
        // если запрашивается не текущий пользователь
        if ((!Objects.equals(currentUser.getId(), id) && !currentUser.getRole().equals("ADMIN"))) {
            throw new ForbiddenException();
        }
        // Доступ разрешен
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));

        log.debug("Пользователь с id = {} успешно найден", id);

        return new UserResponse(user.getId(), user.getUsername(), user.getName());

    }


    public UserResponse updateUser(UserPutRequest userRequest, Long id, UserDetailsImplementation currentUser) {

        // если пользователь не авторизован
        if (currentUser == null) {
            throw new UnauthorizedException();
        }
        // если запрашивается не текущий пользователь
        if ((!Objects.equals(currentUser.getId(), id) && !currentUser.getRole().equals("ADMIN"))) {
            throw new ForbiddenException();
        }
        User user_to_update = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        if (userRequest.getUsername() != null) { // если передан новый username
            user_to_update.setUsername(userRequest.getUsername());
        }
        if (userRequest.getName() != null) { // если передано новое имя
            user_to_update.setName(userRequest.getName());
        }
        if (userRequest.getNew_password() != null && userRequest.getOld_password() != null) { // если переданы пароли
            String old_password = userRequest.getOld_password();
            String new_password = userRequest.getNew_password();
            if (passwordEncoder.matches(old_password, user_to_update.getPasswordHash())) { // если введен верный старый пароль
                // меняем хэш пароля
                user_to_update.setPasswordHash(passwordEncoder.encode(new_password));
            }
            else {
                throw new InvalidPasswordException();
            }
        }

        User saved_user = userRepository.save(user_to_update);

        return new UserResponse(saved_user.getId(), saved_user.getUsername(), saved_user.getName());
    }

    public void deleteUser(Long id, UserDetailsImplementation currentUser) {
        // если пользователь не авторизован
        if (currentUser == null) {
            throw new UnauthorizedException();
        }
        // если запрашивается не текущий пользователь
        if ((!Objects.equals(currentUser.getId(), id) && !currentUser.getRole().equals("ADMIN"))) {
            throw new ForbiddenException();
        }
        userRepository.deleteById(id);
    }


    public UserResponse findByUsername(String username, UserDetailsImplementation currentUser) {
        // если пользователь не авторизован
        if (currentUser == null) {
            throw new UnauthorizedException();
        }
        // если запрашивается не текущий пользователь
        if ((!Objects.equals(currentUser.getUsername(), username) && !currentUser.getRole().equals("ADMIN"))) {
            throw new ForbiddenException();
        }

        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new UserNotFoundException(username));

        return new UserResponse(user.getId(), user.getUsername(), user.getName());
    }
}
