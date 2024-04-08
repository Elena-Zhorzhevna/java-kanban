package service.InMemory;

import enums.Status;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import service.managers.HistoryManager;
import service.managers.TaskManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InMemoryHistoryManagerTest {
    HistoryManager historyManager = new InMemoryHistoryManager();
    TaskManager taskManager = new InMemoryTaskManager();

    @Test
        // проверяем, что задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных
    void tasksAddedToHistoryManagerRetainThePreviousVersionOfTaskTest() {
        Task task1 = new Task("Первая задача", "Описание первой задачи", Status.NEW);
        taskManager.createTask(task1);
        historyManager.add(task1);
        assertNotNull(historyManager.getHistory(), "История не пустая.");
        assertEquals(1, historyManager.getHistory().size(), "История не пустая.");
        Task task2 = new Task("Задача2", "Описание2", Status.NEW);
        taskManager.createTask(task2);
        historyManager.add(task2);
        taskManager.getTaskById(task2.getId());
        assertEquals(2, historyManager.getHistory().size());
        assertEquals(taskManager.getTaskById(task1.getId()).getTaskName(), task1.getTaskName());
        assertEquals(taskManager.getTaskById(task1.getId()).getDescription(), task1.getDescription());
        assertEquals(taskManager.getTaskById(task1.getId()).getStatus(), task1.getStatus());
    }

    @Test
        //проверка добавления задачи в историю
    void addTaskInHistoryTest() {
        Task task = new Task("Первая задача", "Описание первой задачи");
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
        //проверка добавления эпика в историю
    void addEpicInHistoryTest() {
        Epic epic = new Epic("Первый эпик", "Описание первого эпика");
        historyManager.add(epic);
        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
        //проверка добавления подзадачи в историю
    void addSubtaskInHistoryTest() {
        Subtask subtask = new Subtask("Первая подзадача", "Описание первой подзадачи");
        historyManager.add(subtask);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @AfterEach
    void afterEach() {
        historyManager.getHistory().clear();
    }
}


