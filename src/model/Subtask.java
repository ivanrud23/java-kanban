package model;

import java.util.Objects;

public class Subtask extends Task {
    private Integer parentId;
//    protected LocalDate

    public Subtask(String name, String description, Integer id, Integer parentId) {
        super(name, description, id);
        this.parentId = parentId;
    }
    public Subtask(String name, String description, Integer id, Status status, Integer parentId) {
        super(name, description, id, status);
        this.parentId = parentId;
    }
    public Subtask(String name, String description, Integer id, String startTime, String duration, Integer parentId) {
        super(name, description, id, startTime, duration);
        this.parentId = parentId;
    }
    public Subtask(String name, String description, Integer id, Status status, String startTime, String duration, Integer parentId) {
        super(name, description, id, status, startTime, duration);
        this.parentId = parentId;
    }


    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    @Override
    public String toString() {
        return super.toString()
        + ", startTime=" + startTime
        + ", period=" + duration
        + ", endTime=" + endTime
        + ", parentId=" + parentId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        Subtask subtask = (Subtask) o;
        return super.equals(o)
                && Objects.equals(parentId, subtask.parentId);
    }
}