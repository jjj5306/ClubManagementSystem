package clubManagement.service;

import clubManagement.domain.Professor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProfessorService {
    private final Connection conn;

    public ProfessorService(Connection conn) {
        this.conn = conn;
    }

    // 지도교수 등록
    public boolean registerProfessor(Professor professor) throws SQLException {
        String sql = String.format(
                "INSERT INTO professors (prof_id, name, department, contact) " +
                        "VALUES ('%s', '%s', '%s', '%s')",
                professor.getProfId(),
                professor.getName(),
                professor.getDepartment(),
                professor.getContact()
        );

        try (Statement stmt = conn.createStatement()) {
            int result = stmt.executeUpdate(sql);
            return result > 0;
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                if (e.getMessage().contains("prof_id")) {
                    throw new SQLException("이미 등록된 교번입니다.");
                } else if (e.getMessage().contains("contact")) {
                    throw new SQLException("이미 등록된 연락처입니다.");
                }
            }
            throw e;
        }
    }

    // 지도교수 정보 수정
    public boolean updateProfessor(Professor professor) throws SQLException {
        // 지도교수 검사
        Professor existingProfessor = getProfessorById(professor.getProfId());
        if (existingProfessor == null) {
            throw new SQLException("존재하지 않는 교수입니다.");
        }

        String sql = String.format(
                "UPDATE professors SET name = '%s', department = '%s', contact = '%s' " +
                        "WHERE prof_id = '%s'",
                professor.getName(),
                professor.getDepartment(),
                professor.getContact(),
                professor.getProfId()
        );

        try (Statement stmt = conn.createStatement()) {
            int result = stmt.executeUpdate(sql);
            return result > 0;
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                if (e.getMessage().contains("contact")) {
                    throw new SQLException("이미 등록된 연락처입니다.");
                }
            }
            throw e;
        }
    }

    // 지도교수 삭제
    public boolean deleteProfessor(String profId) throws SQLException {
        // 지도교수 검사
        Professor professor = getProfessorById(profId);
        if (professor == null) {
            throw new SQLException("해당 교번의 교수를 찾을 수 없습니다.");
        }

        // 동아리 지도 여부 확인
        String checkClubSql = String.format(
                "SELECT 1 FROM clubs WHERE prof_id = '%s' LIMIT 1",
                profId
        );

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkClubSql)) {
            if (rs.next()) {
                throw new SQLException("현재 동아리 지도를 맡고 있는 교수는 삭제할 수 없습니다.");
            }
        }

        String sql = String.format(
                "DELETE FROM professors WHERE prof_id = '%s'",
                profId
        );

        try (Statement stmt = conn.createStatement()) {
            int result = stmt.executeUpdate(sql);
            return result > 0;
        }
    }

    // 특정 지도교수 조회
    public Professor getProfessorById(String profId) throws SQLException {
        String sql = String.format(
                "SELECT * FROM professors WHERE prof_id = '%s'",
                profId
        );

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return new Professor(
                        rs.getString("prof_id"),
                        rs.getString("name"),
                        rs.getString("department"),
                        rs.getString("contact")
                );
            }
            return null;
        }
    }

    // 모든 지도교수 조회
    public List<Professor> getAllProfessors() throws SQLException {
        List<Professor> professors = new ArrayList<>();
        String sql = "SELECT * FROM professors ORDER BY prof_id";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                professors.add(new Professor(
                        rs.getString("prof_id"),
                        rs.getString("name"),
                        rs.getString("department"),
                        rs.getString("contact")
                ));
            }
            return professors;
        }
    }
}
