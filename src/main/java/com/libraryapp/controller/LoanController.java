package com.libraryapp.controller;

import com.libraryapp.dto.loan.LoanRequestDto;
import com.libraryapp.dto.loan.LoanResponseDto;
import com.libraryapp.service.LoanService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping(value = "/loans")
public class LoanController {

    private final LoanService loanService;

    @GetMapping
    public List<LoanResponseDto> findAll(Pageable pageable) {
        return loanService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public LoanResponseDto findById(@PathVariable String id) {
        return loanService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LoanResponseDto createLoan(@RequestBody @Valid LoanRequestDto loanRequestDto) {
        return loanService.createLoan(loanRequestDto);
    }

    @PutMapping("/{id}/return")
    public LoanResponseDto returnLoan(@PathVariable String id) {
        return loanService.returnLoan(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable String id) {
        loanService.deleteById(id);
        return ResponseEntity.ok("Loan (ID: " + id + ") successfully deleted.");
    }

    @PutMapping("/{id}")
    public LoanResponseDto updateById(
            @PathVariable String id,
            @RequestBody @Valid LoanRequestDto loanRequestDto) {
        return loanService.updateById(id, loanRequestDto);
    }
}
