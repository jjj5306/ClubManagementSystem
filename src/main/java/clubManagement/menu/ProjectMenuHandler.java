package clubManagement.menu;

import clubManagement.domain.Project;
import clubManagement.domain.ProjectParticipant;
import clubManagement.service.ProjectService;

import java.sql.Connection;
import java.util.List;
import java.util.Scanner;

public class ProjectMenuHandler {
    private final Scanner scanner;
    private final ProjectService projectService;

    public ProjectMenuHandler(Connection conn, Scanner scanner) {
        this.scanner = scanner;
        this.projectService = new ProjectService(conn);
    }

    public void handleProjectManagement() {
        while (true) {
            displayProjectMenu();
            System.out.print("메뉴를 선택하세요: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // 버퍼 비우기

            try {
                switch (choice) {
                    case 1 -> handleRegisterProject();
                    case 2 -> handleUpdateProject();
                    case 3 -> handleDeleteProject();
                    case 4 -> handleViewProject();
                    case 5 -> handleAddProjectParticipant();
                    case 6 -> handleDeleteProjectParticipant();
                    case 7 -> handleViewProjectParticipants();
                    case 8 -> handleViewClubProjects();
                    case 9 -> {
                        return;
                    }
                    default -> System.out.println("잘못된 선택입니다. 다시 시도해주세요.");
                }
            } catch (Exception e) {
                System.out.println("오류 발생: " + e.getMessage());
            }
            System.out.println();
        }
    }

    private void displayProjectMenu() {
        System.out.println("\n========= 프로젝트 관리 메뉴 =========");
        System.out.println("1. 프로젝트 등록");
        System.out.println("2. 프로젝트 수정");
        System.out.println("3. 프로젝트 삭제");
        System.out.println("4. 전체 프로젝트 조회");
        System.out.println("5. 프로젝트 참여자 추가");
        System.out.println("6. 프로젝트 참여자 삭제");
        System.out.println("7. 프로젝트 참여자 조회");
        System.out.println("8. 동아리별 프로젝트 조회");
        System.out.println("9. 이전 메뉴로");
        System.out.println("================================");
    }

    private void handleRegisterProject() throws Exception {
        System.out.print("동아리 ID: ");
        String clubId = scanner.nextLine();

        System.out.print("프로젝트 이름: ");
        String projectName = scanner.nextLine();

        System.out.print("프로젝트 목적: ");
        String projectPurpose = scanner.nextLine();

        System.out.print("프로젝트 주제: ");
        String projectTopic = scanner.nextLine();

        System.out.print("사용할 도구: ");
        String managementTool = scanner.nextLine();

        Project project = new Project(clubId, projectName, projectPurpose, projectTopic, managementTool);

        boolean success = projectService.registerProject(project);
        if (success) {
            System.out.println("프로젝트가 성공적으로 등록되었습니다.");
        } else {
            System.out.println("프로젝트 등록에 실패했습니다.");
        }
    }

    private void handleUpdateProject() throws Exception {
        System.out.print("동아리 ID: ");
        String clubId = scanner.nextLine();

        System.out.print("프로젝트 이름: ");
        String projectName = scanner.nextLine();

        Project existingProject = projectService.getProject(clubId, projectName);
        if (existingProject == null) {
            System.out.println("존재하지 않는 프로젝트입니다.");
            return;
        }

        System.out.print("새로운 프로젝트 목적 (엔터시 기존값 유지): ");
        String projectPurpose = scanner.nextLine();
        if (!projectPurpose.trim().isEmpty()) {
            existingProject.setProjectPurpose(projectPurpose);
        }

        System.out.print("새로운 프로젝트 주제 (엔터시 기존값 유지): ");
        String projectTopic = scanner.nextLine();
        if (!projectTopic.trim().isEmpty()) {
            existingProject.setProjectTopic(projectTopic);
        }

        System.out.print("새로운 사용 도구 (엔터시 기존값 유지): ");
        String managementTool = scanner.nextLine();
        if (!managementTool.trim().isEmpty()) {
            existingProject.setManagementTool(managementTool);
        }

        boolean success = projectService.updateProject(existingProject);
        if (success) {
            System.out.println("프로젝트가 성공적으로 수정되었습니다.");
        } else {
            System.out.println("프로젝트 수정에 실패했습니다.");
        }
    }

    private void handleDeleteProject() throws Exception {
        System.out.print("동아리 ID: ");
        String clubId = scanner.nextLine();

        System.out.print("프로젝트 이름: ");
        String projectName = scanner.nextLine();

        boolean success = projectService.deleteProject(clubId, projectName);
        if (success) {
            System.out.println("프로젝트가 성공적으로 삭제되었습니다.");
        } else {
            System.out.println("프로젝트 삭제에 실패했습니다.");
        }
    }

    private void handleAddProjectParticipant() throws Exception {
        System.out.print("동아리 ID: ");
        String clubId = scanner.nextLine();

        System.out.print("프로젝트 이름: ");
        String projectName = scanner.nextLine();

        System.out.print("참여할 학생 ID: ");
        String studentId = scanner.nextLine();

        ProjectParticipant participant = new ProjectParticipant(studentId, clubId, projectName);

        boolean success = projectService.addProjectParticipant(participant);
        if (success) {
            System.out.println("프로젝트 참여자가 성공적으로 추가되었습니다.");
        } else {
            System.out.println("프로젝트 참여자 추가에 실패했습니다.");
        }
    }

    private void handleDeleteProjectParticipant() throws Exception {
        System.out.print("동아리 ID: ");
        String clubId = scanner.nextLine();

        System.out.print("프로젝트 이름: ");
        String projectName = scanner.nextLine();

        System.out.print("삭제할 학생 ID: ");
        String studentId = scanner.nextLine();

        ProjectParticipant participant = new ProjectParticipant(studentId, clubId, projectName);

        boolean success = projectService.deleteProjectParticipant(participant);
        if (success) {
            System.out.println("프로젝트 참여자가 성공적으로 삭제되었습니다.");
        } else {
            System.out.println("프로젝트 참여자 삭제에 실패했습니다.");
        }
    }

    private void handleViewProjectParticipants() throws Exception {
        System.out.print("동아리 ID: ");
        String clubId = scanner.nextLine();

        System.out.print("프로젝트 이름: ");
        String projectName = scanner.nextLine();

        List<String> participants = projectService.getProjectParticipants(clubId, projectName);
        if (participants.isEmpty()) {
            System.out.println("프로젝트 참여자가 없습니다.");
            return;
        }

        System.out.println("\n=== 프로젝트 참여자 목록 ===");
        for (String participant : participants) {
            System.out.println(participant);
        }
    }

    private void handleViewClubProjects() throws Exception {
        System.out.print("동아리 ID: ");
        String clubId = scanner.nextLine();

        List<Project> projects = projectService.getClubProjects(clubId);
        if (projects.isEmpty()) {
            System.out.println("해당 동아리의 프로젝트가 없습니다.");
            return;
        }

        System.out.println("\n=== 동아리 프로젝트 목록 ===");
        String currentSemester = "";

        for (Project project : projects) {
            if (!project.getSemester().equals(currentSemester)) {
                if (!currentSemester.isEmpty()) {
                    System.out.println();
                }
                currentSemester = project.getSemester();
                System.out.println("\n" + currentSemester + " 학기:");
            }

            System.out.printf("프로젝트명: %s\n", project.getProjectName());
            System.out.printf("목적: %s\n", project.getProjectPurpose());
            System.out.printf("주제: %s\n", project.getProjectTopic());
            System.out.printf("사용 도구: %s\n", project.getManagementTool());
            System.out.printf("참여 인원: %d명\n", project.getMemberCount());
            System.out.println("--------------------");
        }
    }

    private void handleViewProject() throws Exception {
        List<Project> projects = projectService.getAllProjects();
        if (projects.isEmpty()) {
            System.out.println("등록된 프로젝트가 없습니다.");
            return;
        }

        System.out.println("\n=== 전체 프로젝트 목록 ===");
        String currentSemester = "";

        for (Project project : projects) {
            if (!project.getSemester().equals(currentSemester)) {
                if (!currentSemester.isEmpty()) {
                    System.out.println();
                }
                currentSemester = project.getSemester();
                System.out.println("\n" + currentSemester + " 학기:");
            }

            System.out.println("\n-------------------");
            System.out.printf("동아리: %s\n", project.getClubId());
            System.out.printf("프로젝트명: %s\n", project.getProjectName());
            System.out.printf("목적: %s\n", project.getProjectPurpose());
            System.out.printf("주제: %s\n", project.getProjectTopic());
            System.out.printf("사용 도구: %s\n", project.getManagementTool());
            System.out.printf("참여 인원: %d명\n", project.getMemberCount());
        }
    }
}