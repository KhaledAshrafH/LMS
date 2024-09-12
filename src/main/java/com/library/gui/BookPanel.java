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

        bookTableModel = new BookTableModel();
        bookTable = new JTable(bookTableModel);
        JScrollPane scrollPane = new JScrollPane(bookTable);

        panel.add(listBooksButton, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

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
}
