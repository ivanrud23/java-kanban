package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Epic;
import model.Subtask;
import model.Task;

import java.io.*;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class HttpTaskManager extends FileBackedTasksManager{
    private final KVTaskClient kvTaskClient = new KVTaskClient();
    private final URI url = URI.create("http://localhost:8078");

    public KVTaskClient getKvTaskClient() {
        return kvTaskClient;
    }

    public URI getUrl() {
        return url;
    }

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new HttpTaskServer.LocalDateAdapter().nullSafe())
            .create();

    public HttpTaskManager() throws IOException, InterruptedException {

    }

    private void saveToServer() throws IOException, NullPointerException, InterruptedException {
        kvTaskClient.put("task", gson.toJson(storageToJson(taskStorage)));
        kvTaskClient.put("subtask", gson.toJson(storageToJson(subTaskStorage)));
        kvTaskClient.put("epic", gson.toJson(storageToJson(epicStorage)));
        kvTaskClient.put("history", gson.toJson(historyToString(inMemoryHistoryManager)));
        kvTaskClient.put("prioritized", gson.toJson(prioritizedToJson(getPrioritizedTasks())));
    }
    public HttpTaskManager loadAllTasks() throws IOException, InterruptedException {
        HttpTaskManager newHttpTaskManager = new HttpTaskManager();
        String responseTask = kvTaskClient.load("task");
        newHttpTaskManager.taskStorage = taskStorageFromJson(responseTask);
        String responseEpic = kvTaskClient.load("epic");
        newHttpTaskManager.epicStorage = epicStorageFromJson(responseEpic);
        String responseSubtask = kvTaskClient.load("subtask");
        newHttpTaskManager.subTaskStorage = subTaskStorageFromJson(responseSubtask);
        String responsePrioritized = kvTaskClient.load("prioritized");
        List<Task> listOfTask= prioritizedFromJson(responsePrioritized);
        newHttpTaskManager.taskSortByTime.addAll(listOfTask);
        String responseHistory = kvTaskClient.load("history");
        String stringOfId = gson.fromJson(responseHistory, String.class);
        if (!stringOfId.equals("")) {
            String[] listOfId = stringOfId.split(",");
            for (String id : listOfId) {
                Integer taskId = Integer.parseInt(id.trim());
                Task task = newHttpTaskManager.getById(taskId);
            }
        }
        return newHttpTaskManager;
    }

    private List prioritizedToJson(List list) {
        List readyList = new ArrayList();
        for (Object task : list) {
            readyList.add(gson.toJson(task));
        }
        return readyList;
    }
    private List prioritizedFromJson(String json) {
        List readyList = new ArrayList();
        List list = gson.fromJson(json, List.class);
        for (Object task : list) {
            readyList.add(gson.fromJson((String) task, Task.class));
        }
        return readyList;
    }
    private HashMap storageToJson(HashMap storage) {
        HashMap readyStorage = new HashMap<>();
        for (Object key : storage.keySet()) {
            readyStorage.put(key, gson.toJson(storage.get(key)));
        }
        return readyStorage;
    }

    private HashMap taskStorageFromJson(String json) {
        HashMap readyStorage = gson.fromJson(json, HashMap.class);
        HashMap finalStorage = new HashMap<>();
        for (Object key : readyStorage.keySet()) {
            String task = (String) readyStorage.get(key);
            finalStorage.put(Integer.parseInt((String) key), gson.fromJson(task, Task.class));
        }
        return finalStorage;
    }
    private HashMap subTaskStorageFromJson(String json) {
        HashMap readyStorage = gson.fromJson(json, HashMap.class);
        HashMap finalStorage = new HashMap<>();
        for (Object key : readyStorage.keySet()) {
            String task = (String) readyStorage.get(key);
            finalStorage.put(Integer.parseInt((String) key), gson.fromJson(task, Subtask.class));
        }
        return finalStorage;
    }
    private HashMap epicStorageFromJson(String json) {
        HashMap readyStorage = gson.fromJson(json, HashMap.class);
        HashMap finalStorage = new HashMap<>();
        for (Object key : readyStorage.keySet()) {
            String task = (String) readyStorage.get(key);
            finalStorage.put(Integer.parseInt((String) key), gson.fromJson(task, Epic.class));
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

    @Override
    public void createTask(Task task) throws IOException, InterruptedException {
        super.createTask(task);
        saveToServer();
    }

    @Override
    public void createSubTask(Subtask subtask) throws IOException, InterruptedException {
        super.createSubTask(subtask);
        saveToServer();
    }

    @Override
    public void createEpic(Epic epic) throws IOException, InterruptedException {
        super.createEpic(epic);
        saveToServer();
    }

    @Override
    public void updateTask(Integer id, Task newTask) throws IOException, InterruptedException {
        super.updateTask(id, newTask);
        saveToServer();
    }

    @Override
    public void updateSubTask(Integer id, Subtask newSubTask) throws IOException, InterruptedException {
        super.updateSubTask(id, newSubTask);
        saveToServer();
    }

    @Override
    public void updateEpic(Integer id, Epic newEpic) throws IOException, InterruptedException {
        super.updateEpic(id, newEpic);
        saveToServer();
    }

    @Override
    public void addSubTaskToEpic(Integer parentId, Integer id) throws IOException, InterruptedException {
        super.addSubTaskToEpic(parentId, id);
        saveToServer();
    }

    @Override
    public void removeTask(Integer id) throws IOException, InterruptedException {
        super.removeTask(id);
        saveToServer();
    }

    @Override
    public void clearSubtask() throws IOException, InterruptedException {
        super.clearSubtask();
        saveToServer();
    }

    @Override
    public void clearTask() throws IOException, InterruptedException {
        super.clearTask();
        saveToServer();
    }

    @Override
    public void clearEpic() throws IOException, InterruptedException {
        super.clearEpic();
        saveToServer();
    }

    @Override
    public Task getById(Integer id) throws IOException, InterruptedException {
        saveToServer();
        return super.getById(id);
    }
}
