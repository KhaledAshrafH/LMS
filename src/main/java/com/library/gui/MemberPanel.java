package com.library.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import com.library.model.Book;
import com.library.model.Member;
import com.library.service.LibraryService;

public class MemberPanel extends JPanel {

    private final LibraryService libraryService;
    private JTextField nameField;
    private JTextField membershipIdField;
    private JTextField contactField;
    private JTable memberTable;
    private JTable borrowedBooksTable;
    private DefaultTableModel memberTableModel;

    public MemberPanel(LibraryService libraryService) {
        this.libraryService = libraryService;
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));

        // Add Member Panel
        JPanel addMemberPanel = createAddMemberPanel();
        add(addMemberPanel, BorderLayout.NORTH);

        // List Members Panel
        JPanel listMembersPanel = createListMembersPanel();
        add(listMembersPanel, BorderLayout.WEST);

        // Panel for showing borrowed books
        JPanel borrowedBooksPanel = createBorrowedBooksPanel();
        add(borrowedBooksPanel, BorderLayout.CENTER);
    }

    private JPanel createAddMemberPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(255, 255, 255));

        nameField = createTextField("Name");
        membershipIdField = createTextField("Membership ID");
        contactField = createTextField("Contact");

        JButton addMemberButton = createStyledButton("Register Member");
        addMemberButton.addActionListener(e -> registerMember());

        panel.add(nameField);
        panel.add(membershipIdField);
        panel.add(contactField);
        panel.add(Box.createRigidArea(new Dimension(0, 10))); // Space between fields and button
        panel.add(addMemberButton);

        return panel;
    }

    private JPanel createListMembersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(255, 255, 255));

        JButton listMembersButton = createStyledButton("List Members");
        listMembersButton.addActionListener(e -> listMembers());

        // Initialize member table
        String[] columnNames = {"ID", "Name", "Contact"};
        memberTableModel = new DefaultTableModel(columnNames, 0);
        memberTable = new JTable(memberTableModel);
        memberTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        memberTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = memberTable.getSelectedRow();
                if (selectedRow != -1) {
                    Long memberId = (Long) memberTable.getValueAt(selectedRow, 0);
                    showBorrowedBooks(borrowedBooksTable, memberId);
                }
            }
        });

        panel.add(listMembersButton, BorderLayout.NORTH);
        panel.add(new JScrollPane(memberTable), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBorrowedBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(255, 255, 255));

        JLabel borrowedBooksLabel = new JLabel("Borrowed Books:");
        panel.add(borrowedBooksLabel, BorderLayout.NORTH);

        borrowedBooksTable = new JTable();
        borrowedBooksTable.setModel(new DefaultTableModel(new String[]{"Title", "Author"}, 0));
        panel.add(new JScrollPane(borrowedBooksTable), BorderLayout.CENTER);

        return panel;
    }

    private JTextField createTextField(String label) {
        JTextField textField = new JTextField(15);
        textField.setBorder(BorderFactory.createTitledBorder(label));
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
        button.setPreferredSize(new Dimension(150, 40));

        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> button.setBackground(new Color(70, 129, 217))); // Change on hover

        return button;
    }

    private void registerMember() {
        String name = nameField.getText();
        String membershipId = membershipIdField.getText();
        String contact = contactField.getText();

        // Validation
        if (name.isEmpty() || membershipId.isEmpty() || contact.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        Member member = new Member(); // Assuming a default constructor and setters
        member.setName(name);
        member.setMembershipId(membershipId);
        member.setContactInfo(contact);

        libraryService.registerMember(member);
        JOptionPane.showMessageDialog(this, "Member registered successfully!");
        clearFields();
    }

    private void clearFields() {
        nameField.setText("");
        membershipIdField.setText("");
        contactField.setText("");
    }

    private void listMembers() {
        memberTableModel.setRowCount(0); // Clear existing entries
        List<Member> members = libraryService.getMembers();
        for (Member member : members) {
            memberTableModel.addRow(new Object[]{member.getId(), member.getName(), member.getContactInfo()});
        }
    }

    private void showBorrowedBooks(JTable table, Long memberId) {
        try {
            List<Book> borrowedBooks = libraryService.getBooksByMemberId(memberId);
            updateBorrowedBooksTable(table, borrowedBooks);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching borrowed books.");
            e.printStackTrace();
        }
    }

    private void updateBorrowedBooksTable(JTable table, List<Book> books) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0); // Clear existing data

        for (Book book : books) {
            model.addRow(new Object[]{book.getTitle(), book.getAuthor()});
        }
    }
}
