package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.AbstractList;
import java.util.HashMap;
import java.util.InputMismatchException;
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
        idCounter++;
    }

    public void createSubTask(String name, String description, Integer parentId) {
        Subtask subtask = new Subtask(name, description, idCounter, parentId);
        subTaskStorage.put(idCounter, subtask);
        epicStorage.get(parentId).getChildren().add(idCounter);
        idCounter++;
    }

    public void createEpic(String name, String description) {
        Epic epic = new Epic(name, description, idCounter);
        epicStorage.put(idCounter, epic);
        idCounter++;
    }

    public void printAll(){
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

    public void changeTask(Integer id) {
        Object task = null;
        int flag = 0;
        if (taskStorage.containsKey(id)) {
            task = taskStorage.get(id);
            flag = 1;
        } else if (subTaskStorage.containsKey(id)) {
            task = subTaskStorage.get(id);
            flag = 2;
        } else if (epicStorage.containsKey(id)) {
            task = epicStorage.get(id);
            flag = 3;
        }
        while (true) {
            changeMenu();
            int command = scanUserInputSafely(scanner);
            switch (command) {
                case -1:
                    System.out.println("Введена несуществующая команда");
                    break;
                case 1:
                    System.out.println("Введите новое имя");
                    String newName = scanner.next();
                    if (flag == 1) {
                        ((Task) task).setName(newName);
                        taskStorage.put(id, (Task) task);
                    } else if (flag == 2) {
                        ((Subtask) task).setName(newName);
                        subTaskStorage.put(id, (Subtask) task);
                    } else if (flag == 3) {
                        ((Epic) task).setName(newName);
                        epicStorage.put(id, (Epic) task);
                    }
                    break;
                case 2:
                    String newDescription = scanner.next();
                    if (flag == 1) {
                        ((Task) task).setDescription(newDescription);
                        taskStorage.put(id, (Task) task);
                    } else if (flag == 2) {
                        ((Subtask) task).setDescription(newDescription);
                        subTaskStorage.put(id, (Subtask) task);
                    } else if (flag == 3) {
                        ((Epic) task).setDescription(newDescription);
                        epicStorage.put(id, (Epic) task);
                    }
                    break;
                case 3:
                    if (flag == 3) {
                        System.out.println("Введите номер эпика");
                        Integer parentId = scanner.nextInt();
                        ((Subtask) task).setParentId(parentId);
                        subTaskStorage.put(id, (Subtask) task);
                        addSubtask(parentId, id);
                    } else {
                        System.out.println("Невозможно прикрпеить данную задачу");
                    }
                    break;
                case 4:
                    if (flag == 2) {
                        System.out.println("Введите номер подзадачи");
                        Integer childId = scanner.nextInt();
                        addSubtask(id, childId);
                        addChild(id, childId);
                    }
                    break;
                case 5:
                    scanner.nextLine();
                    String newStatus = scanner.nextLine();
                    if (flag == 1) {
                        ((Task) task).setStatus(newStatus);
                        taskStorage.put(id, (Task) task);
                     } else if (flag == 2) {
                        ((Subtask) task).setStatus(newStatus);
                        subTaskStorage.put(id, (Subtask) task);
                        checkEpicStatus(subTaskStorage.get(id).getParentId());
                    } else if (flag == 3) {
                        System.out.println("Вы не можете менять статус эпика");
                    }
                    break;
                case 0:
                    return;
            }

        }
    }

    public void changeMenu() {
        System.out.println("Введите номер команды");
        System.out.println("1. - Изменить имя");
        System.out.println("2. - Изменить описание");
        System.out.println("3. - Прикрепить к эпику");
        System.out.println("4. - Прикрепить подзадачу");
        System.out.println("5. - Изменить статус");
        System.out.println("0. - Завершить изменения");
    }

    public void addSubtask(Integer parentId, Integer id) {
        Epic epicTask = epicStorage.get(parentId);
        epicTask.getChildren().add(id);
        epicStorage.put(parentId, epicTask);
        checkEpicStatus(parentId);
    }

    public void addChild(Integer id, Integer childId) {
        Subtask subtask = subTaskStorage.get(childId);
        subtask.setParentId(id);
        subTaskStorage.put(childId, subtask);
    }

    public void removeTask(Integer id) {
        if (epicStorage.containsKey(id)) {
            AbstractList<Integer> childId = epicStorage.get(id).getChildren();
            for (Object child : childId) {
                subTaskStorage.remove(child);
            }
            epicStorage.remove(id);
        } else if (subTaskStorage.containsKey(id)) {
            subTaskStorage.remove(id);
            Integer parentId = subTaskStorage.get(id).getParentId();
            Epic epic = epicStorage.get(parentId);
            epic.getChildren().remove(id);
            epicStorage.put(parentId, epic);
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
        for (Object subtaskId : epic.getChildren()) {
            if (subTaskStorage.get(subtaskId).getStatus().equals("NEW")) {
                newCount++;
            } else if (subTaskStorage.get(subtaskId).getStatus().equals("IN_PROGRESS")){
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

    public static int scanUserInputSafely(Scanner scanner) {
        try {
            return scanner.nextInt();
        } catch (InputMismatchException ime) {
            scanner.nextLine();
            return -1;
        }
    }

    public Object checkId(Integer id) {
        if (0 < id && id < idCounter) {
            return true;
        } else {
            System.out.println("Задачи с таким номером не существует");
            return false;
        }
    }

}
