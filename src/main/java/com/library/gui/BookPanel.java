package com.library.gui;

import com.library.model.Book;
import com.library.service.LibraryService;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

@Slf4j
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

        // Create top panel for adding books
        JPanel addBookPanel = createAddBookPanel();
        add(addBookPanel, BorderLayout.NORTH);

        // Create center panel for listing books
        JPanel listBooksPanel = createListBooksPanel();
        add(listBooksPanel, BorderLayout.CENTER);
    }

    private JPanel createAddBookPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(255, 255, 255));
        panel.setOpaque(true);

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

    private JTextField createTextField(String label) {
        JTextField textField = new JTextField(15);
        textField.setBorder(BorderFactory.createTitledBorder(label));
        textField.setPreferredSize(new Dimension(250, 45));
        return textField;
    }

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

    private void loadBooks() {
        List<Book> books = libraryService.getBooks();
        bookTableModel.setBooks(books);
    }

    private class AddBookActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String title = titleField.getText();
            String author = authorField.getText();
            String isbn = isbnField.getText();

            if (!title.isEmpty() && !author.isEmpty() && !isbn.isEmpty()) {
                Book book = new Book();
                book.setAuthor(author);
                book.setTitle(title);
                book.setIsbn(isbn);
                book.setAvailable(true);
                libraryService.addBook(book);

                JOptionPane.showMessageDialog(BookPanel.this, "Book added successfully!");
                titleField.setText("");
                authorField.setText("");
                isbnField.setText("");
                loadBooks();
            } else {
                JOptionPane.showMessageDialog(BookPanel.this, "All fields are required.");
            }
        }
    }

    private class ListBooksActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            loadBooks();
        }
    }

    private class UpdateBookActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = bookTable.getSelectedRow();
            if (selectedRow != -1) {
                Book selectedBook = bookTableModel.getBookAt(selectedRow);
                new BookEditDialog(selectedBook).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(BookPanel.this, "Please select a book to update.");
            }
        }
    }

    private class DeleteBookActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = bookTable.getSelectedRow();
            if (selectedRow != -1) {
                Book selectedBook = bookTableModel.getBookAt(selectedRow);
                int option = JOptionPane.showConfirmDialog(BookPanel.this, "Are you sure you want to delete this book?", "Delete Book", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    libraryService.removeBook(selectedBook.getId());
                    JOptionPane.showMessageDialog(BookPanel.this, "Book deleted successfully!");
                    loadBooks();
                }
            } else {
                JOptionPane.showMessageDialog(BookPanel.this, "Please select a book to delete.");
            }
        }
    }

    private class BookEditDialog extends JDialog {
        private final JTextField titleField;
        private final JTextField authorField;
        private final JTextField isbnField;

        public BookEditDialog(Book book) {
            setTitle("Edit Book");
            setModal(true);
            setSize(300, 200);
            setLocationRelativeTo(BookPanel.this); // Center the dialog on the parent

            // Set layout manager
            setLayout(new GridBagLayout());
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.weightx = 1.0; // Allow components to stretch

            // Title field
            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.gridwidth = 2; // Use two columns
            add(new JLabel("Title:"), constraints);

            constraints.gridx = 0;
            constraints.gridy = 1;
            constraints.gridwidth = 2; // Use two columns
            titleField = new JTextField(book.getTitle());
            add(titleField, constraints);

            // Author field
            constraints.gridx = 0;
            constraints.gridy = 2;
            constraints.gridwidth = 2; // Use two columns
            add(new JLabel("Author:"), constraints);

            constraints.gridx = 0;
            constraints.gridy = 3;
            constraints.gridwidth = 2; // Use two columns
            authorField = new JTextField(book.getAuthor());
            add(authorField, constraints);

            // ISBN field
            constraints.gridx = 0;
            constraints.gridy = 4;
            constraints.gridwidth = 2; // Use two columns
            add(new JLabel("ISBN:"), constraints);

            constraints.gridx = 0;
            constraints.gridy = 5;
            constraints.gridwidth = 2; // Use two columns
            isbnField = new JTextField(book.getIsbn());
            add(isbnField, constraints);

            // Create a styled save button
            JButton saveButton = createStyledButton("Save");
            saveButton.addActionListener(e -> {
                book.setTitle(titleField.getText());
                book.setAuthor(authorField.getText());
                book.setIsbn(isbnField.getText());
                log.info(book.toString());
                log.info("--------------------");
                log.info(book.getId().toString());
                libraryService.updateBook(book.getId(), book);
                loadBooks();
                JOptionPane.showMessageDialog(BookEditDialog.this, "Book updated successfully!");
                dispose();
            });

            // Center the save button
            constraints.fill = GridBagConstraints.NONE; // Do not stretch
            constraints.weighty = 1.0; // Allow vertical space to grow
            constraints.gridx = 0;
            constraints.gridy = 6;
            constraints.gridwidth = 2; // Use two columns for the button
            constraints.anchor = GridBagConstraints.CENTER; // Center the button
            add(saveButton, constraints);
        }
    }
}
