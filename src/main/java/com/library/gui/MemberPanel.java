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

        // Create and add the panels
        add(createAddMemberPanel(), BorderLayout.NORTH);
        add(createListMembersPanel(), BorderLayout.WEST);
        add(createBorrowedBooksPanel(), BorderLayout.CENTER);
    }

    private JPanel createAddMemberPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);

        // Initialize text fields
        nameField = createTextField("Name");
        membershipIdField = createTextField("Membership ID");
        contactField = createTextField("Contact");

        // create button for adding a new member
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
        panel.setBackground(Color.WHITE);

        // Buttons for listing, updating, and deleting members
        panel.add(createListMembersButton(), BorderLayout.NORTH);
        memberTable = initializeMemberTable();
        panel.add(new JScrollPane(memberTable), BorderLayout.CENTER);
        panel.add(createMemberActionButtons(), BorderLayout.SOUTH);
        return panel;
    }

    private JButton createListMembersButton() {
        JButton listMembersButton = createStyledButton("List Members");
        listMembersButton.addActionListener(e -> listMembers());
        return listMembersButton;
    }

    private JTable initializeMemberTable() {
        String[] columnNames = {"ID", "Name", "Contact"};
        memberTableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(memberTableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    Long memberId = (Long) table.getValueAt(selectedRow, 0);
                    showBorrowedBooks(memberId);
                }
            }
        });
        return table;
    }

    private JPanel createMemberActionButtons() {
        JPanel southPanel = new JPanel();
        JButton updateMemberButton = createStyledButton("Update Member");
        updateMemberButton.addActionListener(e -> updateMember());
        JButton deleteMemberButton = createStyledButton("Delete Member");
        deleteMemberButton.addActionListener(e -> deleteMember());
        southPanel.add(updateMemberButton);
        southPanel.add(deleteMemberButton);
        return southPanel;
    }

    private JPanel createBorrowedBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);

        JLabel borrowedBooksLabel = new JLabel("Borrowed Books:");
        panel.add(borrowedBooksLabel, BorderLayout.NORTH);

        borrowedBooksTable = new JTable(new DefaultTableModel(new String[]{"Book ID", "Title", "Author"}, 0));
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

        Color originalColor = button.getBackground();
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(70, 129, 217)); // Hover color
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor); // Reset color
            }
        });
        return button;
    }

    private void registerMember() {
        String name = nameField.getText();
        String membershipId = membershipIdField.getText();
        String contact = contactField.getText();

        // Validate input fields
        if (validateMemberInput(name, membershipId, contact)) {
            Member member = new Member(name, membershipId, contact);
            libraryService.registerMember(member);
            JOptionPane.showMessageDialog(this, "Member registered successfully!");
            clearFields();
            listMembers();
        }
    }

    private boolean validateMemberInput(String name, String membershipId, String contact) {
        if (name.isEmpty() || membershipId.isEmpty() || contact.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return false;
        }
        return true;
    }

    private void clearFields() {
        nameField.setText("");
        membershipIdField.setText("");
        contactField.setText("");
    }

    private void listMembers() {
        memberTableModel.setRowCount(0); // clear existing entries
        List<Member> members = libraryService.getMembers();
        members.forEach(member -> memberTableModel.addRow(new Object[]{
                member.getId(), member.getName(), member.getContactInfo()}));
    }

    private void deleteMember() {
        int selectedRow = memberTable.getSelectedRow();
        if (selectedRow != -1) {
            Long memberId = (Long) memberTable.getValueAt(selectedRow, 0);
            int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this member?", "Delete Member", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                libraryService.removeMember(memberId);
                JOptionPane.showMessageDialog(this, "Member deleted successfully!");
                listMembers();
            }
        }
        else
            JOptionPane.showMessageDialog(this, "Please select a member to delete.");

    }

    private void updateMember() {
        int selectedRow = memberTable.getSelectedRow();
        if (selectedRow != -1) {
            Long memberId = (Long) memberTable.getValueAt(selectedRow, 0);
            Member selectedMember = libraryService.getMemberById(memberId);
            new MemberEditDialog(selectedMember).setVisible(true);
        }
        else
            JOptionPane.showMessageDialog(this, "Please select a member to update.");

    }

    private void showBorrowedBooks(Long memberId) {
        try {
            List<Book> borrowedBooks = libraryService.getBooksByMemberId(memberId);
            updateBorrowedBooksTable(borrowedBooks);
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching borrowed books.");
            e.printStackTrace();
        }
    }

    private void updateBorrowedBooksTable(List<Book> books) {
        DefaultTableModel model = (DefaultTableModel) borrowedBooksTable.getModel();
        model.setRowCount(0); // clear existing data
        books.forEach(book -> model.addRow(new Object[]{book.getId(), book.getTitle(), book.getAuthor()}));
    }

    private class MemberEditDialog extends JDialog {
        private final JTextField nameField;
        private final JTextField membershipIdField;
        private final JTextField contactField;

        public MemberEditDialog(Member member) {
            setTitle("Edit Member");
            setModal(true);
            setSize(300, 200);
            setLocationRelativeTo(MemberPanel.this);
            setLayout(new GridBagLayout());
            GridBagConstraints constraints = createGridBagConstraints();

            // Set up form fields
            nameField = createLabeledField(constraints, member.getName(), "Name:", 0);
            membershipIdField = createLabeledField(constraints, member.getMembershipId(), "Membership ID:", 2);
            contactField = createLabeledField(constraints, member.getContactInfo(), "Contact:", 4);

            // Create save button
            JButton saveButton = createStyledButton("Save");
            saveButton.addActionListener(e -> saveUpdatedMember(member));
            constraints.fill = GridBagConstraints.NONE;
            constraints.weighty = 1.0;
            constraints.gridx = 0;
            constraints.gridy = 6;
            constraints.gridwidth = 2;
            constraints.anchor = GridBagConstraints.CENTER;
            add(saveButton, constraints);
        }

        private GridBagConstraints createGridBagConstraints() {
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.weightx = 1.0; // to stretch the components
            return constraints;
        }

        private JTextField createLabeledField(GridBagConstraints constraints, String text, String label, int gridY) {
            constraints.gridx = 0;
            constraints.gridy = gridY;
            constraints.gridwidth = 2;
            add(new JLabel(label), constraints);

            constraints.gridy++;
            JTextField field = new JTextField(text);
            add(field, constraints);
            return field;
        }

        private void saveUpdatedMember(Member member) {
            member.setName(nameField.getText());
            member.setMembershipId(membershipIdField.getText());
            member.setContactInfo(contactField.getText());
            libraryService.updateMember(member.getId(), member);
            JOptionPane.showMessageDialog(MemberEditDialog.this, "Member updated successfully!");
            dispose();
            listMembers();
        }
    }
}
