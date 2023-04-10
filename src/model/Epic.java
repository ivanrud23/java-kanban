package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {


    List<Integer> children = new ArrayList<>();

    public Epic(String name, String description, Integer id) {
        super(name, description, id);
    }

    public Epic(String name, String description, Integer id, Status status) {
        super(name, description, id, status);
    }


    public List<Integer> getChildren() {
        return children;
    }

    public void setChildren(List<Integer> children) {
        this.children = children;
    }
}