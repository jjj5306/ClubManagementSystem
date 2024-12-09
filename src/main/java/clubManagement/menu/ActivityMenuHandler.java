package clubManagement.menu;

import clubManagement.domain.Activity;
import clubManagement.domain.ActivityParticipant;
import clubManagement.service.ActivityService;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class ActivityMenuHandler {
    private final Scanner scanner;
    private final ActivityService activityService;

    public ActivityMenuHandler(Connection conn, Scanner scanner) {
        this.scanner = scanner;
        this.activityService = new ActivityService(conn);
    }

    public void handleActivityManagement() {
        while (true) {
            displayActivityMenu();
            System.out.print("메뉴를 선택하세요: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // 버퍼 비우기

            try {
                switch (choice) {
                    case 1 -> handleRegisterActivity();
                    case 2 -> handleUpdateActivity();
                    case 3 -> handleDeleteActivity();
                    case 4 -> handleViewAllActivities();
                    case 5 -> handleViewClubActivities();
                    case 6 -> handleUpdateActivityAward();
                    case 7 -> handleAddActivityParticipant();
                    case 8 -> handleDeleteActivityParticipant();
                    case 9 -> handleViewActivityParticipants();
                    case 0 -> {
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

    private void displayActivityMenu() {
        System.out.println("\n========= 주요활동 관리 메뉴 =========");
        System.out.println("1. 주요활동 등록");
        System.out.println("2. 주요활동 수정");
        System.out.println("3. 주요활동 삭제");
        System.out.println("4. 전체 활동 조회");
        System.out.println("5. 동아리별 활동 조회");
        System.out.println("6. 활동 수상 여부 변경");
        System.out.println("7. 활동 참여자 추가");
        System.out.println("8. 활동 참여자 삭제");
        System.out.println("9. 활동 참여자 조회");
        System.out.println("0. 이전 메뉴로");
        System.out.println("================================");
    }

    private void handleRegisterActivity() throws Exception {
        System.out.print("동아리 ID: ");
        String clubId = scanner.nextLine();

        System.out.print("활동 이름: ");
        String activityName = scanner.nextLine();

        System.out.print("활동 날짜 (YYYY-MM-DD): ");
        String dateStr = scanner.nextLine();
        LocalDate activityDate = LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE);

        Activity activity = new Activity(clubId, activityName, activityDate);

        boolean success = activityService.registerActivity(activity);
        if (success) {
            System.out.println("주요활동이 성공적으로 등록되었습니다.");
        } else {
            System.out.println("주요활동 등록에 실패했습니다.");
        }
    }

    private void handleUpdateActivity() throws Exception {
        System.out.print("동아리 ID: ");
        String clubId = scanner.nextLine();

        System.out.print("활동 이름: ");
        String activityName = scanner.nextLine();

        Activity existingActivity = activityService.getActivity(clubId, activityName);
        if (existingActivity == null) {
            System.out.println("존재하지 않는 활동입니다.");
            return;
        }

        System.out.print("새로운 활동 날짜 (YYYY-MM-DD) (엔터시 기존값 유지): ");
        String dateStr = scanner.nextLine();
        if (!dateStr.trim().isEmpty()) {
            LocalDate newDate = LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE);
            existingActivity.setActivityDate(newDate);
        }

        System.out.print("새로운 수상 여부 (true/false) (엔터시 기존값 유지): ");
        String hasAwardStr = scanner.nextLine();
        if (!hasAwardStr.trim().isEmpty()) {
            boolean hasAward = Boolean.parseBoolean(hasAwardStr);
            existingActivity.setHasAward(hasAward);
        }

        boolean success = activityService.updateActivity(existingActivity);
        if (success) {
            System.out.println("주요활동이 성공적으로 수정되었습니다.");
        } else {
            System.out.println("주요활동 수정에 실패했습니다.");
        }
    }

    private void handleDeleteActivity() throws Exception {
        System.out.print("동아리 ID: ");
        String clubId = scanner.nextLine();

        System.out.print("활동 이름: ");
        String activityName = scanner.nextLine();

        boolean success = activityService.deleteActivity(clubId, activityName);
        if (success) {
            System.out.println("주요활동이 성공적으로 삭제되었습니다.");
        } else {
            System.out.println("주요활동 삭제에 실패했습니다.");
        }
    }

    private void handleAddActivityParticipant() throws Exception {
        System.out.print("동아리 ID: ");
        String clubId = scanner.nextLine();

        System.out.print("활동 이름: ");
        String activityName = scanner.nextLine();

        System.out.print("참여할 학생 ID: ");
        String studentId = scanner.nextLine();

        ActivityParticipant participant = new ActivityParticipant(studentId, clubId, activityName);

        boolean success = activityService.addActivityParticipant(participant);
        if (success) {
            System.out.println("활동 참여자가 성공적으로 추가되었습니다.");
        } else {
            System.out.println("활동 참여자 추가에 실패했습니다.");
        }
    }

    private void handleDeleteActivityParticipant() throws Exception {
        System.out.print("동아리 ID: ");
        String clubId = scanner.nextLine();

        System.out.print("활동 이름: ");
        String activityName = scanner.nextLine();

        System.out.print("삭제할 학생 ID: ");
        String studentId = scanner.nextLine();

        ActivityParticipant participant = new ActivityParticipant(studentId, clubId, activityName);

        boolean success = activityService.deleteActivityParticipant(participant);
        if (success) {
            System.out.println("활동 참여자가 성공적으로 삭제되었습니다.");
        } else {
            System.out.println("활동 참여자 삭제에 실패했습니다.");
        }
    }

    private void handleViewClubActivities() throws Exception {
        System.out.print("동아리 ID: ");
        String clubId = scanner.nextLine();

        List<Activity> activities = activityService.getClubActivities(clubId);
        if (activities.isEmpty()) {
            System.out.println("해당 동아리의 활동이 없습니다.");
            return;
        }

        System.out.println("\n=== 동아리 활동 목록 ===");
        for (Activity activity : activities) {
            displayActivity(activity);
        }
    }

    private void handleViewAllActivities() throws Exception {
        List<Activity> activities = activityService.getAllActivities();
        if (activities.isEmpty()) {
            System.out.println("등록된 활동이 없습니다.");
            return;
        }

        System.out.println("\n=== 전체 활동 목록 ===");
        for (Activity activity : activities) {
            displayActivity(activity);
            System.out.println("--------------------");
        }
    }

    private void handleViewActivityParticipants() throws Exception {
        System.out.print("동아리 ID: ");
        String clubId = scanner.nextLine();

        System.out.print("활동 이름: ");
        String activityName = scanner.nextLine();

        List<String> participants = activityService.getActivityParticipants(clubId, activityName);
        if (participants.isEmpty()) {
            System.out.println("활동 참여자가 없습니다.");
            return;
        }

        System.out.println("\n=== 활동 참여자 목록 ===");
        for (String participant : participants) {
            System.out.println(participant);
        }
    }

    private void handleUpdateActivityAward() throws Exception {
        System.out.print("동아리 ID: ");
        String clubId = scanner.nextLine();

        System.out.print("활동 이름: ");
        String activityName = scanner.nextLine();

        System.out.print("수상 여부 (true/false): ");
        boolean hasAward = scanner.nextBoolean();
        scanner.nextLine(); // 버퍼 비우기

        boolean success = activityService.updateActivityAward(clubId, activityName, hasAward);
        if (success) {
            System.out.println("수상 여부가 성공적으로 변경되었습니다.");
        } else {
            System.out.println("수상 여부 변경에 실패했습니다.");
        }
    }

    private void displayActivity(Activity activity) {
        System.out.printf("\n동아리: %s\n", activity.getClubId());
        System.out.printf("활동명: %s\n", activity.getActivityName());
        System.out.printf("활동일: %s\n", activity.getActivityDate());
        System.out.printf("참여 인원: %d명\n", activity.getMemberCount());
        System.out.printf("수상 여부: %s\n", activity.isHasAward() ? "O" : "X");
    }
}