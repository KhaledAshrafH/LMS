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
        PreparedStatement borrowStatement = null;
        PreparedStatement updateBookStatement = null;

        try {
            borrowStatement = connection.prepareStatement(
                    "INSERT INTO member_book (member_id, book_id, borrowed_date) VALUES (?, ?, CURDATE())");
            updateBookStatement = connection.prepareStatement(
                    "UPDATE books SET is_available = ? WHERE id = ?");

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
            } else {
                throw new RuntimeException("Book is not available or Member not found");
            }
        } finally {
            closeStatement(borrowStatement);
            closeStatement(updateBookStatement);
        }
    }

    @Override
    public void returnBook(Long memberId, Long bookId) throws SQLException {
        PreparedStatement deleteBorrowStatement = null;
        PreparedStatement updateBookStatement = null;

        try {
            deleteBorrowStatement = connection.prepareStatement(
                    "DELETE FROM member_book WHERE member_id = ? AND book_id = ?");
            updateBookStatement = connection.prepareStatement(
                    "UPDATE books SET is_available = ? WHERE id = ?");

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
            } else {
                throw new RuntimeException("This book was not borrowed by the member");
            }
        } finally {
            closeStatement(deleteBorrowStatement);
            closeStatement(updateBookStatement);
        }
    }

    private void closeStatement(PreparedStatement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
