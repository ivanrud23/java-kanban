package service;

import com.google.gson.Gson;
import managers.HttpTaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import servers.KVServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager>  {

    KVServer kvServer;
    Gson gson;

    @BeforeEach
    void createManager() throws IOException, InterruptedException {
        taskManager = new HttpTaskManager();
        kvServer = new KVServer();
        kvServer.start();
        gson = new Gson();
    }

    @AfterEach
    void stopKVServer() throws IOException {
        kvServer.stop();
    }

    @Test
    void saveTest() throws IOException, InterruptedException {
        taskManager.createTask(new Task("Task_1", "Desk_task_1"
                ,"01.07.2023 10:00", "PT10M"));
        taskManager.createTask(new Task("Task_2", "Desk_task_2"));
        taskManager.createEpic(new Epic("Epic_1", "Desk_Epic_1"));
        taskManager.createEpic(new Epic("Epic_2", "Desk_Epic_2"));
        taskManager.createSubTask(new Subtask("Sub_1", "Desk_Sub_1",
                "01.07.2023 10:10", "PT10M", 3));
        taskManager.createSubTask(new Subtask("Sub_2", "Desk_Sub_2", 3));
        taskManager.createSubTask(new Subtask("Sub_3", "Desk_Sub_3",
                "01.07.2023 10:20", "PT10M", 3));
        taskManager.getById(6);
        taskManager.getById(5);
        taskManager.getById(3);
        taskManager.getById(4);
        taskManager.getById(1);
        taskManager.getById(2);
        taskManager.getById(7);
        String taskString = taskManager.getKvTaskClient().load("task");
        String subtaskString = taskManager.getKvTaskClient().load("subtask");
        String epicString = taskManager.getKvTaskClient().load("epic");
        String historyString = taskManager.getKvTaskClient().load("history");
        String prioritizedString = taskManager.getKvTaskClient().load("prioritized");
        assertEquals("{\n" +
                        "  \"1\": \"{\\n  \\\"name\\\": \\\"Task_1\\\",\\n  \\\"description\\\": \\\"Desk_task_1\\\"," +
                        "\\n  \\\"id\\\": 1,\\n  \\\"status\\\": \\\"NEW\\\",\\n  \\\"duration\\\": 600,\\n  \\\"startTime\\\": " +
                        "\\\"01.07.2023 10:00\\\",\\n  \\\"endTime\\\": \\\"01.07.2023 10:10\\\"\\n}\",\n" +
                        "  \"2\": \"{\\n  \\\"name\\\": \\\"Task_2\\\",\\n  \\\"description\\\": \\\"Desk_task_2\\\",\\n " +
                        " \\\"id\\\": 2,\\n  \\\"status\\\": \\\"NEW\\\"\\n}\"\n" +
                        "}",
                taskString);
        assertEquals("{\n" +
                        "  \"5\": \"{\\n  \\\"parentId\\\": 3,\\n  \\\"name\\\": \\\"Sub_1\\\",\\n  \\\"description\\\": " +
                        "\\\"Desk_Sub_1\\\",\\n  \\\"id\\\": 5,\\n  \\\"status\\\": \\\"NEW\\\",\\n  \\\"duration\\\": 600," +
                        "\\n  \\\"startTime\\\": \\\"01.07.2023 10:10\\\",\\n  \\\"endTime\\\": \\\"01.07.2023 10:20\\\"\\n}\",\n" +
                        "  \"6\": \"{\\n  \\\"parentId\\\": 3,\\n  \\\"name\\\": \\\"Sub_2\\\",\\n  \\\"description\\\": " +
                        "\\\"Desk_Sub_2\\\",\\n  \\\"id\\\": 6,\\n  \\\"status\\\": \\\"NEW\\\"\\n}\",\n" +
                        "  \"7\": \"{\\n  \\\"parentId\\\": 3,\\n  \\\"name\\\": \\\"Sub_3\\\",\\n  \\\"description\\\": " +
                        "\\\"Desk_Sub_3\\\",\\n  \\\"id\\\": 7,\\n  \\\"status\\\": \\\"NEW\\\",\\n  \\\"duration\\\": 600," +
                        "\\n  \\\"startTime\\\": \\\"01.07.2023 10:20\\\",\\n  \\\"endTime\\\": \\\"01.07.2023 10:30\\\"\\n}\"\n" +
                        "}",
                subtaskString);
        assertEquals("{\n" +
                        "  \"3\": \"{\\n  \\\"children\\\": [\\n    5,\\n    6,\\n    7\\n  ],\\n  \\\"name\\\": " +
                        "\\\"Epic_1\\\",\\n  \\\"description\\\": \\\"Desk_Epic_1\\\",\\n  \\\"id\\\": 3,\\n  \\\"status\\\": " +
                        "\\\"NEW\\\",\\n  \\\"epic\\\": true,\\n  \\\"duration\\\": 1200,\\n  \\\"startTime\\\":" +
                        " \\\"01.07.2023 10:10\\\",\\n  \\\"endTime\\\": \\\"01.07.2023 10:30\\\"\\n}\",\n" +
                        "  \"4\": \"{\\n  \\\"children\\\": [],\\n  \\\"name\\\": \\\"Epic_2\\\",\\n  \\\"description\\\":" +
                        " \\\"Desk_Epic_2\\\",\\n  \\\"id\\\": 4,\\n  \\\"status\\\": \\\"NEW\\\",\\n  \\\"epic\\\": true\\n}\"\n" +
                        "}",
                epicString);
        assertEquals("\"6,5,3,4,1,2,7\"", historyString);
        assertEquals("[\n" +
                        "  \"{\\n  \\\"name\\\": \\\"Task_1\\\",\\n  \\\"description\\\": \\\"Desk_task_1\\\",\\n  \\\"id\\\": " +
                        "1,\\n  \\\"status\\\": \\\"NEW\\\",\\n  \\\"duration\\\": 600,\\n  \\\"startTime\\\": " +
                        "\\\"01.07.2023 10:00\\\",\\n  \\\"endTime\\\": \\\"01.07.2023 10:10\\\"\\n}\",\n" +
                        "  \"{\\n  \\\"parentId\\\": 3,\\n  \\\"name\\\": \\\"Sub_1\\\",\\n  \\\"description\\\":" +
                        " \\\"Desk_Sub_1\\\",\\n  \\\"id\\\": 5,\\n  \\\"status\\\": \\\"NEW\\\",\\n  \\\"duration\\\": " +
                        "600,\\n  \\\"startTime\\\": \\\"01.07.2023 10:10\\\",\\n  \\\"endTime\\\": \\\"01.07.2023 10:20\\\"\\n}\",\n" +
                        "  \"{\\n  \\\"parentId\\\": 3,\\n  \\\"name\\\": \\\"Sub_3\\\",\\n  \\\"description\\\": " +
                        "\\\"Desk_Sub_3\\\",\\n  \\\"id\\\": 7,\\n  \\\"status\\\": \\\"NEW\\\",\\n  \\\"duration\\\": 600,\\n  \\\"startTime\\\": \\\"01.07.2023 10:20\\\",\\n  \\\"endTime\\\": \\\"01.07.2023 10:30\\\"\\n}\",\n" +
                        "  \"{\\n  \\\"name\\\": \\\"Task_2\\\",\\n  \\\"description\\\": \\\"Desk_task_2\\\"," +
                        "\\n  \\\"id\\\": 2,\\n  \\\"status\\\": \\\"NEW\\\"\\n}\",\n" +
                        "  \"{\\n  \\\"children\\\": [\\n    5,\\n    6,\\n    7\\n  ],\\n  \\\"name\\\": \\\"Epic_1\\\"," +
                        "\\n  \\\"description\\\": \\\"Desk_Epic_1\\\",\\n  \\\"id\\\": 3,\\n  \\\"status\\\": \\\"NEW\\\"," +
                        "\\n  \\\"epic\\\": true,\\n  \\\"duration\\\": 1200,\\n  \\\"startTime\\\": \\\"01.07.2023 10:10\\\"," +
                        "\\n  \\\"endTime\\\": \\\"01.07.2023 10:30\\\"\\n}\",\n" +
                        "  \"{\\n  \\\"children\\\": [],\\n  \\\"name\\\": \\\"Epic_2\\\",\\n  \\\"description\\\": " +
                        "\\\"Desk_Epic_2\\\",\\n  \\\"id\\\": 4,\\n  \\\"status\\\": \\\"NEW\\\",\\n  \\\"epic\\\": true\\n}\",\n" +
                        "  \"{\\n  \\\"parentId\\\": 3,\\n  \\\"name\\\": \\\"Sub_2\\\",\\n  \\\"description\\\":" +
                        " \\\"Desk_Sub_2\\\",\\n  \\\"id\\\": 6,\\n  \\\"status\\\": \\\"NEW\\\"\\n}\"\n" +
                        "]",
                prioritizedString);
    }

    @Test
    void loadAllTasksTest() throws IOException, InterruptedException {


        taskManager.createTask(new Task("Task_2", "Desk_task_2"));
        taskManager.createEpic(new Epic("Epic_2", "Desk_Epic_2"));
        taskManager.createSubTask(new Subtask("Sub_2", "Desk_Sub_2", 2));
        HttpTaskManager taskManagerLoad = new HttpTaskManager();
        HashMap<Integer, Task> listOfTask = new HashMap<>();
        HashMap<Integer, Subtask> listOfSubTask = new HashMap<>();
        HashMap<Integer, Epic> listOfEpic = new HashMap<>();
        listOfTask.put(1, new Task("Task_2", "Desk_task_2", 1));
        listOfEpic.put(2, new Epic("Epic_2", "Desk_Epic_2", 2, Status.NEW, new ArrayList<>(List.of(3))));
        listOfSubTask.put(3, new Subtask("Sub_2", "Desk_Sub_2", 3, 2));
        assertEquals(listOfTask, taskManagerLoad.getTaskStorage());
        assertEquals(listOfSubTask, taskManagerLoad.getSubTaskStorage());
        assertEquals(listOfEpic, taskManagerLoad.getEpicStorage());
    }

}