package com.libraryapp.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.libraryapp.dto.book.BookRequestDto;
import com.libraryapp.dto.book.BookResponseDto;
import com.libraryapp.exception.custom.EntityNotFoundException;
import com.libraryapp.service.BookService;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    private BookRequestDto bookRequestDto;
    private BookResponseDto bookResponseDto;

    @BeforeEach
    void setUp() {
        bookRequestDto = new BookRequestDto(
                "Book Title", "Book Author", "Book Category");

        bookResponseDto = new BookResponseDto(
                "1", "Book Title", "Book Author", "Book Category", false);
    }

    @Test
    @DisplayName("Retrieve all books successfully")
    @WithMockUser(roles = {"MANAGER"})
    void findAllBooks_ReturnsBookList() throws Exception {
        // Given
        Mockito.when(bookService.findAll(Mockito.any()))
                .thenReturn(Collections.singletonList(bookResponseDto));

        // When & Then
        mockMvc.perform(get("/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookResponseDto.id()))
                .andExpect(jsonPath("$[0].title").value(bookResponseDto.title()))
                .andExpect(jsonPath("$[0].author").value(bookResponseDto.author()))
                .andExpect(jsonPath("$[0].category").value(bookResponseDto.category()));
    }

    @Test
    @DisplayName("Retrieve a book by ID successfully")
    @WithMockUser(roles = {"MANAGER"})
    void findBookById_ExistingBook_ReturnsBookResponseDto() throws Exception {
        // Given
        Mockito.when(bookService.findById("1"))
                .thenReturn(bookResponseDto);

        // When & Then
        mockMvc.perform(get("/books/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookResponseDto.id()))
                .andExpect(jsonPath("$.title").value(bookResponseDto.title()))
                .andExpect(jsonPath("$.author").value(bookResponseDto.author()))
                .andExpect(jsonPath("$.category").value(bookResponseDto.category()));
    }

    @Test
    @DisplayName("Retrieve a book by ID not found")
    @WithMockUser(roles = {"MANAGER"})
    void findBookById_NonExistingBook_ReturnsNotFound() throws Exception {
        // Given
        Mockito.when(bookService.findById("1"))
                .thenThrow(new EntityNotFoundException("Book not found"));

        // When & Then
        mockMvc.perform(get("/books/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Add a new book successfully")
    @WithMockUser(roles = {"MANAGER"})
    void createBook_ValidData_ReturnsCreatedBookResponseDto() throws Exception {
        // Given
        Mockito.when(bookService.save(Mockito.any(BookRequestDto.class)))
                .thenReturn(bookResponseDto);

        // When & Then
        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(bookResponseDto.id()))
                .andExpect(jsonPath("$.title").value(bookResponseDto.title()))
                .andExpect(jsonPath("$.author").value(bookResponseDto.author()))
                .andExpect(jsonPath("$.category").value(bookResponseDto.category()));
    }

    @Test
    @DisplayName("Soft delete book by ID successfully")
    @WithMockUser(roles = {"MANAGER"})
    void deleteBookById_ExistingBook_ReturnsOk() throws Exception {
        // Given
        Mockito.doNothing().when(bookService).deleteById("1");

        // When & Then
        mockMvc.perform(delete("/books/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Book (ID: 1) successfully deleted."));
    }

    @Test
    @DisplayName("Update book by ID successfully")
    @WithMockUser(roles = {"MANAGER"})
    void updateBookById_ValidData_ReturnsUpdatedBookResponseDto() throws Exception {
        // Given
        Mockito.when(bookService.updateById(Mockito.anyString(), Mockito.any(BookRequestDto.class)))
                .thenReturn(bookResponseDto);

        // When & Then
        mockMvc.perform(put("/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookResponseDto.id()))
                .andExpect(jsonPath("$.title").value(bookResponseDto.title()))
                .andExpect(jsonPath("$.author").value(bookResponseDto.author()))
                .andExpect(jsonPath("$.category").value(bookResponseDto.category()));
    }
}
