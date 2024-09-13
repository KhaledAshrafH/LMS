package com.library.service;

import com.library.model.Book;
import com.library.model.Member;

import java.sql.SQLException;
import java.util.List;

/**
 * Service interface for managing library operations including books and members.
 */
public interface LibraryService {

    // Book management methods
    void addBook(Book book);
    void removeBook(Long bookId);
    void updateBook(Long bookId, Book book);
    Book getBookById(Long bookId);
    List<Book> getBooks();

    // Member management methods
    void registerMember(Member member);
    void removeMember(Long memberId);
    void updateMember(Long memberId, Member member);
    Member getMemberById(Long memberId);
    List<Member> getMembers();
    List<Book> getBooksByMemberId(Long memberId) throws SQLException;

    // Borrowing and returning operations
    void borrowBook(Long memberId, Long bookId) throws SQLException;
    void returnBook(Long memberId, Long bookId) throws SQLException;
}
