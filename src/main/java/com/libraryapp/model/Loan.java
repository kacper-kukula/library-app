package com.libraryapp.model;

import java.time.LocalDate;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "loans")
public class Loan {

    @Id
    private String id;
    private String bookId;
    private String customerId;
    private LocalDate borrowedDate;
    private LocalDate returnedDate;
    private Boolean isDeleted = false;
}
