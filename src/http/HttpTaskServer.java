package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import service.managers.Managers;
import service.managers.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.regex.Pattern;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private HttpServer server;
    private TaskManager taskManager;

    private Gson gson;






    GsonBuilder gsonBuilder = new GsonBuilder();
gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateAdapter());
    Gson gson = gsonBuilder.create();


    public HttpTaskServer() throws IOException {
        this(Managers.getDefault());
    }
    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        gson = Managers.getGson();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT),0);
        server.createContext("/api/v1/tasks", this::TaskHandler);
    }


    private void TaskHandler(HttpExchange httpExchange) {
        try{
            String path = httpExchange.getRequestURI().getPath();
            String requestMethod = httpExchange.getRequestMethod();

            switch (requestMethod) {
                case "GET": {
                    if (Pattern.matches("^/api/v1/tasks/$", path)) {
                        String response = gson.toJson(taskManager.getAllTasks());
                        sendText(httpExchange, response);
                        break;
                    }

                    if (Pattern.matches("^/api/v1/tasks/\\d+$", path)) {
                        String pathId = path.replaceFirst("api/v1/tasks/", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {
                            String response = gson.toJson(taskManager.getTaskById(id));
                            sendText(httpExchange, response);
                            break;
                        } else {
                            System.out.println("Получен некорректтный идентификатор задачи = " + id);
                            httpExchange.sendResponseHeaders(405, 0);
                            break;
                        }

                    } else {
                        httpExchange.sendResponseHeaders(405, 0);
                        break;
                    }


//Этот метод надо доработать
                   /* if (Pattern.matches("^/api/v1/epics/\\d+/subtasks$", path)) {
                        String pathId = path.replaceFirst("/api/v1/epics/", "")
                                .replaceFirst("/subtasks", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {
                            String response = gson.toJson(taskManager.getAllEpicSubtasks(id));
                            sendText(httpExchange, response);
                            break;
                        } else {
                            System.out.println("Получен некорректтный идентификатор задачи = " + id);
                            httpExchange.sendResponseHeaders(405, 0);
                            break;
                        }
                    }
                        break;
                } */
                }


                case "POST": {

                    break;
                }
                case "DELETE": {
                 if (Pattern.matches("^/api/v1/tasks/\\d+$", path)){
                     String pathId = path.replaceFirst("api/v1/tasks/", "");
                     int id = parsePathId(pathId);
                     if (id != -1) {
                         taskManager.deleteByTaskId(id);
                         System.out.println("Удалена задача с айди = " + id);
                         httpExchange.sendResponseHeaders(200, 0);
                     } else {
                         System.out.println("Получен некорректтный идентификатор задачи = " + id);
                         httpExchange.sendResponseHeaders(405, 0);
                     }

                 } else {
                     httpExchange.sendResponseHeaders(405,0);
                 } break;



                }
                default: {
                    System.out.println("Ждем GET, POST или DELETE запрос, а получили - " + requestMethod);
                    httpExchange.sendResponseHeaders(405, 0);
                }
            }



        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private int parsePathId(String path) {
       try {
           return Integer.parseInt(path);
       } catch (NumberFormatException exception) {
           return -1;
       }
    }
    public void start() {
        System.out.println("Started TaskServer " + PORT);
        System.out.println("http://localhost:" + PORT + "/api/v1/tasks");
        server.start();
    }


    public void stop() {
        server.stop(0);
        System.out.println("Остановили сервер на порту " + PORT);
    }

    private String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    private void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(200,resp.length);
        h.getResponseBody().write(resp);
    }
}

