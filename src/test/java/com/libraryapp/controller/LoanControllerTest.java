package com.libraryapp.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.libraryapp.dto.loan.LoanRequestDto;
import com.libraryapp.dto.loan.LoanResponseDto;
import com.libraryapp.exception.custom.EntityNotFoundException;
import com.libraryapp.service.LoanService;
import java.time.LocalDate;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class LoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoanService loanService;

    @Autowired
    private ObjectMapper objectMapper;

    private LoanRequestDto loanRequestDto;
    private LoanResponseDto loanResponseDto;

    @BeforeEach
    void setUp() {
        loanRequestDto = new LoanRequestDto("12345");

        loanResponseDto = new LoanResponseDto(
                "12345", "bookId", "customerId", LocalDate.now(), null);
    }

    @Test
    @DisplayName("Retrieve all loans successfully")
    @WithMockUser(roles = {"MANAGER"})
    void findAllLoans_ReturnsLoanList() throws Exception {
        // Given
        Mockito.when(loanService.findAll(Mockito.any(Pageable.class)))
                .thenReturn(Collections.singletonList(loanResponseDto));

        // When & Then
        mockMvc.perform(get("/loans")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(loanResponseDto.id()))
                .andExpect(jsonPath("$[0].bookId").value(loanResponseDto.bookId()))
                .andExpect(jsonPath("$[0].customerId").value(loanResponseDto.customerId()));
    }

    @Test
    @DisplayName("Retrieve a loan by ID successfully")
    @WithMockUser(roles = {"MANAGER"})
    void findLoanById_ExistingLoan_ReturnsLoanResponseDto() throws Exception {
        // Given
        Mockito.when(loanService.findById("loanId"))
                .thenReturn(loanResponseDto);

        // When & Then
        mockMvc.perform(get("/loans/loanId")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(loanResponseDto.id()))
                .andExpect(jsonPath("$.bookId").value(loanResponseDto.bookId()))
                .andExpect(jsonPath("$.customerId").value(loanResponseDto.customerId()));
    }

    @Test
    @DisplayName("Retrieve a loan by ID not found")
    @WithMockUser(roles = {"MANAGER"})
    void findLoanById_NonExistingLoan_ReturnsNotFound() throws Exception {
        // Given
        Mockito.when(loanService.findById("loanId"))
                .thenThrow(new EntityNotFoundException("Loan not found"));

        // When & Then
        mockMvc.perform(get("/loans/loanId")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Create a new loan successfully")
    @WithMockUser(roles = {"CUSTOMER"})
    void createLoan_ValidData_ReturnsCreatedLoanResponseDto() throws Exception {
        // Given
        Mockito.when(loanService.createLoan(Mockito.any(LoanRequestDto.class)))
                .thenReturn(loanResponseDto);

        // When & Then
        mockMvc.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(loanResponseDto.id()))
                .andExpect(jsonPath("$.bookId").value(loanResponseDto.bookId()))
                .andExpect(jsonPath("$.customerId").value(loanResponseDto.customerId()));
    }

    @Test
    @DisplayName("Return a loan successfully")
    @WithMockUser(roles = {"CUSTOMER"})
    void returnLoan_ExistingLoan_ReturnsUpdatedLoanResponseDto() throws Exception {
        // Given
        Mockito.when(loanService.returnLoan("loanId"))
                .thenReturn(loanResponseDto);

        // When & Then
        mockMvc.perform(put("/loans/loanId/return")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(loanResponseDto.id()))
                .andExpect(jsonPath("$.bookId").value(loanResponseDto.bookId()))
                .andExpect(jsonPath("$.customerId").value(loanResponseDto.customerId()));
    }

    @Test
    @DisplayName("Soft delete a loan by ID successfully")
    @WithMockUser(roles = {"MANAGER"})
    void deleteLoanById_ExistingLoan_ReturnsOk() throws Exception {
        // Given
        Mockito.doNothing().when(loanService).deleteById("loanId");

        // When & Then
        mockMvc.perform(delete("/loans/loanId")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Loan (ID: loanId) successfully deleted."));
    }

    @Test
    @DisplayName("Update a loan by ID successfully")
    @WithMockUser(roles = {"MANAGER"})
    void updateLoanById_ValidData_ReturnsUpdatedLoanResponseDto() throws Exception {
        // Given
        Mockito.when(loanService.updateById(Mockito.anyString(), Mockito.any(LoanRequestDto.class)))
                .thenReturn(loanResponseDto);

        // When & Then
        mockMvc.perform(put("/loans/loanId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(loanResponseDto.id()))
                .andExpect(jsonPath("$.bookId").value(loanResponseDto.bookId()))
                .andExpect(jsonPath("$.customerId").value(loanResponseDto.customerId()));
    }
}
