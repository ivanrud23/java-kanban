package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.HashMap;
import java.util.List;

public interface TaskManager {

    Task getById(Integer id);

    public List<Task> getTaskStorage();

    public List<Task> getSubTaskStorage();

    public List<Task> getEpicStorage();

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
