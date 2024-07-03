package com.libraryapp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.libraryapp.dto.book.BookRequestDto;
import com.libraryapp.dto.book.BookResponseDto;
import com.libraryapp.exception.custom.EntityNotFoundException;
import com.libraryapp.mapper.BookMapper;
import com.libraryapp.model.Book;
import com.libraryapp.repository.BookRepository;
import com.libraryapp.service.impl.BookServiceImpl;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    @DisplayName("Save book successfully")
    void save_ValidBookRequestDto_ReturnsBookResponseDto() {
        // Given
        BookRequestDto bookRequestDto = new BookRequestDto("Title", "Author", "Category");
        Book book = Book.builder()
                .title("Title")
                .author("Author")
                .build();
        Book savedBook = Book.builder()
                .id("1")
                .title("Title")
                .author("Author")
                .build();
        BookResponseDto bookResponseDto =
                new BookResponseDto("1", "Title", "Author", "Category", false);

        when(bookMapper.toEntity(bookRequestDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(savedBook);
        when(bookMapper.toDto(savedBook)).thenReturn(bookResponseDto);

        // When
        BookResponseDto actual = bookService.save(bookRequestDto);

        // Then
        assertThat(actual).isEqualTo(bookResponseDto);
        verify(bookMapper).toEntity(bookRequestDto);
        verify(bookRepository).save(book);
        verify(bookMapper).toDto(savedBook);
    }

    @Test
    @DisplayName("Find all books successfully")
    void findAll_ReturnsListOfBooks() {
        // Given
        Pageable pageable = Pageable.unpaged();
        Book book = Book.builder()
                .id("1")
                .title("Title")
                .author("Author")
                .build();
        BookResponseDto bookResponseDto =
                new BookResponseDto("1", "Title", "Author", "Category", false);
        List<Book> books = Collections.singletonList(book);
        List<BookResponseDto> bookResponseDtos = Collections.singletonList(bookResponseDto);

        when(bookRepository.findAllByIsDeletedFalse(pageable)).thenReturn(books);
        when(bookMapper.toDto(book)).thenReturn(bookResponseDto);

        // When
        List<BookResponseDto> actual = bookService.findAll(pageable);

        // Then
        assertThat(actual).isEqualTo(bookResponseDtos);
        verify(bookRepository).findAllByIsDeletedFalse(pageable);
        verify(bookMapper).toDto(book);
    }

    @Test
    @DisplayName("Find book by ID successfully")
    void findById_ValidId_ReturnsBookResponseDto() {
        // Given
        String id = "1";
        Book book = Book.builder()
                .id(id)
                .title("Title")
                .author("Author")
                .isDeleted(false)
                .build();
        BookResponseDto bookResponseDto =
                new BookResponseDto(id, "Title", "Author", "Category", false);

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(bookResponseDto);

        // When
        BookResponseDto actual = bookService.findById(id);

        // Then
        assertThat(actual).isEqualTo(bookResponseDto);
        verify(bookRepository).findById(id);
        verify(bookMapper).toDto(book);
    }

    @Test
    @DisplayName("Find book by ID throws exception for non-existent book")
    void findById_InvalidId_ThrowsEntityNotFoundException() {
        // Given
        String id = "1";

        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> bookService.findById(id));
        verify(bookRepository).findById(id);
    }

    @Test
    @DisplayName("Delete book by ID successfully")
    void deleteById_ValidId_SoftDeletesBook() {
        // Given
        String id = "1";
        Book book = Book.builder()
                .id(id)
                .title("Title")
                .author("Author")
                .build();

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        // When
        bookService.deleteById(id);

        // Then
        verify(bookRepository).findById(id);
        verify(bookRepository).save(book);
        assertThat(book.getIsDeleted()).isTrue();
    }

    @Test
    @DisplayName("Update book by ID successfully")
    void updateById_ValidId_ReturnsUpdatedBookResponseDto() {
        // Given
        String id = "1";
        BookRequestDto bookRequestDto =
                new BookRequestDto("Updated Title", "Updated Author", "Category");
        Book existingBook = Book.builder()
                .id(id)
                .title("Old Title")
                .author("Old Author")
                .isDeleted(false)
                .build();
        Book updatedBook = Book.builder()
                .id(id)
                .title("Updated Title")
                .author("Updated Author")
                .category("Category")
                .isDeleted(false)
                .build();
        BookResponseDto bookResponseDto = new BookResponseDto(id, "Updated Title",
                "Updated Author", "Category", false);

        when(bookRepository.findById(id)).thenReturn(Optional.of(existingBook));
        doNothing().when(bookMapper).updateBookFromDto(existingBook, bookRequestDto);
        when(bookRepository.save(existingBook)).thenReturn(updatedBook);
        when(bookMapper.toDto(updatedBook)).thenReturn(bookResponseDto);

        // When
        BookResponseDto actual = bookService.updateById(id, bookRequestDto);

        // Then
        assertThat(actual).isEqualTo(bookResponseDto);
        verify(bookRepository).findById(id);
        verify(bookMapper).updateBookFromDto(existingBook, bookRequestDto);
        verify(bookRepository).save(existingBook);
        verify(bookMapper).toDto(updatedBook);
    }

    @Test
    @DisplayName("Update book by ID throws exception for non-existent book")
    void updateById_InvalidId_ThrowsEntityNotFoundException() {
        // Given
        String id = "1";
        BookRequestDto bookRequestDto =
                new BookRequestDto("Title", "Author", "Category");

        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class,
                () -> bookService.updateById(id, bookRequestDto));
        verify(bookRepository).findById(id);
    }
}
