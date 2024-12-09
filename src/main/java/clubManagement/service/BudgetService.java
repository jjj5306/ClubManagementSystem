package clubManagement.service;

import clubManagement.domain.Budget;
import clubManagement.domain.Club;
import clubManagement.domain.ClubStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BudgetService {
    private final Connection conn;
    private final ClubService clubService;

    public BudgetService(Connection conn) {
        this.conn = conn;
        this.clubService = new ClubService(conn);
    }

    // 예산 등록
    public boolean registerBudget(Budget budget) throws SQLException {
        // 동아리 검사
        Club club = clubService.getClubById(budget.getClubId());
        if (club == null) {
            throw new SQLException("존재하지 않는 동아리입니다.");
        }

        // CLOSED 상태의 동아리는 예산을 등록할 수 없음.
        if (club.getStatusName().equals(ClubStatus.CLOSED.getStatusName())) {
            throw new SQLException("삭제(CLOSED) 상태의 동아리는 예산을 등록할 수 없습니다.");
        }

        String sql = String.format(
                "INSERT INTO budgets (receipt_no, club_id, use_date, use_field, amount, semester) " +
                        "VALUES ('%s', '%s', '%s', '%s', %.2f, '%s')",
                budget.getReceiptNo(),
                budget.getClubId(),
                budget.getUseDate(),
                budget.getUseField(),
                budget.getAmount(),
                budget.getSemester()
        );

        try (Statement stmt = conn.createStatement()) {
            int result = stmt.executeUpdate(sql);
            return result > 0;
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("receipt_no")) {
                throw new SQLException("이미 등록된 영수증 번호입니다.");
            }
            throw e;
        }
    }

    // 예산 수정
    public boolean updateBudget(Budget budget) throws SQLException {
        // 예산 검사
        Budget existingBudget = getBudgetByReceiptNo(budget.getReceiptNo());
        if (existingBudget == null) {
            throw new SQLException("존재하지 않는 예산입니다.");
        }

        String sql = String.format(
                "UPDATE budgets SET use_field = '%s', amount = %.2f " +
                        "WHERE receipt_no = '%s'",
                budget.getUseField(),
                budget.getAmount(),
                budget.getReceiptNo()
        );

        try (Statement stmt = conn.createStatement()) {
            int result = stmt.executeUpdate(sql);
            return result > 0;
        }
    }

    // 예산 삭제
    public boolean deleteBudget(String receiptNo) throws SQLException {
        // 예산 검사
        Budget budget = getBudgetByReceiptNo(receiptNo);
        if (budget == null) {
            throw new SQLException("존재하지 않는 예산입니다.");
        }

        String sql = String.format(
                "DELETE FROM budgets WHERE receipt_no = '%s'",
                receiptNo
        );

        try (Statement stmt = conn.createStatement()) {
            int result = stmt.executeUpdate(sql);
            return result > 0;
        }
    }

    // 영수증 번호로 예산 조회
    public Budget getBudgetByReceiptNo(String receiptNo) throws SQLException {
        String sql = String.format(
                "SELECT * FROM budgets WHERE receipt_no = '%s'",
                receiptNo
        );

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return createBudgetFromResultSet(rs);
            }
        }
        return null;
    }

    // 동아리의 특정 학기 예산 목록 조회
    public List<Budget> getClubBudgetsBySemester(String clubId, String semester) throws SQLException {
        List<Budget> budgets = new ArrayList<>();
        String sql = String.format(
                "SELECT * FROM budgets WHERE club_id = '%s' AND semester = '%s' ORDER BY use_date",
                clubId, semester
        );

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                budgets.add(createBudgetFromResultSet(rs));
            }
        }

        return budgets;
    }

    // ResultSet에서 Budget 객체 생성
    private Budget createBudgetFromResultSet(ResultSet rs) throws SQLException {
        return new Budget(
                rs.getString("receipt_no"),
                rs.getString("club_id"),
                rs.getDate("use_date").toLocalDate(),
                rs.getString("use_field"),
                rs.getDouble("amount"),
                rs.getString("semester")
        );
    }

    public List<Budget> getAllClubBudgets(String clubId) throws SQLException {
        List<Budget> budgets = new ArrayList<>();
        String sql = String.format(
                "SELECT * FROM budgets WHERE club_id = '%s' ORDER BY semester DESC, use_date",
                clubId
        );

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                budgets.add(createBudgetFromResultSet(rs));
            }
        }
        return budgets;
    }
}