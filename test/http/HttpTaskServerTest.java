package http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import enums.Status;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.managers.Managers;
import service.managers.TaskManager;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static enums.TaskType.TASK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskServerTest {

    private HttpTaskServer taskServer;
    private Gson gson = Managers.getGson();
    //private Gson gson = HttpTaskServer.GSON;
    private TaskManager taskManager;

    private Task task;
    private Epic epic;
    private Subtask subtask;

    @BeforeEach
    void init() throws IOException {
        taskManager = Managers.getDefault();
        taskServer = new HttpTaskServer(taskManager);
        task = new Task(1, TASK,"TaskName", Status.IN_PROGRESS,"TaskDescription",
                LocalDateTime.of(2024, Month.JUNE, 15, 10,10), Duration.ofMinutes(40),
                LocalDateTime.of(2024, Month.JUNE, 15, 10,10).plusMinutes(40));
        taskManager.createTask(task);
        epic = new Epic("EpicForSubtask", "DE");
        taskManager.createEpic(epic);
        subtask = new Subtask("S", "SD", epic.getId());
        taskManager.createSubtask(subtask);
        taskServer.start();
    }

    @AfterEach
    void tearDown() throws IOException {
        taskManager.deleteAllTasks();
        taskServer.stop();
    }


    @Test
    void getAllTasksTest() throws IOException, InterruptedException {
    HttpClient client = HttpClient.newHttpClient();
    URI uri = URI.create("http://localhost:8080/api/v1/tasks");
    HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    assertEquals(200, response.statusCode());

    Type taskType = new TypeToken<ArrayList<Task>>() {}.getType();

    List<Task> actual = gson.fromJson(response.body(), taskType);
    assertNotNull(actual, "Задачи не возвращаются");
    assertEquals(1, actual.size(), "Неверное количество задач");
    assertEquals(task, actual.get(0), "Задачи не совпадают");
       /* List<Task> actual = gson.fromJson(response.body(), taskType);
        assertNotNull(actual, "Задачи не возвращаются");
        assertEquals(1, actual.size(), "Неверное количество задач");
        assertEquals(task, actual.get(0), "Задачи не совпадают");
*/

    }


    @Test
    void getTaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<Task>() {}.getType();

        Task actual = gson.fromJson(response.body(), taskType);
        assertNotNull(actual, "Задачи не возвращаются");
        assertEquals(task, actual, "Задачи не совпадают");

    }



    @Test
     public void testAddTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task(5, TASK,"Test 2", Status.NEW, "Testing task 2",
                LocalDateTime.now(), Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(5));
        // конвертируем её в JSON
        String taskJson = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/v1/tasks");
       // URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = taskManager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.get(0).getTaskName(), "Некорректное имя задачи");
    }

    @Test
    public void taskHandlerTestGet() throws IOException, InterruptedException {
        Task task1 = new Task(1, TASK,"Задача",
                Status.NEW, "Описание", LocalDateTime.of(2024,11,10,10,10),
                Duration.ofMinutes(15), LocalDateTime.of(2024,11,10,10,10)
                .plusMinutes(15));
        taskManager.createTask(task1);
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        assertEquals(200, response.statusCode());
        assertEquals("["+gson.toJson(task1)+"]", response.body());
    }




}




/*




    @Test
    void getTaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<Task>() {}.getType();

        Task actual = gson.fromJson(response.body(), taskType);
        assertNotNull(actual, "Задачи не возвращаются");
        assertEquals(task, actual, "Задачи не совпадают");

    }


    @Test
    public void testAddTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task(5, TaskType.TASK, "Test 2", Status.NEW, "Testing task 2",
                LocalDateTime.now(), Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(5));
        // конвертируем её в JSON
        String taskJson = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/v1/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = taskManager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.get(0).getTaskName(), "Некорректное имя задачи");
    }
}

 */