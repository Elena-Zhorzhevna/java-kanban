package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import http.handlers.*;
import service.managers.Managers;
import service.managers.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {

    public static final int PORT = 8080;
    private static HttpServer server;
    private Gson gson;
    private TaskManager taskManager;

    public void start() {
        System.out.println("Started TaskServer " + PORT);
        System.out.println("http://localhost:" + PORT + "/api/v1/tasks");
        server.start();
    }

    public HttpTaskServer() throws IOException {
        this(Managers.getDefault());
    }

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        gson = Managers.getGson();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/api/v1/tasks", new TasksHandler(this.taskManager, this.gson));
        server.createContext("/api/v1/epics", new EpicsHandler(this.taskManager, this.gson));
        server.createContext("/api/v1/subtasks", new SubtasksHandler(this.taskManager, this.gson));
        server.createContext("/api/v1/history", new HistoryHandler(this.taskManager, this.gson));
        server.createContext("/api/v1/prioritized", new PrioritizedHandler(this.taskManager, this.gson));
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public static void main(String[] args) throws IOException {
        server.start();
    }

    public void stop() {
        server.stop(0);
        System.out.println("Остановили сервер на порту " + PORT);
    }
}
