package clubManagement.service.admin;


import clubManagement.domain.Student;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class StudentService {
    private final Connection conn;

    public StudentService(Connection conn) {
        this.conn = conn;
    }

    // 학생 등록
    public boolean registerStudent(Student student) throws SQLException {
        String sql = String.format(
                "INSERT INTO students (student_id, contact, name, department) " +
                        "VALUES ('%s', '%s', '%s', '%s')",
                student.getStudentId(),
                student.getContact(),
                student.getName(),
                student.getDepartment()
        );

        try {
            Statement stmt = conn.createStatement();
            int result = stmt.executeUpdate(sql);
            return result > 0;
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                if (e.getMessage().contains("student_id")) {
                    throw new SQLException("이미 등록된 학번입니다.");
                } else if (e.getMessage().contains("contact")) {
                    throw new SQLException("이미 등록된 연락처입니다.");
                }
            }
            throw e;
        }
    }

    // 모든 학생 조회
    public List<Student> getAllStudents() throws SQLException {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY student_id";

        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            students.add(createStudentFromResultSet(rs));
        }

        return students;
    }

    // 특정 학생 조회
    public Student getStudentById(String studentId) throws SQLException {
        String sql = String.format(
                "SELECT * FROM students WHERE student_id = '%s'",
                studentId
        );

        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        if (rs.next()) {
            return createStudentFromResultSet(rs);
        }
        return null;
    }

    public boolean deleteStudent(String studentId) throws SQLException {
        // 학생 검사
        Student student = getStudentById(studentId);
        if (student == null) {
            throw new SQLException("해당 학번의 학생을 찾을 수 없습니다.");
        }

        String sql = String.format(
                "DELETE FROM students WHERE student_id = '%s'",
                studentId
        );

        try {
            Statement stmt = conn.createStatement();
            int result = stmt.executeUpdate(sql);
            return result > 0;
        } catch (SQLException e) {
            if (e.getMessage().contains("foreign key constraint")) {
                throw new SQLException("해당 학생은 동아리 회장이거나 활동/프로젝트에 참여 중이어서 삭제할 수 없습니다.");
            }
            throw e;
        }
    }

    // Student 객체 생성
    private Student createStudentFromResultSet(ResultSet rs) throws SQLException {
        Date joinDate = rs.getDate("join_date");
        return new Student(
                rs.getString("student_id"),
                rs.getString("contact"),
                rs.getString("name"),
                rs.getString("department"),
                Student.Role.fromString(rs.getString("role")),
                joinDate == null ? null : joinDate.toLocalDate(),
                rs.getString("club_id")
        );
    }
    
    //학생 수정
    public boolean updateStudent(Student student) throws SQLException {
        // 학생 검사
        Student existingStudent = getStudentById(student.getStudentId());
        if (existingStudent == null) {
            throw new SQLException("존재하지 않는 학생입니다.");
        }

        String sql = String.format(
                "UPDATE students SET contact = '%s', name = '%s', department = '%s' " +
                        "WHERE student_id = '%s'",
                student.getContact(),
                student.getName(),
                student.getDepartment(),
                student.getStudentId()
        );

        try {
            Statement stmt = conn.createStatement();
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

    //학생 소속 동아리 등록
    public boolean registerToClub(String studentId, String clubId) throws SQLException {
        // 1. 학생 검사
        Student student = getStudentById(studentId);
        if (student == null) {
            throw new SQLException("존재하지 않는 학생입니다.");
        }
        if (student.getClubId() != null) {
            throw new SQLException("이미 다른 동아리에 가입된 학생입니다.");
        }

        // 2. 동아리 검사
        String checkClubSql = String.format(
                "SELECT status_name FROM clubs WHERE club_id = '%s'",
                clubId
        );

        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(checkClubSql);

        if (!rs.next()) {
            throw new SQLException("존재하지 않는 동아리입니다.");
        }
        if (!rs.getString("status_name").equals("active")) {
            throw new SQLException("현재 활동 중인 동아리가 아닙니다.");
        }

        // 3. 학생 정보 업데이트 (동아리 ID와 가입일 설정)
        String updateSql = String.format(
                "UPDATE students SET club_id = '%s', join_date = CURDATE(), role = 'member' " +
                        "WHERE student_id = '%s'",
                clubId, studentId
        );

        try {
            int result = stmt.executeUpdate(updateSql);

            // 4. 동아리 회원 수 증가
            if (result > 0) {
                String updateMemberCountSql = String.format(
                        "UPDATE clubs SET member_count = member_count + 1 " +
                                "WHERE club_id = '%s'",
                        clubId
                );
                stmt.executeUpdate(updateMemberCountSql);
            }

            return result > 0;
        } catch (SQLException e) {
            throw new SQLException("동아리 등록 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

}