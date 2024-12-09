package clubManagement.service;

import clubManagement.domain.Club;
import clubManagement.domain.ClubStatus;
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
                ClubStatus.fromString(rs.getString("status_name")),
                rs.getString("club_name"),
                rs.getString("activity_field"),
                rs.getInt("member_count"),
                rs.getString("page_url"),
                rs.getString("club_info")
        );
    }

    // 동아리 URL과 소개 정보 업데이트
    public boolean updateClubInfo(String clubId, String pageUrl, String clubInfo) throws SQLException {
        Club club = getClubById(clubId);
        if (club == null) {
            throw new SQLException("존재하지 않는 동아리입니다.");
        }

        StringBuilder sql = new StringBuilder("UPDATE clubs SET ");
        List<String> updates = new ArrayList<>();

        if (pageUrl != null) {
            updates.add("page_url = " + (pageUrl.isEmpty() ? "NULL" : "'" + pageUrl + "'"));
        }
        if (clubInfo != null) {
            updates.add("club_info = " + (clubInfo.isEmpty() ? "NULL" : "'" + clubInfo + "'"));
        }

        if (updates.isEmpty()) {
            return false;
        }

        sql.append(String.join(", ", updates));
        sql.append(" WHERE club_id = '").append(clubId).append("'");

        try (Statement stmt = conn.createStatement()) {
            int result = stmt.executeUpdate(sql.toString());
            return result > 0;
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("page_url")) {
                throw new SQLException("이미 등록된 페이지 URL입니다.");
            }
            throw e;
        }
    }

    // 동아리 기본 정보 수정
    public boolean updateClubBasicInfo(String clubId, String clubName, String activityField) throws SQLException {
        Club club = getClubById(clubId);
        if (club == null) {
            throw new SQLException("존재하지 않는 동아리입니다.");
        }

        String sql = String.format(
                "UPDATE clubs SET club_name = '%s', activity_field = '%s' WHERE club_id = '%s'",
                clubName, activityField, clubId
        );

        try (Statement stmt = conn.createStatement()) {
            int result = stmt.executeUpdate(sql.toString());
            return result > 0;
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("club_name")) {
                throw new SQLException("이미 존재하는 동아리 이름입니다.");
            }
            throw e;
        }
    }

    // 동아리 상태 변경
    public boolean updateClubStatus(String clubId, ClubStatus newStatus) throws SQLException {
        Club club = getClubById(clubId);
        if (club == null) {
            throw new SQLException("존재하지 않는 동아리입니다.");
        }

        if (club.getStatusName().equals(ClubStatus.CLOSED.getStatusName())) {
            throw new SQLException("삭제(CLOSED) 상태의 동아리는 상태를 변경할 수 없습니다.");
        }

        String sql = String.format(
                "UPDATE clubs SET status_name = '%s' WHERE club_id = '%s'",
                newStatus.getStatusName(), clubId
        );

        try (Statement stmt = conn.createStatement()) {
            int result = stmt.executeUpdate(sql.toString());
            return result > 0;
        }
    }

    // 동아리 삭제 (closed로 변경)
    public boolean deleteClub(String clubId) throws SQLException {
        // 1. 동아리 존재 여부 확인
        Club club = getClubById(clubId);
        if (club == null) {
            throw new SQLException("존재하지 않는 동아리입니다.");
        }

        // 2. 동아리 상태를 CLOSED로 변경
        String updateStatusSql = String.format(
                "UPDATE clubs SET status_name = '%s' WHERE club_id = '%s'",
                ClubStatus.CLOSED.getStatusName(), clubId
        );

        try (Statement stmt = conn.createStatement()) {
            int statusResult = stmt.executeUpdate(updateStatusSql);
            if (statusResult <= 0) {
                return false;
            }

            // 3. 소속 학생들의 정보 초기화 (회장 포함)
            String updateMembersSql = String.format(
                    "UPDATE students SET club_id = NULL, role = NULL, join_date = NULL " +
                            "WHERE club_id = '%s'", clubId
            );
            stmt.executeUpdate(updateMembersSql);

            // 4. 동아리의 회원 수를 0으로 변경
            String updateClubSql = String.format(
                    "UPDATE clubs SET member_count = 0 WHERE club_id = '%s'", clubId
            );
            stmt.executeUpdate(updateClubSql);

            return true;
        }
    }

    // 특정 상태의 동아리 목록 조회
    public List<Club> getClubsByStatus(ClubStatus status) throws SQLException {
        List<Club> clubs = new ArrayList<>();
        String sql = String.format(
                "SELECT * FROM clubs WHERE status_name = '%s' ORDER BY club_id",
                status.getStatusName()
        );

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                clubs.add(createClubFromResultSet(rs));
            }
        }

        return clubs;
    }

    // 동아리 회원 목록 조회
    public List<Student> getClubMembers(String clubId) throws SQLException {
        // 동아리 존재 여부 확인
        Club club = getClubById(clubId);
        if (club == null) {
            throw new SQLException("존재하지 않는 동아리입니다.");
        }

        List<Student> members = new ArrayList<>();
        String sql = String.format(
                "SELECT * FROM students WHERE club_id = '%s' ORDER BY role DESC, student_id",
                clubId
        );

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                members.add(new Student(
                        rs.getString("student_id"),
                        rs.getString("contact"),
                        rs.getString("name"),
                        rs.getString("department"),
                        Student.Role.fromString(rs.getString("role")),
                        rs.getDate("join_date") != null ? rs.getDate("join_date").toLocalDate() : null,
                        rs.getString("club_id")
                ));
            }
        }
        return members;
    }
}