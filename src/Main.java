import service.Manager;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            menu();
            Integer command = scanUserInputSafely(scanner);

            switch (command) {
                case -1:
                    System.out.println("Введена несуществующая команда");
                    break;
                case 1:
                    scanner.nextLine();
                    System.out.println("Введите название задачи");
                    String nameTask = scanner.nextLine();
                    System.out.println("Введите описание задачи");
                    String descriptionTask = scanner.nextLine();
                    manager.createTask(nameTask, descriptionTask);
                    break;
                case 2:
                    scanner.nextLine();
                    System.out.println("Введите название эпика");
                    String nameEpic = scanner.nextLine();
                    System.out.println("Введите описание эпика");
                    String descriptionEpic = scanner.nextLine();
                    manager.createEpic(nameEpic, descriptionEpic);
                    break;
                case 3:
                    scanner.nextLine();
                    System.out.println("Введите название подзадачи");
                    String nameSub = scanner.nextLine();
                    System.out.println("Введите описание подзадачи");
                    String descriptionSub = scanner.nextLine();
                    System.out.println("Введите номер эпика");
                    Integer parentId = scanUserInputSafely(scanner);
                    manager.createSubTask(nameSub, descriptionSub, parentId);
                    break;
                case 4:
                    System.out.println("Введите номер задачи");
                    Integer taskId = scanUserInputSafely(scanner);
                    if ((Boolean) manager.checkId(taskId)) {
                        manager.changeTask(taskId);
                    } else {
                        break;
                    }
                case 5:
                    manager.printAll();
                    break;
                case 6:
                    manager.printTask();
                    break;
                case 7:
                    manager.printEpic();
                    break;
                case 8:
                    manager.printSubtask();
                    break;
                case 9:
                    System.out.println("Введите номер задачи");
                    Integer removeTask = scanUserInputSafely(scanner);
                    manager.removeTask(removeTask);
                    break;
                case 10:
                    manager.clearTask();
                    break;
                case 11:
                    manager.clearEpic();
                    break;
                case 12:
                    manager.clearSubtask();
                    break;
            }
        }
    }

    public static void menu() {
        System.out.println();
        System.out.println("Введите номер команды");
        System.out.println("1. - Создание задачи");
        System.out.println("2. - Создание эпика");
        System.out.println("3. - Создание подзадачи");
        System.out.println("4. - Изменение задачи");
        System.out.println("5. - Посмотреть спсиок всех задач");
        System.out.println("6. - Посмотреть спсиок задач");
        System.out.println("7. - Посмотреть спсиок эпиков");
        System.out.println("8. - Посмотреть спсиок подзадач");
        System.out.println("9. - Удаление задачи");
        System.out.println("10. - Очистить список задач");
        System.out.println("11. - Очистить список эпиков");
        System.out.println("12. - Очистить список подзадач");
        System.out.println();
    }

    public static int scanUserInputSafely(Scanner scanner) {
        try {
            return scanner.nextInt();
        } catch (InputMismatchException ime) {
            scanner.nextLine();
            return -1;
        }
    }
}
