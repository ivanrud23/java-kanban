package service;

import model.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {

    InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();


    @Test
    void add() throws IOException {
        Task newTask =  new Task("Task_1", "Desk_task_1", 1);
        inMemoryTaskManager.inMemoryHistoryManager.add(newTask);
        assertEquals(1, inMemoryTaskManager.inMemoryHistoryManager.getHistory().size());
    }
    @Test
    void remove() {
        Task newTask =  new Task("Task_1", "Desk_task_1", 1);
        inMemoryTaskManager.inMemoryHistoryManager.add(newTask);
        inMemoryTaskManager.inMemoryHistoryManager.remove(1);
        assertEquals(new ArrayList<>(), inMemoryTaskManager.inMemoryHistoryManager.getHistory());
    }
    @Test
    void emptyHistoryTest() {
        List<Task> history = new ArrayList<>();
        assertEquals(history, inMemoryTaskManager.inMemoryHistoryManager.getHistory());
    }
    @Test
    void doubleTaskTest() throws IOException {
        Task task1 =  new Task("Task_1", "Desk_task_1", 1);
        Task task2 =  new Task("Task_2", "Desk_task_2", 2);
        Task task3 =  new Task("Task_3", "Desk_task_3", 3);

        inMemoryTaskManager.createTask(new Task("Task_1", "Desk_task_1", inMemoryTaskManager.idCounter()));
        inMemoryTaskManager.createTask(new Task("Task_2", "Desk_task_2", inMemoryTaskManager.idCounter()));
        inMemoryTaskManager.createTask(new Task("Task_3", "Desk_task_3", inMemoryTaskManager.idCounter()));
        inMemoryTaskManager.getById(1);
        inMemoryTaskManager.getById(2);
        inMemoryTaskManager.getById(3);
        inMemoryTaskManager.getById(2);
        inMemoryTaskManager.getById(1);
        inMemoryTaskManager.getById(1);
        inMemoryTaskManager.getById(1);
        inMemoryTaskManager.getById(3);

        List<Task> history = new ArrayList<>(List.of(task2, task1, task3));
        assertEquals(history, inMemoryTaskManager.inMemoryHistoryManager.getHistory());
    }
    @Test
    void deleteFromStartHistory() throws IOException {
        Task task1 =  new Task("Task_1", "Desk_task_1", 1);
        Task task2 =  new Task("Task_2", "Desk_task_2", 2);
        Task task3 =  new Task("Task_3", "Desk_task_3", 3);

        inMemoryTaskManager.createTask(new Task("Task_1", "Desk_task_1", inMemoryTaskManager.idCounter()));
        inMemoryTaskManager.createTask(new Task("Task_2", "Desk_task_2", inMemoryTaskManager.idCounter()));
        inMemoryTaskManager.createTask(new Task("Task_3", "Desk_task_3", inMemoryTaskManager.idCounter()));
        inMemoryTaskManager.getById(1);
        inMemoryTaskManager.getById(2);
        inMemoryTaskManager.getById(3);
        inMemoryTaskManager.getById(2);
        inMemoryTaskManager.getById(1);
        inMemoryTaskManager.getById(1);
        inMemoryTaskManager.getById(1);
        inMemoryTaskManager.getById(3);
        inMemoryTaskManager.removeTask(2);

        List<Task> history = new ArrayList<>(List.of(task1, task3));
        assertEquals(history, inMemoryTaskManager.inMemoryHistoryManager.getHistory());
    }
    @Test
    void deleteFromMiddleHistory() throws IOException {
        Task task1 =  new Task("Task_1", "Desk_task_1", 1);
        Task task2 =  new Task("Task_2", "Desk_task_2", 2);
        Task task3 =  new Task("Task_3", "Desk_task_3", 3);

        inMemoryTaskManager.createTask(new Task("Task_1", "Desk_task_1", inMemoryTaskManager.idCounter()));
        inMemoryTaskManager.createTask(new Task("Task_2", "Desk_task_2", inMemoryTaskManager.idCounter()));
        inMemoryTaskManager.createTask(new Task("Task_3", "Desk_task_3", inMemoryTaskManager.idCounter()));
        inMemoryTaskManager.getById(1);
        inMemoryTaskManager.getById(2);
        inMemoryTaskManager.getById(3);
        inMemoryTaskManager.getById(2);
        inMemoryTaskManager.getById(1);
        inMemoryTaskManager.getById(1);
        inMemoryTaskManager.getById(1);
        inMemoryTaskManager.getById(3);
        inMemoryTaskManager.removeTask(1);

        List<Task> history = new ArrayList<>(List.of(task2, task3));
        assertEquals(history, inMemoryTaskManager.inMemoryHistoryManager.getHistory());
    }
    @Test
    void deleteFromEndHistory() throws IOException {
        Task task1 =  new Task("Task_1", "Desk_task_1", 1);
        Task task2 =  new Task("Task_2", "Desk_task_2", 2);
        Task task3 =  new Task("Task_3", "Desk_task_3", 3);

        inMemoryTaskManager.createTask(new Task("Task_1", "Desk_task_1", inMemoryTaskManager.idCounter()));
        inMemoryTaskManager.createTask(new Task("Task_2", "Desk_task_2", inMemoryTaskManager.idCounter()));
        inMemoryTaskManager.createTask(new Task("Task_3", "Desk_task_3", inMemoryTaskManager.idCounter()));
        inMemoryTaskManager.getById(1);
        inMemoryTaskManager.getById(2);
        inMemoryTaskManager.getById(3);
        inMemoryTaskManager.getById(2);
        inMemoryTaskManager.getById(1);
        inMemoryTaskManager.getById(1);
        inMemoryTaskManager.getById(1);
        inMemoryTaskManager.getById(3);
        inMemoryTaskManager.removeTask(3);

        List<Task> history = new ArrayList<>(List.of(task2, task1));
        assertEquals(history, inMemoryTaskManager.inMemoryHistoryManager.getHistory());
    }

    @Test
    void addHistoryTest() {
        Task task1 =  new Task("Task_1", "Desk_task_1", 1);
        Task task2 =  new Task("Task_1", "Desk_task_1", 2);
        inMemoryTaskManager.inMemoryHistoryManager.addHistory(task1);
        inMemoryTaskManager.inMemoryHistoryManager.addHistory(task2);
        assertEquals(2, inMemoryTaskManager.inMemoryHistoryManager.getHistory().size());
    }

    @Test
    void removeFromHistory() {
        Task task1 =  new Task("Task_1", "Desk_task_1", 1);
        Task task2 =  new Task("Task_1", "Desk_task_1", 2);
        inMemoryTaskManager.inMemoryHistoryManager.addHistory(task1);
        inMemoryTaskManager.inMemoryHistoryManager.addHistory(task2);
        inMemoryTaskManager.inMemoryHistoryManager.remove(2);
        assertEquals(1, inMemoryTaskManager.inMemoryHistoryManager.getHistory().size());
    }

}