package http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.managers.Managers;
import service.managers.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    TaskManager taskManager = Managers.getDefault();
    Gson gson = Managers.getGson();

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
    public void handle(HttpExchange httpExchange) throws IOException {



    }
}



