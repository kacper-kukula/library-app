package com.libraryapp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.libraryapp.dto.loan.LoanRequestDto;
import com.libraryapp.dto.loan.LoanResponseDto;
import com.libraryapp.exception.custom.BookAlreadyLoanedException;
import com.libraryapp.exception.custom.EntityNotFoundException;
import com.libraryapp.exception.custom.LoanAlreadyReturnedException;
import com.libraryapp.mapper.LoanMapper;
import com.libraryapp.model.Book;
import com.libraryapp.model.Loan;
import com.libraryapp.model.User;
import com.libraryapp.repository.BookRepository;
import com.libraryapp.repository.LoanRepository;
import com.libraryapp.security.util.AuthenticationUtil;
import com.libraryapp.service.impl.LoanServiceImpl;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private LoanMapper loanMapper;

    @Mock
    private AuthenticationUtil authenticationUtil;

    @InjectMocks
    private LoanServiceImpl loanService;

    @Test
    @DisplayName("Find all loans for manager")
    void findAll_ForManager_ReturnsListOfLoans() {
        // Given
        Pageable pageable = Pageable.unpaged();
        Loan loan = getDummyLoan();
        LoanResponseDto loanResponseDto = getDummyLoanResponseDto();
        Page<Loan> loanPage = new PageImpl<>(Collections.singletonList(loan), pageable, 1);

        when(authenticationUtil.isManager()).thenReturn(true);
        when(loanRepository.findAllByIsDeletedFalse(pageable)).thenReturn(loanPage);
        when(loanMapper.toDto(loan)).thenReturn(loanResponseDto);

        // When
        List<LoanResponseDto> actual = loanService.findAll(pageable);

        // Then
        assertThat(actual).containsExactly(loanResponseDto);
        verify(loanRepository).findAllByIsDeletedFalse(pageable);
        verify(loanMapper).toDto(loan);
    }

    @Test
    @DisplayName("Find all loans for customer")
    void findAll_ForCustomer_ReturnsListOfLoans() {
        // Given
        Pageable pageable = Pageable.unpaged();
        Loan loan = getDummyLoan();
        LoanResponseDto loanResponseDto = getDummyLoanResponseDto();
        Page<Loan> loanPage = new PageImpl<>(Collections.singletonList(loan), pageable, 1);

        when(authenticationUtil.isManager()).thenReturn(false);
        when(authenticationUtil.getCurrentUserFromDb()).thenReturn(getDummyUser());
        when(loanRepository.findAllByCustomerIdAndIsDeletedFalse(
                "customer1", pageable)).thenReturn(loanPage);
        when(loanMapper.toDto(loan)).thenReturn(loanResponseDto);

        // When
        List<LoanResponseDto> actual = loanService.findAll(pageable);

        // Then
        assertThat(actual).containsExactly(loanResponseDto);
        verify(loanRepository).findAllByCustomerIdAndIsDeletedFalse("customer1", pageable);
        verify(loanMapper).toDto(loan);
    }

    @Test
    @DisplayName("Find loan by ID")
    void findById_ValidId_ReturnsLoan() {
        // Given
        Loan loan = getDummyLoan();
        LoanResponseDto loanResponseDto = getDummyLoanResponseDto();

        when(loanRepository.findById("1")).thenReturn(Optional.of(loan));
        when(loanMapper.toDto(loan)).thenReturn(loanResponseDto);
        when(authenticationUtil.isManager()).thenReturn(true);

        // When
        LoanResponseDto actual = loanService.findById("1");

        // Then
        assertThat(actual).isEqualTo(loanResponseDto);
        verify(loanRepository).findById("1");
        verify(loanMapper).toDto(loan);
    }

    @Test
    @DisplayName("Find loan by ID for customer")
    void findById_InvalidId_ThrowsException() {
        // Given
        when(loanRepository.findById("1")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> loanService.findById("1"));
        verify(loanRepository).findById("1");
    }

    @Test
    @DisplayName("Create loan successfully")
    void createLoan_ValidRequest_ReturnsLoan() {
        // Given
        LoanRequestDto loanRequestDto = getDummyLoanRequestDto();
        Book book = getDummyBook();
        Loan loan = getDummyLoan();
        Loan savedLoan = getDummyLoan();
        LoanResponseDto loanResponseDto = getDummyLoanResponseDto();

        when(bookRepository.findById("book1")).thenReturn(Optional.of(book));
        when(loanMapper.toEntity(loanRequestDto)).thenReturn(loan);
        when(authenticationUtil.getCurrentUserFromDb()).thenReturn(getDummyUser());
        when(loanRepository.save(loan)).thenReturn(savedLoan);
        when(bookRepository.save(book)).thenReturn(book);
        when(loanMapper.toDto(savedLoan)).thenReturn(loanResponseDto);

        // When
        LoanResponseDto actual = loanService.createLoan(loanRequestDto);

        // Then
        assertThat(actual).isEqualTo(loanResponseDto);
        verify(bookRepository).findById("book1");
        verify(loanRepository).save(loan);
        verify(bookRepository).save(book);
        verify(loanMapper).toDto(savedLoan);
    }

    @Test
    @DisplayName("Create loan when book is already borrowed")
    void createLoan_BookAlreadyBorrowed_ThrowsException() {
        // Given
        Book book = getDummyBook();
        book.setIsBorrowed(true);
        LoanRequestDto loanRequestDto = getDummyLoanRequestDto();

        when(bookRepository.findById("book1")).thenReturn(Optional.of(book));

        // When & Then
        assertThrows(BookAlreadyLoanedException.class,
                () -> loanService.createLoan(loanRequestDto));
        verify(bookRepository).findById("book1");
    }

    @Test
    @DisplayName("Return loan successfully")
    void returnLoan_ValidId_ReturnsLoan() {
        // Given
        Loan loan = getDummyLoan();
        Book book = getDummyBook();
        LoanResponseDto loanResponseDto = getDummyLoanResponseDto();

        when(loanRepository.findById("1")).thenReturn(Optional.of(loan));
        when(bookRepository.findById("book1")).thenReturn(Optional.of(book));
        when(loanRepository.save(loan)).thenReturn(loan);
        when(bookRepository.save(book)).thenReturn(book);
        when(loanMapper.toDto(loan)).thenReturn(loanResponseDto);

        // When
        LoanResponseDto actual = loanService.returnLoan("1");

        // Then
        assertThat(actual).isEqualTo(loanResponseDto);
        verify(loanRepository).findById("1");
        verify(bookRepository).findById("book1");
        verify(loanRepository).save(loan);
        verify(bookRepository).save(book);
    }

    @Test
    @DisplayName("Return loan when already returned")
    void returnLoan_AlreadyReturned_ThrowsException() {
        // Given
        Loan loan = getDummyLoan();
        loan.setReturnedDate(LocalDate.now());

        when(loanRepository.findById("1")).thenReturn(Optional.of(loan));

        // When & Then
        assertThrows(LoanAlreadyReturnedException.class, () -> loanService.returnLoan("1"));
        verify(loanRepository).findById("1");
    }

    @Test
    @DisplayName("Delete loan by ID")
    void deleteById_ValidId_DeletesLoan() {
        // Given
        Loan loan = getDummyLoan();

        when(loanRepository.findById("1")).thenReturn(Optional.of(loan));
        when(loanRepository.save(loan)).thenReturn(loan);

        // When
        loanService.deleteById("1");

        // Then
        verify(loanRepository).findById("1");
        verify(loanRepository).save(loan);
    }

    @Test
    @DisplayName("Update loan by ID")
    void updateById_ValidId_UpdatesLoan() {
        // Given
        Loan existingLoan = getDummyLoan();
        LoanRequestDto loanRequestDto = getDummyLoanRequestDto();
        LoanResponseDto loanResponseDto = getDummyLoanResponseDto();

        when(loanRepository.findById("1")).thenReturn(Optional.of(existingLoan));
        //when(loanMapper.updateLoanFromDto(existingLoan, loanRequestDto)).thenReturn(existingLoan);
        when(loanRepository.save(existingLoan)).thenReturn(existingLoan);
        when(loanMapper.toDto(existingLoan)).thenReturn(loanResponseDto);

        // When
        LoanResponseDto actual = loanService.updateById("1", loanRequestDto);

        // Then
        assertThat(actual).isEqualTo(loanResponseDto);
        verify(loanRepository).findById("1");
        verify(loanMapper).updateLoanFromDto(existingLoan, loanRequestDto);
        verify(loanRepository).save(existingLoan);
        verify(loanMapper).toDto(existingLoan);
    }

    private User getDummyUser() {
        User user = new User();
        user.setId("customer1");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole(User.Role.CUSTOMER);
        user.setIsDeleted(false);
        return user;
    }

    private Loan getDummyLoan() {
        Loan loan = new Loan();
        loan.setId("1");
        loan.setBookId("book1");
        loan.setCustomerId("customer1");
        loan.setBorrowedDate(LocalDate.now());
        loan.setReturnedDate(null);
        loan.setIsDeleted(false);
        return loan;
    }

    private Book getDummyBook() {
        return Book.builder()
                .id("book1")
                .title("Book Title")
                .author("Author")
                .isBorrowed(false)
                .isDeleted(false)
                .build();
    }

    private LoanRequestDto getDummyLoanRequestDto() {
        return new LoanRequestDto("book1");
    }

    private LoanResponseDto getDummyLoanResponseDto() {
        return new LoanResponseDto("1", "book1", "customer1", LocalDate.now(), null);
    }
}
