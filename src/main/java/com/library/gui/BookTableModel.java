package com.library.gui;

import com.library.model.Book;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class BookTableModel extends AbstractTableModel {
    private final String[] columnNames = {"Title", "ISBN", "Author", "Available"};
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
        return columnNames[column];
    }

    public void setBooks(List<Book> books) {
        this.books = books;
        fireTableDataChanged();
    }

    // New method to get a book at a specific row
    public Book getBookAt(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < books.size()) {
            return books.get(rowIndex);
        }
        throw new IndexOutOfBoundsException("Row index out of bounds: " + rowIndex);
    }
}
