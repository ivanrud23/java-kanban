package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager>{

    @BeforeEach
    void createManager() {
        taskManager = new FileBackedTasksManager();
    }

    @Test
    void saveToFileTask() throws IOException {
        taskManager.createTask(new Task("Task_1", "Desk_task_1", taskManager.idCounter()
                ,"01.05.2023 10:00", "PT10M"));
        File file = new File("history.csv");
        Reader fileReader = new FileReader(file);
        BufferedReader br = new BufferedReader(fileReader);
        String taskString = br.readLine();
        assertEquals("1, TASK, Task_1, NEW, Desk_task_1, 01.05.2023 10:00, PT10M", taskString);
    }
    @Test
    void saveToFileSubtask() throws IOException {
        taskManager.createEpic(new Epic("Epic_1", "Desk_Epic_1", taskManager.idCounter()));
        taskManager.createSubTask(new Subtask("Sub_1", "Desk_Sub_1", taskManager.idCounter(),
                "01.05.2023 10:00", "PT10M", 1));
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager();
        File file = new File("history.csv");
        Reader fileReader = new FileReader(file);
        BufferedReader br = new BufferedReader(fileReader);
        List<String> listStringOfTask = new ArrayList<>();
        String taskString = null;
        while (br.ready()) {
            taskString = br.readLine();
            listStringOfTask.add(taskString);
        }
        assertEquals("2, SUBTASK, Sub_1, NEW, Desk_Sub_1, 01.05.2023 10:00, PT10M, 1", listStringOfTask.get(1));
    }
    @Test
    void saveToFileEpic() throws IOException {
        taskManager.createEpic(new Epic("Epic_1", "Desk_Epic_1", taskManager.idCounter()));
        File file = new File("history.csv");
        Reader fileReader = new FileReader(file);
        BufferedReader br = new BufferedReader(fileReader);
        String taskString = br.readLine();
        assertEquals("1, EPIC, Epic_1, NEW, Desk_Epic_1", taskString);
    }

    @Test
    void loadFromFileTask() throws IOException {
        taskManager.createTask(new Task("Task_1", "Desk_task_1", taskManager.idCounter()
                ,"01.05.2023 10:00", "PT10M"));
        File file = new File("history.csv");
        FileBackedTasksManager fileBackedTasksManager = FileBackedTasksManager.loadFromFile(file);
        assertEquals(new Task("Task_1", "Desk_task_1", 1, Status.NEW, "01.05.2023 10:00", "PT10M"), fileBackedTasksManager.getById(1));
    }
    @Test
    void loadFromFileSubtask() throws IOException {
        taskManager.createEpic(new Epic("Epic_1", "Desk_Epic_1", taskManager.idCounter()));
        taskManager.createSubTask(new Subtask("Sub_1", "Desk_Sub_1", taskManager.idCounter(),
                "01.05.2023 10:00", "PT10M", 1));
        File file = new File("history.csv");
        FileBackedTasksManager fileBackedTasksManager = FileBackedTasksManager.loadFromFile(file);
        assertEquals(new Subtask("Sub_1", "Desk_Sub_1", 2, Status.NEW, "01.05.2023 10:00", "PT10M", 1), fileBackedTasksManager.getById(2));
    }
    @Test
    void loadFromFileEpic() throws IOException {
        taskManager.createEpic(new Epic("Epic_1", "Desk_Epic_1", taskManager.idCounter()));
        File file = new File("history.csv");
        FileBackedTasksManager fileBackedTasksManager = FileBackedTasksManager.loadFromFile(file);
        assertEquals(new Epic("Epic_1", "Desk_Epic_1", 1), fileBackedTasksManager.getById(1));
    }
    @Test
    void getPrioritizedTasks() throws IOException {
        taskManager.createTask(new Task("Task_1", "Desk_task_1", taskManager.idCounter()
                ,"01.05.2023 10:00", "PT10M"));
        taskManager.createTask(new Task("Task_2", "Desk_task_2", taskManager.idCounter()));
        taskManager.createEpic(new Epic("Epic_1", "Desk_Epic_1", taskManager.idCounter()));
        taskManager.createEpic(new Epic("Epic_2", "Desk_Epic_2", taskManager.idCounter()));
        taskManager.createSubTask(new Subtask("Sub_1", "Desk_Sub_1", taskManager.idCounter(),
                "01.05.2023 10:00", "PT10M", 3));
        taskManager.createSubTask(new Subtask("Sub_2", "Desk_Sub_2", taskManager.idCounter(), 3));
        taskManager.createSubTask(new Subtask("Sub_3", "Desk_Sub_3", taskManager.idCounter(),
                "01.05.2023 10:00", "PT10M", 3));
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        List<Task> listOfTask = new ArrayList<>(List.of(taskManager.getById(1),
                taskManager.getById(5),
                taskManager.getById(7),
                taskManager.getById(2),
                taskManager.getById(3),
                taskManager.getById(4),
                taskManager.getById(6)));
        assertEquals(listOfTask, prioritizedTasks);
    }

}
