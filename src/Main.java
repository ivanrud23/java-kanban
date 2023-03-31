import model.Subtask;
import model.Task;
import service.Manager;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();
        Scanner scanner = new Scanner(System.in);

        manager.createTask("Task_1", "Desk_task_1");
        manager.createTask("Task_2", "Desk_task_2");
        manager.createEpic("Epic_1", "Desk_Epic_1");
        manager.createEpic("Epic_2", "Desk_Epic_2");
        manager.createSubTask("Sub_1", "Desk_Sub_1", 3);
        manager.createSubTask("Sub_2", "Desk_Sub_2", 3);
        manager.createSubTask("Sub_3", "Desk_Sub_3", 4);

        manager.printAll();
        System.out.println("****************************************************");

        Task newTask = new Task("Task_1_change", "Desk_task_1", 1);
        manager.updateTask(1, newTask);
        Subtask newSubTask = new Subtask("Sub_1_change", "Desk_Sub_1_change", 3, 3);
        manager.updateSubTask(5, newSubTask);
        Subtask newSubTask2 = new Subtask("Sub_2_change", "Desk_Sub_2_change", 3, 4);
        manager.updateSubTask(7, newSubTask2);

        manager.printAll();
        System.out.println("****************************************************");

        manager.removeTask(2);
        manager.removeTask(4);
        manager.removeTask(5);

        manager.printAll();
        System.out.println("****************************************************");

        manager.clearTask();
        manager.clearSubtask();
        manager.clearEpic();
        manager.printAll();

    }
}
