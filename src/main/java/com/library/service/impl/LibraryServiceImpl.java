package com.library.service.impl;

import com.library.model.Book;
import com.library.model.Member;
import com.library.repository.BookRepository;
import com.library.repository.BorrowingRepository;
import com.library.repository.MemberRepository;
import com.library.service.LibraryService;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
public class LibraryServiceImpl implements LibraryService {
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final BorrowingRepository borrowingRepository;
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
        Book bookReturned = bookRepository.findById(bookId);
        if(!bookReturned.getAuthor().equals(book.getAuthor()))
            bookReturned.setAuthor(book.getAuthor());

        if(!bookReturned.getTitle().equals(book.getTitle()))
            bookReturned.setTitle(book.getTitle());

        if(!bookReturned.getIsbn().equals(book.getIsbn()))
            bookReturned.setIsbn(book.getIsbn());

        bookRepository.update(bookReturned);
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
    public void updateMember(Long memberId,Member member) {
        Member memberReturned = memberRepository.findById(memberId);
        if(!memberReturned.getName().equals(member.getName()))
            memberReturned.setName(member.getName());

        if(!memberReturned.getMembershipId().equals(member.getMembershipId()))
            memberReturned.setMembershipId(member.getMembershipId());

        if(!memberReturned.getContactInfo().equals(member.getContactInfo()))
            memberReturned.setContactInfo(member.getContactInfo());

        memberRepository.update(memberReturned);
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
        connection.setAutoCommit(false);
        try {
            borrowingRepository.borrowBook(memberId, bookId);
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void returnBook(Long memberId, Long bookId) throws SQLException {
        connection.setAutoCommit(false);
        try {
            borrowingRepository.returnBook(memberId, bookId);
            connection.commit();
        }
        catch (SQLException e) {
            connection.rollback();
            throw new RuntimeException(e);
        }
    }
}

