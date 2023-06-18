package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import model.Epic;
import model.Subtask;
import model.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class HttpTaskServer {
    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter().nullSafe())
            .setPrettyPrinting()
            .create();
    private static HttpTaskManager httpTaskManager;
    private static HttpTaskManager httpTaskManager1;
    private HttpServer httpServer;
    private KVServer kvServer = new KVServer();

    public HttpTaskServer(HttpTaskManager httpTaskManager) throws IOException, InterruptedException {
        this.httpServer = HttpServer.create();
        this.httpServer.bind(new InetSocketAddress(PORT), 0);
        this.httpServer.createContext("/tasks", new TasksHandler());
        this.httpServer.start();
        HttpTaskServer.httpTaskManager = httpTaskManager;
        kvServer.start();

        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public KVServer getKvServer() {
        return kvServer;
    }

    public void setKvServer(KVServer kvServer) {
        this.kvServer = kvServer;
    }

    public void stopHttpTaskServer() {
        httpServer.stop(0);
    }
    static class TasksHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Endpoint endpoint = getEndpoint(exchange, exchange.getRequestMethod());

            switch (endpoint) {
                case GET_PRIORITIZED: {
                    handleGetPrioritized(exchange);
                    break;
                } case GET_HISTORY: {
                    handleGetHistory(exchange);
                    break;
                } case GET_BY_ID: {
                    try {
                        handleGetById(exchange);
                    } catch (InterruptedException | NumberFormatException e) {
                        throw new RuntimeException(e.getMessage());
                    }
                    break;
                } case CREATE_TASK: {
                    try {
                        handleCreateTask(exchange);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                } case CREATE_SUBTASK: {
                    try {
                        handleCreateSubtask(exchange);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                } case CREATE_EPIC: {
                    try {
                        handleCreateEpic(exchange);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                } case GET_EPIC_SUBTASKS: {
                    handleGetEpicSubtasks(exchange);
                    break;
                } case GET_TASK: {
                    handleGetTasks(exchange);
                    break;
                } case GET_SUBTASK: {
                    handleGetSubtasks(exchange);
                    break;
                } case GET_EPIC: {
                    handleGetEpic(exchange);
                    break;
                } case UPDATE_TASK: {
                    try {
                        handleUpdateTask(exchange);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                } case UPDATE_SUBTASK: {
                    try {
                        handleUpdateSubtask(exchange);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                } case UPDATE_EPIC: {
                    try {
                        handleUpdateEpic(exchange);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                } case DELETE_TASK_BY_ID: {
                    handleDeleteTaskById(exchange);
                    break;
                } case DELETE_ALL_TASKS: {
                    handleDeleteAllTasks(exchange);
                    break;
                } case DELETE_ALL_SUBTASKS: {
                    handleDeleteAllSubtasks(exchange);
                    break;
                } case DELETE_ALL_EPIC: {
                    try {
                        handleDeleteAllEpics(exchange);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                } default: {
                    writeResponse(exchange, "Такого эндпоинта не существует", 404);
                }
            }
        }
        private Endpoint getEndpoint(HttpExchange exchange, String requestMethod) throws NullPointerException {
            String requestPath = exchange.getRequestURI().getPath();
            String[] pathParts = requestPath.split("/");
            String query = exchange.getRequestURI().getQuery();


            if (pathParts.length == 2 && requestMethod.equals("GET")) {
                return Endpoint.GET_PRIORITIZED;
            } else if (pathParts.length == 3 && pathParts[2].equals("history")) {
                return  Endpoint.GET_HISTORY;
            } else if (pathParts.length == 3 && pathParts[2].equals("task") && requestMethod.equals("GET") && query == null) {
                return  Endpoint.GET_TASK;
            } else if (pathParts.length == 3 && pathParts[2].equals("subtask") && requestMethod.equals("GET") && query == null) {
                return  Endpoint.GET_SUBTASK;
            } else if (pathParts.length == 4 && pathParts[2].equals("subtask") && requestMethod.equals("GET") && query != null) {
                return  Endpoint.GET_EPIC_SUBTASKS;
            } else if (pathParts.length == 3 && pathParts[2].equals("epic") && requestMethod.equals("GET") && query == null) {
                return  Endpoint.GET_EPIC;
            } else if (pathParts.length == 3 && requestMethod.equals("GET") && query != null) {
                return Endpoint.GET_BY_ID;
            } else if (pathParts.length == 3 && pathParts[2].equals("task") && requestMethod.equals("POST") && query == null) {
                return Endpoint.CREATE_TASK;
            } else if (pathParts.length == 3 && pathParts[2].equals("subtask") && requestMethod.equals("POST") && query == null) {
                return Endpoint.CREATE_SUBTASK;
            } else if (pathParts.length == 3 && pathParts[2].equals("epic") && requestMethod.equals("POST") && query == null) {
                return Endpoint.CREATE_EPIC;
            } else if (pathParts.length == 3 && pathParts[2].equals("task") && requestMethod.equals("POST") && query != null) {
                return Endpoint.UPDATE_TASK;
            } else if (pathParts.length == 3 && pathParts[2].equals("subtask") && requestMethod.equals("POST") && query != null) {
                return Endpoint.UPDATE_SUBTASK;
            } else if (pathParts.length == 3 && pathParts[2].equals("epic") && requestMethod.equals("POST") && query != null) {
                return Endpoint.UPDATE_EPIC;
            } else if (pathParts.length == 3 && pathParts[2].equals("task") && requestMethod.equals("DELETE") && query != null) {
                return Endpoint.DELETE_TASK_BY_ID;
            } else if (pathParts.length == 3 && pathParts[2].equals("task") && requestMethod.equals("DELETE") && query == null) {
                return Endpoint.DELETE_ALL_TASKS;
            } else if (pathParts.length == 3 && pathParts[2].equals("subtask") && requestMethod.equals("DELETE") && query == null) {
                return Endpoint.DELETE_ALL_SUBTASKS;
            } else if (pathParts.length == 3 && pathParts[2].equals("epic") && requestMethod.equals("DELETE") && query == null) {
                return Endpoint.DELETE_ALL_EPIC;
            }
            return Endpoint.UNKNOWN;
        }

        private void handleGetPrioritized(HttpExchange exchange) throws IOException {
            writeResponse(exchange, gson.toJson(httpTaskManager1.getPrioritizedTasks()), 200);
        }
        private void handleGetHistory(HttpExchange exchange)  throws IOException {
            writeResponse(exchange, gson.toJson(httpTaskManager1.inMemoryHistoryManager.getHistory()), 200);
        }
        private void handleGetById(HttpExchange exchange) throws IOException, InterruptedException {
            try {
                int id = parsePathId(exchange.getRequestURI().getQuery());
                if (id >= 1) {
                    try {
                        writeResponse(exchange, gson.toJson(httpTaskManager.getById(id)), 200);
                    } catch (IOException e) {
                        writeResponse(exchange, "Задачи с " + exchange.getRequestURI().getQuery() + " не существует", 400);
                    }
                } else {
                    writeResponse(exchange, "Номер задачи не может быть отрицательным или равен нулю " + exchange.getRequestURI().getQuery(), 400);
                }
            } catch (NumberFormatException e) {
                writeResponse(exchange, "Введен не числовой идентификатор" + exchange.getRequestURI().getQuery(), 400);
            }

        }
        private void handleDeleteTaskById(HttpExchange exchange) throws IOException{
            try {
                int id = parsePathId(exchange.getRequestURI().getQuery());
                if (id >= 1) {
                    try {
                        httpTaskManager.removeTask(id);
                        writeResponse(exchange, "", 204);
                    } catch (IOException | InterruptedException e) {
                        writeResponse(exchange, "Задачи с id = " + id + " не существует", 400);
                    }
                } else {
                    writeResponse(exchange, "Номер задачи не может быть отрицательным или равен нулю " + exchange.getRequestURI().getQuery(), 400);
                }
            } catch (NumberFormatException e) {
                writeResponse(exchange, "Введен не числовой идентификатор" + exchange.getRequestURI().getQuery(), 400);
            }
        }
        private void handleDeleteAllTasks(HttpExchange exchange) throws IOException{
            httpTaskManager.taskStorage.clear();
            writeResponse(exchange, "Все задачи успешно удалены", 204);
        }
        private void handleDeleteAllSubtasks(HttpExchange exchange) throws IOException{
            httpTaskManager.subTaskStorage.clear();
            for (Epic epic : httpTaskManager.epicStorage.values()) {
                epic.getChildren().clear();
            }
            writeResponse(exchange, "Все подзадачи успешно удалены", 204);
        }
        private void handleDeleteAllEpics(HttpExchange exchange) throws IOException, InterruptedException {
            for (Object id : httpTaskManager.epicStorage.keySet()) {
                httpTaskManager.removeTask((Integer) id);
            }
            writeResponse(exchange, "Все эпики с подзадачами успешно удалены", 204);
        }
        private void handleGetEpicSubtasks(HttpExchange exchange) throws IOException{
            try {
                int id = parsePathId(exchange.getRequestURI().getQuery());
                if (id < 1) {
                    writeResponse(exchange, "Номер задачи не может быть отрицательным или равен нулю " + exchange.getRequestURI().getQuery(), 400);
                    return;
                }
                if (!httpTaskManager.epicStorage.containsKey(id)) {
                    writeResponse(exchange, "Эпика с идентификатором " + exchange.getRequestURI().getQuery() + " не существует", 400);
                    return;
                }
                List<Integer> childrenSubtask = httpTaskManager.epicStorage.get(id).getChildren();
                List<Subtask> mapOfSubtask = new ArrayList<>();
                for (Integer subId : childrenSubtask) {
                    mapOfSubtask.add(httpTaskManager.subTaskStorage.get(subId));
                }
                writeResponse(exchange, gson.toJson(mapOfSubtask), 200);
            } catch (NumberFormatException e) {
                writeResponse(exchange, "Введен не числовой идентификатор" + exchange.getRequestURI().getQuery(), 400);
            }
        }
        private void handleGetTasks(HttpExchange exchange) throws IOException{
            writeResponse(exchange, gson.toJson(httpTaskManager1.getTaskStorage()), 200);
        }
        private void handleGetSubtasks(HttpExchange exchange) throws IOException{
            writeResponse(exchange, gson.toJson(httpTaskManager.getSubTaskStorage()), 200);
        }
        private void handleGetEpic(HttpExchange exchange) throws IOException{
            writeResponse(exchange, gson.toJson(httpTaskManager.getEpicStorage()), 200);
        }
        private void handleCreateTask(HttpExchange exchange) throws IOException, InterruptedException {
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            Task task = gson.fromJson(body, Task.class);
            if (task.getName() == null) {
                writeResponse(exchange, "У задачи отсутствует название" , 400);
                return;
            }
            if (task.getDescription() == null) {
                writeResponse(exchange, "У задачи отсутствует описание" , 400);
                return;
            }
            httpTaskManager.createTask(task);
            writeResponse(exchange, "Задача создана", 201);
        }
        private void handleCreateSubtask(HttpExchange exchange) throws IOException, InterruptedException {
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            Subtask subtask = gson.fromJson(body, Subtask.class);
            if (subtask.getName() == null) {
                writeResponse(exchange, "У подзадачи отсутствует название" , 400);
                return;
            }
            if (subtask.getDescription() == null) {
                writeResponse(exchange, "У подзадачи отсутствует описание" , 400);
                return;
            }
            if (subtask.getParentId() == null) {
                writeResponse(exchange, "У подзадачи отсутствует родительский эпик" , 400);
                return;
            }
            if (!httpTaskManager.epicStorage.containsKey(subtask.getParentId())) {
                writeResponse(exchange, "Задан номер несуществующего эпика", 400);
                return;
            }
            httpTaskManager.createSubTask(subtask);
            writeResponse(exchange, "Подзадача создана", 201);
        }
        private void handleCreateEpic(HttpExchange exchange) throws IOException, InterruptedException {
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            Epic epic = gson.fromJson(body, Epic.class);
            if (epic.getName() == null) {
                writeResponse(exchange, "У эпика отсутствует название" , 400);
                return;
            }
            if (epic.getDescription() == null) {
                writeResponse(exchange, "У эпика отсутствует описание" , 400);
                return;
            }
            if (epic.getEpic() == null) {
                writeResponse(exchange, "У задачи не указано, что она Эпик" , 400);
                return;
            }
            httpTaskManager.createEpic(epic);
            writeResponse(exchange, "Задача создана", 200);
        }
        private void handleUpdateTask(HttpExchange exchange) throws IOException, InterruptedException {
            try {
                InputStream inputStream = exchange.getRequestBody();
                String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                int id = parsePathId(exchange.getRequestURI().getQuery());
                if (!httpTaskManager.taskStorage.containsKey(id)) {
                    writeResponse(exchange, "Задан номер несуществующей задачи", 404);
                    return;
                }
                Task task = gson.fromJson(body, Task.class);
                httpTaskManager.updateTask(id, task);
                writeResponse(exchange, "Задача обновлена", 200);
            } catch (NumberFormatException e) {
                writeResponse(exchange, "Введен не числовой идентификатор" + exchange.getRequestURI().getQuery(), 400);
            }
        }
        private void handleUpdateSubtask(HttpExchange exchange) throws IOException, InterruptedException {
            try {
                InputStream inputStream = exchange.getRequestBody();
                String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                int id = parsePathId(exchange.getRequestURI().getQuery());
                if (!httpTaskManager.subTaskStorage.containsKey(id)) {
                    writeResponse(exchange, "Задан номер несуществующей подзадачи", 404);
                    return;
                }
                Subtask subtask = gson.fromJson(body, Subtask.class);
                httpTaskManager.updateSubTask(id, subtask);
                writeResponse(exchange, "Подзадача обновлена", 200);
            } catch (NumberFormatException e) {
                writeResponse(exchange, "Введен не числовой идентификатор" + exchange.getRequestURI().getQuery(), 400);
            }
        }
        private void handleUpdateEpic(HttpExchange exchange) throws IOException, InterruptedException {
            try {
                InputStream inputStream = exchange.getRequestBody();
                String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                int id = parsePathId(exchange.getRequestURI().getQuery());
                if (!httpTaskManager.epicStorage.containsKey(id)) {
                    writeResponse(exchange, "Задан номер несуществующего эпика", 404);
                    return;
                }
                Epic epic = gson.fromJson(body, Epic.class);
                httpTaskManager.updateEpic(id, epic);
                writeResponse(exchange, "Эпик обновлен", 200);
            } catch (NumberFormatException e) {
                writeResponse(exchange, "Введен не числовой идентификатор" + exchange.getRequestURI().getQuery(), 400);
            }
        }
        private int parsePathId(String path) {
            StringBuilder id = new StringBuilder(path);
            id.replace(0, 3, "");
            return Integer.parseInt(id.toString());
        }

        private void writeResponse(HttpExchange exchange,
                                   String responseString,
                                   int responseCode) throws IOException {
            if(responseString.isBlank()) {
                exchange.sendResponseHeaders(responseCode, 0);
            } else {
                byte[] bytes = responseString.getBytes(DEFAULT_CHARSET);
                exchange.sendResponseHeaders(responseCode, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            }
            exchange.close();
        }

        enum Endpoint {
            CREATE_SUBTASK, CREATE_EPIC, CREATE_TASK, UPDATE_TASK, UPDATE_SUBTASK, UPDATE_EPIC,
            GET_BY_ID, GET_EPIC_SUBTASKS, GET_TASK, GET_SUBTASK, GET_EPIC,
            DELETE_TASK_BY_ID, DELETE_ALL_TASKS, DELETE_ALL_SUBTASKS, DELETE_ALL_EPIC,
            GET_HISTORY, GET_PRIORITIZED, UNKNOWN}
    }
    static class LocalDateAdapter extends TypeAdapter<LocalDateTime> {
        private static final DateTimeFormatter formatterWriter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        private static final DateTimeFormatter formatterReader = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        @Override
        public void write(final JsonWriter jsonWriter, final LocalDateTime localDateTime) throws IOException, NullPointerException {
            jsonWriter.value((localDateTime.format(formatterWriter)));

        }
        @Override
        public LocalDateTime read(final JsonReader jsonReader) throws IOException, NullPointerException  {
            return LocalDateTime.parse(jsonReader.nextString(), formatterReader);
        }
    }
    public static void main(String[] args) throws IOException, InterruptedException {

        try {
                httpTaskManager = new HttpTaskManager();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }

        HttpTaskServer httpTaskServer = new HttpTaskServer(httpTaskManager);

        httpTaskManager.createTask(new Task("Task_1", "Desk_task_1"
                ,"01.07.2023 10:00", "PT10M"));
        httpTaskManager.createTask(new Task("Task_2", "Desk_task_2"));
        httpTaskManager.createEpic(new Epic("Epic_1", "Desk_Epic_1"));
        httpTaskManager.createEpic(new Epic("Epic_2", "Desk_Epic_2"));
        httpTaskManager.createSubTask(new Subtask("Sub_1", "Desk_Sub_1",
                "01.07.2023 10:10", "PT10M", 3));
        httpTaskManager.createSubTask(new Subtask("Sub_2", "Desk_Sub_2", 3));
        httpTaskManager.createSubTask(new Subtask("Sub_3", "Desk_Sub_3",
                "01.07.2023 10:20", "PT10M", 3));
        httpTaskManager.getById(6);
        httpTaskManager.getById(5);
        httpTaskManager.getById(3);
        httpTaskManager.getById(4);
        httpTaskManager.getById(1);
        httpTaskManager.getById(2);
        httpTaskManager.getById(7);

        httpTaskManager1 = new HttpTaskManager();
        try {
            httpTaskManager1 = new HttpTaskManager();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        httpTaskManager1 = httpTaskManager1.loadAllTasks();

    }



}
