package http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Task;
import service.in_memory.InMemoryHistoryManager;
import service.in_memory.InMemoryTaskManager;
import service.managers.HistoryManager;
import service.managers.Managers;
import service.managers.TaskManager;

import java.io.IOException;
import java.util.Set;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    HistoryManager historyManager = new InMemoryHistoryManager();
    TaskManager taskManager = new InMemoryTaskManager(historyManager);

    Gson gson = Managers.getGson();
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        System.out.println("Началась обработка /tasks/prioritized/ запроса от клиента.");
        if ("GET".equals(httpExchange.getRequestMethod())) {
            Set<Task> tasks = taskManager.getPrioritizedTasks();
            String s = gson.toJson(tasks);
            sendText200(httpExchange, s, 200);
            return;
        }
        httpExchange.close();
    }
}