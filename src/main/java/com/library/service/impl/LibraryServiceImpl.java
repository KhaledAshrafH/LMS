package com.library.service.impl;

import com.library.model.Book;
import com.library.model.Member;
import com.library.repository.BookRepository;
import com.library.repository.BorrowingRepository;
import com.library.repository.MemberRepository;
import com.library.service.LibraryService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class LibraryServiceImpl implements LibraryService {
    private static final Logger logger = LoggerFactory.getLogger(LibraryServiceImpl.class);

    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final BorrowingRepository borrowingRepository;
    private final Connection connection;

    @Override
    public void addBook(Book book) {
        book.setAvailable(true);
        bookRepository.save(book);
        logger.info("Book added: {}", book);
    }

    @Override
    public void removeBook(Long bookId) {
        Book book = bookRepository.findById(bookId);
        if (book == null) {
            logger.warn("Attempted to remove non-existent book with ID: {}", bookId);
            throw new IllegalArgumentException("Book not found");
        }
        bookRepository.delete(book);
        logger.info("Book removed: {}", book);
    }

    @Override
    public void updateBook(Long bookId, Book updatedBook) {
        Book existingBook = Optional.ofNullable(bookRepository.findById(bookId))
                .orElseThrow(() -> new IllegalArgumentException("Book not found for ID: " + bookId));

        // Update only if values are different
        if (!existingBook.equals(updatedBook)) {
            existingBook.updateWith(updatedBook); // Assuming a method exists for updating fields
            bookRepository.update(existingBook);
            logger.info("Book updated: {}", existingBook);
        }
        else {
            logger.info("No updates made for book ID: {}", bookId);
        }
    }

    @Override
    public Book getBookById(Long bookId) {
        return Optional.ofNullable(bookRepository.findById(bookId))
                .orElseThrow(() -> new IllegalArgumentException("Book not found for ID: " + bookId));
    }

    @Override
    public List<Book> getBooks() {
        List<Book> books = bookRepository.findAll();
        logger.info("Retrieved {} books.", books.size());
        return books;
    }

    @Override
    public void registerMember(Member member) {
        memberRepository.save(member);
        logger.info("Member registered: {}", member);
    }

    @Override
    public void removeMember(Long memberId) {
        Member member = memberRepository.findById(memberId);
        if (member == null) {
            logger.warn("Attempted to remove non-existent member with ID: {}", memberId);
            throw new IllegalArgumentException("Member not found");
        }
        memberRepository.delete(member);
        logger.info("Member removed: {}", member);
    }

    @Override
    public void updateMember(Long memberId, Member updatedMember) {
        Member existingMember = Optional.ofNullable(memberRepository.findById(memberId))
                .orElseThrow(() -> new IllegalArgumentException("Member not found for ID: " + memberId));

        // Update only if values are different
        if (!existingMember.equals(updatedMember)) {
            existingMember.updateWith(updatedMember); // Assuming a method exists for updating fields
            memberRepository.update(existingMember);
            logger.info("Member updated: {}", existingMember);
        } else {
            logger.info("No updates made for member ID: {}", memberId);
        }
    }

    @Override
    public Member getMemberById(Long memberId) {
        return Optional.ofNullable(memberRepository.findById(memberId))
                .orElseThrow(() -> new IllegalArgumentException("Member not found for ID: " + memberId));
    }

    @Override
    public List<Member> getMembers() {
        List<Member> members = memberRepository.findAll();
        logger.info("Retrieved {} members.", members.size());
        return members;
    }

    @Override
    public List<Book> getBooksByMemberId(Long memberId) throws SQLException {
        List<Book> books = memberRepository.findAllBooksByMemberId(memberId);
        logger.info("Retrieved {} books for member ID: {}", books.size(), memberId);
        return books;
    }

    @Override
    public void borrowBook(Long memberId, Long bookId) {
        executeBorrowing(() -> {
            try {
                borrowingRepository.borrowBook(memberId, bookId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, memberId, bookId);
    }

    @Override
    public void returnBook(Long memberId, Long bookId) {
        executeBorrowing(() -> {
            try {
                borrowingRepository.returnBook(memberId, bookId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, memberId, bookId);
    }

    private void executeBorrowing(Runnable borrowingAction, Long memberId, Long bookId) {
        try {
            connection.setAutoCommit(false);
            borrowingAction.run();
            connection.commit();
            logger.info("Transaction committed for memberId: {}, bookId: {}", memberId, bookId);
        } catch (SQLException e) {
            rollbackTransaction();
            logger.error("Failed transaction for memberId: {}, bookId: {}", memberId, bookId, e);
            throw new RuntimeException("Transaction failed", e);
        }
    }

    private void rollbackTransaction() {
        try {
            connection.rollback();
            logger.warn("Transaction rolled back due to an error.");
        } catch (SQLException e) {
            logger.error("Failed to rollback transaction", e);
        }
    }
}
