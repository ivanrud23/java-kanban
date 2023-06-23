package managers;

import client.KVTaskClient;
import model.Epic;
import model.Subtask;
import model.Task;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpTaskManager extends FileBackedTasksManager {
    private final KVTaskClient kvTaskClient = new KVTaskClient();
    private final URI url = URI.create("http://localhost:8078");

    public HttpTaskManager() {
        loadAllTasks();
    }

    public KVTaskClient getKvTaskClient() {
        return kvTaskClient;
    }

    public URI getUrl() {
        return url;
    }

    @Override
    protected void save() throws IOException, NullPointerException, InterruptedException {
        kvTaskClient.put("task", Managers.getGson().toJson(taskStorageToJson(taskStorage)));
        kvTaskClient.put("subtask", Managers.getGson().toJson(subtaskStorageToJson(subTaskStorage)));
        kvTaskClient.put("epic", Managers.getGson().toJson(epicStorageToJson(epicStorage)));
        kvTaskClient.put("history", Managers.getGson().toJson(historyToString(inMemoryHistoryManager)));
        kvTaskClient.put("prioritized", Managers.getGson().toJson(prioritizedToJson(getPrioritizedTasks())));
    }

    private void loadAllTasks() {
        try {
            String responseTask = kvTaskClient.load("task");
            this.taskStorage = (HashMap<Integer, Task>) taskStorageFromJson(responseTask);
            String responseEpic = kvTaskClient.load("epic");
            this.epicStorage = (HashMap<Integer, Epic>) epicStorageFromJson(responseEpic);
            String responseSubtask = kvTaskClient.load("subtask");
            this.subTaskStorage = (HashMap<Integer, Subtask>) subTaskStorageFromJson(responseSubtask);
            String responsePrioritized = kvTaskClient.load("prioritized");
            List<Task> listOfTask = prioritizedFromJson(responsePrioritized);
            this.taskSortByTime.addAll(listOfTask);
            String responseHistory = kvTaskClient.load("history");
            String stringOfId = Managers.getGson().fromJson(responseHistory, String.class);
            if (!stringOfId.equals("")) {
                String[] listOfId = stringOfId.split(",");
                for (String id : listOfId) {
                    Integer taskId = Integer.parseInt(id.trim());
                    Task task = this.getById(taskId);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private List<String> prioritizedToJson(List<Task> list) {
        List<String> readyList = new ArrayList<>();
        for (Task task : list) {
            readyList.add(Managers.getGson().toJson(task));
        }
        return readyList;
    }

    private List<Task> prioritizedFromJson(String json) {
        List<Task> readyList = new ArrayList<>();
        List<String> list = Managers.getGson().fromJson(json, List.class);
        for (String task : list) {
            readyList.add(Managers.getGson().fromJson(task, Task.class));
        }
        return readyList;
    }

    private Map<Integer, String> taskStorageToJson(HashMap<Integer, Task> storage) {
        Map<Integer, String> readyStorage = new HashMap<>();
        for (Integer key : storage.keySet()) {
            readyStorage.put(key, Managers.getGson().toJson(storage.get(key)));
        }
        return readyStorage;
    }

    private Map<Integer, String> subtaskStorageToJson(HashMap<Integer, Subtask> storage) {
        Map<Integer, String> readyStorage = new HashMap<>();
        for (Integer key : storage.keySet()) {
            readyStorage.put(key, Managers.getGson().toJson(storage.get(key)));
        }
        return readyStorage;
    }

    private Map<Integer, String> epicStorageToJson(HashMap<Integer, Epic> storage) {
        Map<Integer, String> readyStorage = new HashMap<>();
        for (Integer key : storage.keySet()) {
            readyStorage.put(key, Managers.getGson().toJson(storage.get(key)));
        }
        return readyStorage;
    }

    private Map<Integer, Task> taskStorageFromJson(String json) {
        Map<String, String> taskInJson = Managers.getGson().fromJson(json, HashMap.class);
        Map<Integer, Task> finalStorage = new HashMap<>();
        for (String key : taskInJson.keySet()) {
            String task = taskInJson.get(key);
            finalStorage.put(Integer.parseInt(key), Managers.getGson().fromJson(task, Task.class));
        }
        return finalStorage;
    }

    private Map<Integer, Subtask> subTaskStorageFromJson(String json) {
        Map<String, String> readyStorage = Managers.getGson().fromJson(json, HashMap.class);
        Map<Integer, Subtask> finalStorage = new HashMap<>();
        for (String key : readyStorage.keySet()) {
            String task = readyStorage.get(key);
            finalStorage.put(Integer.parseInt(key), Managers.getGson().fromJson(task, Subtask.class));
        }
        return finalStorage;
    }

    private Map<Integer, Epic> epicStorageFromJson(String json) {
        Map<String, String> readyStorage = Managers.getGson().fromJson(json, HashMap.class);
        Map<Integer, Epic> finalStorage = new HashMap<>();
        for (String key : readyStorage.keySet()) {
            String task = readyStorage.get(key);
            finalStorage.put(Integer.parseInt(key), Managers.getGson().fromJson(task, Epic.class));
        }
        return finalStorage;
    }

    private static String historyToString(HistoryManager manager) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Task task : manager.getHistory()) {
            stringBuilder.append(task.getId());
            stringBuilder.append(",");
        }
        if (!String.valueOf(stringBuilder).isEmpty()) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        String history = stringBuilder.toString();
        return history;
    }
}
