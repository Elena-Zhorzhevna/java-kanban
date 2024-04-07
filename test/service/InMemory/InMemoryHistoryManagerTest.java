package service.InMemory;

import enums.Status;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InMemoryHistoryManagerTest {
    InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
    InMemoryTaskManager memoryTaskManager = new InMemoryTaskManager();

    @Test // убедитесь, что задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных.
    public void addHistoryTest() {
        Task task1 = new Task("Задача1", "Описание1", Status.NEW);
        memoryTaskManager.createTask(task1);
        Task task2 = new Task("Задача", "Описание", Status.NEW);
        memoryTaskManager.createTask(task2);
        memoryTaskManager.getTaskById(task1.getId());

        assertEquals(1, historyManager.getHistory().size());
        assertEquals(memoryTaskManager.getTaskById(task1.getId()).getTaskName(), task1.getTaskName());
        assertEquals(memoryTaskManager.getTaskById(task1.getId()).getDescription(), task1.getDescription());
        assertEquals(memoryTaskManager.getTaskById(task1.getId()).getStatus(), task1.getStatus());
    }

    @Test
    void addTaskInHistoryTest() {
        Task task = new Task("Первая задача", "Описание первой задачи");
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    void addEpicInHistoryTest() {
        Epic epic = new Epic("Первый эпик", "Описание первого эпика");
        historyManager.add(epic);
        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
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


