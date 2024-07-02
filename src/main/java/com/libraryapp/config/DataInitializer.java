package com.libraryapp.config;

import com.libraryapp.model.Book;
import com.libraryapp.model.User;
import com.libraryapp.repository.BookRepository;
import com.libraryapp.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initializeData() {
        return args -> {
            // Add sample books and a manager account only if database is empty
            List<Book> books = List.of(
                    Book.builder().title("Dune").author("F. Herbert")
                            .category("Sci-Fi").isBorrowed(false).isDeleted(false).build(),
                    Book.builder().title("It").author("S. King")
                            .category("Horror").isBorrowed(false).isDeleted(false).build(),
                    Book.builder().title("Murder").author("A. Christie")
                            .category("Mystery").isBorrowed(false).isDeleted(false).build(),
                    Book.builder().title("Fahrenheit 451").author("R. Bradbury")
                            .category("Dystopian").isBorrowed(false).isDeleted(false).build(),
                    Book.builder().title("Brave New World").author("A. Huxley")
                            .category("Sci-Fi").isBorrowed(false).isDeleted(false).build(),
                    Book.builder().title("Emma").author("J. Austen")
                            .category("Classic").isBorrowed(false).isDeleted(false).build(),
                    Book.builder().title("Pride and Prejudice").author("J. Austen")
                            .category("Classic").isBorrowed(false).isDeleted(false).build(),
                    Book.builder().title("Dracula").author("B. Stoker")
                            .category("Horror").isBorrowed(false).isDeleted(false).build(),
                    Book.builder().title("Neuromancer").author("W. Gibson")
                            .category("Sci-Fi").isBorrowed(false).isDeleted(false).build(),
                    Book.builder().title("The Hobbit").author("J.R.R. Tolkien")
                            .category("Fantasy").isBorrowed(false).isDeleted(false).build()
            );

            if (bookRepository.count() == 0) {
                bookRepository.saveAll(books);
            }

            User manager = new User();
            manager.setEmail("manager@library.com");
            manager.setPassword(passwordEncoder.encode("safePassword"));
            manager.setFirstName("Manager");
            manager.setLastName("Manager");
            manager.setRole(User.Role.MANAGER);

            if (userRepository.count() == 0) {
                userRepository.save(manager);
            }
        };
    }
}
