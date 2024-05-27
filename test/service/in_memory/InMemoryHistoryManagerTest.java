package service.in_memory;

import enums.Status;
import enums.TaskType;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.managers.HistoryManager;
import service.managers.TaskManager;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;
    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager(historyManager);
    }

    @Test
        // проверяем, что задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных
    void tasksAddedToHistoryManagerRetainThePreviousVersionOfTaskTest() throws IOException {
        Task task1 = new Task("Первая задача", "Описание первой задачи", Status.NEW);
        taskManager.createTask(task1);
        taskManager.getTaskById(task1.getId());
        assertNotNull(historyManager.getHistory(), "История не пустая.");
        assertEquals(1, historyManager.getHistory().size(), "История не пустая.");
        Task task2 = new Task("Задача2", "Описание2", Status.NEW);
        taskManager.createTask(task2);
        taskManager.getTaskById(task2.getId());
        assertEquals(2, historyManager.getHistory().size());
        assertEquals(taskManager.getTaskById(task1.getId()).getTaskName(), task1.getTaskName());
        assertEquals(taskManager.getTaskById(task1.getId()).getDescription(), task1.getDescription());
        assertEquals(taskManager.getTaskById(task1.getId()).getStatus(), task1.getStatus());
    }

    @Test
        //проверка добавления задачи в историю
    void addTaskInHistoryTest() throws IOException {
        Task task = new Task("Первая задача", "Описание первой задачи");
        taskManager.createTask(task);
        taskManager.getTaskById(task.getId());
        assertNotNull(historyManager.getHistory(), "История не пустая.");
        assertEquals(1, historyManager.getHistory().size(), "История не пустая.");
    }

    @Test
        //проверка добавления эпика в историю
    void addEpicInHistoryTest() throws IOException {
        Epic epic = new Epic("Первый эпик", "Описание первого эпика");
        taskManager.createEpic(epic);
        taskManager.getEpicById(epic.getId());
        assertNotNull(historyManager.getHistory(), "История не пустая.");
        assertEquals(1, historyManager.getHistory().size(), "История не пустая.");
    }

    @Test
        //проверка добавления подзадачи в историю
    void addSubtaskInHistoryTest() throws IOException {
        Epic epic = new Epic("epic", "de");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask(1, TaskType.SUBTASK, "Первая подзадача", Status.IN_PROGRESS,
                "Описание первой подзадачи", LocalDateTime.now(), Duration.ofMinutes(15),
                LocalDateTime.now().plusMinutes(15), epic.getId());
        taskManager.createSubtask(subtask);
        assertNotNull(historyManager.getHistory(), "История не пустая.");
        assertEquals(1, historyManager.getHistory().size(), "История не пустая.");
    }

    @Test
        //проверка удаления задачи из просмотра
    void removeTaskTest() throws IOException {
        Task task1 = new Task(20, TaskType.TASK, "Задача1", Status.NEW, "Описание1",
                LocalDateTime.of(2024, Month.MAY, 20, 20, 20), Duration.ofMinutes(120),
                LocalDateTime.of(2024, Month.MAY, 20, 20, 20).plusMinutes(120));
        Task task2 = new Task(21, TaskType.TASK, "Задача2", Status.NEW, "Описание2",
                LocalDateTime.of(2024, Month.MAY, 10, 21, 21), Duration.ofMinutes(120),
                LocalDateTime.of(2024, Month.MAY, 10, 21, 21).plusMinutes(120));
        Task task3 = new Task(22, TaskType.TASK, "Задача3", Status.NEW, "Описание3",
                LocalDateTime.of(2024, Month.MAY, 15, 15, 15), Duration.ofMinutes(120),
                LocalDateTime.of(2024, Month.MAY, 15, 15, 15).plusMinutes(120));
        Task task4 = new Task(23, TaskType.TASK, "Задача4", Status.NEW, "Описание4",
                LocalDateTime.of(2024, Month.MAY, 18, 18, 18), Duration.ofMinutes(120),
                LocalDateTime.of(2024, Month.MAY, 18, 18, 18).plusMinutes(120));

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);
        taskManager.createTask(task4);
        //дублирование
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task3.getId());
        taskManager.getTaskById(task4.getId());
        taskManager.getTaskById(task3.getId());
        taskManager.getTaskById(task4.getId());
        assertEquals(4, historyManager.getHistory().size());
        //удаление из истории: начало, середина, конец.
        historyManager.remove(task1.getId());
        historyManager.remove(task4.getId());
        historyManager.remove(task3.getId());
        assertEquals(1, historyManager.getHistory().size());
        historyManager.remove(task2.getId());
        //пустая история задач
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @AfterEach
    void afterEach() throws IOException {
        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();
        taskManager.deleteAllSubtasks();
        historyManager.getHistory().clear();
        taskManager.getHistory().clear();
    }
}
