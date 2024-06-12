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
    private final String SUBTASKS_PATH = "^/api/v1/subtasks$";
    private final String SUBTASKS_ID_PATH = "^/api/v1/subtasks/\\d+$";
    TaskManager taskManager;
    Gson gson;

    public SubtasksHandler(final TaskManager taskManager, final Gson gson) {
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

    private void handleGetSubtask(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        //get
        if (Pattern.matches(SUBTASKS_PATH, path)) {
            String response = gson.toJson(taskManager.getAllSubtasks());
            sendText200(httpExchange, response, 200);
        }
        //задача по айди
        if (Pattern.matches(SUBTASKS_ID_PATH, path)) {
            String pathId = path.replaceFirst("/api/v1/subtasks/", "");
            int id = parsePathId(pathId);
            if (id != -1) {
                String response = gson.toJson(taskManager.getSubtaskById(id));
                sendText200(httpExchange, response, 200);
            } else {
                System.out.println("Получен некорректный идентификатор задачи = " + id);
                httpExchange.sendResponseHeaders(405, 0);
            }
        }
    }

    private void handlePostSubtask(HttpExchange exchange, String path) throws IOException, TaskNotFoundException {
        if (Pattern.matches(SUBTASKS_PATH, path)) {
            handleAddSubtask(exchange);
        } else if (Pattern.matches(SUBTASKS_ID_PATH, path)) {
            handleUpdateSubtask(exchange, path);
        } else {
            sendBadRequest400(exchange, "Неверный путь запроса", 400);
        }
    }

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

    //обновление задачи
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

    private void handleDeleteSubtask(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        //все задачи
        if (Pattern.matches(SUBTASKS_PATH, path)) {
            taskManager.deleteAllSubtasks();
            sendSuccessButNoNeedToReturn201(httpExchange, "Подзадачи удалены", 201);
        }
        //задачи по айди
        if (Pattern.matches(SUBTASKS_ID_PATH, path)) {
            String requestMethod = httpExchange.getRequestMethod();
            String pathId = path.replaceFirst("/api/v1/subtasks/", "");
            int id = parsePathId(pathId);
            if (id != -1) {
                taskManager.deleteSubtaskById(id);
                sendText200(httpExchange, "Удалена подзадача с айди = " + id, 200);
            } else {
                System.out.println("Получен некорректный идентификатор задачи = " + id);
                httpExchange.sendResponseHeaders(405, 0);
            }
        } else {
            httpExchange.sendResponseHeaders(405, 0);
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
}