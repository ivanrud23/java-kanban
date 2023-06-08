package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    Integer idCounter = 0;
    protected HashMap<Integer, Task> taskStorage = new HashMap<>();
    protected HashMap<Integer, Subtask> subTaskStorage = new HashMap<>();
    protected HashMap<Integer, Epic> epicStorage = new HashMap<>();
    protected InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
    protected Set<Task> taskSortByTime = new TreeSet<>(Comparator.comparing((Task::getStartTime), Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(Task::getId));
    protected DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");


    @Override
    public Task getById(Integer id) throws IOException {
        Task task;
        if (taskStorage.containsKey(id)) {
            task = taskStorage.get(id);
        } else if (subTaskStorage.containsKey(id)) {
            task = subTaskStorage.get(id);
        } else if (epicStorage.containsKey(id)) {
            task = epicStorage.get(id);
        } else {
            throw new IOException("Введен несуществующий идентификатор");
        }
        inMemoryHistoryManager.add(task);
        return task;
    }

    @Override
    public List<Task> getTaskStorage() throws IOException {
        return new ArrayList<>(taskStorage.values());
    }

    @Override
    public List<Task> getSubTaskStorage() {
        return new ArrayList<>(subTaskStorage.values());
    }

    @Override
    public List<Task> getEpicStorage() {
        return new ArrayList<>(epicStorage.values());
    }

    @Override
    public void createTask(Task task) throws IOException {
        task.setId(idCounter());
        taskStorage.put(task.getId(), task);
        if (checkTaskTime(task)) {
            taskSortByTime.add(task);
        }
    }

    @Override
    public void createSubTask(Subtask subtask) throws IOException, NullPointerException, NoSuchElementException {
        subtask.setId(idCounter());
        subTaskStorage.put(subtask.getId(), subtask);
        if (checkTaskTime(subtask)) {
            taskSortByTime.add(subtask);
        }
        Epic parentEpic = epicStorage.get(subtask.getParentId());
        addSubTaskToEpic(subtask.getParentId(), subtask.getId());
        LocalDateTime epicStartTime;
        LocalDateTime epicEndTime;
        if (subtask.getStartTime() != null) {
            if (parentEpic.getChildren().size() > 1) {
                epicStartTime = taskSortByTime.stream()
                        .filter(task -> parentEpic.getChildren().contains(task.getId()))
                        .filter(task -> task.getStartTime() != null)
                        .min(Task::compareByStartTime)
                        .get().getStartTime();
                epicEndTime = taskSortByTime.stream()
                        .filter(task -> parentEpic.getChildren().contains(task.getId()))
                        .filter(task -> task.getStartTime() != null)
                        .max(Task::compareByEndTime)
                        .get().getEndTime();
            } else {
                epicStartTime = subtask.getStartTime();
                epicEndTime = subtask.getEndTime();
            }
            Duration epicDuration;
            if (parentEpic.getChildren().size() > 1) {
                epicDuration = Duration.between(epicStartTime, epicEndTime);
            } else {
                epicDuration = subtask.getDuration();
            }

            parentEpic.setStartTime(epicStartTime);
            parentEpic.setDuration(epicDuration);
        }
    }

    @Override
    public void createEpic(Epic epic) throws IOException {
        if (epic.getId() == null) {
            epic.setId(idCounter());
        }
        epicStorage.put(epic.getId(), epic);
        if (checkTaskTime(epic)) {
            taskSortByTime.add(epic);
        }
    }

    @Override
    public void idCounterPlus() {
        idCounter++;
    }

    @Override
    public Integer idCounter() {
        idCounter++;
        return idCounter;
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
        System.out.println("Приоретизированный список задач");
        for (Task task : finalList) {
            if (task.getClass().getName().equals("model.Epic")) {
                Epic epicTask = (Epic) task;
                System.out.println("Идентификатор — " + epicTask.getId());
                System.out.println("Название — " + epicTask.getName());
                System.out.println("Описание — " + epicTask.getDescription());
                System.out.println("Статус — " + epicTask.getStatus());
                System.out.println("Подзадачи — " + epicTask.getChildren());
                System.out.println("StartTime — " + epicTask.getStartTime());
                System.out.println("EndTime — " + epicTask.getEndTime());
                System.out.println();
            } else if (task.getClass().getName().equals("model.Task")) {
                System.out.println("Идентификатор — " + task.getId());
                System.out.println("Название — " + task.getName());
                System.out.println("Описание — " + task.getDescription());
                System.out.println("Статус — " + task.getStatus());
                System.out.println("StartTime — " + task.getStartTime());
                System.out.println("EndTime — " + task.getEndTime());
                System.out.println();
            } else {
                Subtask subTaskTask = (Subtask) task;
                System.out.println("Идентификатор — " + subTaskTask.getId());
                System.out.println("Название — " + subTaskTask.getName());
                System.out.println("Описание — " + subTaskTask.getDescription());
                System.out.println("Статус — " + subTaskTask.getStatus());
                System.out.println("Родительский эпик — " + subTaskTask.getParentId());
                System.out.println("StartTime — " + subTaskTask.getStartTime());
                System.out.println("EndTime — " + subTaskTask.getEndTime());
                System.out.println();
            }
        }
        return finalList;
    }

    public Boolean checkTaskTime(Task taskCheck) {
        boolean flag = true;
        if (taskSortByTime != null) {
            for (Task task : taskSortByTime) {
                if (taskCheck.getStartTime() != null && task.getStartTime() != null) {
                    if (taskCheck.getEndTime().isAfter(task.getStartTime())
                            && taskCheck.getEndTime().isBefore(task.getEndTime())) {
                        flag = false;
                        break;
                    } else if (taskCheck.getStartTime().isAfter(task.getStartTime())
                            && taskCheck.getStartTime().isBefore(task.getEndTime())) {
                        flag = false;
                        break;
                    } else if (taskCheck.getStartTime().isEqual(task.getStartTime())
                            && taskCheck.getEndTime().isEqual(task.getEndTime())) {
                        flag = false;
                        break;
                    } else {
                        flag = true;
                        break;
                    }
                }
            }
        }
        return flag;
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
            taskSortByTime.remove(task);
        }
        taskStorage.clear();
    }

    @Override
    public void clearEpic() throws IOException {
        for (Epic epic : epicStorage.values()) {
            taskSortByTime.remove(epic);
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
