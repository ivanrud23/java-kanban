package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Manager {
    Integer idCounter = 1;
    HashMap<Integer, Task> taskStorage = new HashMap<>();
    HashMap<Integer, Subtask> subTaskStorage = new HashMap<>();
    HashMap<Integer, Epic> epicStorage = new HashMap<>();
    Scanner scanner = new Scanner(System.in);

    public HashMap<Integer, Task> getTaskStorage() {
        return taskStorage;
    }

    public void setTaskStorage(HashMap<Integer, Task> taskStorage) {
        this.taskStorage = taskStorage;
    }

    public HashMap<Integer, Subtask> getSubTaskStorage() {
        return subTaskStorage;
    }

    public void setSubTaskStorage(HashMap<Integer, Subtask> subTaskStorage) {
        this.subTaskStorage = subTaskStorage;
    }

    public HashMap<Integer, Epic> getEpicStorage() {
        return epicStorage;
    }

    public void setEpicStorage(HashMap<Integer, Epic> epicStorage) {
        this.epicStorage = epicStorage;
    }

    public void createTask(String name, String description) {
        Task task = new Task(name, description, idCounter);
        taskStorage.put(idCounter, task);
        idCounterPlus();
    }

    public void createSubTask(String name, String description, Integer parentId) {
        Subtask subtask = new Subtask(name, description, idCounter, parentId);
        subTaskStorage.put(idCounter, subtask);
        epicStorage.get(parentId).getChildren().add(idCounter);
        idCounterPlus();
    }

    public void createEpic(String name, String description) {
        Epic epic = new Epic(name, description, idCounter);
        epicStorage.put(idCounter, epic);
        idCounterPlus();
    }

    public void idCounterPlus() {
        idCounter++;
    }


    public void printAll() {
        printTask();
        printSubtask();
        printEpic();
    }

    public void printTask() {
        for (Integer id : taskStorage.keySet()) {
            System.out.println("Идентификатор — " + taskStorage.get(id).getId());
            System.out.println("Название — " + taskStorage.get(id).getName());
            System.out.println("Описание — " + taskStorage.get(id).getDescription());
            System.out.println("Статус — " + taskStorage.get(id).getStatus());
            System.out.println();
        }
    }

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

    public void updateTask(Integer id, Task newTask) {
        if (taskStorage.containsKey(id)) {
            newTask.setId(id);
            taskStorage.put(id, newTask);
        }
    }

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

    public void updateEpic(Integer id, Epic newEpic) {
        if (epicStorage.containsKey(id)) {
            List<Integer> childId = epicStorage.get(id).getChildren();
            newEpic.setStatus(epicStorage.get(id).getStatus());
            newEpic.setChildren(childId);
            newEpic.setId(id);
            epicStorage.put(id, newEpic);
        }
    }

    public void addSubTaskToEpic(Integer parentId, Integer id) {
        Epic epicTask = epicStorage.get(parentId);
        if (!epicTask.getChildren().contains(id)) {
            epicTask.getChildren().add(id);
        }
        epicStorage.put(parentId, epicTask);
        checkEpicStatus(parentId);
    }

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

    public void clearSubtask() {
        subTaskStorage.clear();
    }

    public void clearTask() {
        taskStorage.clear();
    }

    public void clearEpic() {
        epicStorage.clear();
    }

    public void checkEpicStatus(Integer id) {
        Epic epic = epicStorage.get(id);
        String newStatus = "";
        int newCount = 0;
        int progressCount = 0;
        int doneCount = 0;
        for (Integer subtaskId : epic.getChildren()) {
            if (subTaskStorage.get(subtaskId).getStatus().equals("NEW")) {
                newCount++;
            } else if (subTaskStorage.get(subtaskId).getStatus().equals("IN_PROGRESS")) {
                progressCount++;
            } else {
                doneCount++;
            }
        }
        if (progressCount > 0 || (newCount > 0 && doneCount > 0)) {
            newStatus = "IN_PROGRESS";
        } else if (newCount == 0 && doneCount > 0) {
            newStatus = "DONE";
        } else {
            newStatus = "NEW";
        }
        epic.setStatus(newStatus);
        epicStorage.put(id, epic);
    }
}
