package clubManagement.domain;

import java.time.LocalDate;

public class Activity {
    private String clubId;
    private String activityName;
    private LocalDate activityDate;
    private int memberCount;
    private boolean hasAward;

    // 새 활동 생성시 사용할 생성자
    public Activity(String clubId, String activityName, LocalDate activityDate) {
        this.clubId = clubId;
        this.activityName = activityName;
        this.activityDate = activityDate;
        this.memberCount = 0;  // 초기 참여자 수 0
        this.hasAward = false;  // 초기 수상 여부 false
    }

    // 모든 필드를 받는 생성자
    public Activity(String clubId, String activityName, LocalDate activityDate,
                    int memberCount, boolean hasAward) {
        this.clubId = clubId;
        this.activityName = activityName;
        this.activityDate = activityDate;
        this.memberCount = memberCount;
        this.hasAward = hasAward;
    }

    public String getClubId() {
        return clubId;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public LocalDate getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(LocalDate activityDate) {
        this.activityDate = activityDate;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public boolean isHasAward() {
        return hasAward;
    }

    public void setHasAward(boolean hasAward) {
        this.hasAward = hasAward;
    }
}