package service;

import managers.InMemoryTaskManager;
import model.Task;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HistoryManagerTest {

    InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();


    @Test
    void add() throws IOException {
        Task newTask = new Task("Task_1", "Desk_task_1", 1);
        inMemoryTaskManager.getInMemoryHistoryManager().add(newTask);
        assertEquals(1, inMemoryTaskManager.getInMemoryHistoryManager().getHistory().size());
    }

    @Test
    void remove() {
        Task newTask = new Task("Task_1", "Desk_task_1", 1);
        inMemoryTaskManager.getInMemoryHistoryManager().add(newTask);
        inMemoryTaskManager.getInMemoryHistoryManager().remove(1);
        assertEquals(new ArrayList<>(), inMemoryTaskManager.getInMemoryHistoryManager().getHistory());
    }

    @Test
    void emptyHistoryTest() {
        List<Task> history = new ArrayList<>();
        assertEquals(history, inMemoryTaskManager.getInMemoryHistoryManager().getHistory());
    }

    @Test
    void doubleTaskTest() throws IOException, InterruptedException {
        Task task1 = new Task("Task_1", "Desk_task_1", 1);
        Task task2 = new Task("Task_2", "Desk_task_2", 2);
        Task task3 = new Task("Task_3", "Desk_task_3", 3);

        inMemoryTaskManager.createTask(new Task("Task_1", "Desk_task_1"));
        inMemoryTaskManager.createTask(new Task("Task_2", "Desk_task_2"));
        inMemoryTaskManager.createTask(new Task("Task_3", "Desk_task_3"));
        inMemoryTaskManager.getById(1);
        inMemoryTaskManager.getById(2);
        inMemoryTaskManager.getById(3);
        inMemoryTaskManager.getById(2);
        inMemoryTaskManager.getById(1);
        inMemoryTaskManager.getById(1);
        inMemoryTaskManager.getById(1);
        inMemoryTaskManager.getById(3);

        List<Task> history = new ArrayList<>(List.of(task2, task1, task3));
        assertEquals(history, inMemoryTaskManager.getInMemoryHistoryManager().getHistory());
    }

    @Test
    void deleteFromStartHistory() throws IOException, InterruptedException {
        Task task1 = new Task("Task_1", "Desk_task_1", 1);
        Task task2 = new Task("Task_2", "Desk_task_2", 2);
        Task task3 = new Task("Task_3", "Desk_task_3", 3);

        inMemoryTaskManager.createTask(new Task("Task_1", "Desk_task_1"));
        inMemoryTaskManager.createTask(new Task("Task_2", "Desk_task_2"));
        inMemoryTaskManager.createTask(new Task("Task_3", "Desk_task_3"));
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
        assertEquals(history, inMemoryTaskManager.getInMemoryHistoryManager().getHistory());
    }

    @Test
    void deleteFromMiddleHistory() throws IOException, InterruptedException {
        Task task1 = new Task("Task_1", "Desk_task_1", 1);
        Task task2 = new Task("Task_2", "Desk_task_2", 2);
        Task task3 = new Task("Task_3", "Desk_task_3", 3);

        inMemoryTaskManager.createTask(new Task("Task_1", "Desk_task_1"));
        inMemoryTaskManager.createTask(new Task("Task_2", "Desk_task_2"));
        inMemoryTaskManager.createTask(new Task("Task_3", "Desk_task_3"));
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
        assertEquals(history, inMemoryTaskManager.getInMemoryHistoryManager().getHistory());
    }

    @Test
    void deleteFromEndHistory() throws IOException, InterruptedException {
        Task task1 = new Task("Task_1", "Desk_task_1", 1);
        Task task2 = new Task("Task_2", "Desk_task_2", 2);
        Task task3 = new Task("Task_3", "Desk_task_3", 3);

        inMemoryTaskManager.createTask(new Task("Task_1", "Desk_task_1"));
        inMemoryTaskManager.createTask(new Task("Task_2", "Desk_task_2"));
        inMemoryTaskManager.createTask(new Task("Task_3", "Desk_task_3"));
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
        assertEquals(history, inMemoryTaskManager.getInMemoryHistoryManager().getHistory());
    }

    @Test
    void addHistoryTest() {
        Task task1 = new Task("Task_1", "Desk_task_1", 1);
        Task task2 = new Task("Task_1", "Desk_task_1", 2);
        inMemoryTaskManager.getInMemoryHistoryManager().addHistory(task1);
        inMemoryTaskManager.getInMemoryHistoryManager().addHistory(task2);
        assertEquals(2, inMemoryTaskManager.getInMemoryHistoryManager().getHistory().size());
    }

    @Test
    void removeFromHistory() {
        Task task1 = new Task("Task_1", "Desk_task_1", 1);
        Task task2 = new Task("Task_1", "Desk_task_1", 2);
        inMemoryTaskManager.getInMemoryHistoryManager().addHistory(task1);
        inMemoryTaskManager.getInMemoryHistoryManager().addHistory(task2);
        inMemoryTaskManager.getInMemoryHistoryManager().remove(2);
        assertEquals(1, inMemoryTaskManager.getInMemoryHistoryManager().getHistory().size());
    }

}