package ru.ssau.tk.faible.coplate.coplatebackend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ru.ssau.tk.faible.coplate.coplatebackend.entity.User;
import ru.ssau.tk.faible.coplate.coplatebackend.repository.UserRepository;

@SpringBootApplication
public class CoplateBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoplateBackendApplication.class, args);
    }
}
