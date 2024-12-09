package clubManagement.service;

import clubManagement.domain.Project;
import clubManagement.domain.ProjectParticipant;
import clubManagement.domain.Club;
import clubManagement.domain.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectService {
    private final Connection conn;
    private final ClubService clubService;
    private final StudentService studentService;

    public ProjectService(Connection conn) {
        this.conn = conn;
        this.clubService = new ClubService(conn);
        this.studentService = new StudentService(conn);
    }

    // 프로젝트 등록
    public boolean registerProject(Project project) throws SQLException {
        // 동아리 검사
        Club club = clubService.getClubById(project.getClubId());
        if (club == null) {
            throw new SQLException("존재하지 않는 동아리입니다.");
        }

        String sql = String.format(
                "INSERT INTO projects (club_id, project_name, member_count, project_purpose, " +
                        "project_topic, management_tool, semester) " +
                        "VALUES ('%s', '%s', %d, '%s', '%s', '%s', '%s')",
                project.getClubId(),
                project.getProjectName(),
                project.getMemberCount(),
                project.getProjectPurpose(),
                project.getProjectTopic(),
                project.getManagementTool(),
                project.getSemester()
        );

        try (Statement stmt = conn.createStatement()) {
            int result = stmt.executeUpdate(sql);
            return result > 0;
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                throw new SQLException("이미 존재하는 프로젝트 이름입니다.");
            }
            throw e;
        }
    }

    // 프로젝트 수정
    public boolean updateProject(Project project) throws SQLException {
        // 프로젝트 검사
        Project existingProject = getProject(project.getClubId(), project.getProjectName());
        if (existingProject == null) {
            throw new SQLException("존재하지 않는 프로젝트입니다.");
        }

        String sql = String.format(
                "UPDATE projects SET project_purpose = '%s', project_topic = '%s', " +
                        "management_tool = '%s' WHERE club_id = '%s' AND project_name = '%s'",
                project.getProjectPurpose(),
                project.getProjectTopic(),
                project.getManagementTool(),
                project.getClubId(),
                project.getProjectName()
        );

        try (Statement stmt = conn.createStatement()) {
            int result = stmt.executeUpdate(sql);
            return result > 0;
        }
    }

    // 프로젝트 삭제
    public boolean deleteProject(String clubId, String projectName) throws SQLException {
        // 프로젝트 검사
        Project project = getProject(clubId, projectName);
        if (project == null) {
            throw new SQLException("존재하지 않는 프로젝트입니다.");
        }

        // 프로젝트 삭제
        String sql = String.format(
                "DELETE FROM projects WHERE club_id = '%s' AND project_name = '%s'",
                clubId, projectName
        );

        try (Statement stmt = conn.createStatement()) {
            int result = stmt.executeUpdate(sql);
            return result > 0;
        }
    }

    // 프로젝트 조회
    public Project getProject(String clubId, String projectName) throws SQLException {
        String sql = String.format(
                "SELECT * FROM projects WHERE club_id = '%s' AND project_name = '%s'",
                clubId, projectName
        );

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return new Project(
                        rs.getString("club_id"),
                        rs.getString("project_name"),
                        rs.getInt("member_count"),
                        rs.getString("project_purpose"),
                        rs.getString("project_topic"),
                        rs.getString("management_tool"),
                        rs.getString("semester")
                );
            }
        }
        return null;
    }

    // 프로젝트 참여자 추가
    public boolean addProjectParticipant(ProjectParticipant participant) throws SQLException {
        // 프로젝트 검사
        Project project = getProject(participant.getClubId(), participant.getProjectName());
        if (project == null) {
            throw new SQLException("존재하지 않는 프로젝트입니다.");
        }

        // 학생 검사
        Student student = studentService.getStudentById(participant.getStudentId());
        if (student == null) {
            throw new SQLException("존재하지 않는 학생입니다.");
        }

        if (student.getClubId() == null || !student.getClubId().equals(participant.getClubId())) {
            throw new SQLException("해당 동아리에 소속된 학생만 프로젝트에 참여할 수 있습니다.");
        }

        if (isStudentInProject(participant.getStudentId(), participant.getClubId(), participant.getProjectName())) {
            throw new SQLException("이미 프로젝트에 참여 중인 학생입니다.");
        }

        String sql = String.format(
                "INSERT INTO project_participants (student_id, club_id, project_name) " +
                        "VALUES ('%s', '%s', '%s')",
                participant.getStudentId(),
                participant.getClubId(),
                participant.getProjectName()
        );

        try (Statement stmt = conn.createStatement()) {
            int result = stmt.executeUpdate(sql);

            if (result > 0) {
                // 프로젝트 참여자 수 증가
                String updateCountSql = String.format(
                        "UPDATE projects SET member_count = member_count + 1 " +
                                "WHERE club_id = '%s' AND project_name = '%s'",
                        participant.getClubId(),
                        participant.getProjectName()
                );
                stmt.executeUpdate(updateCountSql);
            }

            return result > 0;
        }
    }

    // 프로젝트 참여자 삭제
    public boolean deleteProjectParticipant(ProjectParticipant participant) throws SQLException {
        // 학생 검사
        if (!isStudentInProject(participant.getStudentId(), participant.getClubId(), participant.getProjectName())) {
            throw new SQLException("프로젝트에 참여하지 않은 학생입니다.");
        }

        String sql = String.format(
                "DELETE FROM project_participants WHERE student_id = '%s' AND club_id = '%s' " +
                        "AND project_name = '%s'",
                participant.getStudentId(),
                participant.getClubId(),
                participant.getProjectName()
        );

        try (Statement stmt = conn.createStatement()) {
            int result = stmt.executeUpdate(sql);

            if (result > 0) {
                // 프로젝트 참여자 수 감소
                String updateCountSql = String.format(
                        "UPDATE projects SET member_count = member_count - 1 " +
                                "WHERE club_id = '%s' AND project_name = '%s'",
                        participant.getClubId(),
                        participant.getProjectName()
                );
                stmt.executeUpdate(updateCountSql);
            }

            return result > 0;
        }
    }

    // 프로젝트 참여 여부 확인
    private boolean isStudentInProject(String studentId, String clubId, String projectName) throws SQLException {
        String sql = String.format(
                "SELECT 1 FROM project_participants WHERE student_id = '%s' AND club_id = '%s' " +
                        "AND project_name = '%s'",
                studentId, clubId, projectName
        );

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next();
        }
    }

    // 프로젝트 참여자 목록 조회
    public List<String> getProjectParticipants(String clubId, String projectName) throws SQLException {
        List<String> participants = new ArrayList<>();
        String sql = String.format(
                "SELECT p.student_id, s.name FROM project_participants p " +
                        "JOIN students s ON p.student_id = s.student_id " +
                        "WHERE p.club_id = '%s' AND p.project_name = '%s'",
                clubId, projectName
        );

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String studentInfo = String.format("%s (%s)",
                        rs.getString("name"),
                        rs.getString("student_id")
                );
                participants.add(studentInfo);
            }
        }
        return participants;
    }

    // 동아리별 프로젝트 목록 조회
    public List<Project> getClubProjects(String clubId) throws SQLException {
        List<Project> projects = new ArrayList<>();
        String sql = String.format(
                "SELECT * FROM projects WHERE club_id = '%s' ORDER BY semester DESC, project_name",
                clubId
        );

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                projects.add(new Project(
                        rs.getString("club_id"),
                        rs.getString("project_name"),
                        rs.getInt("member_count"),
                        rs.getString("project_purpose"),
                        rs.getString("project_topic"),
                        rs.getString("management_tool"),
                        rs.getString("semester")
                ));
            }
        }
        return projects;
    }

    // 모든 프로젝트 조회
    public List<Project> getAllProjects() throws SQLException {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT * FROM projects ORDER BY semester DESC, club_id, project_name";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                projects.add(new Project(
                        rs.getString("club_id"),
                        rs.getString("project_name"),
                        rs.getInt("member_count"),
                        rs.getString("project_purpose"),
                        rs.getString("project_topic"),
                        rs.getString("management_tool"),
                        rs.getString("semester")
                ));
            }
        }
        return projects;
    }
}