package clubManagement.domain;

public enum ClubStatus {
    CLOSED("closed", false),    // 폐쇄
    PENDING("pending", false),  // 대기
    ACTIVE("active", true);     // 활동중

    private final String statusName;
    private final boolean isPageVisible;

    ClubStatus(String statusName, boolean isPageVisible) {
        this.statusName = statusName;
        this.isPageVisible = isPageVisible;
    }

    public String getStatusName() { return statusName; }
    public boolean isPageVisible() { return isPageVisible; }

    public static ClubStatus fromString(String text) {
        for (ClubStatus status : ClubStatus.values()) {
            if (status.statusName.equalsIgnoreCase(text)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + text);
    }
}