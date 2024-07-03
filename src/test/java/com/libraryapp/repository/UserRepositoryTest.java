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
    void testSaveAndFindUserByEmail() {
        // Given
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole(User.Role.CUSTOMER);
        user.setIsDeleted(false);

        // When
        userRepository.save(user);
        Optional<User> foundUser = userRepository.findByEmail("test@example.com");

        // Then
        assertTrue(foundUser.isPresent(), "User should be present");
        assertEquals("John", foundUser.get().getFirstName(), "User first name should match");
        assertEquals("Doe", foundUser.get().getLastName(), "User last name should match");
        assertEquals(User.Role.CUSTOMER, foundUser.get().getRole(), "User role should match");
    }

    @Test
    void testFindByEmail_NotFound() {
        // Given
        String nonExistentEmail = "invalid@example.com";

        // When
        Optional<User> foundUser = userRepository.findByEmail(nonExistentEmail);

        // Then
        assertFalse(foundUser.isPresent(), "User should not be present");
    }
}
