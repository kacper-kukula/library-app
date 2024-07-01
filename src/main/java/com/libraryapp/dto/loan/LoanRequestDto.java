package com.libraryapp.dto.loan;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoanRequestDto(
        @NotBlank
        @Size(max = 100)
        String bookId
) {
}
