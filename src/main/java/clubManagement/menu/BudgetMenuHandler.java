package clubManagement.menu;

import clubManagement.domain.Budget;
import clubManagement.service.BudgetService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class BudgetMenuHandler {
    private final Scanner scanner;
    private final BudgetService budgetService;

    public BudgetMenuHandler(Connection conn, Scanner scanner) {
        this.scanner = scanner;
        this.budgetService = new BudgetService(conn);
    }

    public void handleBudgetManagement() {
        while (true) {
            displayBudgetMenu();
            System.out.print("메뉴를 선택하세요: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // 버퍼 비우기

            try {
                switch (choice) {
                    case 1 -> handleRegisterBudget();
                    case 2 -> handleUpdateBudget();
                    case 3 -> handleDeleteBudget();
                    case 4 -> handleGetBudgetsBySemester();
                    case 5 -> handleGetAllBudgets();
                    case 6 -> {
                        return;
                    }
                    default -> System.out.println("잘못된 선택입니다. 다시 시도해주세요.");
                }
            } catch (Exception e) {
                System.out.println("오류 발생: " + e.getMessage());
            }
            System.out.println();
        }
    }

    private void displayBudgetMenu() {
        System.out.println("\n========= 예산 관리 메뉴 =========");
        System.out.println("1. 예산 등록");
        System.out.println("2. 예산 수정");
        System.out.println("3. 예산 삭제");
        System.out.println("4. 특정 동아리의 학기별 예산 조회");
        System.out.println("5. 특정 동아리의 전체 예산 조회");
        System.out.println("6. 뒤로 가기");
        System.out.println("==============================");
    }

    private void handleRegisterBudget() {
        try {
            System.out.print("동아리 ID: ");
            String clubId = scanner.nextLine();

            System.out.print("영수증 번호: ");
            String receiptNo = scanner.nextLine();

            System.out.print("사용 분야: ");
            String useField = scanner.nextLine();

            System.out.print("금액: ");
            double amount = scanner.nextDouble();
            scanner.nextLine(); // 버퍼 비우기

            Budget budget = new Budget(receiptNo, clubId, useField, amount);
            boolean success = budgetService.registerBudget(budget);

            if (success) {
                System.out.println("예산이 성공적으로 등록되었습니다.");
            } else {
                System.out.println("예산 등록에 실패했습니다.");
            }
        } catch (SQLException e) {
            System.out.println("예산 등록 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private void handleUpdateBudget() {
        try {
            System.out.print("영수증 번호: ");
            String receiptNo = scanner.nextLine();

            System.out.print("새로운 사용 분야: ");
            String useField = scanner.nextLine();

            System.out.print("새로운 금액: ");
            double amount = scanner.nextDouble();
            scanner.nextLine(); // 버퍼 비우기

            Budget existingBudget = budgetService.getBudgetByReceiptNo(receiptNo);
            if (existingBudget == null) {
                System.out.println("존재하지 않는 영수증 번호입니다.");
                return;
            }

            existingBudget.setUseField(useField);
            existingBudget.setAmount(amount);

            boolean success = budgetService.updateBudget(existingBudget);
            if (success) {
                System.out.println("예산이 성공적으로 수정되었습니다.");
            } else {
                System.out.println("예산 수정에 실패했습니다.");
            }
        } catch (SQLException e) {
            System.out.println("예산 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private void handleDeleteBudget() {
        try {
            System.out.print("삭제할 영수증 번호: ");
            String receiptNo = scanner.nextLine();

            boolean success = budgetService.deleteBudget(receiptNo);
            if (success) {
                System.out.println("예산이 성공적으로 삭제되었습니다.");
            } else {
                System.out.println("예산 삭제에 실패했습니다.");
            }
        } catch (SQLException e) {
            System.out.println("예산 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private void handleGetBudgetsBySemester() {
        try {
            System.out.print("동아리 ID: ");
            String clubId = scanner.nextLine();

            System.out.print("학기 (예: 2024-1): ");
            String semester = scanner.nextLine();

            List<Budget> budgets = budgetService.getClubBudgetsBySemester(clubId, semester);
            if (budgets.isEmpty()) {
                System.out.println("해당 학기의 예산 내역이 없습니다.");
                return;
            }

            System.out.printf("\n=== %s 학기 예산 내역 ===\n", semester);
            double total = 0;

            for (Budget budget : budgets) {
                System.out.printf("영수증 번호: %s, 사용 분야: %s, 금액: %.2f원, 사용일: %s\n",
                        budget.getReceiptNo(),
                        budget.getUseField(),
                        budget.getAmount(),
                        budget.getUseDate()
                );
                total += budget.getAmount();
            }

            System.out.printf("\n총 지출액: %.2f원\n", total);
        } catch (SQLException e) {
            System.out.println("예산 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private void handleGetAllBudgets() {
        try {
            System.out.print("동아리 ID: ");
            String clubId = scanner.nextLine();

            List<Budget> budgets = budgetService.getAllClubBudgets(clubId);
            if (budgets.isEmpty()) {
                System.out.println("예산 내역이 없습니다.");
                return;
            }

            System.out.println("\n=== 전체 예산 내역 ===");
            String currentSemester = "";
            double semesterTotal = 0;
            double grandTotal = 0;

            for (Budget budget : budgets) {
                if (!budget.getSemester().equals(currentSemester)) {
                    if (!currentSemester.isEmpty()) {
                        System.out.printf("\n%s 학기 총액: %.2f원\n", currentSemester, semesterTotal);
                    }
                    currentSemester = budget.getSemester();
                    semesterTotal = 0;
                    System.out.println("\n" + currentSemester + " 학기:");
                }

                System.out.printf("영수증 번호: %s, 사용 분야: %s, 금액: %.2f원, 사용일: %s\n",
                        budget.getReceiptNo(),
                        budget.getUseField(),
                        budget.getAmount(),
                        budget.getUseDate()
                );

                semesterTotal += budget.getAmount();
                grandTotal += budget.getAmount();
            }

            if (!currentSemester.isEmpty()) {
                System.out.printf("\n%s 학기 총액: %.2f원\n", currentSemester, semesterTotal);
            }
            System.out.printf("\n총 지출액: %.2f원\n", grandTotal);
        } catch (SQLException e) {
            System.out.println("예산 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}