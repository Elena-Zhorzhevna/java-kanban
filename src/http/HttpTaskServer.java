package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import http.adapters.DurationAdapter;
import http.adapters.LocalDateTimeAdapter;
import http.handlers.*;
import model.Task;
import service.in_memory.InMemoryTaskManager;
import service.managers.Managers;
import service.managers.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.regex.Pattern;

public class HttpTaskServer {

    // Listens to 8080 port
    public static final int PORT = 8080;

    private HttpServer server;
    private Gson gson;

    private TaskManager taskManager;


/*    public HttpTaskServer(final TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        gson = Managers.getGson();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/api/v1/tasks", new TasksHandler());
        //server.createContext("/api/v1/subtasks",new SubtaskHandler());
        //server.createContext("/api/v1/epics",new EpicHandler());
        server.createContext("/api/v1/history",new HistoryHandler());
        server.createContext("/api/v1/prioritized",new PrioritizedHandler());
    }*/

    public void start() {
        System.out.println("Started TaskServer " + PORT);
        System.out.println("http://localhost:" + PORT + "/api/v1/tasks");
        server.start();
    }


  /*      public class HttpTaskServer {

    private static final int PORT = 8080;
    private HttpServer server;
    private TaskManager taskManager;
    private Gson gson;*/

  /*  public HttpTaskServer() throws IOException {
        this(Managers.getDefault());
    }*/

    public HttpTaskServer() throws IOException {
        this(Managers.getFileBackedTaskManager(Path.of("testTask18152325523283051340.csv").toFile()));
    }


    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/api/v1/tasks/", new TasksHandler());
        server.createContext("/api/v1/epics/", new EpicsHandler());
        server.createContext("/api/v1/subtasks/", new SubtasksHandler());
        server.createContext("/tasks/history/", new HistoryHandler());
        server.createContext("/tasks/prioritized/", new PrioritizedHandler());
        server.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");

        gson = new GsonBuilder()
                //.registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .serializeNulls()  //насчет этого не уверена
                .create();
    }
    public static void main(String[] args) throws IOException {
        HttpTaskServer taskServer = new HttpTaskServer(Managers
                .getFileBackedTaskManager(Path.of("testTask18152325523283051340.csv").toFile()));
        taskServer.start();
    }

    private String readText(HttpExchange httpExchange) throws IOException {
        return new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }


   // id,type,name,status,description,start_time, duration, end_time, epic
    private boolean isValidJsonTask(JsonObject jsonObject) {
        return jsonObject.has("id") &&
                jsonObject.has("type") &&
                jsonObject.has("name") &&
                jsonObject.has("description") &&
                jsonObject.has("status") &&
                jsonObject.has("duration") &&
                jsonObject.has("startTime");
    }
/*    public void handler(HttpExchange exchange) throws IOException {
        String response;
        String path = exchange.getRequestURI().getPath();
        String parameters = exchange.getRequestURI().getQuery();
        switch (path) {
            case "/tasks/task":
                handleTask(exchange);
                break;
            case "/tasks/subtask":
                handleSubtask(exchange);
                break;
            case "/tasks/epic":
                handleEpic(exchange);
                break;
            case "/tasks/subtask/epic":
                int id = Integer.parseInt(parameters.split("=")[1]);
                List<Subtask> subtasks = taskManager.getAllEpicSubtasks(id);
                if (subtasks == null) {
                    exchange.sendResponseHeaders(404, 0);
                    response = "Epic задача не найдена.";
                } else {
                    response = gson.toJson(subtasks);
                    exchange.sendResponseHeaders(200, 0);
                }
                sendText(exchange, response);
                exchange.close();
                break;
            case "/tasks/history":
                response = gson.toJson(TaskManager.getHistory());
                exchange.sendResponseHeaders(200, 0);
                sendText(exchange, response);
                exchange.close();
                break;
            case "/tasks":
                response = gson.toJson(taskManager.());
                exchange.sendResponseHeaders(200, 0);
                sendText(exchange, response);
                exchange.close();
                break;
        }
    }*/
//public class TasksHandler extends BaseHttpHandler {

    private int parsePathId(String path) {
        try {
            return Integer.parseInt(path);
        } catch (NumberFormatException exception) {
            return -1;
        }
    }
/*
    protected void taskHandler(HttpExchange httpExchange) throws IOException {

        try {
            String path = httpExchange.getRequestURI().getPath();
            String requestMethod = httpExchange.getRequestMethod();

            switch (requestMethod) {
                case "GET": { //все задачи
                    if (Pattern.matches("^/api/v1/tasks/$", path)) {
                        String response = gson.toJson(taskManager.getAllTasks());
                        sendText(httpExchange, response);
                        break;
                    }
                    //задача по айди
                    if (Pattern.matches("^/api/v1/tasks/\\d+$", path)) {
                        String pathId = path.replaceFirst("api/v1/tasks/", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {
                            String response = gson.toJson(taskManager.getTaskById(id));
                            sendText(httpExchange, response);
                            break;
                        } else {
                            System.out.println("Получен некорректный идентификатор задачи = " + id);
                            httpExchange.sendResponseHeaders(405, 0);
                            break;
                        }

                    } else {
                        httpExchange.sendResponseHeaders(405, 0);
                        break;
                    }
                }


                case "POST": { //добавление задачи
                    if (Pattern.matches("^/api/v1/tasks/$", path)) {

                        String response;
                        String body = readText(httpExchange);
                        if (body.isBlank()) {
                            httpExchange.sendResponseHeaders(404, 0);
                            sendText(httpExchange, "Task задача отсутствует в теле запроса.");
                        } else {
                            Task task = gson.fromJson(body, Task.class);
                            Task resultTask = taskManager.createTask(task);
                            if (resultTask == null) {
                                httpExchange.sendResponseHeaders(404, 0);
                                response = "Task задача пересекается с другими задачами.";
                            } else {
                                httpExchange.sendResponseHeaders(201, 0);
                                response = "Task задача добавлена.";
                            }

                            String response = gson.toJson(taskManager.createTask(task));
                            sendText(httpExchange, response);
                            break;
                        }
                        //обновление задачи
                        if (Pattern.matches("^/api/v1/tasks/\\d+$", path)) {
                            String response;
                            String pathId = path.replaceFirst("api/v1/tasks/", "");
                            int id = parsePathId(pathId);
                            String body = readText(httpExchange);
                            if (body.isBlank()) {
                                httpExchange.sendResponseHeaders(404, 0);
                                sendText(httpExchange, "Task задача отсутствует в теле запроса.");
                            } else {
                                Task task = gson.fromJson(body, Task.class);
                                Task resultTask = taskManager.createTask(task);
                                if (resultTask == null) {
                                    httpExchange.sendResponseHeaders(404, 0);
                                    response = "Task задача пересекается с другими задачами.";
                                } else {
                                    httpExchange.sendResponseHeaders(201, 0);
                                    response = "Task задача добавлена.";
                                }

                            }
*/
/*                    String parameters = httpExchange.getRequestURI().getQuery();
                    String response;
                    int id = 0;
                    if (parameters != null) {
                        id = Integer.parseInt(parameters.split("=")[1]);
                    }
                    String body = readText(httpExchange);
                    if (body.isBlank()) {
                        httpExchange.sendResponseHeaders(404, 0);
                        sendText(httpExchange, "Task задача отсутствует в теле запроса.");
                    } else {
                        Task task = gson.fromJson(body, Task.class);
                        if (parameters == null) {
                            Task resultTask = taskManager.createTask(task);
                            if (resultTask == null) {
                                httpExchange.sendResponseHeaders(400, 0);
                                response = "Task задача не добавлена.";
                            } else {
                                httpExchange.sendResponseHeaders(201, 0);
                                response = "Task задача добавлена.";
                            }
                        } else {
                            task.setId(id);
                            taskManager.updateTask(task);
                            if (taskManager.getTaskById(id) == null) {
                                httpExchange.sendResponseHeaders(400, 0);
                                response = "Не удалось обновить Task задачу.";
                            } else {
                                httpExchange.sendResponseHeaders(201, 0);
                                response = "Task задача " + id + " обновлена.";
                            }
                        }
                    }
                }
                break;*//*


                            case "DELETE": {
                                if (Pattern.matches("^/api/v1/tasks/\\d+$", path)) {
                                    String pathId = path.replaceFirst("api/v1/tasks/", "");
                                    int id = parsePathId(pathId);
                                    if (id != -1) {
                                        taskManager.deleteByTaskId(id);
                                        //System.out.println("Удалена задача с айди = " + id);
                                        //httpExchange.sendResponseHeaders(200, 0);
                                        sendText(httpExchange, "Удалена задача с айди = " + id);
                                    } else {
                                        System.out.println("Получен некорректтный идентификатор задачи = " + id);
                                        httpExchange.sendResponseHeaders(405, 0);
                                    }
                                } else {
                                    httpExchange.sendResponseHeaders(405, 0);
                                }
                                break;
                            }
                            default: {
                                System.out.println("Ждем GET, POST или DELETE запрос, а получили - " + requestMethod);
                                httpExchange.sendResponseHeaders(405, 0);
                            }
                        }
                    } catch(Exception exception){
                        exception.printStackTrace();
                    } finally{
                        httpExchange.close();
                    }
*/


           /*         public class epicsHandler(HttpExchange httpExchange) {
        try

                        {
                            String path = httpExchange.getRequestURI().getPath();
                            String requestMethod = httpExchange.getRequestMethod();

                            switch (requestMethod) {
                                case "GET": { //все задачи
                                    if (Pattern.matches("^/api/v1/epics/$", path)) {
                                        String response = gson.toJson(taskManager.getAllTasks());
                                        sendText(httpExchange, response);
                                        break;
                                    }
                                    //задача по айди
                                    if (Pattern.matches("^/api/v1/epics/\\d+$", path)) {
                                        String pathId = path.replaceFirst("api/v1/epics/", "");
                                        int id = parsePathId(pathId);
                                        if (id != -1) {
                                            String response = gson.toJson(taskManager.getTaskById(id));
                                            sendText(httpExchange, response);
                                            break;
                                        } else {
                                            System.out.println("Получен некорректный идентификатор эпика = " + id);
                                            httpExchange.sendResponseHeaders(405, 0);
                                            break;
                                        }

                                    } else {
                                        httpExchange.sendResponseHeaders(405, 0);
                                        break;
                                    }
*/

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


       /*                         case "POST": {
                                    String parameters = httpExchange.getRequestURI().getQuery();
                                    String response;
                                    int id = 0;
                                    if (parameters != null) {
                                        id = Integer.parseInt(parameters.split("=")[1]);
                                    }
                                    String body = readText(httpExchange);
                                    if (body.isBlank()) {
                                        httpExchange.sendResponseHeaders(404, 0);
                                        sendText(httpExchange, "Task задача отсутствует в теле запроса.");
                                    } else {
                                        Task task = gson.fromJson(body, Task.class);
                                        if (parameters == null) {
                                            Task resultTask = taskManager.createTask(task);
                                            if (resultTask == null) {
                                                httpExchange.sendResponseHeaders(400, 0);
                                                response = "Task задача не добавлена.";
                                            } else {
                                                httpExchange.sendResponseHeaders(201, 0);
                                                response = "Task задача добавлена.";
                                            }
                                        } else {
                                            task.setId(id);
                                            taskManager.updateTask(task);
                                            if (taskManager.getTaskById(id) == null) {
                                                httpExchange.sendResponseHeaders(400, 0);
                                                response = "Не удалось обновить Task задачу.";
                                            } else {
                                                httpExchange.sendResponseHeaders(201, 0);
                                                response = "Task задача " + id + " обновлена.";
                                            }
                                        }
                                    }
                                }
                                break;

                                case "DELETE": {
                                    if (Pattern.matches("^/api/v1/epics/\\d+$", path)) {
                                        String pathId = path.replaceFirst("api/v1/epics/", "");
                                        int id = parsePathId(pathId);
                                        if (id != -1) {
                                            taskManager.deleteByTaskId(id);
                                            System.out.println("Удален эпик с айди = " + id);
                                            httpExchange.sendResponseHeaders(200, 0);
                                        } else {
                                            System.out.println("Получен некорректтный идентификатор эпика = " + id);
                                            httpExchange.sendResponseHeaders(405, 0);
                                        }

                                    } else {
                                        httpExchange.sendResponseHeaders(405, 0);
                                    }
                                    break;
                                }
                                default: {
                                    System.out.println("Ждем GET, POST или DELETE запрос, а получили - " + requestMethod);
                                    httpExchange.sendResponseHeaders(405, 0);
                                }
                            }
                        } catch(
                        Exception exception)

                        {
                            exception.printStackTrace();
                        } finally

                        {
                            httpExchange.close();
                        }
                    }*/

      /*              public void subtasksHandler (HttpExchange httpExchange){
                        try {
                            String path = httpExchange.getRequestURI().getPath();
                            String requestMethod = httpExchange.getRequestMethod();

                            switch (requestMethod) {
                                case "GET": { //все подзадачи
                                    if (Pattern.matches("^/api/v1/epics/$", path)) {
                                        String response = gson.toJson(taskManager.getAllTasks());
                                        sendText(httpExchange, response);
                                        break;
                                    }
                                    //задача по айди
                                    if (Pattern.matches("^/api/v1/epics/\\d+$", path)) {
                                        String pathId = path.replaceFirst("api/v1/epics/", "");
                                        int id = parsePathId(pathId);
                                        if (id != -1) {
                                            String response = gson.toJson(taskManager.getTaskById(id));
                                            sendText(httpExchange, response);
                                            break;
                                        } else {
                                            System.out.println("Получен некорректный идентификатор эпика = " + id);
                                            httpExchange.sendResponseHeaders(405, 0);
                                            break;
                                        }

                                    } else {
                                        httpExchange.sendResponseHeaders(405, 0);
                                        break;
                                    }*/


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
                                    if (Pattern.matches("^/api/v1/epics/\\d+$", path)) {
                                        String pathId = path.replaceFirst("api/v1/epics/", "");
                                        int id = parsePathId(pathId);
                                        if (id != -1) {
                                            taskManager.deleteByTaskId(id);
                                            System.out.println("Удален эпик с айди = " + id);
                                            httpExchange.sendResponseHeaders(200, 0);
                                        } else {
                                            System.out.println("Получен некорректтный идентификатор эпика = " + id);
                                            httpExchange.sendResponseHeaders(405, 0);
                                        }

                                    } else {
                                        httpExchange.sendResponseHeaders(405, 0);
                                    }
                                    break;


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


                }
            }
        } finally {
            httpExchange.close();
        }
    }

/*    public class PrioritizedHandler implements HttpHandler {
        TaskManager taskManager = new InMemoryTaskManager(h)
        @Override
        public void handle(HttpExchange h) throws IOException {
            System.out.println("Началась обработка /tasks/prioritized/ запроса от клиента.");
            if ("GET".equals(h.getRequestMethod())) {
                Set<Task> tasks = httpTaskManager.getPrioritizedTasks();
                String s = gson.toJson(tasks);
                sendText(h, s);
                return;
            }
            h.close();
        }
    }*/


                public void stop () {
                    server.stop(0);
                    System.out.println("Остановили сервер на порту " + PORT);
                }
            }

/*    private String readText(HttpExchange httpExchange) throws IOException {
        return new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    private void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(200,resp.length);
        h.getResponseBody().write(resp);
    }
}*/

/*
            private void handlePOST(HttpExchange exchange, String path) throws IOException {
                if (Pattern.matches(TASKS_PATH, path)) {
                    handleAddTask(exchange);
                } else if (Pattern.matches(TASKS_ID_PATH, path)) {
                    handleUpdateTask(exchange, path);
                } else {
                    System.out.println("Wrong path - 400");
                    sendBadRequest400(exchange);
                }
            }

            private void handleAddTask(HttpExchange exchange) throws IOException {
                final String requestBody = readText(exchange);
                final JsonObject jsonBody = JsonParser.parseString(requestBody).getAsJsonObject();
                if (!isValidJsonTask(jsonBody)) {
                    sendNotAllowed405(exchange);
                    System.out.println("Wrong set of fields in req body -405");
                    return;
                }

                Task task = gson.fromJson(requestBody, Task.class);
                try {
                    String response = gson.toJson(taskManager.addTask(task));
                    sendCreated201(exchange);
                    System.out.println("task was added to the TM");
                } catch (Exception e) {
                    sendHasInteractions406(exchange);
                    System.out.println("Task has time conflict with existing task - 406");
                }
            }

            private void handleUpdateTask(HttpExchange exchange, String path) throws IOException {
                final String pathId = path.replaceFirst("/tasks/", "");
                final int id = parsePathID(pathId);
                if (id <= 0) {
                    sendNotAllowed405(exchange);
                    System.out.println("Wrong Id format - 405");
                    return;
                }

                final String requestBody = readText(exchange);
                final JsonObject jsonBody = JsonParser.parseString(requestBody).getAsJsonObject();
                if (!isValidJsonTask(jsonBody)) {
                    sendNotAllowed405(exchange);
                    System.out.println("Wrong set of fields in req body -405");
                    return;
                }
                Task task = gson.fromJson(requestBody, Task.class);
                if (task.getId() != id) {
                    sendBadRequest400(exchange);
                    System.out.println("Id in the path and body requests are different - 400");
                    return;
                }
                try {
                    taskManager.updateTask(task);
                    sendCreated201(exchange);
                    System.out.println("Task updated - 201, id = " + id);
                } catch (TaskNotFoundException e) {
                    sendNotFound404(exchange);
                    System.out.println("Not found task with id - 404");
                } catch (TaskPrioritizationException e) {
                    sendHasInteractions406(exchange);
                    System.out.println("Task has time conflict with existing task - 406");
                }
            } */