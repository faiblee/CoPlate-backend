package ru.ssau.tk.faible.coplatebackend.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.utility.DockerImageName;
import ru.ssau.tk.faible.coplatebackend.dto.UserResponse;
import ru.ssau.tk.faible.coplatebackend.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Slf4j
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

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
    void saveUser() throws Exception {
        String userJson = """
            {
                "username": "testuser",
                "password": "testpass",
                "name": "Test Name"
            }
            """;


        var mockRes = mockMvc.perform(post("http://localhost:8080/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andReturn();
        assertThat(mockRes.getResponse().getStatus()).isEqualTo(201);
        assertThat(mockRes.getResponse().getContentAsString()).contains("testuser");
        assertThat(mockRes.getResponse().getContentAsString()).contains("Test Name");
    }

    @Test
    void updateUser() throws Exception {

        String userJson = """
            {
                "username": "testuser2",
                "password": "testpass",
                "name": "Test Name"
            }
            """;

        String updateJson = """
                {
                    "username": "updUsername",
                    "old_password": "testpass",
                    "new_password": "newpass",
                    "name": "New Name"
                }
                """;

        var mockRes = mockMvc.perform(post("http://localhost:8080/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andReturn();

        UserResponse userResponse = new ObjectMapper().readValue(mockRes.getResponse().getContentAsString(), UserResponse.class);

        long id = userResponse.getId();

        mockMvc.perform(put("http://localhost:8080/api/users/" + id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updUsername"))
                .andExpect(jsonPath("$.name").value("New Name"));
    }

    @Test
    void deleteUser() throws Exception {
        String userJson = """
            {
                "username": "testuser3",
                "password": "testpass",
                "name": "Test Name"
            }
            """;

        var result = mockMvc.perform(post("http://localhost:8080/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andReturn();

        long id = new ObjectMapper().readValue(result.getResponse().getContentAsString(), UserResponse.class).getId();

        mockMvc.perform(delete("http://localhost:8080/api/users/" + id))
                .andExpect(status().isOk());

        mockMvc.perform(get("http://localhost:8080/api/users/" + id))
                .andExpect(status().isNotFound());
    }
}
