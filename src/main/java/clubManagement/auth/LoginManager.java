package clubManagement.auth;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginManager {
    private Connection conn;
    // 관리자용 아이디
    private static final String ADMIN_ID = "admin";

    public LoginManager(Connection conn) {
        this.conn = conn;
    }

    public static class LoginResult {
        private final UserRole role;
        private final String userId;
        private final String clubId;

        public LoginResult(UserRole role, String userId, String clubId) {
            this.role = role;
            this.userId = userId;
            this.clubId = clubId;
        }

        public UserRole getRole() { return role; }
        public String getUserId() { return userId; }
        public String getClubId() { return clubId; }
    }

    public LoginResult validateUser(String studentId) throws SQLException {
        // 관리자인지 확인
        if (studentId.equals(ADMIN_ID)) {
            return new LoginResult(UserRole.ADMIN, studentId, null);
        }

        // 동아리 회장인지 확인
        Statement stmt = conn.createStatement();
        String presidentQuery = "SELECT club_id FROM clubs WHERE president_id = '" + studentId + "'";
        ResultSet presidentRs = stmt.executeQuery(presidentQuery);

        if (presidentRs.next()) {
            String clubId = presidentRs.getString("club_id");
            return new LoginResult(UserRole.PRESIDENT, studentId, clubId);
        }

        return new LoginResult(UserRole.INVALID, null, null);
    }
}