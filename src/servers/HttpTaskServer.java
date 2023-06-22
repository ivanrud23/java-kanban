package servers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import managers.HttpTaskManager;
import managers.Managers;
import model.Epic;
import model.Subtask;
import model.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class HttpTaskServer {
    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final Gson gson = Managers.getGson();
    private HttpTaskManager httpTaskManager;

    private final HttpServer httpServer;
//    private final KVServer kvServer = new KVServer();

    public HttpTaskServer() throws IOException, InterruptedException {
//        kvServer.start();
        this.httpServer = HttpServer.create();
        httpTaskManager = new HttpTaskManager();
        this.httpServer.bind(new InetSocketAddress(PORT), 0);
        this.httpServer.createContext("/tasks", new TasksHandler());
        this.httpServer.createContext("/tasks/subtask", new SubTasksHandler());
        this.httpServer.createContext("/tasks/epic", new EpicHandler());
        this.httpServer.createContext("/tasks/history", new HistoryHandler());
        this.httpServer.start();

        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public HttpTaskManager getHttpTaskManager() {
        return httpTaskManager;
    }

//    public KVServer getKvServer() {
//        return kvServer;
//    }

    public void startHttpTaskServer() {
        httpServer.start();
    }

    public void stopHttpTaskServer() {
        System.out.println("Останавливаем порт " + PORT);
        httpServer.stop(0);
    }

    class SubTasksHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestPath = exchange.getRequestURI().getPath();
            String[] pathParts = requestPath.split("/");
            String query = exchange.getRequestURI().getQuery();
            String requestMethod = exchange.getRequestMethod();

            if (pathParts.length == 3 && pathParts[2].equals("subtask") && requestMethod.equals("GET") && query == null) {
                handleGetSubtasks(exchange);
            } else if (pathParts.length == 4 && pathParts[2].equals("subtask") && requestMethod.equals("GET") && query != null) {
                handleGetEpicSubtasks(exchange);
            } else if (pathParts.length == 3 && pathParts[2].equals("subtask") && requestMethod.equals("POST") && query == null) {
                try {
                    handleCreateSubtask(exchange);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else if (pathParts.length == 3 && pathParts[2].equals("subtask") && requestMethod.equals("POST") && query != null) {
                try {
                    handleUpdateSubtask(exchange);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else if (pathParts.length == 3 && pathParts[2].equals("subtask") && requestMethod.equals("DELETE") && query == null) {
                handleDeleteAllSubtasks(exchange);
            } else {
                writeResponse(exchange, "Такого эндпоинта не существует", 404);
            }
        }

        private void handleGetSubtasks(HttpExchange exchange) throws IOException {
            writeResponse(exchange, gson.toJson(httpTaskManager.getSubTaskStorage()), 200);
        }

        private void handleCreateSubtask(HttpExchange exchange) throws IOException, InterruptedException {
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            Subtask subtask = gson.fromJson(body, Subtask.class);
            if (subtask.getName() == null) {
                writeResponse(exchange, "У подзадачи отсутствует название", 400);
                return;
            }
            if (subtask.getParentId() == null) {
                writeResponse(exchange, "У подзадачи отсутствует родительский эпик", 400);
                return;
            }
            if (!httpTaskManager.getEpicStorage().containsKey(subtask.getParentId())) {
                writeResponse(exchange, "Задан номер несуществующего эпика", 400);
                return;
            }
            httpTaskManager.createSubTask(subtask);
            writeResponse(exchange, "Подзадача создана", 201);
        }

        private void handleDeleteAllSubtasks(HttpExchange exchange) throws IOException {
            httpTaskManager.getSubTaskStorage().clear();
            for (Epic epic : httpTaskManager.getEpicStorage().values()) {
                epic.getChildren().clear();
            }
            writeResponse(exchange, "Все подзадачи успешно удалены", 204);
        }

        private void handleGetEpicSubtasks(HttpExchange exchange) throws IOException {
            try {
                int id = parsePathId(exchange.getRequestURI().getQuery());
                if (id < 1) {
                    writeResponse(exchange, "Номер задачи не может быть отрицательным или равен нулю " + exchange.getRequestURI().getQuery(), 400);
                    return;
                }
                if (!httpTaskManager.getEpicStorage().containsKey(id)) {
                    writeResponse(exchange, "Эпика с идентификатором " + exchange.getRequestURI().getQuery() + " не существует", 400);
                    return;
                }
                List<Integer> childrenSubtask = httpTaskManager.getEpicStorage().get(id).getChildren();
                List<Subtask> mapOfSubtask = new ArrayList<>();
                for (Integer subId : childrenSubtask) {
                    mapOfSubtask.add(httpTaskManager.getSubTaskStorage().get(subId));
                }
                writeResponse(exchange, gson.toJson(mapOfSubtask), 200);
            } catch (NumberFormatException e) {
                writeResponse(exchange, "Введен не числовой идентификатор" + exchange.getRequestURI().getQuery(), 400);
            }
        }

        private void handleUpdateSubtask(HttpExchange exchange) throws IOException, InterruptedException {
            try {
                InputStream inputStream = exchange.getRequestBody();
                String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                int id = parsePathId(exchange.getRequestURI().getQuery());
                if (!httpTaskManager.getSubTaskStorage().containsKey(id)) {
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
    }

    class EpicHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestPath = exchange.getRequestURI().getPath();
            String[] pathParts = requestPath.split("/");
            String query = exchange.getRequestURI().getQuery();
            String requestMethod = exchange.getRequestMethod();

            if (pathParts.length == 3 && pathParts[2].equals("epic") && requestMethod.equals("GET") && query == null) {
                handleGetEpic(exchange);
            } else if (pathParts.length == 3 && pathParts[2].equals("epic") && requestMethod.equals("POST") && query == null) {
                try {
                    handleCreateEpic(exchange);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else if (pathParts.length == 3 && pathParts[2].equals("epic") && requestMethod.equals("POST") && query != null) {
                try {
                    handleUpdateEpic(exchange);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else if (pathParts.length == 3 && pathParts[2].equals("epic") && requestMethod.equals("DELETE") && query == null) {
                try {
                    handleDeleteAllEpics(exchange);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else {
                writeResponse(exchange, "Такого эндпоинта не существует", 404);
            }
        }

        private void handleGetEpic(HttpExchange exchange) throws IOException {
            writeResponse(exchange, gson.toJson(httpTaskManager.getEpicStorage()), 200);
        }

        private void handleCreateEpic(HttpExchange exchange) throws IOException, InterruptedException {
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            Epic epic = gson.fromJson(body, Epic.class);
            if (epic.getName() == null) {
                writeResponse(exchange, "У эпика отсутствует название", 400);
                return;
            }
            if (epic.getEpic() == null) {
                writeResponse(exchange, "У задачи не указано, что она Эпик", 400);
                return;
            }
            httpTaskManager.createEpic(epic);
            writeResponse(exchange, "Задача создана", 200);
        }

        private void handleUpdateEpic(HttpExchange exchange) throws IOException, InterruptedException {
            try {
                InputStream inputStream = exchange.getRequestBody();
                String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                int id = parsePathId(exchange.getRequestURI().getQuery());
                if (!httpTaskManager.getEpicStorage().containsKey(id)) {
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

        private void handleDeleteAllEpics(HttpExchange exchange) throws IOException, InterruptedException {
            for (Integer id : httpTaskManager.getEpicStorage().keySet()) {
                httpTaskManager.removeTask(id);
            }
            writeResponse(exchange, "Все эпики с подзадачами успешно удалены", 204);
        }
    }

    class HistoryHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestPath = exchange.getRequestURI().getPath();
            String[] pathParts = requestPath.split("/");

            if (pathParts.length == 3 && pathParts[2].equals("history")) {
                handleGetHistory(exchange);
            }
        }

        private void handleGetHistory(HttpExchange exchange) throws IOException {
            writeResponse(exchange, gson.toJson(httpTaskManager.getInMemoryHistoryManager().getHistory()), 200);
        }
    }


    class TasksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestPath = exchange.getRequestURI().getPath();
            String[] pathParts = requestPath.split("/");
            String query = exchange.getRequestURI().getQuery();
            String requestMethod = exchange.getRequestMethod();

            if (pathParts.length == 2 && requestMethod.equals("GET")) {
                handleGetPrioritized(exchange);
            } else if (pathParts.length == 3 && pathParts[2].equals("task") && requestMethod.equals("GET") && query == null) {
                handleGetTasks(exchange);
            } else if (pathParts.length == 3 && requestMethod.equals("GET") && query != null) {
                try {
                    handleGetById(exchange);
                } catch (InterruptedException | NumberFormatException e) {
                    throw new RuntimeException(e.getMessage());
                }
            } else if (pathParts.length == 3 && pathParts[2].equals("task") && requestMethod.equals("POST") && query == null) {
                try {
                    handleCreateTask(exchange);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else if (pathParts.length == 3 && pathParts[2].equals("task") && requestMethod.equals("POST") && query != null) {
                try {
                    handleUpdateTask(exchange);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else if (pathParts.length == 3 && pathParts[2].equals("task") && requestMethod.equals("DELETE") && query != null) {
                handleDeleteTaskById(exchange);
            } else if (pathParts.length == 3 && pathParts[2].equals("task") && requestMethod.equals("DELETE") && query == null) {
                handleDeleteAllTasks(exchange);
            } else {
                writeResponse(exchange, "Такого эндпоинта не существует", 404);
            }
        }

        private void handleGetPrioritized(HttpExchange exchange) throws IOException {
            writeResponse(exchange, gson.toJson(httpTaskManager.getPrioritizedTasks()), 200);
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

        private void handleDeleteTaskById(HttpExchange exchange) throws IOException {
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

        private void handleDeleteAllTasks(HttpExchange exchange) throws IOException {
            httpTaskManager.getTaskStorage().clear();
            writeResponse(exchange, "Все задачи успешно удалены", 204);
        }


        private void handleGetTasks(HttpExchange exchange) throws IOException {
            writeResponse(exchange, gson.toJson(httpTaskManager.getTaskStorage()), 200);
        }


        private void handleCreateTask(HttpExchange exchange) throws IOException, InterruptedException {
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            Task task = gson.fromJson(body, Task.class);
            if (task.getName() == null) {
                writeResponse(exchange, "У задачи отсутствует название", 400);
                return;
            }

            httpTaskManager.createTask(task);
            writeResponse(exchange, "Задача создана", 201);
        }

        private void handleUpdateTask(HttpExchange exchange) throws IOException, InterruptedException {
            try {
                InputStream inputStream = exchange.getRequestBody();
                String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                int id = parsePathId(exchange.getRequestURI().getQuery());
                if (!httpTaskManager.getTaskStorage().containsKey(id)) {
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
    }

    private static int parsePathId(String path) {
        StringBuilder id = new StringBuilder(path);
        id.replace(0, 3, "");
        return Integer.parseInt(id.toString());
    }

    private static void writeResponse(HttpExchange exchange,
                                      String responseString,
                                      int responseCode) throws IOException {
        if (responseString.isBlank()) {
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

    public static void main(String[] args) throws IOException, InterruptedException {

        KVServer kvServer = new KVServer();
        kvServer.start();
        HttpTaskServer httpTaskServer = new HttpTaskServer();

        HttpTaskManager httpTaskManager = httpTaskServer.getHttpTaskManager();


        httpTaskManager.createTask(new Task("Task_1", "Desk_task_1"
                , "01.07.2023 10:00", "PT10M"));
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


    }
}