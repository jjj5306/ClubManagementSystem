package clubManagement.domain;

import java.time.LocalDate;

public class Budget {
    private String receiptNo;
    private String clubId;
    private LocalDate useDate;
    private String useField;
    private double amount;
    private String semester;

    // 새 예산 항목 생성시 사용할 생성자
    public Budget(String receiptNo, String clubId, String useField, double amount) {
        this.receiptNo = receiptNo;
        this.clubId = clubId;
        this.useDate = LocalDate.now();  // 현재 날짜로 설정
        this.useField = useField;
        this.amount = amount;
        this.semester = calculateCurrentSemester();  // 현재 학기 자동 계산
    }

    // 모든 필드를 받는 생성자
    public Budget(String receiptNo, String clubId, LocalDate useDate,
                  String useField, double amount, String semester) {
        this.receiptNo = receiptNo;
        this.clubId = clubId;
        this.useDate = useDate;
        this.useField = useField;
        this.amount = amount;
        this.semester = semester;
    }

    private String calculateCurrentSemester() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        return year + "-" + (month >= 7 ? "2" : "1");
    }

    public String getReceiptNo() {
        return receiptNo;
    }

    public void setReceiptNo(String receiptNo) {
        this.receiptNo = receiptNo;
    }

    public String getClubId() {
        return clubId;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
    }

    public LocalDate getUseDate() {
        return useDate;
    }

    public void setUseDate(LocalDate useDate) {
        this.useDate = useDate;
    }

    public String getUseField() {
        return useField;
    }

    public void setUseField(String useField) {
        this.useField = useField;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }
}