package com.libraryapp.service;

import com.libraryapp.dto.loan.LoanRequestDto;
import com.libraryapp.dto.loan.LoanResponseDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface LoanService {

    List<LoanResponseDto> findAll(Pageable pageable);

    LoanResponseDto findById(String id);

    LoanResponseDto createLoan(LoanRequestDto loanRequestDto);

    LoanResponseDto returnLoan(String id);

    void deleteById(String id);

    LoanResponseDto updateById(String id, LoanRequestDto loanRequestDto);
}
