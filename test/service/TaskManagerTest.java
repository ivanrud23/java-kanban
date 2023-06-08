package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

abstract class TaskManagerTest<T extends TaskManager> {

    T taskManager;

    @Test
    void getByIdTest() throws IOException {

        assertThrows(
                IOException.class,
                () -> taskManager.getById(1));

        taskManager.createTask(new Task("Task_1", "Desk_task_1"));
        taskManager.createEpic(new Epic("Epic_1", "Desk_Epic_1"));
        taskManager.createSubTask(new Subtask("Sub_1", "Desk_Sub_1", 2));

        assertEquals(new Task("Task_1", "Desk_task_1", 1), taskManager.getById(1));
        List<Integer> children = new ArrayList<>(List.of(3));
        Epic epic = new Epic("Epic_1", "Desk_Epic_1", 2, Status.NEW);
        epic.setChildren(children);
        assertEquals(epic, taskManager.getById(2));
        assertEquals(new Subtask("Sub_1", "Desk_Sub_1", 3, Status.NEW, 2), taskManager.getById(3));

        assertThrows(
                IOException.class,
                () -> taskManager.getById(5));
    }

    @Test
    void getTaskStorageTest() throws IOException {
        taskManager.createTask(new Task("Task_1", "Desk_task_1"));
        Task task = new Task("Task_1", "Desk_task_1", 1);
        List<Task> taskList = new ArrayList<>(List.of(task));
        assertEquals(taskList, taskManager.getTaskStorage());
    }

    @Test
    void getSubTaskStorageTest() throws IOException {
        taskManager.createEpic(new Epic("Epic_1", "Desk_Epic_1"));
        taskManager.createSubTask(new Subtask("Sub_1", "Desk_Sub_1", 1));

        Subtask subtask = new Subtask("Sub_1", "Desk_Sub_1", 2, Status.NEW, 1);
        List<Task> subTaskList = new ArrayList<>(List.of(subtask));
        assertEquals(subTaskList, taskManager.getSubTaskStorage());
    }

    @Test
    void getEpicStorageTest() throws IOException {
        taskManager.createEpic(new Epic("Epic_1", "Desk_Epic_1"));
        taskManager.createSubTask(new Subtask("Sub_2", "Desk_Sub_2", 1));

        Epic epic = new Epic("Epic_1", "Desk_Epic_1", 1, Status.NEW);
        List<Integer> children = new ArrayList<>(List.of(2));
        epic.setChildren(children);
        List<Task> epicList = new ArrayList<>(List.of(epic));
        assertEquals(epicList, taskManager.getEpicStorage());
    }

    @Test
    void createTask() throws IOException {
        taskManager.createTask(new Task("Task_1", "Desk_task_1"));
        assertEquals(new Task("Task_1", "Desk_task_1", 1), taskManager.getById(1));
    }

    @Test
    void createTaskFromTask() throws IOException {
        taskManager.createTask(new Task("Task_1", "Desk_task_1", 1));
        assertEquals(new Task("Task_1", "Desk_task_1", 1), taskManager.getById(1));
    }

    @Test
    void createTaskWithTime() throws IOException {
        taskManager.createTask(new Task("Task_1", "Desk_task_1", "01.05.2023 10:00", "PT10M"));
        assertEquals(new Task("Task_1", "Desk_task_1", 1, Status.NEW, "01.05.2023 10:00", "PT10M"), taskManager.getById(1));
    }

    @Test
    void createSubtask() throws IOException {
        taskManager.createEpic(new Epic("Epic_1", "Desk_Epic_1"));
        taskManager.createSubTask(new Subtask("Sub_1", "Desk_Sub_1", 1));
        assertEquals(new Subtask("Sub_1", "Desk_Sub_1", 2, Status.NEW, 1), taskManager.getById(2));
    }

    @Test
    void createSubtaskFromSubtask() throws IOException {
        taskManager.createEpic(new Epic("Epic_1", "Desk_Epic_1"));
        taskManager.createSubTask(new Subtask("Sub_1", "Desk_Sub_1", 2, Status.NEW, 1));
        assertEquals(new Subtask("Sub_1", "Desk_Sub_1", 2, Status.NEW, 1), taskManager.getById(2));
    }

    @Test
    void createSubtaskWithTime() throws IOException {
        taskManager.createEpic(new Epic("Epic_1", "Desk_Epic_1"));
        taskManager.createSubTask(new Subtask("Sub_1", "Desk_Sub_1",
                "01.05.2023 10:00", "PT10M", 1));
        assertEquals(new Subtask("Sub_1", "Desk_Sub_1", 2, Status.NEW, "01.05.2023 10:00", "PT10M", 1), taskManager.getById(2));
    }

    @Test
    void epicGetSubtaskTime() throws IOException {
        taskManager.createEpic(new Epic("Epic_1", "Desk_Epic_1"));
        taskManager.createSubTask(new Subtask("Sub_1", "Desk_Sub_1",
                "01.05.2023 10:00", "PT10M", 1));
        List<Integer> children = new ArrayList<>(List.of(2));
        assertEquals(new Epic("Epic_1", "Desk_Epic_1", 1, Status.NEW, "01.05.2023 10:00", "PT10M", children), taskManager.getById(1));
    }

    @Test
    void epicCalculateDuration() throws IOException {
        taskManager.createEpic(new Epic("Epic_1", "Desk_Epic_1"));
        taskManager.createSubTask(new Subtask("Sub_1", "Desk_Sub_1",
                "01.05.2023 10:00", "PT10M", 1));
        taskManager.createSubTask(new Subtask("Sub_1", "Desk_Sub_1",
                "01.05.2023 10:00", "PT10M", 1));
        taskManager.createSubTask(new Subtask("Sub_1", "Desk_Sub_1",
                "01.05.2023 10:00", "PT10M", 1));
        List<Integer> children = new ArrayList<>(List.of(2, 3, 4));
        assertEquals(new Epic("Epic_1", "Desk_Epic_1", 1, Status.NEW, "01.05.2023 10:00", "PT10M", children), taskManager.getById(1));
    }

    @Test
    void checkTimeIntersections() throws IOException {
        taskManager.createEpic(new Epic("Epic_1", "Desk_Epic_1"));
        taskManager.createSubTask(new Subtask("Sub_1", "Desk_Sub_1",
                "01.05.2023 10:00", "PT10M", 1));
        taskManager.createSubTask(new Subtask("Sub_1", "Desk_Sub_1",
                "01.05.2023 10:00", "PT10M", 1));
        taskManager.createSubTask(new Subtask("Sub_1", "Desk_Sub_1",
                "01.05.2023 10:00", "PT10M", 1));
        assertEquals(new Subtask("Sub_1", "Desk_Sub_1", 2, Status.NEW, "01.05.2023 10:30", "PT10M", 1), taskManager.getById(2));
    }

    @Test
    void createEpic() throws IOException {
        taskManager.createEpic(new Epic("Epic_1", "Desk_Epic_1"));
        assertEquals(new Epic("Epic_1", "Desk_Epic_1", 1, Status.NEW), taskManager.getById(1));
    }

    @Test
    void createEpicFromEpic() throws IOException {
        taskManager.createEpic(new Epic("Epic_1", "Desk_Epic_1", 1, Status.NEW));
        assertEquals(new Epic("Epic_1", "Desk_Epic_1", 1, Status.NEW), taskManager.getById(1));
    }

    @Test
    void idCounterPlus() throws IOException {
        taskManager.createTask(new Task("Task_1", "Desk_task_1"));
        taskManager.createTask(new Task("Task_2", "Desk_task_2"));
        taskManager.createTask(new Task("Task_3", "Desk_task_3"));
        assertEquals(3, taskManager.getById(3).getId());
    }

    @Test
    void updateTask() throws IOException {
        taskManager.createTask(new Task("Task_1", "Desk_task_1"));
        taskManager.updateTask(1, new Task("Task_1_update", "Task_1_Description_update", 1));
        Task task = new Task("Task_1_update", "Task_1_Description_update", 1);
        assertEquals(task, taskManager.getById(1));
    }

    @Test
    void updateSubTask() throws IOException {
        taskManager.createEpic(new Epic("Epic_1", "Desk_Epic_1"));
        taskManager.createSubTask(new Subtask("Sub_1", "Desk_Sub_1", 1));
        taskManager.updateSubTask(2, new Subtask("Subtask_1_update", "Subtask_1_Description_update", 2, 1));
        Subtask subtask = new Subtask("Subtask_1_update", "Subtask_1_Description_update", 2, 1);
        assertEquals(subtask, taskManager.getById(2));
    }

    @Test
    void updateEpic() throws IOException {
        taskManager.createEpic(new Epic("Epic_1", "Desk_Epic_1"));
        taskManager.updateEpic(1, new Epic("Epic_1_update", "Desk_Epic_1_update", 1));
        Epic epic = new Epic("Epic_1_update", "Desk_Epic_1_update", 1);
        assertEquals(epic, taskManager.getById(1));
    }

    @Test
    void addSubTaskToEpic() throws IOException {
        taskManager.createEpic(new Epic("Epic_1", "Desk_Epic_1"));
        taskManager.createSubTask(new Subtask("Sub_1", "Desk_Sub_1", 1));
        taskManager.updateEpic(1, new Epic("Epic_1_update", "Desk_Epic_1_update", 1));
        List<Integer> children = new ArrayList<>(List.of(2));
        Epic epic = new Epic("Epic_1_update", "Desk_Epic_1_update", 1, Status.NEW, children);
        epic.setChildren(children);
        assertEquals(epic.getChildren(), ((Epic) taskManager.getById(1)).getChildren());
    }


    @Test
    void removeTask() throws IOException {
        taskManager.createTask(new Task("Task_1", "Desk_task_1"));
        taskManager.getById(1);
        taskManager.removeTask(1);
        assertThrows(
                IOException.class,
                () -> taskManager.getById(1));
    }

    @Test
    void clearSubtask() throws IOException {
        taskManager.createEpic(new Epic("Epic_1", "Desk_Epic_1"));
        taskManager.createSubTask(new Subtask("Sub_1", "Desk_Sub_1", 1));
        taskManager.createSubTask(new Subtask("Sub_2", "Desk_Sub_2", 1));
        taskManager.createSubTask(new Subtask("Sub_3", "Desk_Sub_3", 1));
        taskManager.clearSubtask();

        Epic epic = new Epic("Epic_1", "Desk_Epic_1", 1, Status.NEW);
        List<Integer> children = new ArrayList<>();
        epic.setChildren(children);
        assertEquals(epic.getChildren(), ((Epic) taskManager.getById(1)).getChildren());
    }


    @Test
    void clearTask() throws IOException {
        taskManager.createTask(new Task("Task_1", "Desk_task_1"));
        taskManager.createTask(new Task("Task_2", "Desk_task_2"));
        taskManager.createTask(new Task("Task_3", "Desk_task_3"));
        taskManager.clearTask();
        List<Task> taskStorage = new ArrayList<>();
        assertEquals(taskStorage, taskManager.getTaskStorage());
    }

    @Test
    void clearEpic() throws IOException {
        taskManager.createEpic(new Epic("Epic_1", "Desk_Epic_1"));
        taskManager.createEpic(new Epic("Epic_2", "Desk_Epic_2"));
        taskManager.createEpic(new Epic("Epic_3", "Desk_Epic_3"));
        taskManager.clearEpic();
        List<Task> taskStorage = new ArrayList<>();
        assertEquals(taskStorage, taskManager.getEpicStorage());
    }

    @Test
    void checkEpicStatus() throws IOException {
        taskManager.createEpic(new Epic("Epic_1", "Desk_Epic_1"));
        taskManager.createSubTask(new Subtask("Sub_1", "Desk_Sub_1", 1));
        taskManager.createSubTask(new Subtask("Sub_2", "Desk_Sub_2", 1));

        taskManager.updateSubTask(2, new Subtask("Subtask_1", "Desk_Subtask_1", 2, Status.IN_PROGRESS, 1));
        assertEquals(Status.IN_PROGRESS, taskManager.getById(1).getStatus());

        taskManager.updateSubTask(2, new Subtask("Subtask_1", "Desk_Subtask_1", 2, Status.DONE, 1));
        taskManager.updateSubTask(3, new Subtask("Subtask_2", "Desk_Subtask_2", 3, Status.DONE, 1));
        assertEquals(Status.DONE, taskManager.getById(1).getStatus());
    }
}