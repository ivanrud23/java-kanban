package managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.DurationTypeAdapter;
import model.LocalDateAdapter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class Managers {
    private static final LocalDateAdapter localDateAdapter = new LocalDateAdapter();

    private final static Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, localDateAdapter.nullSafe())
            .registerTypeAdapter(Duration.class,new DurationTypeAdapter().nullSafe())
            .setPrettyPrinting()
            .create();

    public static Gson getGson() {
        return gson;
    }

    public static TaskManager getDefault() throws IOException, InterruptedException {
        return new HttpTaskManager();
    }
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
