package com.libraryapp.dto.book;

public record BookResponseDto(
        String id,
        String title,
        String author,
        String category,
        boolean isBorrowed
) {
}
