package com.library.gui;

import com.library.model.Book;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class BookTableModel extends AbstractTableModel {

    // Column names for the table
    private final String[] columnNames = {"Title", "ISBN", "Author", "Available"};

    // List to hold book data
    private List<Book> books = new ArrayList<>();

    @Override
    public int getRowCount() {
        return books.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Book book = books.get(rowIndex);

        return switch (columnIndex) {
            case 0 -> book.getTitle();
            case 1 -> book.getIsbn();
            case 2 -> book.getAuthor();
            case 3 -> book.isAvailable() ? "Yes" : "No";
            default -> null;
        };
    }

    @Override
    public String getColumnName(int column) {
        if (column < 0 || column >= columnNames.length) {
            throw new IndexOutOfBoundsException("Column index out of bounds: " + column);
        }
        return columnNames[column];
    }

    // updates the list of books and notifies the table of changes
    public void setBooks(List<Book> books) {
        this.books = books;
        fireTableDataChanged(); // Notify listeners that the table data has changed
    }

    // retrieves the book at the specified index of row
    public Book getBookAt(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= books.size()) {
            throw new IndexOutOfBoundsException("Row index out of bounds: " + rowIndex);
        }
        return books.get(rowIndex);
    }
}
