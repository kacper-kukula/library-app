package com.libraryapp.service;

import com.libraryapp.dto.book.BookRequestDto;
import com.libraryapp.dto.book.BookResponseDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface BookService {

    BookResponseDto save(BookRequestDto bookRequestDto);

    List<BookResponseDto> findAll(Pageable pageable);

    BookResponseDto findById(String id);

    void deleteById(String id);

    BookResponseDto updateById(String id, BookRequestDto bookRequestDto);
}
