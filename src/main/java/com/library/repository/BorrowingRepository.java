package com.library.repository;

import java.sql.SQLException;

public interface BorrowingRepository {
    void borrowBook(Long memberId, Long bookId) throws SQLException;
    void returnBook(Long memberId, Long bookId) throws SQLException;
}
