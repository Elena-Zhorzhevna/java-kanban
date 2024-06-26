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
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    private final String epicsPath = "^/api/v1/epics$";
    private final String epicsIdPath = "^/api/v1/epics/\\d+$";
    private TaskManager taskManager;
    private Gson gson;

    public EpicsHandler(final TaskManager taskManager, final Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
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

    //обработчик GET-запроса
    private void handleGetEpics(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        //GET все эпики
        if (Pattern.matches(epicsPath, path)) {
            String response = gson.toJson(taskManager.getAllEpics());
            sendText200(httpExchange, response, 200);
        }
        //GET эпик по айди
        if (Pattern.matches(epicsIdPath, path)) {
            String pathId = path.replaceFirst("/api/v1/epics/", "");
            int id = parsePathId(pathId);
            if (id != -1) {
                String response = gson.toJson(taskManager.getEpicById(id));
                sendText200(httpExchange, response, 200);
            } else {
                sendNotAllowed405(httpExchange, "Получен некорректный идентификатор задачи = " + id,
                        405);
            }
        }

        //GET все сабтаски эпика
        String epicSubtasksById = "^/api/v1/epics/\\d+/subtasks$";
        if (Pattern.matches(epicSubtasksById, path)) {
            String pathId = path.replaceFirst("/api/v1/epics/", "")
                    .replaceFirst("/subtasks", "");
            int id = parsePathId(pathId);
            if (id != -1) {
                String response = gson.toJson(taskManager.getAllEpicSubtasks(id));
                sendText200(httpExchange, response, 200);
            } else {
                sendNotAllowed405(httpExchange, "Получен некорректтный идентификатор задачи = " + id,
                        405);
            }
        }
    }

    //обработчик POST-запроса
    private void handlePostEpics(HttpExchange exchange, String path) throws IOException, TaskNotFoundException {
        if (Pattern.matches(epicsPath, path)) {
            handleAddEpic(exchange);
        } else if (Pattern.matches(epicsIdPath, path)) {
            handleUpdateEpic(exchange, path);
        } else {
            sendBadRequest400(exchange, "Неверный путь запроса", 400);
        }
    }

    //обработчик добавления задачи
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

    //обработчик обновления задачи
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

    //обработчик DELETE-запроса
    private void handleDeleteEpics(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        //все задачи
        if (Pattern.matches(epicsPath, path)) {
            taskManager.deleteAllEpics();
            sendSuccessButNoNeedToReturn201(httpExchange, "Задачи удалены", 201);
        }
        //задачи по айди
        if (Pattern.matches(epicsIdPath, path)) {
            String requestMethod = httpExchange.getRequestMethod();
            String pathId = path.replaceFirst("/api/v1/epics/", "");
            int id = parsePathId(pathId);
            if (id != -1) {
                taskManager.deleteEpicById(id);
                sendText200(httpExchange, "Удалена задача с айди = " + id, 200);
            } else {
                sendNotAllowed405(httpExchange, "Получен некорректтный идентификатор задачи = " + id,
                        405);
            }
        }
    }

    private int parsePathId(String path) {
        try {
            return Integer.parseInt(path);
        } catch (NumberFormatException exception) {
            return -1;
        }
    }

    private boolean isValidJsonEpic(JsonObject jsonObject) {
        return jsonObject.has("id") &&
                jsonObject.has("taskName") &&
                jsonObject.has("description");
    }
}
