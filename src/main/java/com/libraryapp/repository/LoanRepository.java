package com.libraryapp.repository;

import com.libraryapp.model.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepository extends MongoRepository<Loan, String> {

    Page<Loan> findAllByCustomerIdAndIsDeletedFalse(String userId, Pageable pageable);

    Page<Loan> findAllByIsDeletedFalse(Pageable pageable);
}
