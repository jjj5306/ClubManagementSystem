package clubManagement;

import clubManagement.menu.*;
import clubManagement.utils.DatabaseConnection;

import java.sql.Connection;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Connection conn = null;
        StudentMenuHandler studentMenuHandler = null;
        ClubMenuHandler clubMenuHandler = null;
        ProfessorMenuHandler professorMenuHandler = null;
        BudgetMenuHandler budgetMenuHandler = new BudgetMenuHandler(conn, scanner);
        ProjectMenuHandler projectMenuHandler = null;

        System.out.println("2020039071 조준화 Database System Term Project - Club Management System\n");

        while (true) {
            displayMainMenu();
            System.out.print("Select menu: ");
            int choice = scanner.nextInt();

            try {
                switch (choice) {
                    case 1 -> {
                        // Database Connection
                        try {
                            conn = DatabaseConnection.getConnection();
                            studentMenuHandler = new StudentMenuHandler(conn, scanner);
                            clubMenuHandler = new ClubMenuHandler(conn, scanner);
                            professorMenuHandler = new ProfessorMenuHandler(conn, scanner);
                            budgetMenuHandler = new BudgetMenuHandler(conn, scanner);
                            projectMenuHandler = new ProjectMenuHandler(conn, scanner);
                            System.out.println("Database connection successful!");
                        } catch (Exception e) {
                            System.out.println("Database connection failed: " + e.getMessage());
                        }
                    }
                    case 2 -> {
                        if (conn == null) {
                            System.out.println("Please connect to database first!");
                            continue;
                        }
                        studentMenuHandler.handleStudentManagement();
                    }
                    case 3 -> {
                        if (conn == null) {
                            System.out.println("Please connect to database first!");
                            continue;
                        }
                        clubMenuHandler.handleClubManagement();
                    }
                    case 4 -> {
                        if (conn == null) {
                            System.out.println("Please connect to database first!");
                            continue;
                        }
                        professorMenuHandler.handleProfessorManagement();
                    }
                    case 5 -> {
                        if (conn == null) {
                            System.out.println("Please connect to database first!");
                            continue;
                        }
                        budgetMenuHandler.handleBudgetManagement();
                    }
                    case 6 -> {
                        if (conn == null) {
                            System.out.println("Please connect to database first!");
                            continue;
                        }
                        projectMenuHandler.handleProjectManagement();
                    }
                    case 99 -> {
                        if (conn != null) {
                            try {
                                conn.close();
                            } catch (Exception e) {
                                System.out.println("Error closing database connection: " + e.getMessage());
                            }
                        }
                        scanner.close();
                        System.out.println("Bye...\n");
                        return;
                    }
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
            System.out.println("\n");
        }
    }

    private static void displayMainMenu() {
        System.out.println("------------------------------------------------------------");
        System.out.println("1. Database Connection\t2. Manage Students");
        System.out.println("3. Manage Clubs\t4. Manage Professors");
        System.out.println("5. Manage Budgets\t6. Manage Projects");
        System.out.println("99. Quit");
        System.out.println("------------------------------------------------------------");
    }
}