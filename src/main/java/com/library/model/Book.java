package com.library.model;

import lombok.*;
import java.time.LocalDateTime;


/**
 * Represents a library book with associated information.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

    private Long id;
    private String title;
    private String author;
    private String isbn;
    private LocalDateTime publicationDate;
    private boolean isAvailable = true;


    public Book(String title, String author, String isbn, boolean isAvailable) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publicationDate = LocalDateTime.now();
        this.isAvailable = isAvailable;
    }


    @Override
    public String toString() {
        return String.format("Book{id=%d, title='%s', author='%s', isbn='%s', publicationDate=%s, isAvailable=%s}",
                id, title, author, isbn, publicationDate, isAvailable);
    }

    public void updateWith(Book updatedBook) {
        this.title = updatedBook.title;
        this.author = updatedBook.author;
        this.isbn = updatedBook.isbn;
    }
}
