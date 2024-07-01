package com.libraryapp.service.impl;

import com.libraryapp.dto.book.BookRequestDto;
import com.libraryapp.dto.book.BookResponseDto;
import com.libraryapp.mapper.BookMapper;
import com.libraryapp.model.Book;
import com.libraryapp.repository.BookRepository;
import com.libraryapp.service.BookService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private static final String BOOK_NOT_FOUND_ERROR = "Book doesn't exist. ID: ";

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    @Transactional
    public BookResponseDto save(BookRequestDto bookRequestDto) {
        Book book = bookMapper.toEntity(bookRequestDto);
        Book savedBook = bookRepository.save(book);

        return bookMapper.toDto(savedBook);
    }

    @Override
    @Transactional
    public List<BookResponseDto> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable).stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public BookResponseDto findById(String id) {
        return bookRepository.findById(id)
                .map(bookMapper::toDto)
                .orElseThrow(() -> new RuntimeException(BOOK_NOT_FOUND_ERROR + id));
    }

    @Override
    public void deleteById(String id) {
        bookRepository.deleteById(id);
    }

    @Override
    @Transactional
    public BookResponseDto updateById(String id, BookRequestDto bookRequestDto) {
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(BOOK_NOT_FOUND_ERROR + id));

        bookMapper.updateBookFromDto(existingBook, bookRequestDto);
        Book updatedBook = bookRepository.save(existingBook);

        return bookMapper.toDto(updatedBook);
    }
}
