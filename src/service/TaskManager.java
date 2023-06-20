package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.io.IOException;
import java.util.List;

public interface TaskManager {

    Task getById(Integer id) throws IOException, InterruptedException;

    public List<Task> getTaskStorage() throws IOException;

    public List<Task> getSubTaskStorage() throws IOException;

    public List<Task> getEpicStorage() throws IOException;

    public void save() throws IOException, NullPointerException, InterruptedException;

    void createTask(Task task) throws IOException, InterruptedException;

    void createSubTask(Subtask subtask) throws IOException, InterruptedException;

    public void createEpic(Epic epic) throws IOException, InterruptedException;

    public void idCounterPlus();

    public Integer idCounter();

    public void printAll();

    public void printTask();

    public void printSubtask();

    public void printEpic();

    public void updateTask(Integer id, Task newTask) throws IOException, InterruptedException;

    public void updateSubTask(Integer id, Subtask newSubTask) throws IOException, InterruptedException;

    public void updateEpic(Integer id, Epic newEpic) throws IOException, InterruptedException;

    public void addSubTaskToEpic(Integer parentId, Integer id) throws IOException, InterruptedException;

    public void removeTask(Integer id) throws IOException, InterruptedException;

    public void clearSubtask() throws IOException, InterruptedException;

    public void clearTask() throws IOException, InterruptedException;

    public void clearEpic() throws IOException, InterruptedException;

    public void checkEpicStatus(Integer id);


}
