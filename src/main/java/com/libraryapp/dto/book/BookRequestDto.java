package com.libraryapp.dto.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BookRequestDto(
        @NotBlank
        @Size(max = 100)
        String title,

        @NotBlank
        @Size(max = 50)
        String author,

        @NotBlank
        @Size(max = 50)
        String category
) {
}
