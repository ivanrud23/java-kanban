package model;
public class Task {

    protected String name;
    protected String description;
    protected Integer id;
    protected String status = "NEW";

    public Task(String name, String description, Integer id) {
        this.name = name;
        this.description = description;
        this.id = id;
    }
    public Task(String status) {
        this.status = status;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
