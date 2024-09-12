package com.library.service.impl;

import com.library.model.Book;
import com.library.model.Member;
import com.library.repository.BookRepository;
import com.library.repository.MemberRepository;
import com.library.service.LibraryService;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
public class LibraryServiceImpl implements LibraryService {
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final Connection connection;


    @Override
    public void addBook(Book book) {
        book.setAvailable(true);
        bookRepository.save(book);
    }

    @Override
    public void removeBook(Long bookId) {
        Book book = bookRepository.findById(bookId);
        bookRepository.delete(book);
    }

    @Override
    public void updateBook(Long bookId, Book book) {

    }

    @Override
    public Book getBookById(Long bookId) {
        return bookRepository.findById(bookId);
    }

    @Override
    public List<Book> getBooks() {
        return bookRepository.findAll();
    }

    @Override
    public void registerMember(Member member) {
        memberRepository.save(member);
    }

    @Override
    public void removeMember(Long memberId) {
        Member member = memberRepository.findById(memberId);
        memberRepository.delete(member);
    }

    @Override
    public void updateMember(Long memberId) {

    }

    @Override
    public Member getMemberById(Long memberId) {
        return memberRepository.findById(memberId);
    }

    @Override
    public List<Member> getMembers() {
        return memberRepository.findAll();
    }

    @Override
    public List<Book> getBooksByMemberId(Long memberId) throws SQLException {
        return memberRepository.findAllBooksByMemberId(memberId);
    }

    @Override
    public void borrowBook(Long memberId, Long bookId) throws SQLException {

        PreparedStatement borrowStatement = null;
        try {
            borrowStatement = connection.prepareStatement(
                    "INSERT INTO member_book (member_id, book_id, borrowed_date) VALUES (?, ?, CURDATE())");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        PreparedStatement updateBookStatement = null;

        try {
            updateBookStatement = connection.prepareStatement(
                    "UPDATE books SET is_available = ? WHERE id = ?");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        connection.setAutoCommit(false);
        // Find member and book
        Member member = memberRepository.findById(memberId);
        Book book = bookRepository.findById(bookId);

        if (book != null && member != null && book.isAvailable()) {
            // Borrow the book
            borrowStatement.setLong(1, member.getId());
            borrowStatement.setLong(2, book.getId());
            borrowStatement.executeUpdate();

            // Update book availability
            updateBookStatement.setBoolean(1, false);
            updateBookStatement.setLong(2, bookId);
            updateBookStatement.executeUpdate();

            connection.commit();
        } else {
            connection.rollback();
            throw new RuntimeException("Book is not available or Member not found");
        }

    }

    @Override
    public void returnBook(Long memberId, Long bookId) throws SQLException {
        PreparedStatement deleteBorrowStatement = connection.prepareStatement(
                "DELETE FROM member_book WHERE member_id = ? AND book_id = ?");
        PreparedStatement updateBookStatement = connection.prepareStatement(
                "UPDATE books SET is_available = ? WHERE id = ?");

        connection.setAutoCommit(false);

        // Find member and book
        Member member = memberRepository.findById(memberId);
        Book book = bookRepository.findById(bookId);

        if (member != null && book != null) {
            // Return the book
            deleteBorrowStatement.setLong(1, memberId);
            deleteBorrowStatement.setLong(2, bookId);
            deleteBorrowStatement.executeUpdate();

            // Update book availability
            updateBookStatement.setBoolean(1, true);
            updateBookStatement.setLong(2, bookId);
            updateBookStatement.executeUpdate();

            connection.commit();
        } else {
            connection.rollback();
            throw new RuntimeException("This book was not borrowed by the member");
        }
    }
}

