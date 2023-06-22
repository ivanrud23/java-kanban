package model;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class DurationTypeAdapter extends TypeAdapter<Duration> {

    @Override
    public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
        jsonWriter.value(duration.getSeconds());
    }

    @Override
    public Duration read(JsonReader jsonReader) throws IOException {
        return Duration.of(Long.getLong(jsonReader.nextString()), ChronoUnit.SECONDS);
    }
}
