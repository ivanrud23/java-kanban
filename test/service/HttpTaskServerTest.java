package service;

import com.google.gson.Gson;
import managers.HttpTaskManager;
import managers.Managers;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import servers.HttpTaskServer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest extends TaskManagerTest<HttpTaskManager> {

    HttpTaskServer httpTaskServer;
    Gson gson;

    @BeforeEach
    void createManager() throws IOException, InterruptedException {
        httpTaskServer = new HttpTaskServer();
        taskManager = new HttpTaskManager();
        gson = Managers.getGson();
    }

    @AfterEach
    void stopKVServer() throws IOException {
        httpTaskServer.stopHttpTaskServer();
        httpTaskServer.getKvServer().stop();
    }
    @Test
    void createTask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString("\t{\"name\": \"Task_1\", \"description\": \"Desk_task_1\"}"))
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        client.send(request, handler);
        assertEquals(taskManager.getById(1), new Task("Task_1", "Desk_task_1", 1, Status.NEW));
    }

    @Test
    void createSubTask() throws IOException, InterruptedException {
        URI urlSEpic = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest createEpic = HttpRequest.newBuilder()
                .uri(urlSEpic)
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\": \"Epic_1\", \"description\": \"Desk_epic_1\", \"epic\": true}"))
                .build();
        URI urlSubtask = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest createSub = HttpRequest.newBuilder()
                .uri(urlSubtask)
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\": \"SubTask_1\", \"description\": \"Desk_SubTask_1\", \"parentId\": 1}"))
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        client.send(createEpic, handler);
        client.send(createSub, handler);
        assertEquals(taskManager.getById(2), new Subtask("SubTask_1", "Desk_SubTask_1", 2, Status.NEW, 1));
    }

    @Test
    void createEpic() throws IOException, InterruptedException {
        URI urlSEpic = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest createEpic = HttpRequest.newBuilder()
                .uri(urlSEpic)
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\": \"Epic_1\", \"description\": \"Desk_epic_1\", \"epic\": true}"))
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        client.send(createEpic, handler);
        assertEquals(taskManager.getById(1), new Epic("Epic_1", "Desk_epic_1", 1, Status.NEW, new ArrayList<>()));
    }

    @Test
    void getById() throws IOException, InterruptedException {
        taskManager.createTask(new Task("Task_1", "Desk_task_1", 1, Status.NEW));
        URI url = URI.create("http://localhost:8080/tasks/task/?id=1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        String task = response.body();
        assertEquals(taskManager.getById(1), gson.fromJson("{\"name\"=\"Task_1\", \"description\"=\"Desk_task_1\", " +
                "\"id\"=1, \"status\"=\"NEW\", \"epic\"=null}", Task.class));
    }

    @Test
    void updateTask() throws IOException, InterruptedException {
        taskManager.createTask(new Task("Task_1", "Desk_task_1", 1, Status.NEW));
        URI url = URI.create("http://localhost:8080/tasks/task/?id=1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString("{\"description\": \"Task_1_UPDATE\"}"))
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        String task = response.body();
        assertEquals(taskManager.getById(1), gson.fromJson("{\"name\"=\"Task_1\", \"description\"=\"Task_1_UPDATE\", " +
                "\"id\"=1, \"status\"=\"NEW\", \"epic\"=null}", Task.class));

    }

    @Test
    void updateSubTask() throws IOException, InterruptedException {
        taskManager.createEpic(new Epic("Epic_1", "Desk_Epic_1"));
        taskManager.createSubTask(new Subtask("Sub_1", "Desk_Sub_1", 1));
        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString("{\"description\": \"Desk_Sub_1_UPDATE\"}"))
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        String task = response.body();
        assertEquals(taskManager.getById(2), gson.fromJson("{\"name\"=\"Sub_1\", \"description\"=\"Desk_Sub_1_UPDATE\", " +
                "\"id\"=2, \"status\"=\"NEW\", \"epic\"=null, \"parentId\"=1}", Subtask.class));
    }

    @Test
    void updateEpic() throws IOException, InterruptedException {
        taskManager.createEpic(new Epic("Epic_1", "Desk_Epic_1"));
        URI url = URI.create("http://localhost:8080/tasks/epic/?id=1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString("{\"description\": \"Desk_Epic_1_UPDATE\"}"))
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        client.send(request, handler);
        assertEquals(taskManager.getById(1), gson.fromJson("{\"name\"=\"Epic_1\", \"description\"=\"Desk_Epic_1_UPDATE\", " +
                "\"id\"=1, \"status\"=\"NEW\", \"epic\"=true, \"children\"=[]}", Epic.class));
    }

    @Test
    void removeTask() throws IOException, InterruptedException {
        taskManager.createTask(new Task("Task_1", "Desk_task_1", 1, Status.NEW));
        URI url = URI.create("http://localhost:8080/tasks/task/?id=1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        client.send(request, handler);
        assertEquals(taskManager.getTaskStorage(), new ArrayList<>());
    }

    @Test
    void clearSubtask() throws IOException, InterruptedException {
        taskManager.createEpic(new Epic("Epic_1", "Desk_Epic_1"));
        taskManager.createSubTask(new Subtask("Sub_1", "Desk_Sub_1",
                "01.05.2023 10:00", "PT10M", 1));
        taskManager.createSubTask(new Subtask("Sub_2", "Desk_Sub_2", 1));
        taskManager.createSubTask(new Subtask("Sub_3", "Desk_Sub_3",
                "01.05.2023 10:20", "PT10M", 1));
        URI url = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        client.send(request, handler);
        assertEquals(taskManager.getSubTaskStorage(), new ArrayList<>());
    }

    @Test
    void clearTask() throws IOException, InterruptedException {
        taskManager.createTask(new Task("Task_1", "Desk_task_1"
                ,"01.05.2023 10:00", "PT10M"));
        taskManager.createTask(new Task("Task_2", "Desk_task_2"));
        URI url = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        client.send(request, handler);
        assertEquals(taskManager.getTaskStorage(), new ArrayList<>());
    }

    @Test
    void clearEpic() throws IOException, InterruptedException {
        taskManager.createEpic(new Epic("Epic_1", "Desk_Epic_1"));
        taskManager.createSubTask(new Subtask("Sub_1", "Desk_Sub_1",
                "01.05.2023 10:00", "PT10M", 1));
        taskManager.createSubTask(new Subtask("Sub_2", "Desk_Sub_2", 1));
        taskManager.createSubTask(new Subtask("Sub_3", "Desk_Sub_3",
                "01.05.2023 10:20", "PT10M", 1));
        URI url = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        client.send(request, handler);
        assertEquals(taskManager.getSubTaskStorage(), new ArrayList<>());
        assertEquals(taskManager.getEpicStorage(), new ArrayList<>());
    }


    @Test
    void loadAllTasks() throws IOException, InterruptedException {
        taskManager.createTask(new Task("Task_2", "Desk_task_2"));
        taskManager.createEpic(new Epic("Epic_1", "Desk_Epic_1"));
        taskManager.createSubTask(new Subtask("Sub_2", "Desk_Sub_2", 2));
        HttpTaskManager taskManager2 = new HttpTaskManager();
//        taskManager2 = taskManager2.loadAllTasks();
        URI urlTask = URI.create("http://localhost:8080/tasks/task/?id=1");
        HttpRequest requestTask = HttpRequest.newBuilder()
                .uri(urlTask)
                .GET()
                .build();
        HttpClient clientTask = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handlerTask = HttpResponse.BodyHandlers.ofString();
        clientTask.send(requestTask, handlerTask);
        assertEquals(taskManager2.getById(1), new Task("Task_2", "Desk_task_2", 1, Status.NEW));
        assertEquals(taskManager2.getById(2), new Epic("Epic_1", "Desk_Epic_1", 2, Status.NEW, new ArrayList<>(List.of(3))));
        assertEquals(taskManager2.getById(3), new Subtask("Sub_2", "Desk_Sub_2", 3, Status.NEW, 2));
    }
}