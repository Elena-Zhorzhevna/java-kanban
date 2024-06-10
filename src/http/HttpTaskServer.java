package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import http.handlers.*;
import service.managers.Managers;
import service.managers.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;

public class HttpTaskServer {

    public static final int PORT = 8080;

    private HttpServer server;
    private Gson gson;
    private TaskManager taskManager;



    public void start() {
        System.out.println("Started TaskServer " + PORT);
        System.out.println("http://localhost:" + PORT + "/api/v1/tasks");
        server.start();
    }

    public HttpTaskServer() throws IOException {
        this(Managers.getFileBackedTaskManager(Path.of("testTask18152325523283051340.csv").toFile()));
    }

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        gson = Managers.getGson();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/api/v1/tasks/", new TasksHandler(this.taskManager, this.gson));
        server.createContext("/api/v1/epics/", new EpicsHandler(this.taskManager, this.gson));
        server.createContext("/api/v1/subtasks/", new SubtasksHandler(this.taskManager, this.gson));
        server.createContext("/tasks/history/", new HistoryHandler());
        server.createContext("/tasks/prioritized/", new PrioritizedHandler());
        //server.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer taskServer = new HttpTaskServer(Managers
                .getFileBackedTaskManager(Path.of("testTask18152325523283051340.csv").toFile()));
        taskServer.start();
    }

    public void stop () {
        server.stop(0);
        System.out.println("Остановили сервер на порту " + PORT);
    }
}


/*    public HttpTaskServer(final TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;

        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/api/v1/tasks", new TasksHandler());
        //server.createContext("/api/v1/subtasks",new SubtaskHandler());
        //server.createContext("/api/v1/epics",new EpicHandler());
        server.createContext("/api/v1/history",new HistoryHandler());
        server.createContext("/api/v1/prioritized",new PrioritizedHandler());

          gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .serializeNulls()
                .create();
    }*/
