package clubManagement.domain;

public class Professor {
    private String profId;
    private String name;
    private String department;
    private String contact;

    // 새 교수 정보 등록시 사용할 생성자
    public Professor(String profId, String name, String department, String contact) {
        this.profId = profId;
        this.name = name;
        this.department = department;
        this.contact = contact;
    }

    // Getters and Setters
    public String getProfId() { return profId; }
    public void setProfId(String profId) { this.profId = profId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
}