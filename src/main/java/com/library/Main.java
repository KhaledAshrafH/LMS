package com.library;

import com.library.gui.LibraryManagementFrame;

import com.library.repository.BookRepository;
import com.library.repository.BorrowingRepository;
import com.library.repository.MemberRepository;
import com.library.repository.impl.BookRepositoryImpl;
import com.library.repository.impl.BorrowingRepositoryImpl;
import com.library.repository.impl.MemberRepositoryImpl;
import com.library.service.LibraryService;
import com.library.service.impl.LibraryServiceImpl;


import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    // Database connection constants
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/lms_db";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver"); // Load the database driver

        // Establish connection to the database
        Connection connection = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);

        // Initialize repositories
        BookRepository bookRepository = new BookRepositoryImpl(connection);
        MemberRepository memberRepository = new MemberRepositoryImpl(connection);
        BorrowingRepository borrowingRepository = new BorrowingRepositoryImpl(connection, bookRepository, memberRepository);

        // Initialize the library service
        LibraryService libraryService = new LibraryServiceImpl(bookRepository,memberRepository,borrowingRepository,connection);

        // Launch the GUI
        SwingUtilities.invokeLater(() -> {
            LibraryManagementFrame frame = new LibraryManagementFrame(libraryService);
            frame.setVisible(true);
        });
    }
}