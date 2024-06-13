package http.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Subtask;
import service.exception.TaskIntersectionException;
import service.exception.TaskNotFoundException;
import service.managers.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    private final String subtasksPath = "^/api/v1/subtasks$";
    private final String subtasksIdPath = "^/api/v1/subtasks/\\d+$";
    private TaskManager taskManager;
    private Gson gson;

    public SubtasksHandler(final TaskManager taskManager, final Gson gson) {
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
                    handleGetSubtask(httpExchange);
                    break;
                case "POST": //добавление или обновление задачи
                    handlePostSubtask(httpExchange, path);
                    break;
                case "DELETE": //удаление задач
                    handleDeleteSubtask(httpExchange);
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
    private void handleGetSubtask(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        //все подзадачи
        if (Pattern.matches(subtasksPath, path)) {
            String response = gson.toJson(taskManager.getAllSubtasks());
            sendText200(httpExchange, response, 200);
        }
        //задача по айди
        if (Pattern.matches(subtasksIdPath, path)) {
            String pathId = path.replaceFirst("/api/v1/subtasks/", "");
            int id = parsePathId(pathId);
            if (id != -1) {
                String response = gson.toJson(taskManager.getSubtaskById(id));
                sendText200(httpExchange, response, 200);
            } else {
                sendNotAllowed405(httpExchange, "Получен некорректный идентификатор задачи = " + id,
                        405);
            }
        }
    }

    //обработчик POST-запроса
    private void handlePostSubtask(HttpExchange exchange, String path) throws IOException, TaskNotFoundException {
        if (Pattern.matches(subtasksPath, path)) {
            handleAddSubtask(exchange);
        } else if (Pattern.matches(subtasksIdPath, path)) {
            handleUpdateSubtask(exchange, path);
        } else {
            sendBadRequest400(exchange, "Неверный путь запроса", 400);
        }
    }

    //обработчик добавления подзадачи
    private void handleAddSubtask(HttpExchange exchange) throws IOException {
        final String requestBody = readText(exchange);
        final JsonObject jsonBody = JsonParser.parseString(requestBody).getAsJsonObject();
        if (!isValidJsonSubtask(jsonBody)) {
            sendNotAllowed405(exchange, "Неправильный набор полей в теле запроса", 405);
            return;
        }
        Subtask subtask = gson.fromJson(requestBody, Subtask.class);
        try {
            String response = gson.toJson(taskManager.createSubtask(subtask));
            sendSuccessButNoNeedToReturn201(exchange, "Задача добавлена в TaskManager", 201);
        } catch (Exception e) {
            sendHasInteractions406(exchange, "Задача пересекается по времени с существующей задачей",
                    406);
        }
    }

    //обработчик обновления подзадачи
    private void handleUpdateSubtask(HttpExchange httpExchange, String path) throws IOException, TaskNotFoundException {
        final String pathId = path.replaceFirst("/api/v1/subtasks/", "");
        final int id = parsePathId(pathId);
        if (id <= 0) {
            sendNotAllowed405(httpExchange, "Неправильный формат id", 405);
            return;
        }

        final String requestBody = readText(httpExchange);
        final JsonObject jsonBody = JsonParser.parseString(requestBody).getAsJsonObject();
        if (!isValidJsonSubtask(jsonBody)) {
            sendNotAllowed405(httpExchange, "Неправильный набор полей в теле запроса", 405);
            return;
        }
        Subtask subtask = gson.fromJson(requestBody, Subtask.class);
        if (subtask.getId() != id) {
            sendBadRequest400(httpExchange, "Id в path и теле запроса не равны", 400);
            return;
        }
        try {
            taskManager.updateSubtask(subtask);
            sendSuccessButNoNeedToReturn201(httpExchange, "Подзадача обновлена - 201, id = " + id,
                    201);
        } catch (TaskIntersectionException e) {
            sendHasInteractions406(httpExchange, "Подзадача пересекается по времени с существующей задачей",
                    406);
        }
    }

    //обработчик DELETE - запроса
    private void handleDeleteSubtask(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        //все подзадачи
        if (Pattern.matches(subtasksPath, path)) {
            taskManager.deleteAllSubtasks();
            sendSuccessButNoNeedToReturn201(httpExchange, "Подзадачи удалены", 201);
        }
        //подзадачи по айди
        if (Pattern.matches(subtasksIdPath, path)) {
            String requestMethod = httpExchange.getRequestMethod();
            String pathId = path.replaceFirst("/api/v1/subtasks/", "");
            int id = parsePathId(pathId);
            if (id != -1) {
                taskManager.deleteSubtaskById(id);
                sendText200(httpExchange, "Удалена подзадача с айди = " + id, 200);
            } else {
                sendNotAllowed405(httpExchange, "Получен некорректный идентификатор задачи = " + id,
                        405);
            }
        }
    }

    private boolean isValidJsonSubtask(JsonObject jsonObject) {
        return jsonObject.has("id") &&
                jsonObject.has("taskName") &&
                jsonObject.has("description") &&
                jsonObject.has("status") &&
                jsonObject.has("duration") &&
                jsonObject.has("startTime");
    }

    private int parsePathId(String path) {
        try {
            return Integer.parseInt(path);
        } catch (NumberFormatException exception) {
            return -1;
        }
    }
}