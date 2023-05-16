package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public interface TaskManager {

    Task getById(Integer id) throws IOException;

    public List<Task> getTaskStorage();

    public List<Task> getSubTaskStorage();

    public List<Task> getEpicStorage();

    public void createTask(String name, String description) throws IOException;

    public void createTask(String name, String description, Integer id, Status status) throws IOException;

    public void createSubTask(String name, String description, Integer parentId) throws IOException;

    void createSubTask(String name, String description, Integer id, Status status, Integer parentId) throws IOException;

    void createEpic(String name, String description, Integer id, Status status) throws IOException;

    public void createEpic(String name, String description) throws IOException;

    public void idCounterPlus();

    public void printAll();

    public void printTask();

    public void printSubtask();

    public void printEpic();

    public void updateTask(Integer id, Task newTask) throws IOException;

    public void updateSubTask(Integer id, Subtask newSubTask) throws IOException;

    public void updateEpic(Integer id, Epic newEpic) throws IOException;

    public void addSubTaskToEpic(Integer parentId, Integer id) throws IOException;

    public void removeTask(Integer id) throws IOException;

    public void clearSubtask() throws IOException;

    public void clearTask() throws IOException;

    public void clearEpic() throws IOException;

    public void checkEpicStatus(Integer id);

}
