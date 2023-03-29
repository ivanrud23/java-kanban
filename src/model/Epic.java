package model;

import java.util.ArrayList;

public class Epic extends Task {


    ArrayList<Integer> children = new ArrayList<>();

    public Epic(String name, String description, Integer id) {
        super(name, description, id);
    }

    public ArrayList<Integer> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<Integer> children) {
        this.children = children;
    }


}
