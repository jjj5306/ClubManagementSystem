package clubManagement.domain;

import java.time.LocalDate;

public class Student {
    public enum Role {
        MEMBER("member"),     // 일반 멤버
        EXECUTIVE("executive"), // 임원
        PRESIDENT("president"); // 회장

        private final String roleName;

        Role(String roleName) {
            this.roleName = roleName;
        }

        public String getRoleName() { return roleName; }

        public static Role fromString(String text) {
            if (text == null) return null;

            for (Role role : Role.values()) {
                if (role.roleName.equalsIgnoreCase(text)) {
                    return role;
                }
            }
            throw new IllegalArgumentException("Unknown role: " + text);
        }
    }

    private String studentId;
    private String contact;
    private String name;
    private String department;
    private Role role;
    private LocalDate joinDate;
    private String clubId;

    // 기본 생성자 (신규 멤버)
    public Student(String studentId, String contact, String name, String department) {
        this.studentId = studentId;
        this.contact = contact;
        this.name = name;
        this.department = department;
        this.role = null;
        this.joinDate = null;
        this.clubId = null;
    }

    // 모든 필드를 포함하는 생성자
    public Student(String studentId, String contact, String name, String department,
                   Role role, LocalDate joinDate, String clubId) {
        this.studentId = studentId;
        this.contact = contact;
        this.name = name;
        this.department = department;
        this.role = role;
        this.joinDate = joinDate;
        this.clubId = clubId;
    }

    // Getters and Setters
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public String getRoleName() { return role.getRoleName(); }

    public LocalDate getJoinDate() { return joinDate; }
    public void setJoinDate(LocalDate joinDate) { this.joinDate = joinDate; }

    public String getClubId() { return clubId; }
    public void setClubId(String clubId) { this.clubId = clubId; }
}