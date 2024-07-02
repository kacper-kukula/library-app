package com.libraryapp.repository;

import com.libraryapp.model.Book;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends MongoRepository<Book, String> {

    List<Book> findAllByIsDeletedFalse(Pageable pageable);
}
