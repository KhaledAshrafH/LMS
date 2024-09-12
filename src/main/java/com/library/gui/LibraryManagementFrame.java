package com.library.gui;

import com.library.service.LibraryService;
import lombok.RequiredArgsConstructor;

import javax.swing.*;
import java.awt.*;

public class LibraryManagementFrame extends JFrame {
    private final LibraryService libraryService;
    public LibraryManagementFrame(LibraryService libraryService) {
        this.libraryService = libraryService;
        setTitle("Library Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Books", new BookPanel(libraryService));
        tabbedPane.addTab("Members", new MemberPanel(libraryService));
        tabbedPane.addTab("Borrow/Return", new BorrowReturnPanel(libraryService));

        add(tabbedPane, BorderLayout.CENTER);
    }
}