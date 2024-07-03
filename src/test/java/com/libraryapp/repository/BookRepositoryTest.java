package com.libraryapp.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.libraryapp.model.Book;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataMongoTest
@ActiveProfiles("test")
@Testcontainers
public class BookRepositoryTest {

    @Container
    private static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.3")
            .withExposedPorts(27017);

    @Autowired
    private BookRepository bookRepository;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
    }

    @Test
    void testSaveAndFindBook() {
        // Given
        Book book = Book.builder()
                .title("Effective Java")
                .author("Joshua Bloch")
                .category("Programming")
                .isDeleted(false)
                .build();

        // When
        bookRepository.save(book);
        List<Book> books = bookRepository.findAllByIsDeletedFalse(PageRequest.of(0, 10));

        // Then
        assertEquals(1, books.size(), "There should be one book");
        assertEquals("Effective Java", books.get(0).getTitle(), "Book title should match");
    }

    @Test
    void testFindAllByIsDeletedFalse() {
        // Given
        Book book1 = Book.builder()
                .title("Spring in Action")
                .author("Craig Walls")
                .category("Programming")
                .isDeleted(false)
                .build();
        Book book2 = Book.builder()
                .title("Java Concurrency in Practice")
                .author("Brian Goetz")
                .category("Programming")
                .isDeleted(true) // Marked as deleted
                .build();
        bookRepository.save(book1);
        bookRepository.save(book2);

        // When
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = bookRepository.findAllByIsDeletedFalse(pageable);

        // Then
        assertEquals(1, books.size(), "There should be one non-deleted book");
        assertEquals("Spring in Action", books.get(0).getTitle(), "Book title should match");
    }
}
