package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.HashMap;
import java.util.List;

public interface TaskManager {

    public void getTaskStorage();

    public void setTaskStorage(HashMap<Integer, Task> taskStorage);

    public void getSubTaskStorage();

    public void setSubTaskStorage(HashMap<Integer, Subtask> subTaskStorage);

    public void getEpicStorage();

    public void setEpicStorage(HashMap<Integer, Epic> epicStorage);

    public void createTask(String name, String description);

    public void createSubTask(String name, String description, Integer parentId);

    public void createEpic(String name, String description);

    public void idCounterPlus();

    public void printAll();

    public void printTask();

    public void printSubtask();

    public void printEpic();

    public void updateTask(Integer id, Task newTask);

    public void updateSubTask(Integer id, Subtask newSubTask);

    public void updateEpic(Integer id, Epic newEpic);

    public void addSubTaskToEpic(Integer parentId, Integer id);

    public void removeTask(Integer id);

    public void clearSubtask();

    public void clearTask();

    public void clearEpic();

    public void checkEpicStatus(Integer id);


}
