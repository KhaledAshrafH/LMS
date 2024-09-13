package com.library.gui;

import com.library.model.Book;
import com.library.service.LibraryService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class BookPanel extends JPanel {

    private final LibraryService libraryService;
    private JTextField titleField;
    private JTextField authorField;
    private JTextField isbnField;
    private JTable bookTable;
    private BookTableModel bookTableModel;

    public BookPanel(LibraryService libraryService) {
        this.libraryService = libraryService;
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));

        // create top panel for adding books
        JPanel addBookPanel = createAddBookPanel();
        add(addBookPanel, BorderLayout.NORTH);

        // create center panel for listing books
        JPanel listBooksPanel = createListBooksPanel();
        add(listBooksPanel, BorderLayout.CENTER);
    }

    // Create the panel for adding a book
    private JPanel createAddBookPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(255, 255, 255));

        titleField = createTextField("Title");
        authorField = createTextField("Author");
        isbnField = createTextField("ISBN");

        JButton addButton = createStyledButton("Add Book");
        addButton.addActionListener(new AddBookActionListener());

        panel.add(titleField);
        panel.add(authorField);
        panel.add(isbnField);
        panel.add(addButton);

        return panel;
    }

    // Create the panel for listing books
    private JPanel createListBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(255, 255, 255));

        JButton listBooksButton = createStyledButton("List Books");
        listBooksButton.addActionListener(new ListBooksActionListener());

        JButton updateButton = createStyledButton("Update");
        updateButton.addActionListener(new UpdateBookActionListener());

        JButton deleteButton = createStyledButton("Delete");
        deleteButton.addActionListener(new DeleteBookActionListener());

        bookTableModel = new BookTableModel();
        bookTable = new JTable(bookTableModel);
        JScrollPane scrollPane = new JScrollPane(bookTable);

        panel.add(listBooksButton, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    // To create a text field
    private JTextField createTextField(String label) {
        JTextField textField = new JTextField(15);
        textField.setBorder(BorderFactory.createTitledBorder(label));
        textField.setPreferredSize(new Dimension(250, 45));
        return textField;
    }

    // To create a button with specific style
    private JButton createStyledButton(String label) {
        JButton button = new JButton(label);
        button.setBackground(new Color(100, 149, 237));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(100, 40));

        UIManager.put("Button.border", BorderFactory.createEmptyBorder(10, 20, 10, 20));

        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(70, 129, 217));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(100, 149, 237));
            }
        });
        return button;
    }

    // Load books from the library service and update the table
    private void loadBooks() {
        List<Book> books = libraryService.getBooks();
        bookTableModel.setBooks(books);
    }

    // ActionListener for adding book
    private class AddBookActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String title = titleField.getText();
            String author = authorField.getText();
            String isbn = isbnField.getText();

            if (!title.isEmpty() && !author.isEmpty() && !isbn.isEmpty()) {
                Book book = new Book(title, author, isbn, true);
                libraryService.addBook(book);

                JOptionPane.showMessageDialog(BookPanel.this, "Book added successfully!");
                titleField.setText("");
                authorField.setText("");
                isbnField.setText("");
                loadBooks();
            }
            else
                JOptionPane.showMessageDialog(BookPanel.this, "All fields are required.");

        }
    }

    // ActionListener for listing the books
    private class ListBooksActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            loadBooks();
        }
    }

    // ActionListener for updating book
    private class UpdateBookActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = bookTable.getSelectedRow();
            if (selectedRow != -1) {
                Book selectedBook = bookTableModel.getBookAt(selectedRow);
                new BookEditDialog(selectedBook).setVisible(true);
            }
            else
                JOptionPane.showMessageDialog(BookPanel.this, "Please select book to update it.");

        }
    }

    // ActionListener for deleting book
    private class DeleteBookActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = bookTable.getSelectedRow();
            if (selectedRow != -1) {
                Book selectedBook = bookTableModel.getBookAt(selectedRow);
                int option = JOptionPane.showConfirmDialog(BookPanel.this, "Are you want to delete this book?", "Delete Book", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    libraryService.removeBook(selectedBook.getId());
                    JOptionPane.showMessageDialog(BookPanel.this, "Book deleted successfully!");
                    loadBooks();
                }
            }
            else
                JOptionPane.showMessageDialog(BookPanel.this, "Please select a book to delete.");

        }
    }

    // Dialog for editing specific book
    private class BookEditDialog extends JDialog {
        private final JTextField titleField;
        private final JTextField authorField;
        private final JTextField isbnField;

        public BookEditDialog(Book book) {
            setTitle("Edit Book ("+book.getTitle()+")");
            setModal(true);
            setSize(300, 200);
            setLocationRelativeTo(BookPanel.this);

            setLayout(new GridBagLayout());
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.weightx = 1.0;

            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.gridwidth = 2;
            add(new JLabel("Title:"), constraints);

            constraints.gridx = 0;
            constraints.gridy = 1;
            constraints.gridwidth = 2;
            titleField = new JTextField(book.getTitle());
            add(titleField, constraints);

            constraints.gridx = 0;
            constraints.gridy = 2;
            constraints.gridwidth = 2;
            add(new JLabel("Author:"), constraints);

            constraints.gridx = 0;
            constraints.gridy = 3;
            constraints.gridwidth = 2;
            authorField = new JTextField(book.getAuthor());
            add(authorField, constraints);

            constraints.gridx = 0;
            constraints.gridy = 4;
            constraints.gridwidth = 2;
            add(new JLabel("ISBN:"), constraints);

            constraints.gridx = 0;
            constraints.gridy = 5;
            constraints.gridwidth = 2;
            isbnField = new JTextField(book.getIsbn());
            add(isbnField, constraints);

            JButton saveButton = createStyledButton("Save");
            saveButton.addActionListener(e -> {
                book.setTitle(titleField.getText());
                book.setAuthor(authorField.getText());
                book.setIsbn(isbnField.getText());
                libraryService.updateBook(book.getId(), book);
                loadBooks();
                JOptionPane.showMessageDialog(BookEditDialog.this, "Book updated successfully!");
                dispose();
            });

            constraints.fill = GridBagConstraints.NONE;
            constraints.weighty = 1.0;
            constraints.gridx = 0;
            constraints.gridy = 6;
            constraints.gridwidth = 2;
            constraints.anchor = GridBagConstraints.CENTER;
            add(saveButton, constraints);
        }
    }
}