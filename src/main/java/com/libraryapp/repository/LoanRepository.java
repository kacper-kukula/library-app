package com.libraryapp.repository;

import com.libraryapp.model.Loan;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepository extends MongoRepository<Loan, String> {

    List<Loan> findAllByCustomerId(String userId);
}
