package com.libraryapp.service.impl;

import com.libraryapp.dto.loan.LoanRequestDto;
import com.libraryapp.dto.loan.LoanResponseDto;
import com.libraryapp.mapper.LoanMapper;
import com.libraryapp.model.Book;
import com.libraryapp.model.Loan;
import com.libraryapp.repository.BookRepository;
import com.libraryapp.repository.LoanRepository;
import com.libraryapp.service.LoanService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private static final String LOAN_NOT_FOUND_ERROR = "Loan doesn't exist. ID: ";
    private static final String BOOK_NOT_FOUND_ERROR = "Book doesn't exist. ID: ";

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final LoanMapper loanMapper;

    @Override
    public List<LoanResponseDto> findAll(Pageable pageable) {
        return loanRepository.findAll(pageable).stream()
                .map(loanMapper::toDto)
                .toList();
    }

    @Override
    public LoanResponseDto findById(String id) {
        return loanRepository.findById(id)
                .map(loanMapper::toDto)
                .orElseThrow(() -> new RuntimeException(LOAN_NOT_FOUND_ERROR + id));
    }

    @Override
    @Transactional
    public LoanResponseDto createLoan(LoanRequestDto loanRequestDto) {
        String bookId = loanRequestDto.bookId();
        Book bookToLoan = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException(BOOK_NOT_FOUND_ERROR + bookId));

        if (bookToLoan.isBorrowed()) {
            throw new RuntimeException("This book is already loaned by someone else.");
        }

        bookToLoan.setBorrowed(true);
        bookRepository.save(bookToLoan);

        Loan loan = loanMapper.toEntity(loanRequestDto);
        loan.setBorrowedDate(LocalDate.now());
        //TODO - Set User ID from spring security context
        Loan savedLoan = loanRepository.save(loan);

        return loanMapper.toDto(savedLoan);
    }

    @Override
    @Transactional
    public LoanResponseDto returnLoan(String id) {
        Loan existingLoan = loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(LOAN_NOT_FOUND_ERROR + id));

        if (existingLoan.getReturnedDate() != null) {
            throw new RuntimeException("This loan has already been returned.");
        }

        existingLoan.setReturnedDate(LocalDate.now());

        String bookId = existingLoan.getBookId();
        Book loanedBook = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException(BOOK_NOT_FOUND_ERROR + bookId));
        loanedBook.setBorrowed(false);

        Loan savedLoan = loanRepository.save(existingLoan);
        bookRepository.save(loanedBook);

        return loanMapper.toDto(savedLoan);
    }

    @Override
    public void deleteById(String id) {
        loanRepository.deleteById(id);
    }

    @Override
    public LoanResponseDto updateById(String id, LoanRequestDto loanRequestDto) {
        Loan existingLoan = loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(LOAN_NOT_FOUND_ERROR + id));

        loanMapper.updateLoanFromDto(existingLoan, loanRequestDto);
        loanRepository.save(existingLoan);

        return loanMapper.toDto(existingLoan);
    }
}
