package clubManagement;

import clubManagement.auth.LoginManager;
import clubManagement.auth.LoginManager.LoginResult;
import clubManagement.auth.UserRole;
import clubManagement.utils.DatabaseConnection;

import java.sql.Connection;
import java.util.Scanner;

public class Main {
    private static LoginResult currentUser = null;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Connection conn = null;
        LoginManager loginManager = null;

        System.out.println("2020039071 조준화 Database System Term Project - Club Management System\n");

        while (true) {
            if (currentUser == null) {
                System.out.println("------------------------------------------------------------");
                System.out.println("1. Database Connection\t\t2. Login\t\t3. Quit");
                System.out.println("------------------------------------------------------------");
                System.out.print("Select menu: ");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1 -> {
                        //main menu, database connection
                        try {
                            conn = DatabaseConnection.getConnection();
                            loginManager = new LoginManager(conn);
                            System.out.println("Database connection successful!");
                        } catch (Exception e) {
                            System.out.println("Database connection failed: " + e.getMessage());
                        }
                    }
                    case 2 -> {
                        //main menu, login
                        if (conn == null) {
                            System.out.println("Please connect to database first!");
                            continue;
                        }
                        System.out.print("Enter your ID: ");
                        String studentId = scanner.next();

                        try {
                            currentUser = loginManager.validateUser(studentId);

                            if (currentUser.getRole() == UserRole.INVALID) {
                                System.out.println("잘못된 ID입니다.");
                                currentUser = null;
                            } else {
                                System.out.println("Login successful! Welcome " +
                                        (currentUser.getRole() == UserRole.ADMIN ? "Admin" : "Club President"));
                            }
                        } catch (Exception e) {
                            System.out.println("Login failed: " + e.getMessage());
                        }
                    }
                    case 3 -> {
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
            } else {
                //로그인 이후 화면
                displayMainMenu(currentUser.getRole());
                System.out.print("Select menu: ");
                int choice = scanner.nextInt();

                try {
                    switch (choice) {
                        case 1 -> {
                            // 조회
                            System.out.println("Find operation executed");
                        }
                        case 2 -> {
                            // 삽입
                            System.out.println("Insert operation executed");
                        }
                        case 98 -> {
                            // 로그아웃
                            currentUser = null;
                            System.out.println("Logged out successfully");
                        }
                        case 99 -> {
                            if (conn != null) conn.close();
                            scanner.close();
                            System.out.println("Bye...\n");
                            return;
                        }
                        default -> System.out.println("Feature in preparation.");
                    }
                } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            System.out.println("\n");
        }
    }

    private static void displayMainMenu(UserRole role) {
        System.out.println("------------------------------------------------------------");
        if (role == UserRole.ADMIN) {
            System.out.println("1. View All Clubs\t2. Register New Club");
            System.out.println("3. View All Activities\t4. View All Projects");
            System.out.println("5. View Budget Reports\t6. Manage Club Status");
        } else {  // PRESIDENT
            System.out.println("1. View Club Info\t2. Update Club Info");
            System.out.println("3. Manage Activities\t4. Manage Projects");
            System.out.println("5. Submit Budget Report\t6. View Members");
        }
        System.out.println("98. Logout\t\t99. Quit");
        System.out.println("------------------------------------------------------------");
    }
}