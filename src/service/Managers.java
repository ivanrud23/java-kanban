package service;

import java.io.IOException;

public class Managers {
    public static TaskManager getDefault() throws IOException {
        return new FileBackedTasksManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
