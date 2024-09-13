package com.library.repository.impl;

import com.library.model.Book;
import com.library.model.Member;
import com.library.repository.BookRepository;
import com.library.repository.BorrowingRepository;
import com.library.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@RequiredArgsConstructor
public class BorrowingRepositoryImpl implements BorrowingRepository {
    private final Connection connection;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    @Override
    public void borrowBook(Long memberId, Long bookId) throws SQLException {
        Member member = memberRepository.findById(memberId);
        Book book = bookRepository.findById(bookId);

        validateBorrowingConditions(member, book);

        try (PreparedStatement borrowStatement = createBorrowStatement(memberId, bookId);
             PreparedStatement updateBookStatement = createUpdateBookStatement(false, bookId)) {

            // Borrow the book and update availability
            borrowStatement.executeUpdate();
            updateBookStatement.executeUpdate();
        }
    }

    @Override
    public void returnBook(Long memberId, Long bookId) throws SQLException {
        Member member = memberRepository.findById(memberId);
        Book book = bookRepository.findById(bookId);

        validateReturningConditions(member, book);

        try (PreparedStatement deleteBorrowStatement = createDeleteBorrowStatement(memberId, bookId);
             PreparedStatement updateBookStatement = createUpdateBookStatement(true, bookId)) {

            // Return the book and update availability
            deleteBorrowStatement.executeUpdate();
            updateBookStatement.executeUpdate();
        }
    }

    // Validates whether the conditions for borrowing a book are met
    private void validateBorrowingConditions(Member member, Book book) {
        if (book == null || member == null || !book.isAvailable())
            throw new RuntimeException("Cannot borrow. Either the book is unavailable or the member does not exist.");

    }

    // validates whether the conditions for returning a book are met
    private void validateReturningConditions(Member member, Book book) {
        if (member == null || book == null)
            throw new RuntimeException("Cannot return. Either the book isn't borrowed or the member does not exist.");

    }

    // create PreparedStatement for borrowing book
    private PreparedStatement createBorrowStatement(Long memberId, Long bookId) throws SQLException {
        String sql = "INSERT INTO member_book (member_id, book_id, borrowed_date) VALUES (?, ?, CURDATE())";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setLong(1, memberId);
        statement.setLong(2, bookId);
        return statement;
    }

    // create PreparedStatement for deleting borrowed book entry
    private PreparedStatement createDeleteBorrowStatement(Long memberId, Long bookId) throws SQLException {
        String sql = "DELETE FROM member_book WHERE member_id = ? AND book_id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setLong(1, memberId);
        statement.setLong(2, bookId);
        return statement;
    }

    // create PreparedStatement for updating the book
    private PreparedStatement createUpdateBookStatement(boolean isAvailable, Long bookId) throws SQLException {
        String sql = "UPDATE books SET is_available = ? WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setBoolean(1, isAvailable);
        statement.setLong(2, bookId);
        return statement;
    }
}
