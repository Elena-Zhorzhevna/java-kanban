package http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.managers.Managers;
import service.managers.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {

    TaskManager taskManager = Managers.getDefault();
    Gson gson = Managers.getGson();

    private String readText(HttpExchange httpExchange) throws IOException {
        return new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String path = httpExchange.getRequestURI().getPath();
            String requestMethod = httpExchange.getRequestMethod();

/*            switch (requestMethod) {
                case "GET": { //все подзадачи
                    if (Pattern.matches("^/api/v1/epics/$", path)) {
                        String response = gson.toJson(taskManager.getAllTasks());
                        sendText200(httpExchange, response, 200);
                        break;
                    }
                    //задача по айди
                    if (Pattern.matches("^/api/v1/epics/\\d+$", path)) {
                        String pathId = path.replaceFirst("api/v1/epics/", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {
                            String response = gson.toJson(taskManager.getTaskById(id));
                            sendText200(httpExchange, response, 200);
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


                       //Этот метод надо доработать
                        if (Pattern.matches("^/api/v1/epics/\\d+/subtasks$", path)) {
                        String pathId = path.replaceFirst("/api/v1/epics/", "")
                                .replaceFirst("/subtasks", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {
                            String response = gson.toJson(taskManager.getAllEpicSubtasks(id));
                            sendText200(httpExchange, response, 200);
                            break;
                        } else {
                            System.out.println("Получен некорректтный идентификатор задачи = " + id);
                            httpExchange.sendResponseHeaders(405, 0);
                            break;
                        }
                    }
                        break;
                }
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
    private int parsePathId(String path) {
        try {
            return Integer.parseInt(path);
        } catch (NumberFormatException exception) {
            return -1;
        }
    }
}*/
        } finally {
            httpExchange.close();
        }
    }
}

