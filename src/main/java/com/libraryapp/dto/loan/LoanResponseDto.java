package com.libraryapp.dto.loan;

import java.time.LocalDate;

public record LoanResponseDto(
        String id,
        String bookId,
        String customerId,
        LocalDate borrowedDate,
        LocalDate returnedDate
) {
}
