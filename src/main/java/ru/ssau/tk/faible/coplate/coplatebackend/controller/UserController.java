package ru.ssau.tk.faible.coplate.coplatebackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ssau.tk.faible.coplate.coplatebackend.entity.User;
import ru.ssau.tk.faible.coplate.coplatebackend.repository.UserRepository;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("")
    public List<User> saveUser() {
        User user1 = new User("username1", "123321", "name1");
        User user2 = new User("username2", "123321", "name2");
        User user3 = new User("username3", "123321", "name3");

        return userRepository.saveAll(List.of(user1, user2, user3));
    }
}
