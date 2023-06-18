package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {

    protected List<Integer> children = new ArrayList<>();

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Epic(String name, String description) {
        super(name, description);
        epic = true;
    }
    public Epic(String name, String description, Integer id) {
        super(name, description, id);
        epic = true;
    }

    public Epic(String name, String description, Integer id, Status status) {
        super(name, description, id, status);
        epic = true;
    }

    public Epic(String name, String description, Integer id, Status status, List<Integer> children) {
        super(name, description, id, status);
        this.children = children;
        epic = true;
    }

    public Epic(String name, String description, Integer id, Status status, String startTime, String duration) {
        super(name, description, id, status, startTime, duration);
        epic = true;
    }

    public Epic(String name, String description, Integer id, Status status, String startTime, String duration, List<Integer> children) {
        super(name, description, id, status, startTime, duration);
        this.children = children;
        epic = true;
    }

    public List<Integer> getChildren() {
        return children;
    }

    public void setChildren(List<Integer> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        if (startTime == null) {
            return super.toString()
                    + ", children=" + children +
                    '}';
        } else {
            return super.toString() +
                    ", startTime=" + startTime +
                    ", duration=" + duration +
                    ", children=" + children +
                    '}';
        }
    }

    @Override
    public boolean equals(Object o) {
        Epic epic = (Epic) o;
        return super.equals(o)
                && Objects.equals(children, epic.children);
    }
}