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
import servers.KVServer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskServerTest extends TaskManagerTest<HttpTaskManager> {

    KVServer kvServer;
    HttpTaskServer httpTaskServer;
    Gson gson;

    @BeforeEach
    void createManager() throws IOException, InterruptedException {
        kvServer = new KVServer();
        kvServer.start();
        httpTaskServer = new HttpTaskServer();
        taskManager = new HttpTaskManager();
        gson = Managers.getGson();
    }

    @AfterEach
    void stopKVServer() throws IOException {

        httpTaskServer.stopHttpTaskServer();
        kvServer.stop();

//        httpTaskServer.getKvServer().stop();
    }

    @Test
    void createTask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task");
        Task task = new Task("Task_1","Desk_task_1");
        String json = gson.toJson(task);
        HttpRequest requestCreate = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpRequest getTasks = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task"))
                .GET()
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        client.send(requestCreate, handler);
        HttpResponse<String> response = client.send(getTasks, handler);
        HashMap<Integer, Task> taskStorage = new HashMap<>();
        taskStorage.put(1,  new Task("Task_1", "Desk_task_1", 1, Status.NEW));
        assertEquals(gson.toJson(taskStorage), response.body());
    }

    @Test
    void createSubTask() throws IOException, InterruptedException {
        URI urlSEpic = URI.create("http://localhost:8080/tasks/epic");
        Epic epic = new Epic("Epic_1", "Desk_epic_1");
        Subtask subtask = new Subtask("SubTask_1", "Desk_SubTask_1", 1);
        String  epicJson = gson.toJson(epic);
        String  subtaskJson = gson.toJson(subtask);
        HttpRequest createEpic = HttpRequest.newBuilder()
                .uri(urlSEpic)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();
        URI urlSubtask = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest createSub = HttpRequest.newBuilder()
                .uri(urlSubtask)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();
        HttpRequest getSubtasks = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask"))
                .GET()
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        client.send(createEpic, handler);
        client.send(createSub, handler);
        HttpResponse<String> response = client.send(getSubtasks, handler);
        HashMap<Integer, Subtask> subtaskStorage = new HashMap<>();
        subtaskStorage.put(2,  new Subtask("SubTask_1", "Desk_SubTask_1", 2, Status.NEW, 1));
        assertEquals(gson.toJson(subtaskStorage), response.body());
    }

    @Test
    void createEpic() throws IOException, InterruptedException {
        URI urlSEpic = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest createEpic = HttpRequest.newBuilder()
                .uri(urlSEpic)
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\": \"Epic_1\", \"description\": \"Desk_epic_1\", \"epic\": true}"))
                .build();
        HttpRequest getEpic = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic"))
                .GET()
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        client.send(createEpic, handler);
        HttpResponse<String> response = client.send(getEpic, handler);
        HashMap<Integer, Epic> epicStorage = new HashMap<>();
        epicStorage.put(1, new Epic("Epic_1", "Desk_epic_1", 1));
        assertEquals(gson.toJson(epicStorage), response.body());
    }

    @Test
    void getById() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task");
        Task task = new Task("Task_1","Desk_task_1");
        String json = gson.toJson(task);
        HttpRequest requestCreate = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpRequest getTasks = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task"))
                .GET()
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        client.send(requestCreate, handler);
        HttpResponse<String> response = client.send(getTasks, handler);
        HashMap<Integer, Task> taskStorage = new HashMap<>();
        taskStorage.put(1,  new Task("Task_1", "Desk_task_1", 1, Status.NEW));
        assertEquals(gson.toJson(taskStorage), response.body());
    }

    @Test
    void updateTask() throws IOException, InterruptedException {
        Task task = new Task("Task_1","Desk_task_1");
        String json = gson.toJson(task);
        URI url = URI.create("http://localhost:8080/tasks/task/?id=1");
        HttpRequest createTask = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpRequest updateTask = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString("{\"description\": \"Task_1_UPDATE\"}"))
                .build();
        HttpRequest getTasks = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task"))
                .GET()
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        client.send(createTask, handler);
        client.send(updateTask, handler);
        HttpResponse<String> response = client.send(getTasks, handler);
        HashMap<Integer, Task> taskStorage = new HashMap<>();
        taskStorage.put(1,  new Task("Task_1", "Task_1_UPDATE", 1, Status.NEW));
        assertEquals(gson.toJson(taskStorage), response.body());

    }

    @Test
    void updateSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic_1", "Desk_epic_1");
        Subtask subtask = new Subtask("SubTask_1", "Desk_SubTask_1", 1);
        String  epicJson = gson.toJson(epic);
        String  subtaskJson = gson.toJson(subtask);
        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=2");
        URI urlSubtask = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest createEpic = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic"))
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();
        HttpRequest createSub = HttpRequest.newBuilder()
                .uri(urlSubtask)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();
        HttpRequest updateSubtask = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString("{\"description\": \"Desk_Sub_1_UPDATE\"}"))
                .build();
        HttpRequest getSubtasks = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask"))
                .GET()
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        client.send(createEpic, handler);
        client.send(createSub, handler);
        client.send(updateSubtask, handler);
        HttpResponse<String> response = client.send(getSubtasks, handler);
        HashMap<Integer, Subtask> subtaskStorage = new HashMap<>();
        subtaskStorage.put(2,  new Subtask("SubTask_1", "Desk_Sub_1_UPDATE", 2, Status.NEW, 1));
        assertEquals(gson.toJson(subtaskStorage), response.body());
    }

    @Test
    void updateEpic() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic/?id=1");
        URI urlSEpic = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest createEpic = HttpRequest.newBuilder()
                .uri(urlSEpic)
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\": \"Epic_1\", \"description\": \"Desk_epic_1\", \"epic\": true}"))
                .build();
        HttpRequest updateEpic = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString("{\"description\": \"Desk_Epic_1_UPDATE\"}"))
                .build();
        HttpRequest getEpic = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic"))
                .GET()
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        client.send(createEpic, handler);
        client.send(updateEpic, handler);
        HttpResponse<String> response = client.send(getEpic, handler);
        HashMap<Integer, Epic> epicStorage = new HashMap<>();
        epicStorage.put(1, new Epic("Epic_1", "Desk_Epic_1_UPDATE", 1));
        assertEquals(gson.toJson(epicStorage), response.body());
    }

    @Test
    void removeTask() throws IOException, InterruptedException {
        taskManager.createTask(new Task("Task_1", "Desk_task_1", 1, Status.NEW));
        URI url = URI.create("http://localhost:8080/tasks/task/?id=1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpRequest getTasks = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task"))
                .GET()
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        client.send(request, handler);
        HttpResponse<String> response = client.send(getTasks, handler);
        assertEquals(gson.toJson(new HashMap<Integer, Task>()), response.body());
    }

    @Test
    void clearSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic_1", "Desk_epic_1");
        Subtask subtask = new Subtask("SubTask_1", "Desk_SubTask_1", 1);
        String  epicJson = gson.toJson(epic);
        String  subtaskJson = gson.toJson(subtask);
        URI urlSubtask = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest createEpic = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic"))
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();
        HttpRequest createSub = HttpRequest.newBuilder()
                .uri(urlSubtask)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();
        HttpRequest delete = HttpRequest.newBuilder()
                .uri(urlSubtask)
                .DELETE()
                .build();
        HttpRequest getSubtasks = HttpRequest.newBuilder()
                .uri(urlSubtask)
                .GET()
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        client.send(createEpic, handler);
        client.send(createSub, handler);
        client.send(delete, handler);
        HttpResponse<String> response = client.send(getSubtasks, handler);
        assertEquals(gson.toJson(new HashMap<Integer, Subtask>()), response.body());
    }

    @Test
    void clearTask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task");
        Task task = new Task("Task_1","Desk_task_1");
        String json = gson.toJson(task);
        HttpRequest requestCreate = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpRequest getTasks = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpRequest clear = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        client.send(requestCreate, handler);
        client.send(clear, handler);
        HttpResponse<String> response = client.send(getTasks, handler);
        HashMap<Integer, Task> taskStorage = new HashMap<>();
        taskStorage.put(1,  new Task("Task_1", "Desk_task_1", 1, Status.NEW));
        assertEquals(gson.toJson(new HashMap<Integer, Task>()), response.body());
    }

    @Test
    void clearEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic_1", "Desk_epic_1");
        Subtask subtask = new Subtask("SubTask_1", "Desk_SubTask_1", 1);
        String  epicJson = gson.toJson(epic);
        String  subtaskJson = gson.toJson(subtask);
        URI urlSubtask = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest createEpic = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic"))
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();
        HttpRequest createSub = HttpRequest.newBuilder()
                .uri(urlSubtask)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();
        HttpRequest clearEpic = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic"))
                .DELETE()
                .build();
        HttpRequest getEpic = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic"))
                .GET()
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        client.send(createEpic, handler);
        client.send(createSub, handler);
        client.send(clearEpic, handler);
        HttpResponse<String> responseEpic = client.send(getEpic, handler);
        HttpResponse<String> responseSubtask = client.send(getEpic, handler);
        assertEquals(gson.toJson(new HashMap<Integer, Subtask>()), responseSubtask.body());
        assertEquals(gson.toJson(new HashMap<Integer, Epic>()), responseEpic.body());

    }


    @Test
    void loadAllTasks() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task");
        Task task = new Task("Task_1","Desk_task_1");
        Epic epic = new Epic("Epic_1", "Desk_epic_1");
        Subtask subtask = new Subtask("SubTask_1", "Desk_SubTask_1", 2);
        String taskJson = gson.toJson(task);
        String  epicJson = gson.toJson(epic);
        String  subtaskJson = gson.toJson(subtask);
        URI urlSubtask = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest createTask = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpRequest createEpic = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic"))
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();
        HttpRequest createSub = HttpRequest.newBuilder()
                .uri(urlSubtask)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        client.send(createTask, handler);
        client.send(createEpic, handler);
        client.send(createSub, handler);

        httpTaskServer.stopHttpTaskServer();
        HttpTaskServer neHttpTaskServer = new HttpTaskServer();

        HttpRequest getTask = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task"))
                .GET()
                .build();
        HttpRequest getSubtask = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask"))
                .GET()
                .build();
        HttpRequest getEpic = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic"))
                .GET()
                .build();
        HttpResponse<String> responseTask = client.send(getTask, handler);
        HttpResponse<String> responseEpic = client.send(getEpic, handler);
        HttpResponse<String> responseSubtask = client.send(getSubtask, handler);

        HashMap<Integer, Task> taskStorage = new HashMap<>();
        taskStorage.put(1,  new Task("Task_1", "Desk_task_1", 1, Status.NEW));
        HashMap<Integer, Epic> epicStorage = new HashMap<>();
        epicStorage.put(2, new Epic("Epic_1", "Desk_epic_1", 2, Status.NEW, new ArrayList<>(List.of(3))));
        HashMap<Integer, Subtask> subtaskStorage = new HashMap<>();
        subtaskStorage.put(3,  new Subtask("SubTask_1", "Desk_SubTask_1", 3, Status.NEW, 2));

        neHttpTaskServer.stopHttpTaskServer();
        assertEquals(gson.toJson(taskStorage), responseTask.body());
        assertEquals(gson.toJson(epicStorage), responseEpic.body());
        assertEquals(gson.toJson(subtaskStorage), responseSubtask.body());
    }

}