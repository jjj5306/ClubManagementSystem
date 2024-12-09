package clubManagement.menu;

import clubManagement.domain.Professor;
import clubManagement.service.ProfessorService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class ProfessorMenuHandler {
    private final Connection conn;
    private final Scanner scanner;
    private final ProfessorService professorService;

    public ProfessorMenuHandler(Connection conn, Scanner scanner) {
        this.conn = conn;
        this.scanner = scanner;
        this.professorService = new ProfessorService(conn);
    }

    public void handleProfessorManagement() {
        while (true) {
            System.out.println("\n========== 지도교수 관리 ==========");
            System.out.println("1. 지도교수 등록");
            System.out.println("2. 전체 지도교수 조회");
            System.out.println("3. 특정 지도교수 조회");
            System.out.println("4. 지도교수 정보 수정");
            System.out.println("5. 지도교수 삭제");
            System.out.println("6. 뒤로 가기");
            System.out.println("==============================");
            System.out.print("메뉴 선택: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // 버퍼 비우기

            try {
                switch (choice) {
                    case 1 -> registerProfessor();
                    case 2 -> viewAllProfessors();
                    case 3 -> viewProfessor();
                    case 4 -> updateProfessor();
                    case 5 -> deleteProfessor();
                    case 6 -> {
                        return;
                    }
                    default -> System.out.println("잘못된 메뉴 선택입니다.");
                }
            } catch (Exception e) {
                System.out.println("error : " + e.getMessage());
            }
        }
    }

    private void registerProfessor() {
        try {
            System.out.println("\n========== 지도교수 등록 ==========");

            System.out.print("교번: ");
            String profId = scanner.nextLine();

            System.out.print("이름: ");
            String name = scanner.nextLine();

            System.out.print("학과: ");
            String department = scanner.nextLine();

            System.out.print("연락처: ");
            String contact = scanner.nextLine();

            Professor professor = new Professor(profId, name, department, contact);

            if (professorService.registerProfessor(professor)) {
                System.out.println("지도교수가 성공적으로 등록되었습니다.");
            } else {
                System.out.println("지도교수 등록에 실패했습니다.");
            }
        } catch (Exception e) {
            System.out.println("error : " + e.getMessage());
        }
    }

    private void viewAllProfessors() {
        try {
            List<Professor> professors = professorService.getAllProfessors();

            if (professors.isEmpty()) {
                System.out.println("\n등록된 지도교수가 없습니다.");
                return;
            }

            System.out.println("\n========== 전체 지도교수 목록 ==========");
            for (Professor professor : professors) {
                printProfessorInfo(professor);
                System.out.println("---------------------------");
            }
        } catch (Exception e) {
            System.out.println("error : " + e.getMessage());
        }
    }

    private void viewProfessor() {
        try {
            System.out.print("\n조회할 교수의 교번을 입력하세요: ");
            String profId = scanner.nextLine();

            Professor professor = professorService.getProfessorById(profId);

            if (professor == null) {
                System.out.println("해당 교번의 교수를 찾을 수 없습니다.");
                return;
            }

            System.out.println("\n========== 지도교수 정보 ==========");
            printProfessorInfo(professor);
        } catch (Exception e) {
            System.out.println("error : " + e.getMessage());
        }
    }

    private void updateProfessor() {
        try {
            System.out.print("\n수정할 교수의 교번을 입력하세요: ");
            String profId = scanner.nextLine();

            Professor professor = professorService.getProfessorById(profId);
            if (professor == null) {
                System.out.println("해당 교번의 교수를 찾을 수 없습니다.");
                return;
            }

            System.out.println("\n현재 교수 정보:");
            printProfessorInfo(professor);
            System.out.println("\n수정할 정보를 입력하세요.");
            System.out.println("(변경하지 않을 항목은 Enter키를 누르세요.)");

            System.out.print("변경할 이름(" + professor.getName() + "): ");
            String name = scanner.nextLine();
            if (!name.trim().isEmpty()) {
                professor.setName(name);
            }

            System.out.print("변경할 학과(" + professor.getDepartment() + "): ");
            String department = scanner.nextLine();
            if (!department.trim().isEmpty()) {
                professor.setDepartment(department);
            }

            System.out.print("변경할 연락처(" + professor.getContact() + "): ");
            String contact = scanner.nextLine();
            if (!contact.trim().isEmpty()) {
                professor.setContact(contact);
            }

            if (professorService.updateProfessor(professor)) {
                System.out.println("지도교수 정보가 성공적으로 수정되었습니다.");
                System.out.println("\n수정된 정보:");
                printProfessorInfo(professor);
            } else {
                System.out.println("지도교수 정보 수정에 실패했습니다.");
            }
        } catch (Exception e) {
            System.out.println("error : " + e.getMessage());
        }
    }

    private void deleteProfessor() {
        try {
            System.out.print("\n삭제할 교수의 교번을 입력하세요: ");
            String profId = scanner.nextLine();

            // 교수 존재 여부 확인
            Professor professor = professorService.getProfessorById(profId);
            if (professor == null) {
                System.out.println("해당 교번의 교수를 찾을 수 없습니다.");
                return;
            }

            // 삭제 확인
            System.out.print("정말로 삭제하시겠습니까? (y/n): ");
            String confirm = scanner.nextLine();

            if (!confirm.equalsIgnoreCase("y")) {
                System.out.println("삭제가 취소되었습니다.");
                return;
            }

            if (professorService.deleteProfessor(profId)) {
                System.out.println("지도교수가 성공적으로 삭제되었습니다.");
            } else {
                System.out.println("지도교수 삭제에 실패했습니다.");
            }
        } catch (Exception e) {
            System.out.println("error : " + e.getMessage());
        }
    }

    private void printProfessorInfo(Professor professor) {
        System.out.println("교번: " + professor.getProfId());
        System.out.println("이름: " + professor.getName());
        System.out.println("학과: " + professor.getDepartment());
        System.out.println("연락처: " + professor.getContact());
    }
}