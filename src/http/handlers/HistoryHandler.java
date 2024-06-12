package http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Task;
import service.managers.TaskManager;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    TaskManager taskManager;
    Gson gson;

    public HistoryHandler(final TaskManager taskManager, final Gson gson) throws IOException {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String historyPath = "^/api/v1/history$";
        String path = httpExchange.getRequestURI().getPath();

        if (Pattern.matches(historyPath, path)) {
            List<Task> tasks = taskManager.getHistory();
            String s = gson.toJson(tasks);
            sendText200(httpExchange, s, 200);
        } else {
            sendBadRequest400(httpExchange, "Неверный путь запроса", 400);
        }
        httpExchange.close();
    }
}