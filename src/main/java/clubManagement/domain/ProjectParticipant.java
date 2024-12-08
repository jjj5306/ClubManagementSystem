package clubManagement.domain;

public class ProjectParticipant {
    private String studentId;
    private String clubId;
    private String projectName;

    public ProjectParticipant(String studentId, String clubId, String projectName) {
        this.studentId = studentId;
        this.clubId = clubId;
        this.projectName = projectName;
    }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getClubId() { return clubId; }
    public void setClubId(String clubId) { this.clubId = clubId; }

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
}