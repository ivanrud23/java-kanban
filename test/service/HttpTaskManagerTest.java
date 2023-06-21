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
        assertEquals("{\"1\":\"{\\\"name\\\":\\\"Task_1\\\",\\\"description\\\":\\\"Desk_task_1\\\",\\\"id\\\":1," +
                        "\\\"status\\\":\\\"NEW\\\",\\\"duration\\\":{\\\"seconds\\\":600,\\\"nanos\\\":0},\\\"startTime\\\"" +
                        ":\\\"01.07.2023 10:00\\\",\\\"endTime\\\":\\\"01.07.2023 10:10\\\"}\",\"2\":\"{\\\"name\\\":\\\"" +
                        "Task_2\\\",\\\"description\\\":\\\"Desk_task_2\\\",\\\"id\\\":2,\\\"status\\\":\\\"NEW\\\"}\"}",
                taskString);
        assertEquals("{\"5\":\"{\\\"parentId\\\":3,\\\"name\\\":\\\"Sub_1\\\",\\\"description\\\":\\\"Desk_Sub_1\\\"," +
                        "\\\"id\\\":5,\\\"status\\\":\\\"NEW\\\",\\\"duration\\\":{\\\"seconds\\\":600,\\\"nanos\\\":0}," +
                        "\\\"startTime\\\":\\\"01.07.2023 10:10\\\",\\\"endTime\\\":\\\"01.07.2023 10:20\\\"}\",\"6\":" +
                        "\"{\\\"parentId\\\":3,\\\"name\\\":\\\"Sub_2\\\",\\\"description\\\":\\\"Desk_Sub_2\\\",\\\"id\\\":6," +
                        "\\\"status\\\":\\\"NEW\\\"}\",\"7\":\"{\\\"parentId\\\":3,\\\"name\\\":\\\"Sub_3\\\",\\\"description\\\":" +
                        "\\\"Desk_Sub_3\\\",\\\"id\\\":7,\\\"status\\\":\\\"NEW\\\",\\\"duration\\\":{\\\"seconds\\\":600," +
                        "\\\"nanos\\\":0},\\\"startTime\\\":\\\"01.07.2023 10:20\\\",\\\"endTime\\\":\\\"01.07.2023 10:30\\\"}\"}",
                subtaskString);
        assertEquals("{\"3\":\"{\\\"children\\\":[5,6,7],\\\"name\\\":\\\"Epic_1\\\",\\\"description\\\":" +
                        "\\\"Desk_Epic_1\\\",\\\"id\\\":3,\\\"status\\\":\\\"NEW\\\",\\\"epic\\\":true,\\\"duration\\\":" +
                        "{\\\"seconds\\\":1200,\\\"nanos\\\":0},\\\"startTime\\\":\\\"01.07.2023 10:10\\\",\\\"endTime\\\":" +
                        "\\\"01.07.2023 10:30\\\"}\",\"4\":\"{\\\"children\\\":[],\\\"name\\\":\\\"Epic_2\\\",\\\"description\\\":" +
                        "\\\"Desk_Epic_2\\\",\\\"id\\\":4,\\\"status\\\":\\\"NEW\\\",\\\"epic\\\":true}\"}",
                epicString);
        assertEquals("\"6,5,3,4,1,2,7\"", historyString);
        assertEquals("[\"{\\\"name\\\":\\\"Task_1\\\",\\\"description\\\":\\\"Desk_task_1\\\",\\\"id\\\":1," +
                        "\\\"status\\\":\\\"NEW\\\",\\\"duration\\\":{\\\"seconds\\\":600,\\\"nanos\\\":0},\\\"startTime\\\":" +
                        "\\\"01.07.2023 10:00\\\",\\\"endTime\\\":\\\"01.07.2023 10:10\\\"}\",\"{\\\"parentId\\\":3," +
                        "\\\"name\\\":\\\"Sub_1\\\",\\\"description\\\":\\\"Desk_Sub_1\\\",\\\"id\\\":5,\\\"status\\\":" +
                        "\\\"NEW\\\",\\\"duration\\\":{\\\"seconds\\\":600,\\\"nanos\\\":0},\\\"startTime\\\":" +
                        "\\\"01.07.2023 10:10\\\",\\\"endTime\\\":\\\"01.07.2023 10:20\\\"}\",\"{\\\"parentId\\\":3," +
                        "\\\"name\\\":\\\"Sub_3\\\",\\\"description\\\":\\\"Desk_Sub_3\\\",\\\"id\\\":7,\\\"status\\\":" +
                        "\\\"NEW\\\",\\\"duration\\\":{\\\"seconds\\\":600,\\\"nanos\\\":0},\\\"startTime\\\":" +
                        "\\\"01.07.2023 10:20\\\",\\\"endTime\\\":\\\"01.07.2023 10:30\\\"}\",\"{\\\"name\\\":\\\"Task_2\\\"," +
                        "\\\"description\\\":\\\"Desk_task_2\\\",\\\"id\\\":2,\\\"status\\\":\\\"NEW\\\"}\",\"{\\\"children\\\":[5,6,7]," +
                        "\\\"name\\\":\\\"Epic_1\\\",\\\"description\\\":\\\"Desk_Epic_1\\\",\\\"id\\\":3,\\\"status\\\":\\\"NEW\\\"," +
                        "\\\"epic\\\":true,\\\"duration\\\":{\\\"seconds\\\":1200,\\\"nanos\\\":0},\\\"startTime\\\":" +
                        "\\\"01.07.2023 10:10\\\",\\\"endTime\\\":\\\"01.07.2023 10:30\\\"}\",\"{\\\"children\\\":[]," +
                        "\\\"name\\\":\\\"Epic_2\\\",\\\"description\\\":\\\"Desk_Epic_2\\\",\\\"id\\\":4,\\\"status\\\":" +
                        "\\\"NEW\\\",\\\"epic\\\":true}\",\"{\\\"parentId\\\":3,\\\"name\\\":\\\"Sub_2\\\",\\\"description\\\":" +
                        "\\\"Desk_Sub_2\\\",\\\"id\\\":6,\\\"status\\\":\\\"NEW\\\"}\"]",
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