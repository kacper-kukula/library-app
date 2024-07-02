package com.libraryapp.controller;

import com.libraryapp.dto.book.BookRequestDto;
import com.libraryapp.dto.book.BookResponseDto;
import com.libraryapp.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Book Management",
        description = "Endpoints for managing books in the library.")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/books")
public class BookController {

    private final BookService bookService;

    @Operation(summary = "Retrieve all books",
            description = "Get a list of all books with optional pagination.")
    @GetMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER')")
    public List<BookResponseDto> findAll(Pageable pageable) {
        return bookService.findAll(pageable);
    }

    @Operation(summary = "Retrieve a book by ID",
            description = "Get details of a specific book by its ID.")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER')")
    public BookResponseDto findById(@PathVariable String id) {
        return bookService.findById(id);
    }

    @Operation(summary = "Add a new book",
            description = "Create a new book entry. Can only be performed by a manager.")
    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponseDto createBook(@RequestBody @Valid BookRequestDto bookRequestDto) {
        return bookService.save(bookRequestDto);
    }

    @Operation(summary = "Soft delete book by ID",
            description = "Mark the book as deleted. Can only be performed by a manager.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<String> deleteById(@PathVariable String id) {
        bookService.deleteById(id);
        return ResponseEntity.ok("Book (ID: " + id + ") successfully deleted.");
    }

    @Operation(summary = "Update book by ID",
            description = "Update book's details. Can only be performed by a manager.")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public BookResponseDto updateById(
            @PathVariable String id,
            @RequestBody @Valid BookRequestDto bookRequestDto) {
        return bookService.updateById(id, bookRequestDto);
    }
}
