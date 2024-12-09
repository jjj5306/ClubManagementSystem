package clubManagement.service;

import clubManagement.domain.Club;
import clubManagement.domain.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class ClubService {
    private final Connection conn;
    private final StudentService studentService;

    public ClubService(Connection conn) {
        this.conn = conn;
        this.studentService = new StudentService(conn);
    }

    // 지도교수 존재 여부 확인
    private boolean professorExists(String profId) throws SQLException {
        String sql = String.format("SELECT 1 FROM professors WHERE prof_id = '%s'", profId);
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next();
        }
    }

    // 동아리 등록
    public boolean registerClub(Club club, List<String> initialMembers) throws SQLException {
        // 1. 회장 검사
        Student president = studentService.getStudentById(club.getPresidentId());
        if (president == null) {
            throw new SQLException("존재하지 않는 회장 학번입니다.");
        }
        if (president.getClubId() != null) {
            throw new SQLException("이미 다른 동아리에 소속된 학생입니다.");
        }

        // 2. 지도교수 검사
        if (!professorExists(club.getProfId())) {
            throw new SQLException("존재하지 않는 지도교수 ID입니다.");
        }

        // 3. 초기 회원 수 검사
        if (initialMembers.size() + 1 < 10) {
            throw new SQLException("최소 10명의 부원이 필요합니다. (현재: " + (initialMembers.size() + 1) + "명)");
        }

        // 4. 초기 회원 검사
        if (!initialMembers.isEmpty()) {
            String memberIds = String.join("','", initialMembers);
            String checkMembersSql = String.format(
                    "SELECT student_id, club_id FROM students WHERE student_id IN ('%s')",
                    memberIds
            );

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(checkMembersSql)) {

                Set<String> foundMembers = new HashSet<>();
                while (rs.next()) {
                    String studentId = rs.getString("student_id");
                    String clubId = rs.getString("club_id");
                    foundMembers.add(studentId);

                    if (clubId != null) {
                        throw new SQLException("이미 다른 동아리에 소속된 학생이 있습니다: " + studentId);
                    }
                }

                // 존재하지 않는 회원인지
                for (String memberId : initialMembers) {
                    if (!foundMembers.contains(memberId)) {
                        throw new SQLException("존재하지 않는 학생이 있습니다: " + memberId);
                    }
                }
            }
        }

        // 5. 동아리 등록
        String sql = String.format(
                "INSERT INTO clubs (club_id, president_id, prof_id, status_name, club_name, " +
                        "activity_field, member_count, page_url, club_info) " +
                        "VALUES ('%s', '%s', '%s', '%s', '%s', '%s', %d, %s, %s)",
                club.getClubId(),
                club.getPresidentId(),
                club.getProfId(),
                club.getStatusName(),
                club.getClubName(),
                club.getActivityField(),
                initialMembers.size() + 1,
                club.getPageUrl() != null ? "'" + club.getPageUrl() + "'" : "NULL",
                club.getClubInfo() != null ? "'" + club.getClubInfo() + "'" : "NULL"
        );

        try (Statement stmt = conn.createStatement()) {
            int result = stmt.executeUpdate(sql);

            if (result > 0) {
                // 6. 회장 정보 업데이트
                String updatePresidentSql = String.format(
                        "UPDATE students SET role = 'president', club_id = '%s', join_date = CURDATE() " +
                                "WHERE student_id = '%s'",
                        club.getClubId(), club.getPresidentId()
                );
                stmt.executeUpdate(updatePresidentSql);

                // 7. 초기 회원 정보 일괄 업데이트
                if (!initialMembers.isEmpty()) {
                    String memberIds = String.join("','", initialMembers);
                    String updateMembersSql = String.format(
                            "UPDATE students SET role = 'member', club_id = '%s', join_date = CURDATE() " +
                                    "WHERE student_id IN ('%s')",
                            club.getClubId(), memberIds
                    );
                    stmt.executeUpdate(updateMembersSql);
                }
            }

            return result > 0;
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                if (e.getMessage().contains("club_id")) {
                    throw new SQLException("이미 등록된 동아리 ID입니다.");
                } else if (e.getMessage().contains("club_name")) {
                    throw new SQLException("이미 등록된 동아리 이름입니다.");
                } else if (e.getMessage().contains("page_url")) {
                    throw new SQLException("이미 등록된 페이지 URL입니다.");
                }
            } else if (e.getMessage().contains("foreign key constraint")) {
                throw new SQLException("지도교수 또는 학생 정보가 유효하지 않습니다.");
            }
            throw e;
        }
    }

    // 모든 동아리 조회
    public List<Club> getAllClubs() throws SQLException {
        List<Club> clubs = new ArrayList<>();
        String sql = "SELECT * FROM clubs ORDER BY club_id";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                clubs.add(createClubFromResultSet(rs));
            }
        }

        return clubs;
    }

    // 특정 동아리 조회
    public Club getClubById(String clubId) throws SQLException {
        String sql = String.format(
                "SELECT * FROM clubs WHERE club_id = '%s'",
                clubId
        );

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return createClubFromResultSet(rs);
            }
        }
        return null;
    }

    // ResultSet에서 Club 객체 생성
    private Club createClubFromResultSet(ResultSet rs) throws SQLException {
        return new Club(
                rs.getString("club_id"),
                rs.getString("president_id"),
                rs.getString("prof_id"),
                rs.getString("club_name"),
                rs.getString("activity_field"),
                rs.getString("page_url"),
                rs.getString("club_info")
        );
    }
}