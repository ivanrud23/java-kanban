import model.Epic;
import model.Subtask;
import model.Task;
import service.Manager;

public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();

        manager.createTask("Task_1", "Desk_task_1");
        manager.createTask("Task_2", "Desk_task_2");
        manager.createEpic("Epic_1", "Desk_Epic_1");
        manager.createEpic("Epic_2", "Desk_Epic_2");
        manager.createSubTask("Sub_1", "Desk_Sub_1", 3);
        manager.createSubTask("Sub_2", "Desk_Sub_2", 3);
        manager.createSubTask("Sub_3", "Desk_Sub_3", 4);

        manager.printAll();
        System.out.println("****************************************************");

        Task newTask = new Task("Task_1_change", "Desk_task_1", 1, "IN_PROGRESS");
        manager.updateTask(1, newTask);
        Subtask newSubTask = new Subtask("Sub_1_change", "Desk_Sub_1_change", 3, "IN_PROGRESS", 3);
        manager.updateSubTask(5, newSubTask);
        Subtask newSubTask2 = new Subtask("Sub_2_change", "Desk_Sub_2_change", 3, "DONE", 4);
        manager.updateSubTask(7, newSubTask2);
        Epic newEpic = new Epic("Epic_1_change", "Desk_Epic_1_change", 12);
        manager.updateEpic(3, newEpic);

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
