package clubManagement.domain;

import java.time.LocalDate;

public class Project {
    private String clubId;
    private String projectName;
    private int memberCount;
    private String projectPurpose;
    private String projectTopic;
    private String managementTool;
    private String semester;

    // 새 프로젝트 생성시 사용할 생성자
    public Project(String clubId, String projectName, String projectPurpose,
                   String projectTopic, String managementTool) {
        this.clubId = clubId;
        this.projectName = projectName;
        this.memberCount = 0;  // 초기 멤버 수 0
        this.projectPurpose = projectPurpose;
        this.projectTopic = projectTopic;
        this.managementTool = managementTool;
        this.semester = calculateCurrentSemester();  // 현재 학기 자동 계산
    }

    // 모든 필드를 받는 생성자
    public Project(String clubId, String projectName, int memberCount, String projectPurpose,
                   String projectTopic, String managementTool, String semester) {
        this.clubId = clubId;
        this.projectName = projectName;
        this.memberCount = memberCount;
        this.projectPurpose = projectPurpose;
        this.projectTopic = projectTopic;
        this.managementTool = managementTool;
        this.semester = semester;
    }

    // 현재 학기 계산 메소드, 현재 시간을 기준으로 계산함 (예: "2024-1", "2024-2")
    private String calculateCurrentSemester() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        return year + "-" + (month >= 7 ? "2" : "1");
    }

    public String getClubId() {
        return clubId;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public String getProjectPurpose() {
        return projectPurpose;
    }

    public void setProjectPurpose(String projectPurpose) {
        this.projectPurpose = projectPurpose;
    }

    public String getProjectTopic() {
        return projectTopic;
    }

    public void setProjectTopic(String projectTopic) {
        this.projectTopic = projectTopic;
    }

    public String getManagementTool() {
        return managementTool;
    }

    public void setManagementTool(String managementTool) {
        this.managementTool = managementTool;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }
}