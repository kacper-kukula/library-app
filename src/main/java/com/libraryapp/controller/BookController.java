package com.libraryapp.controller;

import com.libraryapp.dto.BookRequestDto;
import com.libraryapp.dto.BookResponseDto;
import com.libraryapp.service.BookService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/books")
public class BookController {

    private final BookService bookService;

    @GetMapping
    public List<BookResponseDto> findAll(Pageable pageable) {
        return bookService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public BookResponseDto findById(@PathVariable String id) {
        return bookService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponseDto createBook(@RequestBody @Valid BookRequestDto bookRequestDto) {
        return bookService.save(bookRequestDto);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable String id) {
        bookService.deleteById(id);
    }

    @PutMapping("/{id}")
    public BookResponseDto updateById(
            @PathVariable String id,
            @RequestBody @Valid BookRequestDto bookRequestDto) {
        return bookService.updateById(id, bookRequestDto);
    }
}
