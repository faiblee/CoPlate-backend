package ru.ssau.tk.faible.coplatebackend.repository;

import lombok.extern.slf4j.Slf4j;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class UserRepositoryTest {

//    @Autowired
//    private MockMvc mockMvc;

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

    @Test
    void saveUser() {
        User user = new User("john_d", "hash", "John");

        User saved_user = userRepository.save(user);
        Optional<User> founded_user = userRepository.findByUsername("john_d");

        assertThat(founded_user).isPresent();
        assertThat(founded_user.get().getName()).isEqualTo("John");

        log.info("Test1 was completed!");
    }
}