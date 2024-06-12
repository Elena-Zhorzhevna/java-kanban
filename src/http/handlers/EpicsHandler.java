package http.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Epic;
import service.exception.TaskIntersectionException;
import service.exception.TaskNotFoundException;
import service.managers.TaskManager;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final String EPICS_PATH = "^/api/v1/epics$";
    private final String EPICS_ID_PATH = "^/api/v1/epics/\\d+$";
    private
    TaskManager taskManager;
    Gson gson;

    public EpicsHandler(final TaskManager taskManager, final Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    private int parsePathId(String path) {
        try {
            return Integer.parseInt(path);
        } catch (NumberFormatException exception) {
            return -1;
        }
    }

    private String readText(HttpExchange httpExchange) throws IOException {
        return new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        String path = httpExchange.getRequestURI().getPath();
        String requestMethod = httpExchange.getRequestMethod();
        try {
            switch (requestMethod) {

                case "GET": //получение задачи
                    handleGetEpics(httpExchange);
                    break;
                case "POST": //добавление или обновление задачи
                    handlePostEpics(httpExchange, path);
                    break;
                case "DELETE": //удаление задач
                    handleDeleteEpics(httpExchange);
                    break;
                default:
                    sendNotAllowed405(httpExchange, "Ждем GET, POST или DELETE запрос, а получили - "
                            + requestMethod, 405);
            }
        } catch (Exception exception) {
            exception.printStackTrace();

        } finally {
            httpExchange.close();
        }
    }

    private void handleGetEpics(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        //GET все эпики
        if (Pattern.matches(EPICS_PATH, path)) {
            String response = gson.toJson(taskManager.getAllEpics());
            sendText200(httpExchange, response, 200);
        }
        //GET эпик по айди
        if (Pattern.matches(EPICS_ID_PATH, path)) {
            String pathId = path.replaceFirst("/api/v1/epics/", "");
            int id = parsePathId(pathId);
            if (id != -1) {
                String response = gson.toJson(taskManager.getEpicById(id));
                sendText200(httpExchange, response, 200);
            } else {
                System.out.println("Получен некорректный идентификатор задачи = " + id);
                httpExchange.sendResponseHeaders(405, 0);
            }
        }

        //GET все сабтаски эпика
        String EPIC_SUBTASKS_BY_ID = "^/api/v1/epics/\\d+/subtasks$";
        if (Pattern.matches(EPIC_SUBTASKS_BY_ID, path)) {
            String pathId = path.replaceFirst("/api/v1/epics/", "")
                    .replaceFirst("/subtasks", "");
            int id = parsePathId(pathId);
            if (id != -1) {
                String response = gson.toJson(taskManager.getAllEpicSubtasks(id));
                sendText200(httpExchange, response, 200);
            } else {
                System.out.println("Получен некорректтный идентификатор задачи = " + id);
                httpExchange.sendResponseHeaders(405, 0);
            }
        }
    }

    private void handlePostEpics(HttpExchange exchange, String path) throws IOException, TaskNotFoundException {
        if (Pattern.matches(EPICS_PATH, path)) {
            handleAddEpic(exchange);
        } else if (Pattern.matches(EPICS_ID_PATH, path)) {
            handleUpdateEpic(exchange, path);
        } else {
            sendBadRequest400(exchange, "Неверный путь запроса", 400);
        }
    }

    private void handleAddEpic(HttpExchange exchange) throws IOException {
        final String requestBody = readText(exchange);
        final JsonObject jsonBody = JsonParser.parseString(requestBody).getAsJsonObject();
        if (!isValidJsonEpic(jsonBody)) {
            sendNotAllowed405(exchange, "Неправильный набор полей в теле запроса", 405);
            return;
        }
        Epic epic = gson.fromJson(requestBody, Epic.class);
        try {
            String response = gson.toJson(taskManager.createEpic(epic));
            sendSuccessButNoNeedToReturn201(exchange, "Задача добавлена в TaskManager", 201);
        } catch (Exception e) {
            sendHasInteractions406(exchange, "Задача пересекается по времени с существующей задачей",
                    406);
        }
    }

    //обновление задачи
    private void handleUpdateEpic(HttpExchange httpExchange, String path) throws IOException, TaskNotFoundException {
        final String pathId = path.replaceFirst("/api/v1/epics/", "");
        final int id = parsePathId(pathId);
        if (id <= 0) {
            sendNotAllowed405(httpExchange, "Неправильный формат id", 405);
            return;
        }
        final String requestBody = readText(httpExchange);
        final JsonObject jsonBody = JsonParser.parseString(requestBody).getAsJsonObject();
        if (!isValidJsonEpic(jsonBody)) {
            sendNotAllowed405(httpExchange, "Неправильный набор полей в теле запроса", 405);
            return;
        }
        Epic epic = gson.fromJson(requestBody, Epic.class);
        if (epic.getId() != id) {
            sendBadRequest400(httpExchange, "Id в path и теле запроса не равны", 400);
            return;
        }
        try {
            taskManager.updateEpic(epic);
            sendSuccessButNoNeedToReturn201(httpExchange, "Задача обновлена - 201, id = " + id,
                    201);
        } catch (TaskIntersectionException e) {
            sendHasInteractions406(httpExchange, "Задача пересекается по времени с существующей задачей",
                    406);
        }
    }

    private void handleDeleteEpics(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        //все задачи
        if (Pattern.matches(EPICS_PATH, path)) {
            taskManager.deleteAllEpics();
            sendSuccessButNoNeedToReturn201(httpExchange, "Задачи удалены", 201);
        }
        //задачи по айди
        if (Pattern.matches(EPICS_ID_PATH, path)) {
            String requestMethod = httpExchange.getRequestMethod();
            String pathId = path.replaceFirst("/api/v1/epics/", "");
            int id = parsePathId(pathId);
            if (id != -1) {
                taskManager.deleteEpicById(id);
                sendText200(httpExchange, "Удалена задача с айди = " + id, 200);
            } else {
                System.out.println("Получен некорректный идентификатор задачи = " + id);
                httpExchange.sendResponseHeaders(405, 0);
            }
        } else {
            httpExchange.sendResponseHeaders(405, 0);
        }
    }

    private boolean isValidJsonEpic(JsonObject jsonObject) {
        return jsonObject.has("id") &&
                jsonObject.has("taskName") &&
                jsonObject.has("description");
    }
}
