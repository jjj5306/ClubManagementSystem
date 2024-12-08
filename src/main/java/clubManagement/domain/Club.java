package clubManagement.domain;

public class Club {
    private String clubId;
    private String presidentId;
    private String profId;
    private ClubStatus status;
    private String clubName;
    private String activityField;
    private int memberCount;
    private String pageUrl;
    private String clubInfo;

    // 신규 동아리 생성시 사용할 생성자
    public Club(String clubId, String presidentId, String profId, String clubName,
                String activityField, String pageUrl, String clubInfo) {
        this.clubId = clubId;
        this.presidentId = presidentId;
        this.profId = profId;
        this.status = ClubStatus.PENDING;  // 대기 상태로 시작
        this.clubName = clubName;
        this.activityField = activityField;
        this.memberCount = 1;  // 회장 1명으로 시작
        this.pageUrl = pageUrl;
        this.clubInfo = clubInfo;
    }

    // 모든 필드를 받는 생성자
    public Club(String clubId, String presidentId, String profId, ClubStatus status,
                String clubName, String activityField, int memberCount, String pageUrl, String clubInfo) {
        this.clubId = clubId;
        this.presidentId = presidentId;
        this.profId = profId;
        this.status = status;
        this.clubName = clubName;
        this.activityField = activityField;
        this.memberCount = memberCount;
        this.pageUrl = pageUrl;
        this.clubInfo = clubInfo;
    }

    public String getClubId() { return clubId; }
    public void setClubId(String clubId) { this.clubId = clubId; }

    public String getPresidentId() { return presidentId; }
    public void setPresidentId(String presidentId) { this.presidentId = presidentId; }

    public String getProfId() { return profId; }
    public void setProfId(String profId) { this.profId = profId; }

    public ClubStatus getStatus() { return status; }
    public void setStatus(ClubStatus status) { this.status = status; }
    public String getStatusName() { return status.getStatusName(); }
    public boolean isPageVisible() { return status.isPageVisible(); }

    public String getClubName() { return clubName; }
    public void setClubName(String clubName) { this.clubName = clubName; }

    public String getActivityField() { return activityField; }
    public void setActivityField(String activityField) { this.activityField = activityField; }

    public int getMemberCount() { return memberCount; }
    public void setMemberCount(int memberCount) { this.memberCount = memberCount; }

    public String getPageUrl() { return pageUrl; }
    public void setPageUrl(String pageUrl) { this.pageUrl = pageUrl; }

    public String getClubInfo() { return clubInfo; }
    public void setClubInfo(String clubInfo) { this.clubInfo = clubInfo; }
}