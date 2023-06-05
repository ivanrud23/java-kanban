package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;

public interface TaskManager {

    Task getById(Integer id) throws IOException;

    public List<Task> getTaskStorage() throws IOException;

    public List<Task> getSubTaskStorage() throws IOException;

    public List<Task> getEpicStorage() throws IOException;


    void createTask(Task task) throws IOException;

    void createTask(String name, String description) throws IOException;

    void createTask(String name, String description, String startTime, String duration) throws IOException;

    void createSubTask(Subtask subtask) throws IOException;

    void createSubTask(String name, String description, String startTime, String duration, Integer parentId) throws IOException;

    void createSubTask(String name, String description, Integer parentId) throws IOException;

    public void createEpic(Epic epic) throws IOException;

    void createEpic(String name, String description) throws IOException;

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
