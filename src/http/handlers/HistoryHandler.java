package http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Task;
import service.managers.Managers;
import service.managers.TaskManager;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    TaskManager taskManager = Managers.getDefault();
    Gson gson = Managers.getGson();

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

            if ("GET".equals(httpExchange.getRequestMethod())) {
                List<Task> tasks = taskManager.getHistory();
                String s = gson.toJson(tasks);
                sendText200(httpExchange, s, 200);
                return;
            }
            httpExchange.close();
        }
    }