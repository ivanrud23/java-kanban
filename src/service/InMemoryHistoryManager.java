package service;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    static List<Task> historyStorage = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (historyStorage.size() == 10) {
            for (int i = 0; i < historyStorage.size() - 1; i++) {
                historyStorage.add(i, historyStorage.get(i + 1));
            }
            historyStorage.add(10, task);
        } else {
            historyStorage.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        System.out.println("—  —  —  —  —  —  —  —  —  —  —  —");
        System.out.println("Истроия просмотренных задач:");
        for (Task task : historyStorage) {
            System.out.println("Идентификатор — " + task.getId());
            System.out.println("Название — " + task.getName());
            System.out.println("Описание — " + task.getDescription());
            System.out.println("Статус — " + task.getStatus());
            System.out.println();
        }
        System.out.println("—  —  —  —  —  —  —  —  —  —  —  —");
        return historyStorage;
    }

}