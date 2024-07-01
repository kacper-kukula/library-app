package com.libraryapp.mapper;

import com.libraryapp.config.MapperConfig;
import com.libraryapp.dto.loan.LoanRequestDto;
import com.libraryapp.dto.loan.LoanResponseDto;
import com.libraryapp.model.Loan;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface LoanMapper {

    LoanResponseDto toDto(Loan loan);

    Loan toEntity(LoanRequestDto loanRequestDto);

    void updateLoanFromDto(@MappingTarget Loan loan, LoanRequestDto dto);
}
