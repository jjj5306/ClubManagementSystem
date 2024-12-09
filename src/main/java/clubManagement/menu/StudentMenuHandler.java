package clubManagement.menu;

import clubManagement.domain.Student;
import clubManagement.service.StudentService;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class StudentMenuHandler {
    private final Connection conn;
    private final Scanner scanner;
    private final StudentService studentService;

    public StudentMenuHandler(Connection conn, Scanner scanner) {
        this.conn = conn;
        this.scanner = scanner;
        this.studentService = new StudentService(conn);
    }

    public void handleStudentManagement() {
        while (true) {
            System.out.println("\n========== 학생 관리 ==========");
            System.out.println("1. 학생 등록");
            System.out.println("2. 전체 학생 조회");
            System.out.println("3. 특정 학생 조회");
            System.out.println("4. 학생 기본 정보 수정");
            System.out.println("5. 동아리 가입 처리");
            System.out.println("6. 학생 동아리 정보 수정");
            System.out.println("7. 학생 삭제");
            System.out.println("8. 동아리 탈퇴 처리");
            System.out.println("9. 뒤로 가기");
            System.out.println("==============================");
            System.out.print("메뉴 선택: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // 버퍼 비우기

            try {
                switch (choice) {
                    case 1 -> registerStudent();
                    case 2 -> viewAllStudents();
                    case 3 -> viewStudent();
                    case 4 -> updateStudent();
                    case 5 -> registerStudentToClub();
                    case 6 -> updateStudentClubInfo();
                    case 7 -> deleteStudent();
                    case 8 -> leaveClub();
                    case 9 -> {
                        return;
                    }
                    default -> System.out.println("잘못된 메뉴 선택입니다.");
                }
            } catch (Exception e) {
                System.out.println("error : " + e.getMessage());
            }
        }
    }

    private void registerStudent() {
        try {
            System.out.println("\n========== 학생 등록 ==========");

            System.out.print("학번: ");
            String studentId = scanner.nextLine();

            System.out.print("이름: ");
            String name = scanner.nextLine();

            System.out.print("학과: ");
            String department = scanner.nextLine();

            System.out.print("연락처: ");
            String contact = scanner.nextLine();

            Student student = new Student(studentId, contact, name, department);

            if (studentService.registerStudent(student)) {
                System.out.println("학생이 성공적으로 등록되었습니다.");
            } else {
                System.out.println("학생 등록에 실패했습니다.");
            }
        } catch (Exception e) {
            System.out.println("error : " + e.getMessage());
        }
    }

    private void viewAllStudents() {
        try {
            List<Student> students = studentService.getAllStudents();

            if (students.isEmpty()) {
                System.out.println("\n등록된 학생이 없습니다.");
                return;
            }

            System.out.println("\n========== 전체 학생 목록 ==========");
            for (Student student : students) {
                printStudentInfo(student);
                System.out.println("---------------------------");
            }

        } catch (Exception e) {
            System.out.println("error : " + e.getMessage());
        }
    }

    private void viewStudent() {
        try {
            System.out.print("\n조회할 학생의 학번을 입력하세요: ");
            String studentId = scanner.nextLine();

            Student student = studentService.getStudentById(studentId);

            if (student == null) {
                System.out.println("해당 학번의 학생을 찾을 수 없습니다.");
                return;
            }

            System.out.println("\n========== 학생 정보 ==========");
            printStudentInfo(student);

        } catch (Exception e) {
            System.out.println("error : " + e.getMessage());
        }
    }

    private void deleteStudent() {
        try {
            System.out.print("\n삭제할 학생의 학번을 입력하세요: ");
            String studentId = scanner.nextLine();

            // 삭제 확인
            System.out.print("정말로 삭제하시겠습니까? (y/n): ");
            String confirm = scanner.nextLine();

            if (!confirm.equalsIgnoreCase("y")) {
                System.out.println("삭제가 취소되었습니다.");
                return;
            }

            if (studentService.deleteStudent(studentId)) {
                System.out.println("학생이 성공적으로 삭제되었습니다.");
            } else {
                System.out.println("학생 삭제에 실패했습니다.");
            }

        } catch (Exception e) {
            System.out.println("error : " + e.getMessage());
        }
    }

    private void printStudentInfo(Student student) {
        System.out.println("학번: " + student.getStudentId());
        System.out.println("이름: " + student.getName());
        System.out.println("학과: " + student.getDepartment());
        System.out.println("연락처: " + student.getContact());
        System.out.println("역할: " + (student.getRole() != null ? student.getRoleName() : "null"));
        System.out.println("가입일: " + (student.getJoinDate() != null ? student.getJoinDate() : "null"));
        System.out.println("소속 동아리: " + (student.getClubId() != null ? student.getClubId() : "null"));
    }

    private void updateStudent() {
        try {
            System.out.print("\n수정할 학생의 학번을 입력하세요: ");
            String studentId = scanner.nextLine();

            // 학생 검사
            Student student = studentService.getStudentById(studentId);
            if (student == null) {
                System.out.println("해당 학번의 학생을 찾을 수 없습니다.");
                return;
            }

            // 현재 정보 출력
            System.out.println("\n현재 학생 정보:");
            printStudentInfo(student);
            System.out.println("\n수정할 정보를 입력하세요.");
            System.out.println("(변경하지 않을 항목은 Enter키를 누르세요.)");

            System.out.print("변경할 이름(" + student.getName() + "): ");
            String name = scanner.nextLine();
            if (!name.trim().isEmpty()) {
                student.setName(name);
            }

            System.out.print("변경할 학과(" + student.getDepartment() + "): ");
            String department = scanner.nextLine();
            if (!department.trim().isEmpty()) {
                student.setDepartment(department);
            }

            System.out.print("변경할 연락처(" + student.getContact() + "): ");
            String contact = scanner.nextLine();
            if (!contact.trim().isEmpty()) {
                student.setContact(contact);
            }

            if (studentService.updateStudent(student)) {
                System.out.println("학생 정보가 성공적으로 수정되었습니다.");
                System.out.println("\n수정된 정보:");
                printStudentInfo(student);
            } else {
                System.out.println("학생 정보 수정에 실패했습니다.");
            }

        } catch (Exception e) {
            System.out.println("error : " + e.getMessage());
        }
    }

    private void registerStudentToClub() {
        try {
            System.out.println("\n========== 동아리 가입 처리 ==========");

            System.out.print("학번: ");
            String studentId = scanner.nextLine();

            System.out.print("가입할 동아리 ID: ");
            String clubId = scanner.nextLine();

            if (studentService.registerToClub(studentId, clubId)) {
                System.out.println("동아리 가입이 완료되었습니다.");
            } else {
                System.out.println("동아리 가입에 실패했습니다.");
            }

        } catch (SQLException e) {
            System.out.println("error : " + e.getMessage());
        }
    }

    private void updateStudentClubInfo() {
        try {
            System.out.println("\n========== 학생 동아리 정보 수정 ==========");

            System.out.print("학번: ");
            String studentId = scanner.nextLine();

            // 학생 존재 여부 및 동아리 가입 여부 확인
            Student student = studentService.getStudentById(studentId);
            if (student == null) {
                System.out.println("존재하지 않는 학생입니다.");
                return;
            }
            if (student.getClubId() == null) {
                System.out.println("동아리에 가입되지 않은 학생입니다.");
                return;
            }

            System.out.println("\n현재 동아리 정보:");
            System.out.println("소속 동아리: " + student.getClubId());
            System.out.println("역할: " + student.getRoleName());
            System.out.println("가입일: " + student.getJoinDate());

            System.out.println("\n수정할 정보를 입력하세요. (변경하지 않을 항목은 Enter키를 누르세요)");

            System.out.print("변경할 동아리 ID(" + student.getClubId() + "): ");
            String newClubId = scanner.nextLine();
            if (newClubId.trim().isEmpty()) {
                newClubId = null;
            }

            System.out.print("변경할 역할(" + student.getRoleName() + ") [member/executive/president]: ");
            String roleStr = scanner.nextLine();
            Student.Role newRole = roleStr.trim().isEmpty() ? null : Student.Role.fromString(roleStr);

            System.out.print("변경할 가입일(" + student.getJoinDate() + ") [YYYY-MM-DD]: ");
            String dateStr = scanner.nextLine();
            LocalDate newJoinDate = dateStr.trim().isEmpty() ? null : LocalDate.parse(dateStr);

            if (studentService.updateStudentClubInfo(studentId, newClubId, newRole, newJoinDate)) {
                System.out.println("동아리 정보가 성공적으로 수정되었습니다.");
                Student updatedStudent = studentService.getStudentById(studentId);
                System.out.println("\n수정된 정보:");
                printStudentInfo(updatedStudent);
            } else {
                System.out.println("동아리 정보 수정에 실패했습니다.");
            }

        } catch (Exception e) {
            System.out.println("오류 발생: " + e.getMessage());
        }
    }

    private void leaveClub() {
        try {
            System.out.println("\n========== 동아리 탈퇴 처리 ==========");

            System.out.print("학번: ");
            String studentId = scanner.nextLine();

            // 학생 검사
            Student student = studentService.getStudentById(studentId);
            if (student == null) {
                System.out.println("존재하지 않는 학생입니다.");
                return;
            }
            if (student.getClubId() == null) {
                System.out.println("동아리에 가입되지 않은 학생입니다.");
                return;
            }

            System.out.println("\n현재 동아리 정보:");
            System.out.println("소속 동아리: " + student.getClubId());
            System.out.println("역할: " + student.getRoleName());
            System.out.println("가입일: " + student.getJoinDate());

            // 탈퇴 확인
            System.out.print("\n정말로 탈퇴하시겠습니까? (y/n): ");
            String confirm = scanner.nextLine();

            if (!confirm.equalsIgnoreCase("y")) {
                System.out.println("탈퇴가 취소되었습니다.");
                return;
            }

            if (studentService.leaveClub(studentId)) {
                System.out.println("동아리 탈퇴가 완료되었습니다.");
            } else {
                System.out.println("동아리 탈퇴에 실패했습니다.");
            }

        } catch (SQLException e) {
            System.out.println("error : " + e.getMessage());
        }
    }

}