package com.library.gui;

import com.library.service.LibraryService;

import javax.swing.*;
import java.awt.*;

public class LibraryManagementFrame extends JFrame {

    // initializes the main frame of the library management system
    public LibraryManagementFrame(LibraryService libraryService) {
        setupFrame();
        JTabbedPane tabbedPane = createTabbedPane(libraryService);
        add(tabbedPane, BorderLayout.CENTER);
    }

    // Setup main frame properties
    private void setupFrame() {
        setTitle("Library Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    // create the tabbed pane with different panels
    private JTabbedPane createTabbedPane(LibraryService libraryService) {
        JTabbedPane tabbedPane = new JTabbedPane();

        // Adding tabs for the main functionalities
        tabbedPane.addTab("Books", new BookPanel(libraryService));
        tabbedPane.addTab("Members", new MemberPanel(libraryService));
        tabbedPane.addTab("Borrow/Return", new BorrowReturnPanel(libraryService));

        return tabbedPane;
    }
}
