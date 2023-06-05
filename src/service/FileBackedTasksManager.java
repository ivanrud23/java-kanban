package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;


import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static service.TaskType.*;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {

    DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private void saveToFile() throws IOException, NullPointerException{
        Writer fileWriter = new FileWriter("history.csv");
        StringBuilder stringBuilder = new StringBuilder();
        for (Task task : this.taskStorage.values()) {
            stringBuilder.append(taskToString(task));
        }
        for (Task task : this.epicStorage.values()) {
            stringBuilder.append(taskToString(task));
        }
        for (Task task : this.subTaskStorage.values()) {
            stringBuilder.append(taskToString(task));
        }

        if (!String.valueOf(stringBuilder).isEmpty()){
            stringBuilder.deleteCharAt(stringBuilder.length() - 2);
        }
        fileWriter.write(String.valueOf(stringBuilder));

        fileWriter.write("\n");
        fileWriter.append(historyToString(this.inMemoryHistoryManager));

        fileWriter.write("\n");
        fileWriter.close();
    }

    private String taskToString(Task task) throws NullPointerException{
        TaskType taskType;
        if (task.getClass().getName().equals("model.Subtask")) {
            taskType = SUBTASK;
            Subtask subtask = (Subtask) task;
            String startString = task.getId() + ", " + taskType + ", " + task.getName() + ", " + task.getStatus() + ", "
                    + task.getDescription();
            String endString = ", " + subtask.getParentId() + "," + "\n";
            String timeString;
            if (task.getStartTime() != null) {
                timeString = ", " + task.getStartTime().format(outputFormat) + ", " + (task.getDuration()) ;
                return startString + timeString + endString;
            } else {
                return startString + endString;
            }
        } else if (task.getClass().getName().equals("model.Epic")) {
            taskType = EPIC;
            String startString = task.getId() + ", " + taskType + ", " + task.getName() + ", " + task.getStatus() + ", "
                    + task.getDescription();
            String endString = "," + "\n";
            String timeString;
            if (task.getStartTime() != null) {
                timeString = ", " + task.getStartTime().format(outputFormat) + ", " + (task.getDuration());
                return startString + timeString + endString;
            } else {
                return startString + endString;
            }
        } else {
            taskType = TASK;
            String startString = task.getId() + ", " + taskType + ", " + task.getName() + ", " + task.getStatus() + ", "
                    + task.getDescription();
            String endString = "," + "\n";
            String timeString;
            if (task.getStartTime() != null) {
                timeString = ", " + task.getStartTime().format(outputFormat) + ", " + (task.getDuration());
                return startString + timeString + endString;
            } else {
                return startString + endString;
            }
        }
    }


    private Task fromString(String value) throws IOException, ArrayIndexOutOfBoundsException {
        String[] taskLines = value.split(",");
        if (taskLines.length > 6) {
            if (taskLines[1].trim().equals("TASK")) {
                Task task = new Task(taskLines[2].trim(), taskLines[4].trim(), Integer.parseInt(taskLines[0].trim()),
                        Status.valueOf(taskLines[3].trim()), taskLines[5].trim(), taskLines[6].trim());
                return task;
            } else if ((taskLines[1].trim().equals("EPIC"))) {
                Epic epic = new Epic(taskLines[2].trim(), taskLines[4].trim(), Integer.parseInt(taskLines[0].trim()),
                        Status.valueOf(taskLines[3].trim()), taskLines[5].trim(), taskLines[6].trim());
                return epic;
            } else {
                Subtask subtask = new Subtask(taskLines[2].trim(), taskLines[4].trim(), Integer.parseInt(taskLines[0].trim()),
                        Status.valueOf(taskLines[3].trim()), taskLines[5].trim(), taskLines[6].trim(), Integer.parseInt(taskLines[7].trim()));
                return subtask;
            }
        } else {
            if (taskLines[1].trim().equals("TASK")) {
                Task task = new Task(taskLines[2].trim(), taskLines[4].trim(), Integer.parseInt(taskLines[0].trim()),
                        Status.valueOf(taskLines[3].trim()));
                return task;
            } else if ((taskLines[1].trim().equals("EPIC"))) {
                Epic epic = new Epic(taskLines[2].trim(), taskLines[4].trim(), Integer.parseInt(taskLines[0].trim()),
                        Status.valueOf(taskLines[3].trim()));
                return epic;
            } else {
                Subtask subtask = new Subtask(taskLines[2].trim(), taskLines[4].trim(), Integer.parseInt(taskLines[0].trim()),
                        Status.valueOf(taskLines[3].trim()), Integer.parseInt(taskLines[5].trim()));
                return subtask;
            }
        }
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

        return stringBuilder.toString();
    }

    private static List<Integer> historyFromString(String value) {
        List<Integer> history = new ArrayList<>();
        for (String number : value.split(",")) {
            history.add(Integer.parseInt(number));
        }
        return history;
    }

    public static FileBackedTasksManager loadFromFile(File file) throws IOException {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager();
        Reader fileReader = new FileReader(file);
        BufferedReader br = new BufferedReader(fileReader);
        while (br.ready()) {
            String line = br.readLine();
            if (!line.isEmpty()) {
                if (line.split(",")[1].trim().equals("TASK")) {
                    fileBackedTasksManager.createTask(fileBackedTasksManager.fromString(line));
                } else if (line.split(",")[1].trim().equals("SUBTASK")) {
                    fileBackedTasksManager.createSubTask((Subtask) fileBackedTasksManager.fromString(line));
                } else if (line.split(",")[1].trim().equals("EPIC")) {
                    fileBackedTasksManager.createEpic((Epic) fileBackedTasksManager.fromString(line));
                } else {
                    String[] taskLines = line.split(",");
                    for (String id : taskLines) {
                        fileBackedTasksManager.getById(Integer.parseInt(id));
                    }
                }
            }
        }
        return fileBackedTasksManager;
    }

    @Override
    public void createTask(Task task) throws IOException {
        super.createTask(task);
        saveToFile();
    }

    @Override
    public void createTask(String name, String description) throws IOException {
        super.createTask(name, description);
        saveToFile();
    }

    @Override
    public void createTask(String name, String description, String startTime, String duration) throws IOException {
        super.createTask(name, description, startTime, duration);
        saveToFile();
    }

    @Override
    public void createSubTask(Subtask subtask) throws IOException {
        super.createSubTask(subtask);
        saveToFile();
    }

    @Override
    public void createSubTask(String name, String description, String startTime, String duration, Integer parentId) throws IOException {
        super.createSubTask(name, description, startTime, duration, parentId);
        saveToFile();
    }

    @Override
    public void createSubTask(String name, String description, Integer parentId) throws IOException {
        super.createSubTask(name, description, parentId);
        saveToFile();
    }

    @Override
    public void createEpic(Epic epic) throws IOException {
        super.createEpic(epic);
        saveToFile();
    }

    @Override
    public void createEpic(String name, String description) throws IOException {
        super.createEpic(name, description);
        saveToFile();
    }

    @Override
    public void updateTask(Integer id, Task newTask) throws IOException {
        super.updateTask(id, newTask);
        saveToFile();
    }

    @Override
    public void updateSubTask(Integer id, Subtask newSubTask) throws IOException {
        super.updateSubTask(id, newSubTask);
        saveToFile();
    }

    @Override
    public void updateEpic(Integer id, Epic newEpic) throws IOException {
        super.updateEpic(id, newEpic);
        saveToFile();
    }

    @Override
    public void addSubTaskToEpic(Integer parentId, Integer id) throws IOException {
        super.addSubTaskToEpic(parentId, id);
        saveToFile();
    }

    @Override
    public void removeTask(Integer id) throws IOException {
        super.removeTask(id);
        saveToFile();
    }

    @Override
    public void clearSubtask() throws IOException {
       super.clearSubtask();
       saveToFile();
    }

    @Override
    public void clearTask() throws IOException {
        super.clearTask();
        saveToFile();
    }

    @Override
    public void clearEpic() throws IOException {
        super.clearEpic();
        saveToFile();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return super.getPrioritizedTasks();
    }

    @Override
    public Task getById(Integer id) throws IOException {
        Task task;
        if (taskStorage.containsKey(id)) {
            task = taskStorage.get(id);
        } else if (subTaskStorage.containsKey(id)) {
            task = subTaskStorage.get(id);
        } else if (epicStorage.containsKey(id)){
            task = epicStorage.get(id);
        } else {
            throw new IOException("Введен несуществующий идентификатор");
        }
        inMemoryHistoryManager.add(task);
        saveToFile();
        return task;
    }

    public static void main(String[] args) throws IOException {
        FileBackedTasksManager fileBackedTasksManager1 = new FileBackedTasksManager();


        fileBackedTasksManager1.createTask("Task_1", "Desk_task_1", "01.05.2023 10:00", "PT10M");
        fileBackedTasksManager1.createTask("Task_2", "Desk_task_2");
        fileBackedTasksManager1.createEpic("Epic_1", "Desk_Epic_1");
        fileBackedTasksManager1.createEpic("Epic_2", "Desk_Epic_2");
        fileBackedTasksManager1.createSubTask("Sub_1", "Desk_Sub_1", "01.05.2023 10:01", "PT10M", 3);
        fileBackedTasksManager1.createSubTask("Sub_2", "Desk_Sub_2", 3);
        fileBackedTasksManager1.createSubTask("Sub_3", "Desk_Sub_3", "01.05.2023 10:02", "PT10M", 3);

        fileBackedTasksManager1.getById(6);
        fileBackedTasksManager1.getById(5);
        fileBackedTasksManager1.getById(3);
        fileBackedTasksManager1.getById(4);
        fileBackedTasksManager1.getById(1);
        fileBackedTasksManager1.getById(2);
        fileBackedTasksManager1.getById(7);
        fileBackedTasksManager1.inMemoryHistoryManager.getHistory();
        fileBackedTasksManager1.getById(5);
        fileBackedTasksManager1.getById(6);
        fileBackedTasksManager1.getById(3);
        fileBackedTasksManager1.getById(4);
        fileBackedTasksManager1.getById(1);
        fileBackedTasksManager1.getById(7);
        fileBackedTasksManager1.getById(2);
        fileBackedTasksManager1.inMemoryHistoryManager.getHistory();
        fileBackedTasksManager1.getById(3);
        fileBackedTasksManager1.getById(4);
        fileBackedTasksManager1.getById(5);
        fileBackedTasksManager1.getById(2);
        fileBackedTasksManager1.getById(6);
        fileBackedTasksManager1.getById(4);
        fileBackedTasksManager1.getById(1);
        fileBackedTasksManager1.getById(6);
        fileBackedTasksManager1.getById(5);
        fileBackedTasksManager1.getById(1);
        fileBackedTasksManager1.getById(4);
        fileBackedTasksManager1.getById(1);
        fileBackedTasksManager1.getById(1);
        fileBackedTasksManager1.getById(7);
        fileBackedTasksManager1.inMemoryHistoryManager.getHistory();
        fileBackedTasksManager1.getPrioritizedTasks();

        File file = new File("history.csv");
        FileBackedTasksManager fileBackedTasksManager2 = loadFromFile(file);
        fileBackedTasksManager2.removeTask(3);
        fileBackedTasksManager2.inMemoryHistoryManager.getHistory();
        fileBackedTasksManager2.removeTask(2);
        fileBackedTasksManager2.inMemoryHistoryManager.getHistory();
        fileBackedTasksManager1.getPrioritizedTasks();

        fileBackedTasksManager2.createTask("Task_3", "Desk_task_3", "01.05.2023 10:03", "PT10M");
        fileBackedTasksManager2.createSubTask("Subtask_4", "Desk_Subtask_4", "01.05.2023 10:04", "PT20M", 4);
        fileBackedTasksManager2.createEpic("Epic_3", "Desk_Epic_3");
        fileBackedTasksManager2.createSubTask("Subtask_5", "Desk_Subtask_5", "01.05.2023 10:05", "PT20M", 4);
        fileBackedTasksManager2.getById(9);
        fileBackedTasksManager2.getById(8);
    }
}


