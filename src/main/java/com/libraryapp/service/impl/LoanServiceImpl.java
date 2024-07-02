package com.libraryapp.service.impl;

import com.libraryapp.dto.loan.LoanRequestDto;
import com.libraryapp.dto.loan.LoanResponseDto;
import com.libraryapp.exception.custom.BookAlreadyLoanedException;
import com.libraryapp.exception.custom.EntityNotFoundException;
import com.libraryapp.exception.custom.LoanAlreadyReturnedException;
import com.libraryapp.exception.custom.UnauthorizedViewException;
import com.libraryapp.mapper.LoanMapper;
import com.libraryapp.model.Book;
import com.libraryapp.model.Loan;
import com.libraryapp.repository.BookRepository;
import com.libraryapp.repository.LoanRepository;
import com.libraryapp.security.util.AuthenticationUtil;
import com.libraryapp.service.LoanService;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private static final String LOAN_NOT_FOUND_ERROR = "Loan doesn't exist. ID: ";
    private static final String BOOK_NOT_FOUND_ERROR = "Book doesn't exist. ID: ";

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final LoanMapper loanMapper;
    private final AuthenticationUtil authenticationUtil;

    @Override
    public List<LoanResponseDto> findAll(Pageable pageable) {
        Page<Loan> loansPage;

        // Manager will see all non-deleted loans
        if (authenticationUtil.isManager()) {
            loansPage = loanRepository.findAllByIsDeletedFalse(pageable);
        } else {
            // Customer will only see his non-deleted loans
            loansPage = loanRepository.findAllByCustomerIdAndIsDeletedFalse(
                    authenticationUtil.getCurrentUserFromDb().getId(), pageable);
        }

        return loansPage.stream()
                .map(loanMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public LoanResponseDto findById(String id) {
        Loan loan = loanRepository.findById(id)
                .filter(l -> !l.getIsDeleted())
                .orElseThrow(() -> new EntityNotFoundException(LOAN_NOT_FOUND_ERROR + id));

        if (!authenticationUtil.isManager()) {
            validateCurrentUserOwnsLoan(loan);
        }

        return loanMapper.toDto(loan);
    }

    @Override
    public LoanResponseDto createLoan(LoanRequestDto loanRequestDto) {
        String bookId = loanRequestDto.bookId();
        Book bookToLoan = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException(BOOK_NOT_FOUND_ERROR + bookId));

        if (bookToLoan.getIsBorrowed()) {
            throw new BookAlreadyLoanedException("This book is already loaned.");
        }

        bookToLoan.setIsBorrowed(true);
        bookRepository.save(bookToLoan);

        Loan loan = loanMapper.toEntity(loanRequestDto);
        loan.setBorrowedDate(LocalDate.now());
        loan.setCustomerId(authenticationUtil.getCurrentUserFromDb().getId());
        Loan savedLoan = loanRepository.save(loan);

        return loanMapper.toDto(savedLoan);
    }

    @Override
    public LoanResponseDto returnLoan(String id) {
        Loan existingLoan = loanRepository.findById(id)
                .filter(loan -> !loan.getIsDeleted())
                .orElseThrow(() -> new EntityNotFoundException(LOAN_NOT_FOUND_ERROR + id));

        if (existingLoan.getReturnedDate() != null) {
            throw new LoanAlreadyReturnedException("This loan has already been returned.");
        }

        existingLoan.setReturnedDate(LocalDate.now());

        String bookId = existingLoan.getBookId();
        Book loanedBook = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException(BOOK_NOT_FOUND_ERROR + bookId));
        loanedBook.setIsBorrowed(false);

        Loan savedLoan = loanRepository.save(existingLoan);
        bookRepository.save(loanedBook);

        return loanMapper.toDto(savedLoan);
    }

    @Override
    public void deleteById(String id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(LOAN_NOT_FOUND_ERROR + id));
        loan.setIsDeleted(true); // soft delete

        loanRepository.save(loan);
    }

    @Override
    public LoanResponseDto updateById(String id, LoanRequestDto loanRequestDto) {
        Loan existingLoan = loanRepository.findById(id)
                .filter(loan -> !loan.getIsDeleted())
                .orElseThrow(() -> new EntityNotFoundException(LOAN_NOT_FOUND_ERROR + id));

        loanMapper.updateLoanFromDto(existingLoan, loanRequestDto);
        loanRepository.save(existingLoan);

        return loanMapper.toDto(existingLoan);
    }

    private void validateCurrentUserOwnsLoan(Loan loan) {
        String loanOwnerId = loan.getCustomerId();

        if (!loanOwnerId.equals(authenticationUtil.getCurrentUserFromDb().getId())) {
            throw new UnauthorizedViewException("You are not authorized to view this loan.");
        }
    }
}
