package clubManagement.service;

import clubManagement.domain.Activity;
import clubManagement.domain.ActivityParticipant;
import clubManagement.domain.Club;
import clubManagement.domain.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActivityService {
    private final Connection conn;
    private final ClubService clubService;
    private final StudentService studentService;

    public ActivityService(Connection conn) {
        this.conn = conn;
        this.clubService = new ClubService(conn);
        this.studentService = new StudentService(conn);
    }

    // 활동 등록
    public boolean registerActivity(Activity activity) throws SQLException {
        // 동아리 검사
        Club club = clubService.getClubById(activity.getClubId());
        if (club == null) {
            throw new SQLException("존재하지 않는 동아리입니다.");
        }

        String sql = String.format(
                "INSERT INTO activities (club_id, activity_name, activity_date, member_count, has_award) " +
                        "VALUES ('%s', '%s', '%s', %d, %b)",
                activity.getClubId(),
                activity.getActivityName(),
                activity.getActivityDate(),
                activity.getMemberCount(),
                activity.isHasAward()
        );

        try (Statement stmt = conn.createStatement()) {
            int result = stmt.executeUpdate(sql);
            return result > 0;
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                throw new SQLException("이미 존재하는 활동 이름입니다.");
            }
            throw e;
        }
    }

    // 활동 수정
    public boolean updateActivity(Activity activity) throws SQLException {
        Activity existingActivity = getActivity(activity.getClubId(), activity.getActivityName());
        if (existingActivity == null) {
            throw new SQLException("존재하지 않는 활동입니다.");
        }

        String sql = String.format(
                "UPDATE activities SET activity_date = '%s', has_award = %b " +
                        "WHERE club_id = '%s' AND activity_name = '%s'",
                activity.getActivityDate(),
                activity.isHasAward(),
                activity.getClubId(),
                activity.getActivityName()
        );

        try (Statement stmt = conn.createStatement()) {
            int result = stmt.executeUpdate(sql);
            return result > 0;
        }
    }

    // 활동 삭제
    public boolean deleteActivity(String clubId, String activityName) throws SQLException {
        Activity activity = getActivity(clubId, activityName);
        if (activity == null) {
            throw new SQLException("존재하지 않는 활동입니다.");
        }

        String sql = String.format(
                "DELETE FROM activities WHERE club_id = '%s' AND activity_name = '%s'",
                clubId, activityName
        );

        try (Statement stmt = conn.createStatement()) {
            int result = stmt.executeUpdate(sql);
            return result > 0;
        }
    }

    // 활동 조회
    public Activity getActivity(String clubId, String activityName) throws SQLException {
        String sql = String.format(
                "SELECT * FROM activities WHERE club_id = '%s' AND activity_name = '%s'",
                clubId, activityName
        );

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return new Activity(
                        rs.getString("club_id"),
                        rs.getString("activity_name"),
                        rs.getDate("activity_date").toLocalDate(),
                        rs.getInt("member_count"),
                        rs.getBoolean("has_award")
                );
            }
        }
        return null;
    }

    // 전체 활동 조회
    public List<Activity> getAllActivities() throws SQLException {
        List<Activity> activities = new ArrayList<>();
        String sql = "SELECT * FROM activities ORDER BY activity_date DESC, club_id, activity_name";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                activities.add(new Activity(
                        rs.getString("club_id"),
                        rs.getString("activity_name"),
                        rs.getDate("activity_date").toLocalDate(),
                        rs.getInt("member_count"),
                        rs.getBoolean("has_award")
                ));
            }
        }
        return activities;
    }

    // 동아리별 활동 조회
    public List<Activity> getClubActivities(String clubId) throws SQLException {
        List<Activity> activities = new ArrayList<>();
        String sql = String.format(
                "SELECT * FROM activities WHERE club_id = '%s' ORDER BY activity_date DESC",
                clubId
        );

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                activities.add(new Activity(
                        rs.getString("club_id"),
                        rs.getString("activity_name"),
                        rs.getDate("activity_date").toLocalDate(),
                        rs.getInt("member_count"),
                        rs.getBoolean("has_award")
                ));
            }
        }
        return activities;
    }

    // 활동 참여자 추가
    public boolean addActivityParticipant(ActivityParticipant participant) throws SQLException {
        // 활동 검사
        Activity activity = getActivity(participant.getClubId(), participant.getActivityName());
        if (activity == null) {
            throw new SQLException("존재하지 않는 활동입니다.");
        }

        // 학생 검사
        Student student = studentService.getStudentById(participant.getStudentId());
        if (student == null) {
            throw new SQLException("존재하지 않는 학생입니다.");
        }

        if (student.getClubId() == null || !student.getClubId().equals(participant.getClubId())) {
            throw new SQLException("해당 동아리에 소속된 학생만 활동에 참여할 수 있습니다.");
        }

        if (isStudentInActivity(participant.getStudentId(), participant.getClubId(), participant.getActivityName())) {
            throw new SQLException("이미 활동에 참여 중인 학생입니다.");
        }

        String sql = String.format(
                "INSERT INTO activity_participants (student_id, club_id, activity_name) " +
                        "VALUES ('%s', '%s', '%s')",
                participant.getStudentId(),
                participant.getClubId(),
                participant.getActivityName()
        );

        try (Statement stmt = conn.createStatement()) {
            int result = stmt.executeUpdate(sql);

            if (result > 0) {
                // 활동 참여자 수 증가
                String updateCountSql = String.format(
                        "UPDATE activities SET member_count = member_count + 1 " +
                                "WHERE club_id = '%s' AND activity_name = '%s'",
                        participant.getClubId(),
                        participant.getActivityName()
                );
                stmt.executeUpdate(updateCountSql);
            }

            return result > 0;
        }
    }

    // 활동 참여자 삭제
    public boolean deleteActivityParticipant(ActivityParticipant participant) throws SQLException {
        if (!isStudentInActivity(participant.getStudentId(), participant.getClubId(), participant.getActivityName())) {
            throw new SQLException("활동에 참여하지 않은 학생입니다.");
        }

        String sql = String.format(
                "DELETE FROM activity_participants WHERE student_id = '%s' AND club_id = '%s' " +
                        "AND activity_name = '%s'",
                participant.getStudentId(),
                participant.getClubId(),
                participant.getActivityName()
        );

        try (Statement stmt = conn.createStatement()) {
            int result = stmt.executeUpdate(sql);

            if (result > 0) {
                // 활동 참여자 수 감소
                String updateCountSql = String.format(
                        "UPDATE activities SET member_count = member_count - 1 " +
                                "WHERE club_id = '%s' AND activity_name = '%s'",
                        participant.getClubId(),
                        participant.getActivityName()
                );
                stmt.executeUpdate(updateCountSql);
            }

            return result > 0;
        }
    }

    // 활동 수상 여부 업데이트
    public boolean updateActivityAward(String clubId, String activityName, boolean hasAward) throws SQLException {
        Activity activity = getActivity(clubId, activityName);
        if (activity == null) {
            throw new SQLException("존재하지 않는 활동입니다.");
        }

        String sql = String.format(
                "UPDATE activities SET has_award = %b WHERE club_id = '%s' AND activity_name = '%s'",
                hasAward, clubId, activityName
        );

        try (Statement stmt = conn.createStatement()) {
            int result = stmt.executeUpdate(sql);
            return result > 0;
        }
    }

    // 활동 참여 여부 확인
    private boolean isStudentInActivity(String studentId, String clubId, String activityName) throws SQLException {
        String sql = String.format(
                "SELECT 1 FROM activity_participants WHERE student_id = '%s' AND club_id = '%s' " +
                        "AND activity_name = '%s'",
                studentId, clubId, activityName
        );

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next();
        }
    }

    // 활동 참여자 목록 조회
    public List<String> getActivityParticipants(String clubId, String activityName) throws SQLException {
        List<String> participants = new ArrayList<>();
        String sql = String.format(
                "SELECT p.student_id, s.name FROM activity_participants p " +
                        "JOIN students s ON p.student_id = s.student_id " +
                        "WHERE p.club_id = '%s' AND p.activity_name = '%s'",
                clubId, activityName
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
}