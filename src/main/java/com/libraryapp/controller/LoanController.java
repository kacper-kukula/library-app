package com.libraryapp.controller;

import com.libraryapp.dto.loan.LoanRequestDto;
import com.libraryapp.dto.loan.LoanResponseDto;
import com.libraryapp.service.LoanService;
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

@Tag(name = "Loan Management",
        description = "Endpoints for managing loans.")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/loans")
public class LoanController {

    private final LoanService loanService;

    @Operation(summary = "Get all loans",
            description = "Retrieve a paginated list of all loans.")
    @GetMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER')")
    public List<LoanResponseDto> findAll(Pageable pageable) {
        return loanService.findAll(pageable);
    }

    @Operation(summary = "Get loan by ID",
            description = "Retrieve the details of a specific loan by its ID.")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER')")
    public LoanResponseDto findById(@PathVariable String id) {
        return loanService.findById(id);
    }

    @Operation(summary = "Create loan",
            description = "Create a new loan of a specific book ID.")
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @ResponseStatus(HttpStatus.CREATED)
    public LoanResponseDto createLoan(@RequestBody @Valid LoanRequestDto loanRequestDto) {
        return loanService.createLoan(loanRequestDto);
    }

    @Operation(summary = "Return loan",
            description = "Mark a loan as returned.")
    @PutMapping("/{id}/return")
    @PreAuthorize("hasRole('CUSTOMER')")
    public LoanResponseDto returnLoan(@PathVariable String id) {
        return loanService.returnLoan(id);
    }

    @Operation(summary = "Soft delete loan",
            description = "Soft delete a loan by ID. Can be performed only by a manager.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<String> deleteById(@PathVariable String id) {
        loanService.deleteById(id);
        return ResponseEntity.ok("Loan (ID: " + id + ") successfully deleted.");
    }

    @Operation(summary = "Update loan",
            description = "Update loan details by ID. Can be performed only by a manager.")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public LoanResponseDto updateById(
            @PathVariable String id,
            @RequestBody @Valid LoanRequestDto loanRequestDto) {
        return loanService.updateById(id, loanRequestDto);
    }
}
