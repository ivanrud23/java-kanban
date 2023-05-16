package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static service.TaskType.*;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {



    public FileBackedTasksManager() throws IOException {
    }


    public void save() throws IOException {
        Writer fileWriter = new FileWriter("history.csv");
        StringBuilder stringBuilder = new StringBuilder();
        for (Task task : this.taskStorage.values()) {
            stringBuilder.append(toString(task));
        }
        for (Task task : this.epicStorage.values()) {
            stringBuilder.append(toString(task));
        }
        for (Task task : this.subTaskStorage.values()) {
            stringBuilder.append(toString(task));
        }

        try {
            stringBuilder.deleteCharAt(stringBuilder.length() - 2);
        } catch (StringIndexOutOfBoundsException e) {
        }
        fileWriter.write(String.valueOf(stringBuilder));

        fileWriter.write("\n");
        fileWriter.append(historyToString(this.getInMemoryHistoryManager()));

        fileWriter.write("\n");
        fileWriter.close();
    }


    public String toString(Task task){
        TaskType taskType;
        if (String.valueOf(task.getClass()).equals("class model.Subtask")) {
            taskType = SUBTASK;
            Subtask subtask = (Subtask) task;
            return task.getId() + ", " + taskType + ", " + task.getName() + ", " + task.getStatus() + ", "
                    + task.getDescription() + ", " + subtask.getParentId() + "," + "\n";
        } else if (String.valueOf(task.getClass()).equals("class model.Epic")) {
            taskType = EPIC;
            return task.getId() + ", " + taskType + ", " + task.getName() + ", " + task.getStatus() + ", "
                    + task.getDescription() + "," + "\n";
        } else {
            taskType = TASK;
            return task.getId() + ", " + taskType + ", " + task.getName() + ", " + task.getStatus() + ", "
                    + task.getDescription() + "," + "\n";
        }

    }

    public Task fromString(String value) {
        String[] taskLines = value.split(",");
        int id = Integer.parseInt(taskLines[0]);
        String name = taskLines[2];
        Status status = Status.valueOf(taskLines[3]);
        String description = taskLines[4];
        int epic = Integer.parseInt(taskLines[5]);
        Task task = new Task(name, description, id, status, epic);
        return task;
    }

    static String historyToString (HistoryManager manager) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Task task : manager.getHistory()) {
            stringBuilder.append(task.getId());
            stringBuilder.append(",");
        }
         try {
             stringBuilder.deleteCharAt(stringBuilder.length() -1);
         } catch (StringIndexOutOfBoundsException e) {

         }
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }

    static List<Integer> historyFromString(String value) {
        List<Integer> history = new ArrayList<>();
        for (String number : value.split(",")) {
            history.add(Integer.parseInt(number));
        }
        return history;
    }

    static FileBackedTasksManager loadFromFile(File file) throws IOException {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager();
        Reader fileReader = new FileReader(file);
        BufferedReader br = new BufferedReader(fileReader);
        while (br.ready()) {
            String[] line = br.readLine().split(",");
            try {
                if (line[1].trim().equals("TASK")) {
                    fileBackedTasksManager.createTask(line[2].trim(), line[4].trim(), Integer.parseInt(line[0].trim()), Status.valueOf(line[3].trim()));
                } else if (line[1].trim().equals("EPIC")) {
                    fileBackedTasksManager.createEpic(line[2].trim(), line[4].trim(), Integer.parseInt(line[0].trim()), Status.valueOf(line[3].trim()));
                } else if (line[1].trim().equals("SUBTASK")) {
                    fileBackedTasksManager.createSubTask(line[2].trim(), line[4].trim(), Integer.parseInt(line[0].trim()), Status.valueOf(line[3].trim()),
                            Integer.parseInt(line[5].trim()));
                } else {
                    for (String id : line) {
                        fileBackedTasksManager.getById(Integer.parseInt(id));
                    }
                }
            } catch (ArrayIndexOutOfBoundsException e) {}
        }
        return fileBackedTasksManager;
    }

    @Override
    public void createTask(String name, String description) throws IOException {
        super.createTask(name, description);
        save();
    }

    @Override
    public void createTask(String name, String description, Integer id, Status status) throws IOException {
        super.createTask(name, description, id, status);
        save();
    }

    @Override
    public void createSubTask(String name, String description, Integer parentId) throws IOException {
        super.createSubTask(name, description, parentId);
        save();
    }

    @Override
    public void createSubTask(String name, String description, Integer id, Status status, Integer parentId) throws IOException {
        super.createSubTask(name, description, id, status, parentId);
        save();
    }

    @Override
    public void createEpic(String name, String description, Integer id, Status status) throws IOException {
        super.createEpic(name, description, id, status);
        save();
    }

    @Override
    public void createEpic(String name, String description) throws IOException {
        super.createEpic(name, description);
        save();
    }

    @Override
    public void updateTask(Integer id, Task newTask) throws IOException {
        super.updateTask(id, newTask);
        save();
    }

    @Override
    public void updateSubTask(Integer id, Subtask newSubTask) throws IOException {
        super.updateSubTask(id, newSubTask);
        save();
    }

    @Override
    public void updateEpic(Integer id, Epic newEpic) throws IOException {
        super.updateEpic(id, newEpic);
        save();
    }

    @Override
    public void addSubTaskToEpic(Integer parentId, Integer id) throws IOException {
        super.addSubTaskToEpic(parentId, id);
        save();
    }

    @Override
    public void removeTask(Integer id) throws IOException {
        super.removeTask(id);
        save();
    }

    @Override
    public void clearSubtask() throws IOException {
       super.clearSubtask();
       save();
    }

    @Override
    public void clearTask() throws IOException {
        super.clearTask();
        save();
    }

    @Override
    public void clearEpic() throws IOException {
        super.clearEpic();
        save();
    }

    @Override
    public Task getById(Integer id) throws IOException {
        Task task;
        if (taskStorage.containsKey(id)) {
            task = taskStorage.get(id);
        } else if (subTaskStorage.containsKey(id)) {
            task = subTaskStorage.get(id);
        } else {
            task = epicStorage.get(id);
        }
        getInMemoryHistoryManager().add(task);
        save();
        return task;
    }

    public static void main(String[] args) throws IOException {
        FileBackedTasksManager fileBackedTasksManager1 = new FileBackedTasksManager();


        fileBackedTasksManager1.createTask("Task_1", "Desk_task_1");
        fileBackedTasksManager1.createTask("Task_2", "Desk_task_2");
        fileBackedTasksManager1.createEpic("Epic_1", "Desk_Epic_1");
        fileBackedTasksManager1.createEpic("Epic_2", "Desk_Epic_2");
        fileBackedTasksManager1.createSubTask("Sub_1", "Desk_Sub_1", 3);
        fileBackedTasksManager1.createSubTask("Sub_2", "Desk_Sub_2", 3);
        fileBackedTasksManager1.createSubTask("Sub_3", "Desk_Sub_3", 3);

        fileBackedTasksManager1.getById(6);
        fileBackedTasksManager1.getById(5);
        fileBackedTasksManager1.getById(3);
        fileBackedTasksManager1.getById(4);
        fileBackedTasksManager1.getById(1);
        fileBackedTasksManager1.getById(2);
        fileBackedTasksManager1.getById(7);
        fileBackedTasksManager1.getInMemoryHistoryManager().getHistory();
        fileBackedTasksManager1.getById(5);
        fileBackedTasksManager1.getById(6);
        fileBackedTasksManager1.getById(3);
        fileBackedTasksManager1.getById(4);
        fileBackedTasksManager1.getById(1);
        fileBackedTasksManager1.getById(7);
        fileBackedTasksManager1.getById(2);
        fileBackedTasksManager1.getInMemoryHistoryManager().getHistory();
        fileBackedTasksManager1.getById(3);
        fileBackedTasksManager1.getById(4);
        fileBackedTasksManager1.getById(5);
        fileBackedTasksManager1.getById(2);
        fileBackedTasksManager1.getById(6);
        fileBackedTasksManager1.getById(4);
        fileBackedTasksManager1.getById(1);
        fileBackedTasksManager1.getById(6);
        fileBackedTasksManager1.getById(5);
        fileBackedTasksManager1.getById(3);
        fileBackedTasksManager1.getById(4);
        fileBackedTasksManager1.getById(1);
        fileBackedTasksManager1.getById(2);
        fileBackedTasksManager1.getById(7);
        fileBackedTasksManager1.getInMemoryHistoryManager().getHistory();

        File file = new File("history.csv");
        FileBackedTasksManager fileBackedTasksManager2 = loadFromFile(file);
        fileBackedTasksManager2.removeTask(3);
        fileBackedTasksManager2.getInMemoryHistoryManager().getHistory();
        fileBackedTasksManager2.removeTask(2);
        fileBackedTasksManager2.getInMemoryHistoryManager().getHistory();

        fileBackedTasksManager2.createTask("Task_3", "Desk_task_3");
        fileBackedTasksManager2.createTask("Task_4", "Desk_task_4");
        fileBackedTasksManager2.getById(9);
        fileBackedTasksManager2.getById(8);

    }

}


