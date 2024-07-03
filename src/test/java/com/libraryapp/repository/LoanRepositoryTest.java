package com.libraryapp.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.libraryapp.model.Book;
import com.libraryapp.model.Loan;
import java.time.LocalDate;
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
public class LoanRepositoryTest {

    @Container
    private static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.3")
            .withExposedPorts(27017);

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private BookRepository bookRepository;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeEach
    void setUp() {
        loanRepository.deleteAll();
        bookRepository.deleteAll();
    }

    @Test
    void findAllByCustomerIdAndIsDeletedFalse_NoLoansForCustomer_ReturnsEmptyPage() {
        // Given
        String customerId = "customer-1";

        // When
        Pageable pageable = PageRequest.of(0, 10);
        var loans = loanRepository.findAllByCustomerIdAndIsDeletedFalse(customerId, pageable);

        // Then
        assertTrue(loans.isEmpty(), "The page of loans should be empty");
    }

    @Test
    void findAllByCustomerIdAndIsDeletedFalse_OneLoanForCustomer_ReturnsLoanPage() {
        // Given
        Book book = Book.builder()
                .title("Design Patterns")
                .author("Gang of Four")
                .category("Programming")
                .isBorrowed(false)
                .isDeleted(false)
                .build();
        bookRepository.save(book);

        Loan loan = new Loan();
        loan.setBookId(book.getId());
        loan.setCustomerId("customer-1");
        loan.setBorrowedDate(LocalDate.now());
        loanRepository.save(loan);

        // When
        Pageable pageable = PageRequest.of(0, 10);
        var loans = loanRepository.findAllByCustomerIdAndIsDeletedFalse("customer-1", pageable);

        // Then
        assertEquals(1, loans.getTotalElements(),
                "The page of loans should contain one loan");
        assertEquals("customer-1", loans.getContent().get(0).getCustomerId(),
                "The customer ID should match");
    }

    @Test
    void findAllByIsDeletedFalse_OneLoanDeleted_ReturnsOnlyNonDeletedLoans() {
        // Given
        Book book1 = Book.builder()
                .title("Refactoring")
                .author("Martin Fowler")
                .category("Programming")
                .isBorrowed(false)
                .isDeleted(false)
                .build();
        bookRepository.save(book1);

        Book book2 = Book.builder()
                .title("Java Puzzlers")
                .author("Joshua Bloch")
                .category("Programming")
                .isBorrowed(false)
                .isDeleted(false)
                .build();
        bookRepository.save(book2);

        Loan loan1 = new Loan();
        loan1.setBookId(book1.getId());
        loan1.setCustomerId("customer-2");
        loan1.setBorrowedDate(LocalDate.now());
        loanRepository.save(loan1);

        Loan loan2 = new Loan();
        loan2.setBookId(book2.getId());
        loan2.setCustomerId("customer-2");
        loan2.setBorrowedDate(LocalDate.now());
        loan2.setIsDeleted(true); // Marked as deleted
        loanRepository.save(loan2);

        // When
        Pageable pageable = PageRequest.of(0, 10);
        var loans = loanRepository.findAllByIsDeletedFalse(pageable);

        // Then
        assertEquals(1, loans.getTotalElements(),
                "The page should contain only non-deleted loans");
        assertEquals("customer-2", loans.getContent().get(0).getCustomerId(),
                "The customer ID should match");
    }
}
