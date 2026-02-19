package ru.ssau.tk.faible.coplatebackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.ssau.tk.faible.coplatebackend.dto.UserRequest;
import ru.ssau.tk.faible.coplatebackend.dto.UserResponse;
import ru.ssau.tk.faible.coplatebackend.entity.User;
import ru.ssau.tk.faible.coplatebackend.repository.UserRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponse register(UserRequest userRequest) {
        // Создаем Entity для добавления в БД
        User userEntity = new User(
                userRequest.getUsername(),
                userRequest.getPassword(),
                userRequest.getName()
        );

        // Сохраняем User в БД
        User savedUser = userRepository.save(userEntity);

        // Возвращаем DTO ответа
        return new UserResponse(savedUser.getId(), savedUser.getUsername(), savedUser.getName());
    }
}
