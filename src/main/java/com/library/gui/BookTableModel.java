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
        switch (columnIndex) {
            case 0:
                return book.getTitle();
            case 1:
                return book.getIsbn();
            case 2:
                return book.getAuthor();
            case 3:
                return book.isAvailable() ? "Yes" : "No";
            default:
                return null;
        }
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    public void setBooks(List<Book> books) {
        this.books = books;
        fireTableDataChanged();
    }
}

