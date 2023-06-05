package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.io.IOException;
import java.text.Format;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class InMemoryTaskManager implements TaskManager{
    Integer idCounter = 1;
    protected HashMap<Integer, Task> taskStorage = new HashMap<>();
    protected HashMap<Integer, Subtask> subTaskStorage = new HashMap<>();
    protected HashMap<Integer, Epic> epicStorage = new HashMap<>();
    protected InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
    protected Set<Task> taskSortByTime = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    protected List<Task> taskSort = new ArrayList<>();
    protected DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

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
        return task;
    }

    @Override
    public List<Task> getTaskStorage() throws IOException {
        if (taskStorage.values().isEmpty()) {
            throw new IOException("Нет ни одной задачи");
        }
        return new ArrayList<>(taskStorage.values());
    }

    @Override
    public List<Task> getSubTaskStorage() throws IOException {
        if (subTaskStorage.values().isEmpty()) {
            throw new IOException("Нет ни одной подзадачи");
        }
        return new ArrayList<>(subTaskStorage.values());
    }

    @Override
    public List<Task> getEpicStorage() throws IOException {
        if (epicStorage.values().isEmpty()) {
            throw new IOException("Нет ни одного эпика");
        }
        return new ArrayList<>(epicStorage.values());
    }


    @Override
    public void createTask(Task task) throws IOException {
        taskStorage.put(task.getId(), task);
        if (task.getStartTime() == null) {
            taskSort.add(task);
        } else {
            taskSortByTime.add(task);
        }
        if (task.getId() >= idCounter) {
            idCounter = task.getId();
            idCounterPlus();
        }
    }

    @Override
    public void createTask(String name, String description) throws IOException {
        Task task = new Task(name, description, idCounter);
        if (task.getStartTime() == null) {
            taskSort.add(task);
        } else {
            taskSortByTime.add(task);
        }
        taskStorage.put(idCounter, task);
        idCounterPlus();
    }
    @Override
    public void createTask(String name, String description, String startTime, String duration) throws IOException {
        Task task = new Task(name, description, idCounter, startTime, duration);
        task = checkTaskTime(task);
        if (task.getStartTime() == null) {
            taskSort.add(task);
        } else {
            taskSortByTime.add(task);
        }
        taskStorage.put(idCounter, task);
        idCounterPlus();
    }
    @Override
    public void createSubTask(Subtask subtask) throws IOException, NullPointerException {
        subTaskStorage.put(subtask.getId(), subtask);
        if (subtask.getStartTime() == null) {
            taskSort.add(subtask);
        } else {
            taskSortByTime.add(subtask);
        }
        epicStorage.get(subtask.getParentId()).getChildren().add(subtask.getId());
        if (subtask.getId() >= idCounter) {
            idCounter = subtask.getId();
//             epicStorage.get(subtask.getParentId()).getTimeForEpic(subTaskStorage);
        }
        idCounterPlus();
    }

    @Override
    public void createSubTask(String name, String description, Integer parentId) throws IOException {
        Subtask subtask = new Subtask(name, description, idCounter, parentId);
                subTaskStorage.put(idCounter, subtask);
        if (subtask.getStartTime() == null) {
            taskSort.add(subtask);
        } else {
            taskSortByTime.add(subtask);
        }
        addSubTaskToEpic(parentId, idCounter);
        idCounterPlus();
    }
    @Override
    public void createSubTask(String name, String description, String startTime, String duration, Integer parentId) throws IOException {
        Subtask subtask = new Subtask(name, description, idCounter, startTime, duration, parentId);
        subtask = checkSubtaskTime(subtask);
        subTaskStorage.put(idCounter, subtask);
        epicStorage.get(parentId).getChildren().add(idCounter);
        if (subtask.getStartTime() == null) {
            taskSort.add(subtask);
        } else {
            taskSortByTime.add(subtask);
            Epic parentEpic = epicStorage.get(parentId);
            LocalDateTime epicStartTime = taskSortByTime.stream()
                    .filter(task -> parentEpic.getChildren().contains(task.getId()))
                    .min(Task::compareByStartTime)
                    .get().getStartTime();
            LocalDateTime epicEndTime = taskSortByTime.stream()
                    .filter(task -> parentEpic.getChildren().contains(task.getId()))
                    .max(Task::compareByEndTime)
                    .get().getEndTime();
            Duration epicDuration;
            if (parentEpic.getChildren().size() > 1) {
                epicDuration = Duration.between(epicStartTime, epicEndTime);
            } else {
                epicDuration = Duration.parse(duration);
            }

            Epic newEpic = new Epic(parentEpic.getName(), parentEpic.getDescription(), parentEpic.getId(),
                    parentEpic.getStatus(), epicStartTime.format(inputFormat), epicDuration.toString(), parentEpic.getChildren());
            updateEpic(parentId, newEpic);

        }

        idCounterPlus();
    }

    @Override
    public void createEpic(Epic epic) throws IOException {
        epicStorage.put(epic.getId(), epic);
        if (epic.getStartTime() == null) {
            taskSort.add(epic);
        } else {
            taskSortByTime.add(epic);
        }
        if (epic.getId() >= idCounter) {
            idCounter = epic.getId();
        }
    }

    @Override
    public void createEpic(String name, String description) throws IOException {
        Epic epic = new Epic(name, description, idCounter);
        epicStorage.put(idCounter, epic);
        if (epic.getStartTime() == null) {
            taskSort.add(epic);
        } else {
            taskSortByTime.add(epic);
        }
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

    public List<Task> getPrioritizedTasks() {
        List<Task> finalList = new ArrayList<>(taskSortByTime);
        finalList.addAll(taskSort);
//        System.out.println("Приоретизированный список задач");
//        for (Task task : finalList) {
//            if (task.getClass().getName().equals("model.Epic")) {
//                Epic epicTask = (Epic) task;
//                System.out.println("Идентификатор — " + epicTask.getId());
//                System.out.println("Название — " + epicTask.getName());
//                System.out.println("Описание — " + epicTask.getDescription());
//                System.out.println("Статус — " + epicTask.getStatus());
//                System.out.println("Подзадачи — " + epicTask.getChildren());
//                System.out.println();
//            } else if (task.getClass().getName().equals("model.Task")) {
//                System.out.println("Идентификатор — " + task.getId());
//                System.out.println("Название — " + task.getName());
//                System.out.println("Описание — " + task.getDescription());
//                System.out.println("Статус — " + task.getStatus());
//                System.out.println();
//            } else {
//                Subtask subTaskTask = (Subtask) task;
//                System.out.println("Идентификатор — " + subTaskTask.getId());
//                System.out.println("Название — " + subTaskTask.getName());
//                System.out.println("Описание — " + subTaskTask.getDescription());
//                System.out.println("Статус — " + subTaskTask.getStatus());
//                System.out.println("Родительский эпик — " + subTaskTask.getParentId());
//                System.out.println();
//            }
//        }
        return finalList;
    }

    public Task checkTaskTime(Task taskCheck) {
        List<Task> prioritizedTasks = new ArrayList<>(taskSortByTime);
        for (Task task : prioritizedTasks) {
            if ((taskCheck.getStartTime().isAfter(task.getStartTime()) || taskCheck.getStartTime().equals(task.getStartTime()))
            && taskCheck.getStartTime().isBefore(task.getEndTime())) {
                taskCheck.setStartTime(task.getEndTime());
            }
        }
        return taskCheck;
    }

    public Subtask checkSubtaskTime(Subtask taskCheck) {
        List<Task> prioritizedTasks = new ArrayList<>(taskSortByTime);
        for (Task task : prioritizedTasks) {
            if ((taskCheck.getStartTime().isAfter(task.getStartTime()) || taskCheck.getStartTime().equals(task.getStartTime()))
                    && taskCheck.getStartTime().isBefore(task.getEndTime())) {
                taskCheck.setStartTime(task.getEndTime());
            }
        }
        return taskCheck;
    }


    @Override
    public void updateTask(Integer id, Task newTask) throws IOException {
        if (taskStorage.containsKey(id)) {
            newTask.setId(id);
            taskStorage.put(id, newTask);
        }
    }

    @Override
    public void updateSubTask(Integer id, Subtask newSubTask) throws IOException {
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
    public void updateEpic(Integer id, Epic newEpic) throws IOException {
        if (epicStorage.containsKey(id)) {
            List<Integer> childId = epicStorage.get(id).getChildren();
            newEpic.setStatus(epicStorage.get(id).getStatus());
            newEpic.setChildren(childId);
            newEpic.setId(id);
            epicStorage.put(id, newEpic);
            if (newEpic.getStartTime() == null) {
                taskSort.add(newEpic);
            } else {
                taskSortByTime.add(newEpic);
            }
        }

    }

    @Override
    public void addSubTaskToEpic(Integer parentId, Integer id) throws IOException {
        Epic epicTask = epicStorage.get(parentId);
        if (!epicTask.getChildren().contains(id)) {
            epicTask.getChildren().add(id);
        }
        epicStorage.put(parentId, epicTask);
        checkEpicStatus(parentId);
    }

    @Override
    public void removeTask(Integer id) throws IOException, NullPointerException {
        if (epicStorage.containsKey(id)) {
            List<Integer> childId = epicStorage.get(id).getChildren();
            for (Integer child : childId) {
                Task task = subTaskStorage.remove(child);
                inMemoryHistoryManager.remove(child);
                sortRemove(task.getId());
            }
            epicStorage.remove(id);
            inMemoryHistoryManager.remove(id);
            sortRemove(id);
        } else if (subTaskStorage.containsKey(id)) {
            Integer parentId = subTaskStorage.get(id).getParentId();
            Epic epic = epicStorage.get(parentId);
            epic.getChildren().remove(id);
            epicStorage.put(parentId, epic);
            subTaskStorage.remove(id);
            inMemoryHistoryManager.remove(id);
            checkEpicStatus(parentId);
            sortRemove(id);
        } else if (taskStorage.containsKey(id)) {
            taskStorage.remove(id);
            inMemoryHistoryManager.remove(id);
            sortRemove(id);
        } else {
            System.out.println("Задачи с этим номер еще не создана");
        }
    }

    public void sortRemove(Integer id) {
        taskSort.removeIf(task -> Objects.equals(task.getId(), id));
        taskSortByTime.removeIf(task -> Objects.equals(task.getId(), id));
    }
    @Override
    public void clearSubtask() throws IOException {
        for (Epic epic : epicStorage.values()) {
            epic.getChildren().clear();
            checkEpicStatus(epic.getId());
        }
        subTaskStorage.clear();
    }

    @Override
    public void clearTask() throws IOException {
        for (Task task : taskStorage.values()) {
            taskSort.remove(task);
        }
        taskStorage.clear();
    }

    @Override
    public void clearEpic() throws IOException {
        for (Epic epic : epicStorage.values()) {
            taskSort.remove(epic);
        }
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
