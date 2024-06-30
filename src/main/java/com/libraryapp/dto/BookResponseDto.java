package com.libraryapp.dto;

public record BookResponseDto(
        String id,
        String title,
        String author,
        String category,
        boolean isBorrowed
) {
}
