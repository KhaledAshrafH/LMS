package com.library.repository.impl;

import com.library.model.Book;
import com.library.model.Member;
import com.library.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {
    private final Connection connection;

    @Override
    public void save(Member member) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO members (name, membership_id, contact_info) VALUES (?, ?, ?)")) {
            statement.setString(1, member.getName());
            statement.setString(2, member.getMembershipId());
            statement.setString(3, member.getContactInfo());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving member", e);
        }
    }

    @Override
    public Member findById(Long id) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM members WHERE id = ?")) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next())
            {
                Member member = new Member();
                member.setId(resultSet.getLong("id"));
                member.setName(resultSet.getString("name"));
                member.setMembershipId(resultSet.getString("membership_id"));
                member.setContactInfo(resultSet.getString("contact_info"));
                member.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
                return member;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding member by ID", e);
        }
        return null;
    }

    @Override
    public List<Member> findAll() {
        List<Member> members = new ArrayList<>();
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM members");

            while (resultSet.next())
            {
                Member member = new Member();
                member.setId(resultSet.getLong("id"));
                member.setName(resultSet.getString("name"));
                member.setMembershipId(resultSet.getString("membership_id"));
                member.setContactInfo(resultSet.getString("contact_info"));
                member.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
                members.add(member);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all members", e);
        }
        return members;
    }

    @Override
    public void delete(Member member) {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM members WHERE id = ?")) {
            statement.setLong(1, member.getId());
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted == 0) {
                throw new RuntimeException("Member not found for deletion");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting member", e);
        }
    }

    @Override
    public void update(Member member) {
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE members SET name = ?, membership_id = ?, contact_info = ? WHERE id = ?")) {
            statement.setString(1, member.getName());
            statement.setString(2, member.getMembershipId());
            statement.setString(3, member.getContactInfo());
            statement.setLong(4, member.getId());

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated == 0) {
                throw new RuntimeException("Member not found for update");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating member", e);
        }
    }

    @Override
    public List<Book> findAllBooksByMemberId(Long id) throws SQLException {
        List<Book> borrowedBooks = new ArrayList<>();
             PreparedStatement statement = connection.prepareStatement("SELECT b.* FROM books b " +
                             "INNER JOIN member_book mb ON b.id = mb.book_id " +
                             "WHERE mb.member_id = ?");

            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Book book = new Book();
                book.setId(resultSet.getLong("id"));
                book.setTitle(resultSet.getString("title"));
                book.setIsbn(resultSet.getString("isbn"));
                book.setAuthor(resultSet.getString("author"));
                book.setAvailable(resultSet.getBoolean("is_available"));
                book.setPublicationDate(resultSet.getTimestamp("publication_date").toLocalDateTime());
                borrowedBooks.add(book);
            }
            return borrowedBooks;
        }
    }




