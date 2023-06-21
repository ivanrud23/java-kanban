package managers;

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
    public Task getById(Integer id) throws IOException, InterruptedException {
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
    public HashMap<Integer, Task> getTaskStorage() {
        return taskStorage;
    }
    @Override
    public void setTaskStorage(HashMap<Integer, Task> taskStorage) {
        this.taskStorage = taskStorage;
    }
    @Override
    public HashMap<Integer, Subtask> getSubTaskStorage() {
        return subTaskStorage;
    }

    public void setSubTaskStorage(HashMap<Integer, Subtask> subTaskStorage) {
        this.subTaskStorage = subTaskStorage;
    }

    @Override
    public HashMap<Integer, Epic> getEpicStorage() {
        return epicStorage;
    }

    public void setEpicStorage(HashMap<Integer, Epic> epicStorage) {
        this.epicStorage = epicStorage;
    }

    public InMemoryHistoryManager getInMemoryHistoryManager() {
        return inMemoryHistoryManager;
    }

    public void setInMemoryHistoryManager(InMemoryHistoryManager inMemoryHistoryManager) {
        this.inMemoryHistoryManager = inMemoryHistoryManager;
    }

    public Set<Task> getTaskSortByTime() {
        return taskSortByTime;
    }

    public void setTaskSortByTime(Set<Task> taskSortByTime) {
        this.taskSortByTime = taskSortByTime;
    }
//    @Override
//    public List<Task> getTaskStorage() throws IOException {
//        return new ArrayList<>(taskStorage.values());
//    }
//
//    @Override
//    public List<Task> getSubTaskStorage() {
//        return new ArrayList<>(subTaskStorage.values());
//    }
//
//    @Override
//    public List<Task> getEpicStorage() {
//        return new ArrayList<>(epicStorage.values());
//    }

//    @Override
//    protected void save() throws IOException, NullPointerException, InterruptedException {
//
//    }

    @Override
    public void createTask(Task task) throws IOException, InterruptedException {
        task.setId(idCounter());
        if (task.getStatus() == null) {
            task.setStatus(Status.NEW);
        }
        taskStorage.put(task.getId(), task);
        if (checkTaskTime(task)) {
            taskSortByTime.add(task);
        }
    }

    @Override
    public void createSubTask(Subtask subtask) throws IOException, NullPointerException, NoSuchElementException, InterruptedException {
        if (subtask.getStatus() == null) {
            subtask.setStatus(Status.NEW);
        }
        if (subtask.getId() == null) {
            subtask.setId(idCounter());
        }

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
    public void createEpic(Epic epic) throws IOException, InterruptedException {
        if (epic.getStatus() == null) {
            epic.setStatus(Status.NEW);
        }
        if (epic.getEpic() == null) {
            epic.setEpic(true);
        }
        if (epic.getId() == null) {
            epic.setId(idCounter());
        }
        if (epic.getChildren()== null) {
            epic.setChildren(new ArrayList<Integer>());
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
    public void updateTask(Integer id, Task newTask) throws IOException, InterruptedException {
        if (taskStorage.containsKey(id)) {
            if (newTask.getName() == null) {
                newTask.setName(taskStorage.get(id).getName());
            }
            if (newTask.getDescription() == null) {
                newTask.setDescription(taskStorage.get(id).getDescription());
            }
            if (newTask.getStatus() == null) {
                newTask.setStatus(taskStorage.get(id).getStatus());
            }
            if (newTask.getStartTime() == null) {
                newTask.setStartTime(taskStorage.get(id).getStartTime());
            }
            if (newTask.getDuration() == null) {
                newTask.setDuration(taskStorage.get(id).getDuration());
            }
            if (newTask.getEndTime() == null) {
                newTask.setEndTime(taskStorage.get(id).getEndTime());
            }
            newTask.setId(id);
            taskStorage.put(id, newTask);
        }
    }

    @Override
    public void updateSubTask(Integer id, Subtask newSubTask) throws IOException, InterruptedException {
        if (subTaskStorage.containsKey(id)) {
            if (newSubTask.getName() == null) {
                newSubTask.setName(subTaskStorage.get(id).getName());
            }
            if (newSubTask.getDescription() == null) {
                newSubTask.setDescription(subTaskStorage.get(id).getDescription());
            }
            if (newSubTask.getParentId() == null) {
                newSubTask.setParentId(subTaskStorage.get(id).getParentId());
            }
            if (newSubTask.getStatus() == null) {
                newSubTask.setStatus(subTaskStorage.get(id).getStatus());
            }
            if (newSubTask.getStartTime() == null) {
                newSubTask.setStartTime(subTaskStorage.get(id).getStartTime());
            }
            if (newSubTask.getDuration() == null) {
                newSubTask.setDuration(subTaskStorage.get(id).getDuration());
            }
            if (newSubTask.getEndTime() == null) {
                newSubTask.setEndTime(subTaskStorage.get(id).getEndTime());
            }
            newSubTask.setId(id);
            subTaskStorage.put(id, newSubTask);
            addSubTaskToEpic(newSubTask.getParentId(), id);
            checkEpicStatus(newSubTask.getParentId());
        }
    }

    @Override
    public void updateEpic(Integer id, Epic newEpic) throws IOException, InterruptedException {
        if (epicStorage.containsKey(id)) {
            if (newEpic.getEpic() == null) {
                newEpic.setEpic(epicStorage.get(id).getEpic());
            }
            if (newEpic.getName() == null) {
                newEpic.setName(epicStorage.get(id).getName());
            }
            if (newEpic.getDescription() == null) {
                newEpic.setDescription(epicStorage.get(id).getDescription());
            }
            List<Integer> childId = epicStorage.get(id).getChildren();
            if (newEpic.getStatus() == null) {
                newEpic.setStatus(epicStorage.get(id).getStatus());
            }
            if (newEpic.getStartTime() == null) {
                newEpic.setStartTime(epicStorage.get(id).getStartTime());
            }
            if (newEpic.getDuration() == null) {
                newEpic.setDuration(epicStorage.get(id).getDuration());
            }
            if (newEpic.getEndTime() == null) {
                newEpic.setEndTime(epicStorage.get(id).getEndTime());
            }
            if (newEpic.getChildren() == null) {
                newEpic.setChildren(epicStorage.get(id).getChildren());
            }
            newEpic.setChildren(childId);
            newEpic.setId(id);
            epicStorage.put(id, newEpic);
        }

    }

    @Override
    public void addSubTaskToEpic(Integer parentId, Integer id) throws IOException, InterruptedException {
        Epic epicTask = epicStorage.get(parentId);
        for (Epic epic : epicStorage.values()) {
            epic.getChildren().remove(id);
        }
        epicTask.getChildren().add(id);
        epicStorage.put(parentId, epicTask);
        checkEpicStatus(parentId);
    }

    @Override
    public void removeTask(Integer id) throws IOException, NullPointerException, InterruptedException {
        if (epicStorage.containsKey(id)) {
            List<Integer> childId = epicStorage.get(id).getChildren();
            for (Integer child : childId) {
                Subtask task = subTaskStorage.remove(child);
                try {
                    inMemoryHistoryManager.remove(child);
                } catch (NullPointerException ignored) {

                }
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
        for (Task task : taskSortByTime) {
            if (task.getId() == id) {
                taskSortByTime.remove(task);
                return;
            }
        }
//        taskSortByTime.removeIf(task -> Objects.equals(task.getId(), id));
    }

    @Override
    public void clearSubtask() throws IOException, InterruptedException {
        for (Epic epic : epicStorage.values()) {
            epic.getChildren().clear();
            checkEpicStatus(epic.getId());
        }
        subTaskStorage.clear();
    }

    @Override
    public void clearTask() throws IOException, InterruptedException {
        for (Task task : taskStorage.values()) {
            taskSortByTime.remove(task);
        }
        taskStorage.clear();
    }

    @Override
    public void clearEpic() throws IOException, InterruptedException {
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
