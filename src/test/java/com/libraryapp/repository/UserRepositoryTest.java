package com.libraryapp.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.libraryapp.model.User;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataMongoTest
@ActiveProfiles("test")
@Testcontainers
public class UserRepositoryTest {

    @Container
    private static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.3")
            .withExposedPorts(27017);

    @Autowired
    private UserRepository userRepository;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void findByEmail_ValidUserEmail_ReturnsUser() {
        // Given
        User expectedUser = new User();
        expectedUser.setEmail("test@example.com");
        expectedUser.setPassword("password123");
        expectedUser.setFirstName("John");
        expectedUser.setLastName("Doe");
        expectedUser.setRole(User.Role.CUSTOMER);
        expectedUser.setIsDeleted(false);

        // When
        userRepository.save(expectedUser);
        Optional<User> actual = userRepository.findByEmail("test@example.com");

        // Then
        assertTrue(actual.isPresent(), "User should be present");
        assertEquals(actual.get(), expectedUser, "User should be the same");
    }

    @Test
    void findByEmail_InvalidUserEmail_ReturnsEmptyOptional() {
        // Given
        String nonExistentEmail = "invalid@example.com";

        // When
        Optional<User> actual = userRepository.findByEmail(nonExistentEmail);

        // Then
        assertFalse(actual.isPresent(), "User should not be present");
    }
}
