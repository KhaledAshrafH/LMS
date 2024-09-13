package com.library.repository.impl;

import com.library.exception.ResourceNotFoundException;
import com.library.model.Book;
import com.library.model.Member;
import com.library.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {
    private final Connection connection;

    @Override
    public void save(Member member) {
        String query = "INSERT INTO members (name, membership_id, contact_info) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {

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
        String query = "SELECT * FROM members WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();


            if (resultSet.next())
                return mapToMember(resultSet);

        } catch (SQLException e) {
            throw new RuntimeException("Error finding member by ID", e);
        }
        return null;
    }

    @Override
    public List<Member> findAll() {
        List<Member> members = new ArrayList<>();
        String query = "SELECT * FROM members";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {


            while (resultSet.next())
                members.add(mapToMember(resultSet));

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all members", e);
        }
        return members;
    }

    @Override
    public void delete(Member member) {
        String query = "DELETE FROM members WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, member.getId());
            int rowsDeleted = statement.executeUpdate();


            if (rowsDeleted == 0)
                throw new ResourceNotFoundException("Member not found for deletion");

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting member", e);
        }
    }

    @Override
    public void update(Member member) {
        String query = "UPDATE members SET name = ?, membership_id = ?, contact_info = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, member.getName());
            statement.setString(2, member.getMembershipId());
            statement.setString(3, member.getContactInfo());
            statement.setLong(4, member.getId());

            int rowsUpdated = statement.executeUpdate();


            if (rowsUpdated == 0)
                throw new ResourceNotFoundException("Member not found for update");

        } catch (SQLException e) {
            throw new RuntimeException("Error updating member", e);
        }
    }

    @Override
    public List<Book> findAllBooksByMemberId(Long id) throws SQLException {
        List<Book> borrowedBooks = new ArrayList<>();
        String query = "SELECT b.* FROM books b " +
                "INNER JOIN member_book mb ON b.id = mb.book_id " +
                "WHERE mb.member_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            // To build List of books from ResultSet
            BookRepositoryImpl.takeBooksDetailsFromResultSet(borrowedBooks, resultSet);
        }
        return borrowedBooks;
    }

    // Utility function to map ResultSet to Member object
    private Member mapToMember(ResultSet resultSet) throws SQLException {
        Member member = new Member();
        member.setId(resultSet.getLong("id"));
        member.setName(resultSet.getString("name"));
        member.setMembershipId(resultSet.getString("membership_id"));
        member.setContactInfo(resultSet.getString("contact_info"));
        member.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
        return member;
    }

}
