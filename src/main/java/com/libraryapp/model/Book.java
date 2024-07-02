package com.libraryapp.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "books")
public class Book {

    @Id
    private String id;
    private String title;
    private String author;
    private String category;
    private Boolean isBorrowed = false;
    private Boolean isDeleted = false;
}
