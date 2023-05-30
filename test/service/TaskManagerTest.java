package service;

import model.Task;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    TaskManager taskManager;

    @Test
    void getById() throws IOException {
        taskManager.createTask("Task_1", "Desk_task_1");
        assertEquals(new Task("Task_1", "Desk_task_1", 1), taskManager.getById(1));
    }

    @Test
    void getTaskStorage() {
    }

    @Test
    void getSubTaskStorage() {
    }

    @Test
    void getEpicStorage() {
    }

    @Test
    void createTask() {
    }

    @Test
    void testCreateTask() {
    }

    @Test
    void createSubTask() {
    }

    @Test
    void testCreateSubTask() {
    }

    @Test
    void createEpic() {
    }

    @Test
    void testCreateEpic() {
    }

    @Test
    void idCounterPlus() {
    }

    @Test
    void printAll() {
    }

    @Test
    void printTask() {
    }

    @Test
    void printSubtask() {
    }

    @Test
    void printEpic() {
    }

    @Test
    void updateTask() {
    }

    @Test
    void updateSubTask() {
    }

    @Test
    void updateEpic() {
    }

    @Test
    void addSubTaskToEpic() {
    }

    @Test
    void removeTask() {
    }

    @Test
    void clearSubtask() {
    }

    @Test
    void clearTask() {
    }

    @Test
    void clearEpic() {
    }

    @Test
    void checkEpicStatus() {
    }
}