package com.library.repository.impl;

import com.library.exception.ResourceNotFoundException;
import com.library.model.Book;
import com.library.repository.BookRepository;
import lombok.RequiredArgsConstructor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class BookRepositoryImpl implements BookRepository {

    private final Connection connection;

    @Override
    public void save(Book book) {
        String sql = "INSERT INTO books (title, isbn, is_available, author, publication_date) VALUES (?, ?, ?, ?, CURDATE())";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            setBookParameters(preparedStatement, book);
            preparedStatement.setBoolean(3, true); // Setting is_available to true
            preparedStatement.executeUpdate();
        }
        catch (SQLException e) {
            handleSQLException(e, "Error saving book");
        }
    }

    @Override
    public Book findById(Long id) {
        String sql = "SELECT * FROM books WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next())
                return createBookFromResultSet(resultSet);

        } catch (SQLException e) {
            handleSQLException(e, "Error fetching book by ID");
        }
        return null;
    }

    @Override
    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next())
                books.add(createBookFromResultSet(resultSet));

        }
        catch (SQLException e) {
            handleSQLException(e, "Error fetching all books");
        }
        return books;
    }

    @Override
    public void delete(Book book) {
        String sql = "DELETE FROM books WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, book.getId());
            int rowsDeleted = statement.executeUpdate();

            if (rowsDeleted == 0)
                throw new ResourceNotFoundException("Book not found for deletion");

        } catch (SQLException e) {
            handleSQLException(e, "Error deleting book");
        }
    }

    @Override
    public void update(Book book) {
        String sql = "UPDATE books SET title = ?, author = ?, isbn = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            setBookParameters(statement, book);
            statement.setLong(4, book.getId());
            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated == 0)
                throw new ResourceNotFoundException("Book not found for update");

        } catch (SQLException e) {
            handleSQLException(e, "Error updating book");
        }
    }

    // Utility Function to set parameters from a Book object
    private void setBookParameters(PreparedStatement statement, Book book) throws SQLException {
        statement.setString(1, book.getTitle());
        statement.setString(2, book.getAuthor());
        statement.setString(3, book.getIsbn());
    }

    // Creates Book object from a ResultSet
    private Book createBookFromResultSet(ResultSet resultSet) throws SQLException {
        Book book = new Book();
        book.setId(resultSet.getLong("id"));
        book.setTitle(resultSet.getString("title"));
        book.setIsbn(resultSet.getString("isbn"));
        book.setAuthor(resultSet.getString("author"));
        book.setAvailable(resultSet.getBoolean("is_available"));
        book.setPublicationDate(resultSet.getTimestamp("publication_date").toLocalDateTime());
        return book;
    }

    static void takeBooksDetailsFromResultSet(List<Book> books, ResultSet resultSet) throws SQLException {
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

    // Error handling for SQLExceptions
    private void handleSQLException(SQLException e, String message) {
        throw new RuntimeException(message, e);
    }


}
