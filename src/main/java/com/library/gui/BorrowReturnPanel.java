package com.library.gui;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import com.library.service.LibraryService;

public class BorrowReturnPanel extends JPanel {

    private final LibraryService libraryService;
    private JTextField memberIdField;
    private JTextField bookIdField;
    private JTextField returnMemberIdField;
    private JTextField returnBookIdField;

    private static final int TEXT_FIELD_HEIGHT = 35; // Height for input fields

    public BorrowReturnPanel(LibraryService libraryService) {
        this.libraryService = libraryService;
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));

        add(createBorrowPanel(), BorderLayout.NORTH);
        add(createReturnPanel(), BorderLayout.CENTER);
    }

    private JPanel createBorrowPanel() {
        JPanel panel = createInputPanel("Borrow Book");

        memberIdField = createTextField("Member ID");
        bookIdField = createTextField("Book ID");

        JButton borrowButton = createStyledButton("Borrow Book");
        borrowButton.addActionListener(e -> handleBorrow());

        addComponentsToPanel(panel, memberIdField, bookIdField, borrowButton);
        return panel;
    }

    private JPanel createReturnPanel() {
        JPanel panel = createInputPanel("Return Book");

        returnMemberIdField = createTextField("Member ID");
        returnBookIdField = createTextField("Book ID");

        JButton returnButton = createStyledButton("Return Book");
        returnButton.addActionListener(e -> handleReturn());

        addComponentsToPanel(panel, returnMemberIdField, returnBookIdField, returnButton);
        return panel;
    }

    private JPanel createInputPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Changed to BoxLayout for more control over component sizes
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return panel;
    }

    private JTextField createTextField(String title) {
        JTextField textField = new JTextField(10);
        textField.setBorder(BorderFactory.createTitledBorder(title));
        textField.setMaximumSize(new Dimension(Short.MAX_VALUE, TEXT_FIELD_HEIGHT)); // Use Short.MAX_VALUE for width
        textField.setPreferredSize(new Dimension(120, TEXT_FIELD_HEIGHT)); // Fixed height
        return textField;
    }

    private JButton createStyledButton(String label) {
        JButton button = new JButton(label);
        button.setBackground(new Color(100, 149, 237));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(120, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(70, 129, 217));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(100, 149, 237));
            }
        });

        return button;
    }

    private void addComponentsToPanel(JPanel panel, JComponent... components) {
        // Center button using FlowLayout
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        for (JComponent component : components) {
            if (component instanceof JButton) {
                buttonPanel.add(component);
            } else {
                panel.add(component);
            }
        }
        panel.add(buttonPanel); // Add button panel to the main panel
    }

    private void handleBorrow() {
        try {
            long memberId = Long.parseLong(memberIdField.getText());
            long bookId = Long.parseLong(bookIdField.getText());
            libraryService.borrowBook(memberId, bookId);
            showMessage("Book borrowed successfully.");
        } catch (NumberFormatException ex) {
            showMessage("Invalid member ID or book ID.");
        } catch (SQLException ex) {
            showMessage("Error borrowing book: " + ex.getMessage());
        }
    }

    private void handleReturn() {
        try {
            long memberId = Long.parseLong(returnMemberIdField.getText());
            long bookId = Long.parseLong(returnBookIdField.getText());
            libraryService.returnBook(memberId, bookId);
            showMessage("Book returned successfully.");
        } catch (NumberFormatException ex) {
            showMessage("Invalid member ID or book ID.");
        } catch (SQLException ex) {
            showMessage("Error returning book: " + ex.getMessage());
        }
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
}
