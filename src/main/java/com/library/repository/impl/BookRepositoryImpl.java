package com.library.repository.impl;

import com.library.model.Book;
import com.library.repository.BookRepository;
import lombok.RequiredArgsConstructor;


import java.util.ArrayList;
import java.util.List;
import java.sql.*;

@RequiredArgsConstructor

public class BookRepositoryImpl implements BookRepository {

    private final Connection connection;

    @Override
    public void save(Book book) {
        String sql = "INSERT INTO books (title, isbn, is_available,author,publication_date) VALUES (?, ?, ?,?,CURDATE())";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, book.getTitle());
            preparedStatement.setString(2, book.getIsbn());
            preparedStatement.setBoolean(3, true);
            preparedStatement.setString(4, book.getAuthor());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Book findById(Long id) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM books WHERE id = ?")) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next())
            {
                Book book = new Book();
                book.setId(resultSet.getLong("id"));
                book.setTitle(resultSet.getString("title"));
                book.setIsbn(resultSet.getString("isbn"));
                book.setAuthor(resultSet.getString("author"));
                book.setAvailable(resultSet.getBoolean("is_available"));
                book.setPublicationDate(resultSet.getTimestamp("publication_date").toLocalDateTime());
                return book;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching book by ID", e);
        }
        return null;
    }

    @Override
    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM books");

            takeBookDetailsFromDatabase(books, resultSet);
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all books", e);
        }
        return books;
    }

    static void takeBookDetailsFromDatabase(List<Book> books, ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            Book book = new Book();
            book.setId(resultSet.getLong("id"));
            book.setTitle(resultSet.getString("title"));
            book.setIsbn(resultSet.getString("isbn"));
            book.setAuthor(resultSet.getString("author"));
            book.setAvailable(resultSet.getBoolean("is_available"));
            book.setPublicationDate(resultSet.getTimestamp("publication_date").toLocalDateTime());
            books.add(book);
        }
    }

    @Override
    public void delete(Book book) {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM books WHERE id = ?")) {
            statement.setLong(1, book.getId());
            int isDeleted = statement.executeUpdate();

            if (isDeleted == 0) {
                throw new RuntimeException("Book not found for deletion");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting book", e);
        }
    }

    @Override
    public void update(Book book) {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE books SET title = ?, author = ?, isbn = ? WHERE id = ?")) {
            statement.setString(1, book.getTitle());
            statement.setString(2, book.getAuthor());
            statement.setString(3, book.getIsbn());
            statement.setLong(4, book.getId());
            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated == 0) {
                throw new RuntimeException("Book not found for update");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating book", e);
        }
    }
}
