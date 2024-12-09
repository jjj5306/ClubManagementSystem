package clubManagement.menu;

import clubManagement.domain.Club;
import clubManagement.domain.ClubStatus;
import clubManagement.domain.Student;
import clubManagement.service.ClubService;
import clubManagement.service.StudentService;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ClubMenuHandler {
    private final Connection conn;
    private final Scanner scanner;
    private final ClubService clubService;
    private final StudentService studentService;

    public ClubMenuHandler(Connection conn, Scanner scanner) {
        this.conn = conn;
        this.scanner = scanner;
        this.clubService = new ClubService(conn);
        this.studentService = new StudentService(conn);
    }

    public void handleClubManagement() {
        while (true) {
            System.out.println("\n========== 동아리 관리 ==========");
            System.out.println("1. 동아리 등록");
            System.out.println("2. 전체 동아리 조회");
            System.out.println("3. 특정 동아리 조회");
            System.out.println("4. 동아리 정보 수정");
            System.out.println("5. 동아리 상태 변경");
            System.out.println("6. 동아리 삭제");
            System.out.println("7. 상태별 동아리 조회");
            System.out.println("8. 동아리 회원 조회");
            System.out.println("9. 뒤로 가기");
            System.out.println("==============================");
            System.out.print("메뉴 선택: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // 버퍼 비우기

            try {
                switch (choice) {
                    case 1 -> registerClub();
                    case 2 -> viewAllClubs();
                    case 3 -> viewClub();
                    case 4 -> updateClubInfo();
                    case 5 -> updateClubStatus();
                    case 6 -> deleteClub();
                    case 7 -> viewClubsByStatus();
                    case 8 -> viewClubMembers();
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

    private void registerClub() {
        try {
            System.out.println("\n========== 동아리 등록 ==========");

            System.out.print("동아리 ID: ");
            String clubId = scanner.nextLine();

            System.out.print("동아리 이름: ");
            String clubName = scanner.nextLine();

            System.out.print("회장 학번: ");
            String presidentId = scanner.nextLine();

            System.out.print("지도교수 ID: ");
            String profId = scanner.nextLine();

            System.out.print("활동 분야: ");
            String activityField = scanner.nextLine();

            System.out.print("페이지 URL (없으면 Enter): ");
            String pageUrl = scanner.nextLine();
            if (pageUrl.trim().isEmpty()) pageUrl = null;

            System.out.print("동아리 소개 (없으면 Enter): ");
            String clubInfo = scanner.nextLine();
            if (clubInfo.trim().isEmpty()) clubInfo = null;

            // 초기 회원 입력
            List<String> initialMembers = new ArrayList<>();
            System.out.println("\n초기 회원 등록 (회장 제외, 최소 9명 필요)");
            System.out.println("(입력 완료 시 빈 줄 입력)");

            while (true) {
                System.out.printf("회원 학번 (현재 %d명): ", initialMembers.size() + 1);
                String memberId = scanner.nextLine().trim();

                if (memberId.isEmpty()) {
                    if (initialMembers.size() >= 9) {
                        break;
                    } else {
                        System.out.println("최소 9명의 초기 회원이 필요합니다.");
                        continue;
                    }
                }

                if (memberId.equals(presidentId)) {
                    System.out.println("회장은 자동으로 등록됩니다.");
                    continue;
                }

                if (initialMembers.contains(memberId)) {
                    System.out.println("이미 등록된 회원입니다.");
                    continue;
                }

                initialMembers.add(memberId);
            }

            // 신규 동아리 생성자 사용
            Club club = new Club(clubId, presidentId, profId, clubName,
                    activityField, pageUrl, clubInfo);

            if (clubService.registerClub(club, initialMembers)) {
                System.out.println("동아리가 성공적으로 등록되었습니다.");
            } else {
                System.out.println("동아리 등록에 실패했습니다.");
            }
        } catch (Exception e) {
            System.out.println("error : " + e.getMessage());
        }
    }

    private void viewAllClubs() {
        try {
            List<Club> clubs = clubService.getAllClubs();

            if (clubs.isEmpty()) {
                System.out.println("\n등록된 동아리가 없습니다.");
                return;
            }

            System.out.println("\n========== 전체 동아리 목록 ==========");
            for (Club club : clubs) {
                printClubInfo(club);
                System.out.println("---------------------------");
            }
        } catch (Exception e) {
            System.out.println("error : " + e.getMessage());
        }
    }

    private void viewClub() {
        try {
            System.out.print("\n조회할 동아리의 ID를 입력하세요: ");
            String clubId = scanner.nextLine();

            Club club = clubService.getClubById(clubId);

            if (club == null) {
                System.out.println("해당 ID의 동아리를 찾을 수 없습니다.");
                return;
            }

            System.out.println("\n========== 동아리 정보 ==========");
            printClubInfo(club);
        } catch (Exception e) {
            System.out.println("error : " + e.getMessage());
        }
    }

    private void printClubInfo(Club club) {
        System.out.println("동아리 ID: " + club.getClubId());
        System.out.println("동아리 이름: " + club.getClubName());
        System.out.println("회장 학번: " + club.getPresidentId());
        System.out.println("지도교수 ID: " + club.getProfId());
        System.out.println("동아리 상태: " + club.getStatusName());
        System.out.println("페이지 공개 여부: " + (club.isPageVisible() ? "공개" : "비공개"));
        System.out.println("활동 분야: " + club.getActivityField());
        System.out.println("회원 수: " + club.getMemberCount());
        System.out.println("페이지 URL: " + (club.getPageUrl() != null ? club.getPageUrl() : "없음"));
        System.out.println("동아리 소개: " + (club.getClubInfo() != null ? club.getClubInfo() : "없음"));
    }

    // 동아리 정보 수정
    private void updateClubInfo() {
        try {
            System.out.print("\n수정할 동아리의 ID를 입력하세요: ");
            String clubId = scanner.nextLine();

            Club club = clubService.getClubById(clubId);
            if (club == null) {
                System.out.println("해당 ID의 동아리를 찾을 수 없습니다.");
                return;
            }

            System.out.println("\n현재 동아리 정보:");
            printClubInfo(club);
            System.out.println("\n수정할 정보를 입력하세요.");
            System.out.println("(변경하지 않을 항목은 Enter키를 누르세요.)");

            System.out.print("변경할 페이지 URL(" +
                    (club.getPageUrl() != null ? club.getPageUrl() : "없음") + "): ");
            String pageUrl = scanner.nextLine();
            if (pageUrl.trim().isEmpty()) pageUrl = null;

            System.out.print("변경할 동아리 소개(" +
                    (club.getClubInfo() != null ? club.getClubInfo() : "없음") + "): ");
            String clubInfo = scanner.nextLine();
            if (clubInfo.trim().isEmpty()) clubInfo = null;

            if (clubService.updateClubInfo(clubId, pageUrl, clubInfo)) {
                System.out.println("동아리 정보가 성공적으로 수정되었습니다.");
                System.out.println("\n수정된 정보:");
                printClubInfo(clubService.getClubById(clubId));
            } else {
                System.out.println("동아리 정보 수정에 실패했습니다.");
            }
        } catch (Exception e) {
            System.out.println("error : " + e.getMessage());
        }
    }

    // 동아리 상태 변경
    private void updateClubStatus() {
        try {
            System.out.print("\n상태를 변경할 동아리의 ID를 입력하세요: ");
            String clubId = scanner.nextLine();

            Club club = clubService.getClubById(clubId);
            if (club == null) {
                System.out.println("해당 ID의 동아리를 찾을 수 없습니다.");
                return;
            }

            System.out.println("\n현재 동아리 상태: " + club.getStatusName());
            System.out.println("\n변경할 상태를 선택하세요.");
            System.out.println("1. PENDING (대기)");
            System.out.println("2. ACTIVE (활동중)");
            System.out.println("3. CLOSED (폐쇄)");
            System.out.print("선택: ");

            String input = scanner.nextLine();
            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("숫자를 입력해주세요.");
                return;
            }

            ClubStatus newStatus;
            switch (choice) {
                case 1 -> newStatus = ClubStatus.PENDING;
                case 2 -> newStatus = ClubStatus.ACTIVE;
                case 3 -> newStatus = ClubStatus.CLOSED;
                default -> {
                    System.out.println("잘못된 선택입니다.");
                    return;
                }
            }

            if (clubService.updateClubStatus(clubId, newStatus)) {
                System.out.println("동아리 상태가 성공적으로 변경되었습니다.");
                System.out.println("\n변경된 정보:");
                printClubInfo(clubService.getClubById(clubId));
            } else {
                System.out.println("동아리 상태 변경에 실패했습니다.");
            }
        } catch (Exception e) {
            System.out.println("error : " + e.getMessage());
        }
    }

    // 동아리 삭제
    private void deleteClub() {
        try {
            System.out.print("\n삭제할 동아리의 ID를 입력하세요: ");
            String clubId = scanner.nextLine();

            Club club = clubService.getClubById(clubId);
            if (club == null) {
                System.out.println("해당 ID의 동아리를 찾을 수 없습니다.");
                return;
            }

            System.out.println("\n삭제할 동아리 정보:");
            printClubInfo(club);

            System.out.print("\n정말로 삭제하시겠습니까? (y/n): ");
            String confirm = scanner.nextLine();

            if (!confirm.equalsIgnoreCase("y")) {
                System.out.println("삭제가 취소되었습니다.");
                return;
            }

            if (clubService.deleteClub(clubId)) {
                System.out.println("동아리가 성공적으로 삭제되었습니다.");
            } else {
                System.out.println("동아리 삭제에 실패했습니다.");
            }
        } catch (Exception e) {
            System.out.println("error : " + e.getMessage());
        }
    }

    // 상태별 동아리 조회
    private void viewClubsByStatus() {
        try {
            System.out.println("\n조회할 동아리 상태를 선택하세요.");
            System.out.println("1. PENDING (대기)");
            System.out.println("2. ACTIVE (활동중)");
            System.out.println("3. CLOSED (폐쇄)");
            System.out.print("선택: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // 버퍼 비우기

            ClubStatus status;
            switch (choice) {
                case 1 -> status = ClubStatus.PENDING;
                case 2 -> status = ClubStatus.ACTIVE;
                case 3 -> status = ClubStatus.CLOSED;
                default -> {
                    System.out.println("잘못된 선택입니다.");
                    return;
                }
            }

            List<Club> clubs = clubService.getClubsByStatus(status);

            if (clubs.isEmpty()) {
                System.out.println("\n해당 상태의 동아리가 없습니다.");
                return;
            }

            System.out.println("\n========== " + status.getStatusName() + " 상태 동아리 목록 ==========");
            for (Club club : clubs) {
                printClubInfo(club);
                System.out.println("---------------------------");
            }
        } catch (Exception e) {
            System.out.println("error : " + e.getMessage());
        }
    }

    // 동아리 회원 조회
    private void viewClubMembers() {
        try {
            System.out.print("\n조회할 동아리의 ID를 입력하세요: ");
            String clubId = scanner.nextLine();

            Club club = clubService.getClubById(clubId);
            if (club == null) {
                System.out.println("해당 ID의 동아리를 찾을 수 없습니다.");
                return;
            }

            List<Student> members = clubService.getClubMembers(clubId);

            System.out.println("\n========== " + club.getClubName() + " 회원 목록 ==========");
            System.out.println("총 회원 수: " + members.size() + "명");
            System.out.println("---------------------------");

            for (Student member : members) {
                System.out.println("학번: " + member.getStudentId());
                System.out.println("이름: " + member.getName());
                System.out.println("학과: " + member.getDepartment());
                System.out.println("역할: " + member.getRoleName());
                System.out.println("가입일: " + member.getJoinDate());
                System.out.println("---------------------------");
            }
        } catch (Exception e) {
            System.out.println("error : " + e.getMessage());
        }
    }
}