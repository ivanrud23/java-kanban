package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager{
    Integer idCounter = 1;
    HashMap<Integer, Task> taskStorage = new HashMap<>();
    HashMap<Integer, Subtask> subTaskStorage = new HashMap<>();
    HashMap<Integer, Epic> epicStorage = new HashMap<>();
    InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();

    @Override
    public Task getById(Integer id) {
        Task task;
        if (taskStorage.containsKey(id)) {
            task = taskStorage.get(id);
        } else if (subTaskStorage.containsKey(id)) {
            task = subTaskStorage.get(id);
        } else {
            task = epicStorage.get(id);
        }
        return task;
    }
    @Override
    public List<Task> getTaskStorage() {
        List<Task> taskId = new ArrayList<>(taskStorage.values());
        for (Task task : taskId) {
            inMemoryHistoryManager.add(task);
        }
        return taskId;
    }

    @Override
    public List<Task> getSubTaskStorage() {
        List<Task> taskId = new ArrayList<>(subTaskStorage.values());
        for (Task task : taskId) {
            inMemoryHistoryManager.add(task);
        }
        return taskId;
    }

    @Override
    public List<Task> getEpicStorage() {
        List<Task> taskId = new ArrayList<>(epicStorage.values());
        for (Task task : taskId) {
            inMemoryHistoryManager.add(task);
        }
        return taskId;
    }

    @Override
    public void createTask(String name, String description) {
        Task task = new Task(name, description, idCounter);
        taskStorage.put(idCounter, task);
        idCounterPlus();
    }

    @Override
    public void createSubTask(String name, String description, Integer parentId) {
        Subtask subtask = new Subtask(name, description, idCounter, parentId);
        subTaskStorage.put(idCounter, subtask);
        epicStorage.get(parentId).getChildren().add(idCounter);
        idCounterPlus();
    }

    @Override
    public void createEpic(String name, String description) {
        Epic epic = new Epic(name, description, idCounter);
        epicStorage.put(idCounter, epic);
        idCounterPlus();
    }

    @Override
    public void idCounterPlus() {
        idCounter++;
    }

    @Override
    public void printAll() {
        printTask();
        printSubtask();
        printEpic();
    }

    @Override
    public void printTask() {
        for (Integer id : taskStorage.keySet()) {
            System.out.println("Идентификатор — " + taskStorage.get(id).getId());
            System.out.println("Название — " + taskStorage.get(id).getName());
            System.out.println("Описание — " + taskStorage.get(id).getDescription());
            System.out.println("Статус — " + taskStorage.get(id).getStatus());
            System.out.println();
        }
    }

    @Override
    public void printSubtask() {
        for (Integer id : subTaskStorage.keySet()) {
            System.out.println("Идентификатор — " + subTaskStorage.get(id).getId());
            System.out.println("Название — " + subTaskStorage.get(id).getName());
            System.out.println("Описание — " + subTaskStorage.get(id).getDescription());
            System.out.println("Статус — " + subTaskStorage.get(id).getStatus());
            System.out.println("Родительский эпик — " + subTaskStorage.get(id).getParentId());
            System.out.println();
        }
    }

    @Override
    public void printEpic() {
        for (Integer id : epicStorage.keySet()) {
            System.out.println("Идентификатор — " + epicStorage.get(id).getId());
            System.out.println("Название — " + epicStorage.get(id).getName());
            System.out.println("Описание — " + epicStorage.get(id).getDescription());
            System.out.println("Статус — " + epicStorage.get(id).getStatus());
            System.out.println("Подзадачи — " + epicStorage.get(id).getChildren());
            System.out.println();
        }
    }

    @Override
    public void updateTask(Integer id, Task newTask) {
        if (taskStorage.containsKey(id)) {
            newTask.setId(id);
            taskStorage.put(id, newTask);
        }
    }

    @Override
    public void updateSubTask(Integer id, Subtask newSubTask) {
        if (subTaskStorage.containsKey(id)) {
            int parentId = subTaskStorage.get(id).getParentId();
            newSubTask.setParentId(parentId);
            newSubTask.setId(id);
            subTaskStorage.put(id, newSubTask);
            addSubTaskToEpic(parentId, id);
            checkEpicStatus(newSubTask.getParentId());
        }
    }

    @Override
    public void updateEpic(Integer id, Epic newEpic) {
        if (epicStorage.containsKey(id)) {
            List<Integer> childId = epicStorage.get(id).getChildren();
            newEpic.setStatus(epicStorage.get(id).getStatus());
            newEpic.setChildren(childId);
            newEpic.setId(id);
            epicStorage.put(id, newEpic);
        }
    }

    @Override
    public void addSubTaskToEpic(Integer parentId, Integer id) {
        Epic epicTask = epicStorage.get(parentId);
        if (!epicTask.getChildren().contains(id)) {
            epicTask.getChildren().add(id);
        }
        epicStorage.put(parentId, epicTask);
        checkEpicStatus(parentId);
    }

    @Override
    public void removeTask(Integer id) {
        if (epicStorage.containsKey(id)) {
            List<Integer> childId = epicStorage.get(id).getChildren();
            for (Object child : childId) {
                subTaskStorage.remove(child);
            }
            epicStorage.remove(id);
        } else if (subTaskStorage.containsKey(id)) {
            Integer parentId = subTaskStorage.get(id).getParentId();
            Epic epic = epicStorage.get(parentId);
            epic.getChildren().remove(id);
            epicStorage.put(parentId, epic);
            subTaskStorage.remove(id);
        } else if (taskStorage.containsKey(id)) {
            taskStorage.remove(id);
        } else {
            System.out.println("Задачи с этим номер еще не создана");
        }
    }

    @Override
    public void clearSubtask() {
        subTaskStorage.clear();
        for (Epic epic : epicStorage.values()) {
            epic.getChildren().clear();
        }
    }

    @Override
    public void clearTask() {
        taskStorage.clear();
    }

    @Override
    public void clearEpic() {
        epicStorage.clear();
        subTaskStorage.clear();
    }

    @Override
    public void checkEpicStatus(Integer id) {
        Epic epic = epicStorage.get(id);
        Status newStatus = Status.NEW;
        int newCount = 0;
        int progressCount = 0;
        int doneCount = 0;
        for (Integer subtaskId : epic.getChildren()) {
            if (subTaskStorage.get(subtaskId).getStatus().equals(Status.NEW)) {
                newCount++;
            } else if (subTaskStorage.get(subtaskId).getStatus().equals(Status.IN_PROGRESS)) {
                progressCount++;
            } else {
                doneCount++;
            }
        }
        if (progressCount > 0 || (newCount > 0 && doneCount > 0)) {
            newStatus = Status.IN_PROGRESS;
        } else if (newCount == 0 && doneCount > 0) {
            newStatus = Status.DONE;
        }
        epic.setStatus(newStatus);
        epicStorage.put(id, epic);
    }

}