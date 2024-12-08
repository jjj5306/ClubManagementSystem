package clubManagement.menu;

import clubManagement.domain.Student;
import clubManagement.service.admin.StudentService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class AdminMenuHandler {
    private final Connection conn;
    private final Scanner scanner;
    private final StudentService studentService;

    public AdminMenuHandler(Connection conn, Scanner scanner) {
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
            System.out.println("4. 학생 정보 수정");
            System.out.println("5. 학생 삭제");
            System.out.println("6. 동아리 가입 처리");
            System.out.println("7. 뒤로 가기");
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
                    case 5 -> deleteStudent();
                    case 6 -> registerStudentToClub();
                    case 7 -> {
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

            System.out.print("이름(" + student.getName() + "): ");
            String name = scanner.nextLine();
            if (!name.trim().isEmpty()) {
                student.setName(name);
            }

            System.out.print("학과(" + student.getDepartment() + "): ");
            String department = scanner.nextLine();
            if (!department.trim().isEmpty()) {
                student.setDepartment(department);
            }

            System.out.print("연락처(" + student.getContact() + "): ");
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
}