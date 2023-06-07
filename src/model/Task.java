package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class Task {

    protected String name;
    protected String description;
    protected Integer id;
    protected Status status = Status.NEW;
    protected int epic;
    protected Duration duration;
    protected LocalDateTime startTime;
    protected LocalDateTime endTime;
    protected DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");


    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Task(String name, String description, Integer id) {
        this.name = name;
        this.description = description;
        this.id = id;
    }

    public Task(String name, String description, String startTime, String duration) {
        this.name = name;
        this.description = description;
        this.startTime = LocalDateTime.parse(startTime, inputFormat);
        this.duration = Duration.parse(duration);
    }

    public Task(String name, String description, Integer id, Status status) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
    }

    public Task(String name, String description, Integer id, Status status, String startTime, String duration) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        this.startTime = LocalDateTime.parse(startTime, inputFormat);
        this.duration = Duration.parse(duration);
    }


    public LocalDateTime getEndTime() {
        if (startTime == null) {
            return null;
        }
        this.setEndTime(startTime.plus(duration));
        return endTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getEpic() {
        return epic;
    }

    public void setEpic(int epic) {
        this.epic = epic;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() throws NullPointerException{
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public List getPrioritizedTasks() {
        return null;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }


    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", epic=" + epic +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(name, task.name)
                && Objects.equals(description, task.description)
                && id == task.id
                && Objects.equals(status, task.status)
                && epic == task.epic;
    }

    public static int compareByStartTime(Task t1, Task t2) {
        return t1.getStartTime().compareTo(t2.getStartTime());
    }

    public static int compareByEndTime(Task t1, Task t2) {
        return t1.getEndTime().compareTo(t2.getEndTime());
    }

}
