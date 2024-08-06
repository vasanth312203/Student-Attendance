package com.example.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    private static final String URL = "jdbc:mysql://localhost:3306/school";
    private static final String USER = "root"; // Replace with your MySQL username
    private static final String PASSWORD = "root"; // Replace with your MySQL password

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Scanner scanner = new Scanner(System.in)) {

            while (true) {
                System.out.println("Choose an action:");
                System.out.println("1. Input Student Names");
                System.out.println("2. Mark Attendance");
                System.out.println("3. Print Attendance Counts");
                System.out.println("4. Exit");

                int choice = getIntInput(scanner);

                switch (choice) {
                    case 1:
                        inputStudentNames(connection, scanner);
                        break;
                    case 2:
                        markAttendance(connection, scanner);
                        break;
                    case 3:
                        printAttendanceCounts(connection);
                        break;
                    case 4:
                        System.out.println("Exiting...");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void inputStudentNames(Connection connection, Scanner scanner) throws SQLException {
        System.out.println("Enter student names (type 'done' to finish):");

        String name;
        while (true) {
            System.out.print("Enter student name: ");
            name = scanner.nextLine();
            if ("done".equalsIgnoreCase(name)) {
                break;
            }

            String insertSQL = "INSERT INTO students (name) VALUES (?) ON DUPLICATE KEY UPDATE name = name";
            try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
                pstmt.setString(1, name);
                pstmt.executeUpdate();
            }
        }
        System.out.println("Student names recorded.");
    }

    private static void markAttendance(Connection connection, Scanner scanner) throws SQLException {
        String selectSQL = "SELECT name FROM students";
        try (PreparedStatement stmt = connection.prepareStatement(selectSQL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String studentName = rs.getString("name");
                System.out.printf("Mark attendance for %s (present/absent): ", studentName);
                String status = scanner.nextLine().trim().toLowerCase();

                String updateSQL = "UPDATE students SET attendance = ? WHERE name = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(updateSQL)) {
                    pstmt.setBoolean(1, "present".equals(status));
                    pstmt.setString(2, studentName);
                    pstmt.executeUpdate();
                }
            }
        }
    }

    private static void printAttendanceCounts(Connection connection) throws SQLException {
        String presentSQL = "SELECT COUNT(*) FROM students WHERE attendance = TRUE";
        String absentSQL = "SELECT COUNT(*) FROM students WHERE attendance = FALSE";

        try (PreparedStatement pstmt = connection.prepareStatement(presentSQL);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                int presentCount = rs.getInt(1);
                System.out.printf("Number of students present: %d%n", presentCount);
            }
        }

        try (PreparedStatement pstmt = connection.prepareStatement(absentSQL);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                int absentCount = rs.getInt(1);
                System.out.printf("Number of students absent: %d%n", absentCount);
            }
        }
    }

    private static int getIntInput(Scanner scanner) {
        while (true) {
            try {
                return scanner.nextInt();
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); // Clear the invalid input
            }
        }
    }
}
