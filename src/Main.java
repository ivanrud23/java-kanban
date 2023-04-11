import model.Epic;
import model.Subtask;
import model.Task;
import service.InMemoryHistoryManager;
import service.InMemoryTaskManager;
import model.Status;


public class Main {

    public static void main(String[] args) {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();

        inMemoryTaskManager.createTask("Task_1", "Desk_task_1");
        inMemoryTaskManager.createTask("Task_2", "Desk_task_2");
        inMemoryTaskManager.createEpic("Epic_1", "Desk_Epic_1");
        inMemoryTaskManager.createEpic("Epic_2", "Desk_Epic_2");
        inMemoryTaskManager.createSubTask("Sub_1", "Desk_Sub_1", 3);
        inMemoryTaskManager.createSubTask("Sub_2", "Desk_Sub_2", 3);
        inMemoryTaskManager.createSubTask("Sub_3", "Desk_Sub_3", 4);

        inMemoryTaskManager.printAll();
        inMemoryTaskManager.getTaskStorage();
        inMemoryHistoryManager.getHistory();
        System.out.println("****************************************************");

        Task newTask = new Task("Task_1_change", "Desk_task_1", 1, Status.IN_PROGRESS);
        inMemoryTaskManager.updateTask(1, newTask);
        Subtask newSubTask = new Subtask("Sub_1_change", "Desk_Sub_1_change", 3, Status.IN_PROGRESS, 3);
        inMemoryTaskManager.updateSubTask(5, newSubTask);
        Subtask newSubTask2 = new Subtask("Sub_2_change", "Desk_Sub_2_change", 3, Status.DONE, 4);
        inMemoryTaskManager.updateSubTask(7, newSubTask2);
        Epic newEpic = new Epic("Epic_1_change", "Desk_Epic_1_change", 12);
        inMemoryTaskManager.updateEpic(3, newEpic);

        inMemoryTaskManager.printAll();
        inMemoryTaskManager.getSubTaskStorage();
        inMemoryTaskManager.getEpicStorage();
        inMemoryTaskManager.getTaskStorage();
        inMemoryHistoryManager.getHistory();
        System.out.println("****************************************************");

        inMemoryTaskManager.removeTask(2);
        inMemoryTaskManager.removeTask(4);
        inMemoryTaskManager.removeTask(5);

        inMemoryTaskManager.printAll();
        inMemoryTaskManager.getEpicStorage();
        inMemoryHistoryManager.getHistory();
        System.out.println("****************************************************");

    }
}
