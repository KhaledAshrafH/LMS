package com.library.gui;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import com.library.service.LibraryService;

public class BorrowReturnPanel extends JPanel {

    private final LibraryService libraryService;

    // Input fields for borrowing and returning books
    private JTextField memberIdField;
    private JTextField bookIdField;
    private JTextField returnMemberIdField;
    private JTextField returnBookIdField;

    private static final int TEXT_FIELD_HEIGHT = 35; // Height for input fields

    public BorrowReturnPanel(LibraryService libraryService) {
        this.libraryService = libraryService;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));

        add(createBorrowPanel(), BorderLayout.NORTH);
        add(createReturnPanel(), BorderLayout.CENTER);
    }

    private JPanel createBorrowPanel() {
        JPanel panel = createInputPanel("Borrow Book");

        memberIdField = createTextField("Member ID");
        bookIdField = createTextField("Book ID");
        JButton borrowButton = createActionButton("Borrow Book", e -> handleBorrow());

        addComponentsToPanel(panel, memberIdField, bookIdField, borrowButton);
        return panel;
    }

    private JPanel createReturnPanel() {
        JPanel panel = createInputPanel("Return Book");

        returnMemberIdField = createTextField("Member ID");
        returnBookIdField = createTextField("Book ID");
        JButton returnButton = createActionButton("Return Book", e -> handleReturn());

        addComponentsToPanel(panel, returnMemberIdField, returnBookIdField, returnButton);
        return panel;
    }

    private JPanel createInputPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return panel;
    }

    private JTextField createTextField(String title) {
        JTextField textField = new JTextField(10);
        textField.setBorder(BorderFactory.createTitledBorder(title));
        textField.setMaximumSize(new Dimension(Short.MAX_VALUE, TEXT_FIELD_HEIGHT));
        textField.setPreferredSize(new Dimension(120, TEXT_FIELD_HEIGHT));
        return textField;
    }

    private JButton createActionButton(String label, java.awt.event.ActionListener actionListener) {
        JButton button = new JButton(label);
        styleButton(button);
        button.addActionListener(actionListener);
        return button;
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(100, 149, 237));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(120, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // change button color on mouse hover
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
    }

    private void addComponentsToPanel(JPanel panel, JComponent... components) {
        // create panel to center align buttons
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
        processBookAction(memberIdField, bookIdField, libraryService::borrowBook, "Book borrowed successfully.", "Error borrowing book: ");
    }

    private void handleReturn() {
        processBookAction(returnMemberIdField, returnBookIdField, libraryService::returnBook, "Book returned successfully.", "Error returning book: ");
    }

    /**
     * General method to handle borrowing or returning books to reduce code duplication.
     */
    private void processBookAction(JTextField memberIdField, JTextField bookIdField,
                                   BookAction action, String successMessage, String errorMessage) {
        try {
            long memberId = Long.parseLong(memberIdField.getText());
            long bookId = Long.parseLong(bookIdField.getText());
            action.perform(memberId, bookId);
            showMessage(successMessage);
        } catch (NumberFormatException ex) {
            showMessage("Invalid member ID or book ID.");
        } catch (SQLException ex) {
            showMessage(errorMessage + ex.getMessage());
        }
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    // Functional Interface for book actions (borrowing and returning)
    @FunctionalInterface
    private interface BookAction {
        void perform(long memberId, long bookId) throws SQLException;
    }
}
