package http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Task;
import service.managers.TaskManager;

import java.io.IOException;
import java.util.Set;
import java.util.regex.Pattern;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {

    private TaskManager taskManager;
    private Gson gson;

    public PrioritizedHandler(final TaskManager taskManager, final Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String prioritizedPath = "^/api/v1/prioritized$";
        String path = httpExchange.getRequestURI().getPath();

        if (Pattern.matches(prioritizedPath, path)) {
            Set<Task> tasks = taskManager.getPrioritizedTasks();
            String s = gson.toJson(tasks);
            sendText200(httpExchange, s, 200);
        } else {
            sendBadRequest400(httpExchange, "Неверный путь запроса", 400);
        }
        httpExchange.close();
    }
}