package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpTaskManager extends FileBackedTasksManager{
    private final KVTaskClient kvTaskClient = new KVTaskClient();
    private final URI url = URI.create("http://localhost:8078");


    public KVTaskClient getKvTaskClient() {
        return kvTaskClient;
    }
    public URI getUrl() {
        return url;
    }

    @Override
    public void save() throws IOException, NullPointerException, InterruptedException {
        kvTaskClient.put("task", Managers.gson.toJson(storageToJson(taskStorage)));
        kvTaskClient.put("subtask", Managers.gson.toJson(storageToJson(subTaskStorage)));
        kvTaskClient.put("epic", Managers.gson.toJson(storageToJson(epicStorage)));
        kvTaskClient.put("history", Managers.gson.toJson(historyToString(inMemoryHistoryManager)));
        kvTaskClient.put("prioritized", Managers.gson.toJson(prioritizedToJson(getPrioritizedTasks())));
    }
    public HttpTaskManager loadAllTasks() throws IOException, InterruptedException {
        HttpTaskManager newHttpTaskManager = new HttpTaskManager();
        String responseTask = kvTaskClient.load("task");
        newHttpTaskManager.taskStorage = (HashMap<Integer, Task>) taskStorageFromJson(responseTask);
        String responseEpic = kvTaskClient.load("epic");
        newHttpTaskManager.epicStorage = (HashMap<Integer, Epic>) epicStorageFromJson(responseEpic);
        String responseSubtask = kvTaskClient.load("subtask");
        newHttpTaskManager.subTaskStorage = (HashMap<Integer, Subtask>) subTaskStorageFromJson(responseSubtask);
        String responsePrioritized = kvTaskClient.load("prioritized");
        List<Task> listOfTask= prioritizedFromJson(responsePrioritized);
        newHttpTaskManager.taskSortByTime.addAll(listOfTask);
        String responseHistory = kvTaskClient.load("history");
        String stringOfId = Managers.gson.fromJson(responseHistory, String.class);
        if (!stringOfId.equals("")) {
            String[] listOfId = stringOfId.split(",");
            for (String id : listOfId) {
                Integer taskId = Integer.parseInt(id.trim());
                Task task = newHttpTaskManager.getById(taskId);
            }
        }
        return newHttpTaskManager;
    }
    private List<Task> prioritizedToJson(List list) {
        List readyList = new ArrayList();
        for (Object task : list) {
            readyList.add(Managers.gson.toJson(task));
        }
        return readyList;
    }
    private List<Task> prioritizedFromJson(String json) {
        List readyList = new ArrayList();
        List list = Managers.gson.fromJson(json, List.class);
        for (Object task : list) {
            readyList.add(Managers.gson.fromJson((String) task, Task.class));
        }
        return readyList;
    }
    private Map<Integer, Task> storageToJson(HashMap storage) {
        HashMap readyStorage = new HashMap<>();
        for (Object key : storage.keySet()) {
            readyStorage.put(key, Managers.gson.toJson(storage.get(key)));
        }
        return readyStorage;
    }
    private Map<Integer, Task> taskStorageFromJson(String json) {
        HashMap readyStorage = Managers.gson.fromJson(json, HashMap.class);
        HashMap finalStorage = new HashMap<>();
        for (Object key : readyStorage.keySet()) {
            String task = (String) readyStorage.get(key);
            finalStorage.put(Integer.parseInt((String) key), Managers.gson.fromJson(task, Task.class));
        }
        return finalStorage;
    }
    private Map<Integer, Subtask> subTaskStorageFromJson(String json) {
        HashMap readyStorage = Managers.gson.fromJson(json, HashMap.class);
        HashMap finalStorage = new HashMap<>();
        for (Object key : readyStorage.keySet()) {
            String task = (String) readyStorage.get(key);
            finalStorage.put(Integer.parseInt((String) key), Managers.gson.fromJson(task, Subtask.class));
        }
        return finalStorage;
    }
    private Map<Integer, Epic> epicStorageFromJson(String json) {
        HashMap readyStorage = Managers.gson.fromJson(json, HashMap.class);
        HashMap finalStorage = new HashMap<>();
        for (Object key : readyStorage.keySet()) {
            String task = (String) readyStorage.get(key);
            finalStorage.put(Integer.parseInt((String) key), Managers.gson.fromJson(task, Epic.class));
        }
        return finalStorage;
    }
    private static String historyToString (HistoryManager manager) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Task task : manager.getHistory()) {
            stringBuilder.append(task.getId());
            stringBuilder.append(",");
        }
        if (!String.valueOf(stringBuilder).isEmpty()){
            stringBuilder.deleteCharAt(stringBuilder.length() -1);
        }
        String history = stringBuilder.toString();
        return history;
    }
}
