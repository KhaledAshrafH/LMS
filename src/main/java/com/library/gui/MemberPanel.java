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

        JButton deleteMemberButton = createStyledButton("Delete Member");
        deleteMemberButton.addActionListener(e -> deleteMember());

        JButton updateMemberButton = createStyledButton("Update Member");
        updateMemberButton.addActionListener(e -> updateMember());

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

        // Add buttons to the south panel
        JPanel southPanel = new JPanel();
        southPanel.add(updateMemberButton);
        southPanel.add(deleteMemberButton);
        panel.add(southPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createBorrowedBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(255, 255, 255));

        JLabel borrowedBooksLabel = new JLabel("Borrowed Books:");
        panel.add(borrowedBooksLabel, BorderLayout.NORTH);

        borrowedBooksTable = new JTable();
        borrowedBooksTable.setModel(new DefaultTableModel(new String[]{"Book ID", "Title", "Author"}, 0));
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

        // Original background color to reset on mouse exit
        Color originalColor = button.getBackground();

        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // MouseListener for hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(70, 129, 217)); // Change color on hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor); // Restore original color
            }
        });

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
        listMembers(); // Refresh member list after adding
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

    private void deleteMember() {
        int selectedRow = memberTable.getSelectedRow();
        if (selectedRow != -1) {
            Long memberId = (Long) memberTable.getValueAt(selectedRow, 0);
            int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this member?", "Delete Member", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                libraryService.removeMember(memberId); // Update your service with actual implementation to delete member
                JOptionPane.showMessageDialog(this, "Member deleted successfully!");
                listMembers(); // Refresh list after deletion
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a member to delete.");
        }
    }

    private void updateMember() {
        int selectedRow = memberTable.getSelectedRow();
        if (selectedRow != -1) {
            Long memberId = (Long) memberTable.getValueAt(selectedRow, 0);
            Member selectedMember = libraryService.getMemberById(memberId); // Assuming you have this method in your service
            new MemberEditDialog(selectedMember).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a member to update.");
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
            model.addRow(new Object[]{book.getId(), book.getTitle(), book.getAuthor()}); // Added memberId to the row
        }
    }

    private class MemberEditDialog extends JDialog {
        private final JTextField nameField;
        private final JTextField membershipIdField;
        private final JTextField contactField;

        public MemberEditDialog(Member member) {

            setTitle("Edit Member");
            setModal(true);
            setSize(300, 200);
            setLocationRelativeTo(MemberPanel.this); // Center the dialog on the parent

            // Set layout manager
            setLayout(new GridBagLayout());
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.weightx = 1.0; // Allow components to stretch

            // Name field
            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.gridwidth = 2; // Use two columns
            add(new JLabel("Name:"), constraints);

            constraints.gridx = 0;
            constraints.gridy = 1;
            constraints.gridwidth = 2; // Use two columns
            nameField = new JTextField(member.getName());
            add(nameField, constraints);

            // Membership ID field
            constraints.gridx = 0;
            constraints.gridy = 2;
            constraints.gridwidth = 2; // Use two columns
            add(new JLabel("Membership ID:"), constraints);

            constraints.gridx = 0;
            constraints.gridy = 3;
            constraints.gridwidth = 2; // Use two columns
            membershipIdField = new JTextField(member.getMembershipId());
            add(membershipIdField, constraints);

            // Contact field
            constraints.gridx = 0;
            constraints.gridy = 4;
            constraints.gridwidth = 2;
            add(new JLabel("Contact:"), constraints);

            constraints.gridx = 0;
            constraints.gridy = 5;
            constraints.gridwidth = 2;
            contactField = new JTextField(member.getContactInfo());
            add(contactField, constraints);

            // Create a styled save button
            JButton saveButton = createStyledButton("Save");
            saveButton.addActionListener(e -> {
                member.setName(nameField.getText());
                member.setMembershipId(membershipIdField.getText());
                member.setContactInfo(contactField.getText());

                libraryService.updateMember(member.getId(), member);
                JOptionPane.showMessageDialog(MemberEditDialog.this, "Member updated successfully!");
                dispose();
                // Optionally refresh the list in the main panel when dialog is closed
                listMembers();
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
