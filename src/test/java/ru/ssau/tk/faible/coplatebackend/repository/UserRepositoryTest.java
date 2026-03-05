package ru.ssau.tk.faible.coplatebackend.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import ru.ssau.tk.faible.coplatebackend.entity.User;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Container
    private static final PostgreSQLContainer postgres = new PostgreSQLContainer(DockerImageName.parse("postgres:14.21"));

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("john_d", "hash", "John");
    }

    @Test
    void saveUser() {
        User saved_user = userRepository.save(user);
        Optional<User> founded_user = userRepository.findByUsername("john_d");

        assertThat(founded_user).isPresent();
        assertThat(founded_user.get().getName()).isEqualTo("John");

        log.info("Test1 was completed!");

        userRepository.deleteById(saved_user.getId());
    }

    @Test
    void updateUser() {
        User saved_user = userRepository.save(user);
        User new_user = new User(saved_user.getId(), "new_john123", "new_pass", "Johnathan", saved_user.getFamily(), saved_user.getRole());

        User updated_user = userRepository.save(new_user);

        assertThat(updated_user.getName()).isEqualTo("Johnathan");
        assertThat(updated_user.getPasswordHash()).isEqualTo("new_pass");
        assertThat(updated_user.getUsername()).isEqualTo("new_john123");
        assertThat(updated_user.getId()).isEqualTo(saved_user.getId());

        userRepository.deleteById(updated_user.getId());
    }

    @Test
    void deleteUser() {
        User saved_user = userRepository.save(user);

        userRepository.deleteById(saved_user.getId());

        assertThat(userRepository.findById(saved_user.getId())).isEmpty();
    }
}