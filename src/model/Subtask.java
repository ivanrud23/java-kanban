package model;

public class Subtask extends Task {
    private Integer parentId;


    public Subtask(String name, String description, Integer id, Integer parentId) {
        super(name, description, id);
        this.parentId = parentId;
    }

    public Subtask(String name, String description, Integer id, Status status, Integer parentId) {
        super(name, description, id, status, parentId);
        this.parentId = parentId;
    }


    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }
}