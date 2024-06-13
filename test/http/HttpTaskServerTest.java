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
import java.util.Set;

import static enums.TaskType.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskServerTest {
    private HttpTaskServer taskServer;
    private Gson gson;
    private TaskManager taskManager;
    private Task testTask;
    private Epic testEpic;
    private Subtask testSubtask;

    @BeforeEach
    void setUp() throws IOException {
        taskManager = Managers.getDefault();
        taskServer = new HttpTaskServer(taskManager);
        gson = taskServer.getGson();
        taskManager.deleteAllTasks();
        taskManager.deleteAllSubtasks();
        taskManager.deleteAllEpics();
        taskServer.start();
    }

    @AfterEach
    void tearDown() throws IOException {
        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();
        taskManager.deleteAllSubtasks();
        taskServer.stop();
    }

    @Test
        //проверка GET запроса для всех задач TASK
    void getAllTasksTest() throws IOException, InterruptedException {
        testTask = new Task(1, TASK, "TaskName", Status.IN_PROGRESS, "TaskDescription",
                LocalDateTime.of(2024, Month.JUNE, 15, 10, 10), Duration.ofMinutes(40),
                LocalDateTime.of(2024, Month.JUNE, 15, 10, 10).plusMinutes(40));
        taskManager.createTask(testTask);
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Type taskType = new TypeToken<ArrayList<Task>>() {
        }.getType();
        List<Task> actual = gson.fromJson(response.body(), taskType);
        assertNotNull(actual, "Задачи не возвращаются");
        assertEquals(1, actual.size(), "Неверное количество задач");
        assertEquals(testTask, actual.get(0), "Задачи не совпадают");
        assertEquals(1, actual.size(), "Неверное количество задач");
    }


    @Test
        //проверка GET запроса для TASK задачи по айди
    void getTaskByIdTest() throws IOException, InterruptedException {
        Task testTask2 = new Task(1, TASK, "TaskName2", Status.IN_PROGRESS, "TaskDescription2",
                LocalDateTime.of(2024, Month.MAY, 25, 10, 10), Duration.ofMinutes(30),
                LocalDateTime.of(2024, Month.MAY, 25, 10, 10).plusMinutes(30));
        taskManager.createTask(testTask2);
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Type taskType = new TypeToken<Task>() {
        }.getType();
        Task actual = gson.fromJson(response.body(), taskType);
        assertNotNull(actual, "Задачи не возвращаются");
        assertEquals(testTask2, actual, "Задачи не совпадают");
    }

    @Test
        //проверка GET запроса для EPIC задачи по айди
    void getEpicById() throws IOException, InterruptedException {
        Epic testEpic = new Epic(1, EPIC, "EpicServerGetTest", Status.NEW, "EpicDescription",
                LocalDateTime.of(2024, Month.MAY, 25, 10, 10), Duration.ofMinutes(30),
                LocalDateTime.of(2024, Month.MAY, 25, 10, 10).plusMinutes(30));
        taskManager.createEpic(testEpic);
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Type epicType = new TypeToken<Epic>() {
        }.getType();
        Task actual = gson.fromJson(response.body(), epicType);
        assertNotNull(actual, "Задачи не возвращаются");
        assertEquals(testEpic, actual, "Задачи не совпадают");
    }

    @Test
        //проверка GET запроса для SUBTASK задачи по айди
    void getSubtaskById() throws IOException, InterruptedException {
        testEpic = new Epic("ЭпикДляПодзадачиСервер", "Описание эпика для подзадачи");
        taskManager.createEpic(testEpic);
        Subtask testSubtask = new Subtask(2, SUBTASK, "SubtaskServerGetTest", Status.NEW,
                "SubDescription", LocalDateTime.of(2024, Month.MAY, 25, 10, 10),
                Duration.ofMinutes(30), LocalDateTime.of(2024, Month.MAY, 25, 10, 10)
                .plusMinutes(30), testEpic.getId());
        taskManager.createSubtask(testSubtask);
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Type subtaskType = new TypeToken<Subtask>() {
        }.getType();
        Task actual = gson.fromJson(response.body(), subtaskType);
        assertNotNull(actual, "Задачи не возвращаются");
        assertEquals(testSubtask, actual, "Задачи не совпадают");
    }

    @Test //проверка добавления задачи (POST без id)
    public void addTaskTest() throws IOException, InterruptedException {
        Task serverTestTask = new Task(5, TASK, "ServerTestTask", Status.NEW, "DServerTT",
                LocalDateTime.now(), Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(5));
        String taskJson = gson.toJson(serverTestTask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/v1/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        List<Task> tasksFromManager = taskManager.getAllTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("ServerTestTask", tasksFromManager.get(0).getTaskName(),
                "Некорректное имя задачи");
    }

    @Test //проверка добавления эпика (POST без id)
    public void addEpicTest() throws IOException, InterruptedException {
        Epic serverTestEpic = new Epic(8, EPIC, "ServerTestEpic", Status.NEW, "DServerTE",
                LocalDateTime.now(), Duration.ofMinutes(50), LocalDateTime.now().plusMinutes(50));
        String taskJson = gson.toJson(serverTestEpic);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/v1/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        List<Epic> epicsFromManager = taskManager.getAllEpics();
        assertNotNull(epicsFromManager, "Задачи не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество задач");
        assertEquals("ServerTestEpic", epicsFromManager.get(0).getTaskName(),
                "Некорректное имя задачи");
    }

    @Test //проверка добавления подзадачи (POST без id)
    public void addSubtaskTest() throws IOException, InterruptedException {
        testEpic = new Epic("ЭпикДляПодзадачиСервер", "Описание эпика для подзадачи");
        taskManager.createEpic(testEpic);
        Subtask serverTestSubtask = new Subtask(10, SUBTASK, "ServerTestSubtask", Status.NEW, "DServerTS",
                LocalDateTime.now(), Duration.ofMinutes(12), LocalDateTime.now().plusMinutes(12), testEpic.getId());
        String taskJson = gson.toJson(serverTestSubtask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/v1/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        List<Subtask> subtasksFromManager = taskManager.getAllSubtasks();
        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("ServerTestSubtask", subtasksFromManager.get(0).getTaskName(),
                "Некорректное имя подзадачи");
    }

    @Test
        //проверка GET запроса для всех задач EPIC
    void getAllEpicsTest() throws IOException, InterruptedException {
        testEpic = new Epic(1, EPIC, "Эпик", Status.NEW, "ОписаниеЭпика",
                LocalDateTime.of(2024, Month.MAY, 18, 10, 10), Duration.ofMinutes(15),
                LocalDateTime.of(2024, Month.MAY, 18, 10, 10).plusMinutes(15));
        taskManager.createEpic(testEpic);
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Type taskType = new TypeToken<ArrayList<Epic>>() {
        }.getType();
        List<Epic> actual = gson.fromJson(response.body(), taskType);
        assertNotNull(actual, "Эпики не возвращаются");
        assertEquals(1, actual.size(), "Неверное количество эпиков");
        assertEquals(testEpic, actual.get(0), "Эпики не совпадают");
        assertEquals(1, actual.size(), "Неверное количество эпиков");
    }

    @Test //проверка обновления задачи EPIC (POST запрос, когда указан айди)
    public void updateEpicTest() throws IOException, InterruptedException {
        Epic epic = new Epic(1, EPIC, "UpdateServerTestEpic", Status.IN_PROGRESS, "EpicD",
                LocalDateTime.of(2024, Month.MAY, 25, 10, 10), Duration.ofMinutes(30),
                LocalDateTime.of(2024, Month.MAY, 25, 10, 10).plusMinutes(30));
        taskManager.createEpic(epic);
        testEpic = new Epic(1, EPIC, "updateTestEpic", Status.NEW, "descrUTEpic",
                LocalDateTime.of(2024, Month.MAY, 18, 10, 10), Duration.ofMinutes(15),
                LocalDateTime.of(2024, Month.MAY, 18, 10, 10).plusMinutes(15));
        String epicJson = gson.toJson(testEpic);
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        List<Epic> tasksFromManager = taskManager.getAllEpics();
        assertEquals("updateTestEpic", tasksFromManager.get(0).getTaskName(),
                "Некорректное имя задачи");
    }

    @Test //проверка обновления задачи TASK (POST запрос, когда указан айди)
    public void updateTaskTest() throws IOException, InterruptedException {
        Task task = new Task(1, TASK, "TaskName2", Status.IN_PROGRESS, "TaskDescription2",
                LocalDateTime.of(2024, Month.MAY, 25, 10, 10), Duration.ofMinutes(30),
                LocalDateTime.of(2024, Month.MAY, 25, 10, 10).plusMinutes(30));
        taskManager.createTask(task);
        testTask = new Task(1, TASK, "updateTestTask", Status.NEW, "descrUTTask",
                LocalDateTime.of(2024, Month.MAY, 18, 10, 10), Duration.ofMinutes(15),
                LocalDateTime.of(2024, Month.MAY, 18, 10, 10).plusMinutes(15));
        String taskJson = gson.toJson(testTask);
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        List<Task> tasksFromManager = taskManager.getAllTasks();
        assertEquals("updateTestTask", tasksFromManager.get(0).getTaskName(),
                "Некорректное имя задачи");
    }

    @Test //проверка обновления задачи SUBTASK (POST запрос, когда указан айди)
    public void updateSubtaskTest() throws IOException, InterruptedException {
        testEpic = new Epic("ЭпикДляПодзадачиСервер", "Описание эпика для подзадачи");
        taskManager.createEpic(testEpic);
        Subtask serverTestSubtask = new Subtask(2, SUBTASK, "updateTestSubtask", Status.NEW, "DServerTS",
                LocalDateTime.now(), Duration.ofMinutes(12), LocalDateTime.now().plusMinutes(12), testEpic.getId());
        taskManager.createSubtask(serverTestSubtask);
        testSubtask = new Subtask(2, SUBTASK, "updateTestSubtask", Status.NEW, "descrUTSubtask",
                LocalDateTime.of(2024, Month.MAY, 18, 10, 10), Duration.ofMinutes(15),
                LocalDateTime.of(2024, Month.MAY, 18, 10, 10).plusMinutes(15),
                testEpic.getId());
        String subtaskJson = gson.toJson(testSubtask);
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        List<Subtask> tasksFromManager = taskManager.getAllSubtasks();
        assertEquals("updateTestSubtask", tasksFromManager.get(0).getTaskName(),
                "Некорректное имя подзадачи");
    }

    @Test
        //проверка GET запроса для всех задач TASK
    void getAllSubtasksTest() throws IOException, InterruptedException {
        testEpic = new Epic("Эпик для подзадачи", "ОписаниеЭпикаДляПодзадачи");
        taskManager.createEpic(testEpic);
        testSubtask = new Subtask(1, SUBTASK, "SubtaskName", Status.NEW, "SubtaskDescription",
                LocalDateTime.of(2024, Month.JUNE, 13, 10, 10), Duration.ofMinutes(15),
                LocalDateTime.of(2024, Month.JUNE, 13, 10, 10).plusMinutes(15),
                testEpic.getId());
        taskManager.createSubtask(testSubtask);
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Type taskType = new TypeToken<ArrayList<Subtask>>() {
        }.getType();
        List<Subtask> actual = gson.fromJson(response.body(), taskType);
        assertNotNull(actual, "Задачи не возвращаются");
        assertEquals(1, actual.size(), "Неверное количество задач");
        assertEquals(testSubtask, actual.get(0), "Задачи не совпадают");
        assertEquals(1, actual.size(), "Неверное количество задач");
    }

    @Test //проверка GET запроса для приоритетного списка
    public void prioritizedHandlerTest() throws IOException, InterruptedException {
        testTask = new Task(1, TASK, "TaskName2", Status.IN_PROGRESS, "TaskDescription2",
                LocalDateTime.of(2024, Month.MAY, 25, 10, 10), Duration.ofMinutes(30),
                LocalDateTime.of(2024, Month.MAY, 25, 10, 10).plusMinutes(30));
        taskManager.createTask(testTask);
        testEpic = new Epic("Эпик", "Описание");
        taskManager.createEpic(testEpic);
        testSubtask = new Subtask(3, SUBTASK, "SubtaskName", Status.NEW, "SubtaskDescription",
                LocalDateTime.of(2024, Month.JUNE, 13, 10, 10), Duration.ofMinutes(15),
                LocalDateTime.of(2024, Month.JUNE, 13, 10, 10).plusMinutes(15),
                testEpic.getId());
        taskManager.createSubtask(testSubtask);
        Subtask testSubtask2 = new Subtask(7, SUBTASK, "Подзадача1", Status.NEW, "ОписаниеП1",
                LocalDateTime.of(2024, Month.MAY, 5, 10, 10), Duration.ofMinutes(30),
                LocalDateTime.of(2024, Month.MAY, 5, 10, 10).plusMinutes(30),
                testEpic.getId());
        taskManager.createSubtask(testSubtask2);
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(gson.toJson(taskManager.getPrioritizedTasks()), response.body());
    }

    @Test //проверка запроса GET для списка истории
    public void historyHandlerTest() throws IOException, InterruptedException {
        testTask = new Task(1, TASK, "TaskName2", Status.IN_PROGRESS, "TaskDescription2",
                LocalDateTime.of(2024, Month.MAY, 25, 10, 10), Duration.ofMinutes(30),
                LocalDateTime.of(2024, Month.MAY, 25, 10, 10).plusMinutes(30));
        taskManager.createTask(testTask);
        testEpic = new Epic("Эпик", "Описание");
        taskManager.createEpic(testEpic);
        testSubtask = new Subtask(3, SUBTASK, "SubtaskName", Status.NEW, "SubtaskDescription",
                LocalDateTime.of(2024, Month.JUNE, 13, 10, 10), Duration.ofMinutes(15),
                LocalDateTime.of(2024, Month.JUNE, 13, 10, 10).plusMinutes(15),
                testEpic.getId());
        taskManager.createSubtask(testSubtask);
        Subtask testSubtask2 = new Subtask(7, SUBTASK, "Подзадача1", Status.NEW, "ОписаниеП1",
                LocalDateTime.of(2024, Month.MAY, 5, 10, 10), Duration.ofMinutes(30),
                LocalDateTime.of(2024, Month.MAY, 5, 10, 10).plusMinutes(30),
                testEpic.getId());
        taskManager.createSubtask(testSubtask2);
        taskManager.getTaskById(testTask.getId());
        taskManager.getSubtaskById(testSubtask.getId());
        URI uri = URI.create("http://localhost:8080/api/v1/history");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(gson.toJson(taskManager.getHistory()), response.body());
    }

    @Test //проверка DELETE запроса задач TASK по айди
    public void deleteTaskByIdTest() throws IOException, InterruptedException {
        testTask = new Task("TestServerTask", "DescriptionTSTask");
        taskManager.createTask(testTask);
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        taskManager.deleteByTaskId(testTask.getId());
        assertEquals(200, response.statusCode());
        assertEquals(0, taskManager.getAllTasks().size());
    }

    @Test //проверка DELETE запроса для всех задач TASK
    public void deleteAllTasksTest() throws IOException, InterruptedException {
        testTask = new Epic("TestServerTask", "DescriptionTSTask");
        taskManager.createTask(testTask);
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        taskManager.deleteAllTasks();
        assertEquals(200, response.statusCode());
        assertEquals(0, taskManager.getAllTasks().size());
    }


    @Test //проверка DELETE запроса задач EPIC по айди
    public void deleteEpicByIdTest() throws IOException, InterruptedException {
        testEpic = new Epic("TestServerEpic", "DescriptionTSEpic");
        taskManager.createEpic(testEpic);
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        taskManager.deleteEpicById(testEpic.getId());
        assertEquals(200, response.statusCode());
        assertEquals(0, taskManager.getAllEpics().size());
    }

    @Test //проверка DELETE запроса для всех задач TASK
    public void deleteAllEpicsTest() throws IOException, InterruptedException {
        testEpic = new Epic("TestServerEpic", "DescriptionTSEpic");
        taskManager.createEpic(testEpic);
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        taskManager.deleteAllEpics();
        assertEquals(200, response.statusCode());
        assertEquals(0, taskManager.getAllEpics().size());
    }

    @Test //проверка DELETE запроса подзадач по айди
    public void deleteSubtasksByIdTest() throws IOException, InterruptedException {
        testEpic = new Epic("TestServerEpic", "DescriptionTSEpic");
        taskManager.createEpic(testEpic);
        testSubtask = new Subtask("TestServerSubtask", "DescriptionTSSubtask", testEpic.getId());
        taskManager.createSubtask(testSubtask);
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        taskManager.deleteSubtaskById(testSubtask.getId());
        assertEquals(200, response.statusCode());
        assertEquals(0, taskManager.getAllSubtasks().size());
    }

    @Test //проверка DELETE запроса для всех подзадач
    public void deleteAllSubtasksTest() throws IOException, InterruptedException {
        testEpic = new Epic("TestServerEpic", "DescriptionTSEpic");
        taskManager.createEpic(testEpic);
        testSubtask = new Subtask("TestServerSubtask", "DescriptionTSSubtask", testEpic.getId());
        taskManager.createSubtask(testSubtask);
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        taskManager.deleteAllSubtasks();
        assertEquals(200, response.statusCode());
        assertEquals(0, taskManager.getAllSubtasks().size());
    }

    @Test //проверка GET запроса всех сабтасков эпика
    public void getAllEpicSubtasksByEpicId() throws IOException, InterruptedException {
        testEpic = new Epic(1, EPIC, "EpicName", Status.IN_PROGRESS, "EpicDescription",
                LocalDateTime.of(2024, Month.JUNE, 15, 10, 10), Duration.ofMinutes(40),
                LocalDateTime.of(2024, Month.JUNE, 15, 10, 10).plusMinutes(40));
        taskManager.createEpic(testEpic);
        testSubtask = new Subtask(3, SUBTASK, "SubtaskName", Status.NEW, "SubtaskDescription",
                LocalDateTime.of(2024, Month.JUNE, 13, 10, 10), Duration.ofMinutes(15),
                LocalDateTime.of(2024, Month.JUNE, 13, 10, 10).plusMinutes(15),
                testEpic.getId());
        taskManager.createSubtask(testSubtask);
        Subtask testSubtask2 = new Subtask(7, SUBTASK, "Подзадача1", Status.NEW, "ОписаниеП1",
                LocalDateTime.of(2024, Month.MAY, 5, 10, 10), Duration.ofMinutes(30),
                LocalDateTime.of(2024, Month.MAY, 5, 10, 10).plusMinutes(30),
                testEpic.getId());
        taskManager.createSubtask(testSubtask2);
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/epics/1/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Type subtaskType = new TypeToken<ArrayList<Subtask>>() {
        }.getType();
        List<Subtask> actual = gson.fromJson(response.body(), subtaskType);
        assertNotNull(actual, "Задачи не возвращаются");
        assertEquals(2, actual.size(), "Неверное количество задач");
    }

    @Test //проверка POST запроса пересекающихся по времени задач
    public void postTaskAndEpicWithIntersectionTest() throws IOException, InterruptedException {
        testTask = new Task(1, TASK, "TestServerTaskName", Status.NEW, "TaskDescription",
                LocalDateTime.of(2024, Month.JUNE, 10, 10, 10), Duration.ofMinutes(5000),
                LocalDateTime.of(2024, Month.JUNE, 10, 10, 10).plusMinutes(5000));
        taskManager.createTask(testTask);
        testEpic = new Epic(2, EPIC, "TestServerEpicName", Status.IN_PROGRESS, "EpicDescription",
                LocalDateTime.of(2024, Month.JUNE, 11, 11, 11), Duration.ofMinutes(40),
                LocalDateTime.of(2024, Month.JUNE, 11, 11, 11).plusMinutes(40));
        String taskJson = gson.toJson(testEpic);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/v1/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
        Set<Task> tasksFromManager = taskManager.getPrioritizedTasks();
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
    }
}