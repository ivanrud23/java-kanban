package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.time.LocalDateTime;

public class Managers {
    static LocalDateAdapter localDateAdapter = new LocalDateAdapter();

//    static final Gson gson = new GsonBuilder()
////            .registerTypeAdapter(LocalDateTime.class, new HttpTaskServer.LocalDateAdapter.nullSafe())
////            .create();

    static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, localDateAdapter.nullSafe())
            .create();

    public static TaskManager getDefault() throws IOException, InterruptedException {
        return new HttpTaskManager();
    }
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
