package com.library.repository;

import com.library.model.Book;
import com.library.model.Member;

import java.sql.SQLException;
import java.util.List;

public interface MemberRepository {
    void save(Member member);
    Member findById(Long id);
    List<Member> findAll();
    void delete(Member member);
    void update(Member member);
    List<Book> findAllBooksByMemberId(Long id) throws SQLException;
}
