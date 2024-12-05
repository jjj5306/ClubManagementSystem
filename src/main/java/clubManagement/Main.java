package clubManagement;

import clubManagement.utils.DatabaseConnection;

import java.sql.Connection;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Connection conn = null;

        System.out.println("2020039071 조준화 Database System Term Project - Club Management System\n");


        while (true) {
            System.out.println("------------------------------------------------------------");
            System.out.println("1. connection\t\t2. find ****");
            System.out.println("3. insert ****\t\t4. *******");
            System.out.println("5. *******\t\t6. *******");
            System.out.println("7. *******\t\t8. *******");
            System.out.println("99. quit");
            System.out.println("------------------------------------------------------------");
            System.out.print("select menu!: ");
            int choice = scanner.nextInt();

            try {
                switch (choice) {
                    case 1:
                        // DB 연결
                        conn = DatabaseConnection.getConnection();
                        System.out.println("Database connection successful!");
                        break;

                    case 2:
                        // find 작업
                        if (conn == null) {
                            System.out.println("Database has not yet been linked");
                        } else {
                            System.out.println("find 작업 실행");
                        }
                        break;

                    case 3:
                        // insert 작업
                        if (conn == null) {
                            System.out.println("Database has not yet been linked");
                        } else {
                            System.out.println("insert 작업 실행");
                        }
                        break;

                    case 99:
                        System.out.println("Bye...\n");
                        if (conn != null) {
                            conn.close();
                        }
                        scanner.close();
                        return;

                    default:
                        System.out.println("준비 중인 기능입니다.");
                        break;
                }

            } catch (Exception e) {
                System.err.println("error: " + e.getMessage() + "choice: " + choice);
                e.printStackTrace();
            }

            System.out.println("\n");
        }
    }
}