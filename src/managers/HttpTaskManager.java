package managers;

import model.Epic;
import model.Subtask;
import model.Task;
import client.KVTaskClient;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpTaskManager extends FileBackedTasksManager {
    private final KVTaskClient kvTaskClient = new KVTaskClient();
    private final URI url = URI.create("http://localhost:8078");
//    private Managers managers = new Managers();


//    public HttpTaskManager(HttpTaskManager httpTaskManager) throws IOException, InterruptedException {
//        loadAllTasks(httpTaskManager);
//    }

    public HttpTaskManager() throws IOException, InterruptedException {
        loadAllTasks(this);
    }

    public KVTaskClient getKvTaskClient() {
        return kvTaskClient;
    }
    public URI getUrl() {
        return url;
    }



    @Override
    protected void save() throws IOException, NullPointerException, InterruptedException {
        kvTaskClient.put("task", Managers.getGson().toJson(storageToJson(taskStorage)));
        kvTaskClient.put("subtask", Managers.getGson().toJson(storageToJson(subTaskStorage)));
        kvTaskClient.put("epic", Managers.getGson().toJson(storageToJson(epicStorage)));
        kvTaskClient.put("history", Managers.getGson().toJson(historyToString(inMemoryHistoryManager)));
        kvTaskClient.put("prioritized", Managers.getGson().toJson(prioritizedToJson(getPrioritizedTasks())));
    }
    private void loadAllTasks(HttpTaskManager newHttpTaskManager) throws IOException, InterruptedException {
//        HttpTaskManager newHttpTaskManager = new HttpTaskManager();
        try {
            String responseTask = kvTaskClient.load("task");
            newHttpTaskManager.taskStorage = (HashMap<Integer, Task>) taskStorageFromJson(responseTask);
            String responseEpic = kvTaskClient.load("epic");
            newHttpTaskManager.epicStorage = (HashMap<Integer, Epic>) epicStorageFromJson(responseEpic);
            String responseSubtask = kvTaskClient.load("subtask");
            newHttpTaskManager.subTaskStorage = (HashMap<Integer, Subtask>) subTaskStorageFromJson(responseSubtask);
            String responsePrioritized = kvTaskClient.load("prioritized");
            List<Task> listOfTask = prioritizedFromJson(responsePrioritized);
            newHttpTaskManager.taskSortByTime.addAll(listOfTask);
            String responseHistory = kvTaskClient.load("history");
            String stringOfId = Managers.getGson().fromJson(responseHistory, String.class);
            if (!stringOfId.equals("")) {
                String[] listOfId = stringOfId.split(",");
                for (String id : listOfId) {
                    Integer taskId = Integer.parseInt(id.trim());
                    Task task = newHttpTaskManager.getById(taskId);
                }
            }
        } catch (IOException e) {

        }
//        return newHttpTaskManager;
    }
    private List<Task> prioritizedToJson(List list) {
        List readyList = new ArrayList();
        for (Object task : list) {
            readyList.add(Managers.getGson().toJson(task));
        }
        return readyList;
    }
    private List<Task> prioritizedFromJson(String json) {
        List readyList = new ArrayList();
        List list = Managers.getGson().fromJson(json, List.class);
        for (Object task : list) {
            readyList.add(Managers.getGson().fromJson((String) task, Task.class));
        }
        return readyList;
    }
    private Map<Integer, Task> storageToJson(HashMap storage) {
        HashMap readyStorage = new HashMap<>();
        for (Object key : storage.keySet()) {
            readyStorage.put(key, Managers.getGson().toJson(storage.get(key)));
        }
        return readyStorage;
    }
    private Map<Integer, Task> taskStorageFromJson(String json) {
        HashMap readyStorage = Managers.getGson().fromJson(json, HashMap.class);
        HashMap finalStorage = new HashMap<>();
        for (Object key : readyStorage.keySet()) {
            String task = (String) readyStorage.get(key);
            finalStorage.put(Integer.parseInt((String) key), Managers.getGson().fromJson(task, Task.class));
        }
        return finalStorage;
    }
    private Map<Integer, Subtask> subTaskStorageFromJson(String json) {
        HashMap readyStorage = Managers.getGson().fromJson(json, HashMap.class);
        HashMap finalStorage = new HashMap<>();
        for (Object key : readyStorage.keySet()) {
            String task = (String) readyStorage.get(key);
            finalStorage.put(Integer.parseInt((String) key), Managers.getGson().fromJson(task, Subtask.class));
        }
        return finalStorage;
    }
    private Map<Integer, Epic> epicStorageFromJson(String json) {
        HashMap readyStorage = Managers.getGson().fromJson(json, HashMap.class);
        HashMap finalStorage = new HashMap<>();
        for (Object key : readyStorage.keySet()) {
            String task = (String) readyStorage.get(key);
            finalStorage.put(Integer.parseInt((String) key), Managers.getGson().fromJson(task, Epic.class));
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
