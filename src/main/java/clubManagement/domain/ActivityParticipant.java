package clubManagement.domain;

public class ActivityParticipant {
    private String studentId;
    private String clubId;
    private String activityName;

    public ActivityParticipant(String studentId, String clubId, String activityName) {
        this.studentId = studentId;
        this.clubId = clubId;
        this.activityName = activityName;
    }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getClubId() { return clubId; }
    public void setClubId(String clubId) { this.clubId = clubId; }

    public String getActivityName() { return activityName; }
    public void setActivityName(String activityName) { this.activityName = activityName; }
}