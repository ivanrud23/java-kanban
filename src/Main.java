import service.InMemoryTaskManager;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();

        inMemoryTaskManager.createTask("Task_1", "Desk_task_1");
        inMemoryTaskManager.createTask("Task_2", "Desk_task_2");
        inMemoryTaskManager.createEpic("Epic_1", "Desk_Epic_1");
        inMemoryTaskManager.createEpic("Epic_2", "Desk_Epic_2");
        inMemoryTaskManager.createSubTask("Sub_1", "Desk_Sub_1", 3);
        inMemoryTaskManager.createSubTask("Sub_2", "Desk_Sub_2", 3);
        inMemoryTaskManager.createSubTask("Sub_3", "Desk_Sub_3", 3);

        inMemoryTaskManager.getById(6);
        inMemoryTaskManager.getById(5);
        inMemoryTaskManager.getById(3);
        inMemoryTaskManager.getById(4);
        inMemoryTaskManager.getById(1);
        inMemoryTaskManager.getById(2);
        inMemoryTaskManager.getById(7);
        inMemoryTaskManager.getById(5);
        inMemoryTaskManager.getById(6);
        inMemoryTaskManager.getById(3);
        inMemoryTaskManager.getById(4);
        inMemoryTaskManager.getById(1);
        inMemoryTaskManager.getById(7);
        inMemoryTaskManager.getById(2);
        inMemoryTaskManager.getById(3);
        inMemoryTaskManager.getById(4);
        inMemoryTaskManager.getById(5);
        inMemoryTaskManager.getById(2);
        inMemoryTaskManager.getById(6);
        inMemoryTaskManager.getById(4);
        inMemoryTaskManager.getById(1);
        inMemoryTaskManager.getById(6);
        inMemoryTaskManager.getById(5);
        inMemoryTaskManager.getById(3);
        inMemoryTaskManager.getById(4);
        inMemoryTaskManager.getById(1);
        inMemoryTaskManager.getById(2);
        inMemoryTaskManager.getById(7);

        inMemoryTaskManager.removeTask(2);
        inMemoryTaskManager.removeTask(3);



    }
}
