package model;

import enums.Status;
import enums.TaskType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.in_memory.InMemoryHistoryManager;
import service.in_memory.InMemoryTaskManager;
import service.managers.HistoryManager;
import service.managers.TaskManager;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SubtaskTest {
    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager(historyManager);
    }

    @AfterEach
    void deleteTasksAfterTest() throws IOException {
        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();
        taskManager.deleteAllSubtasks();
    }

    @Test
        //проверка добавления новой подзадачи
    void addNewSubtaskTest() throws IOException {
        Epic epic = new Epic("Test epicForSubtask", "Test epicDescription");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask(20, TaskType.SUBTASK, "Test addNewSubtask", Status.IN_PROGRESS,
                "Test addNewSubtask description", LocalDateTime.now(), Duration.ofMinutes(10),
                LocalDateTime.now().plusMinutes(10), epic.getId());
        taskManager.createSubtask(subtask);
        int subtaskId = subtask.getId();
        Subtask savedSubtask = taskManager.getSubtaskById(subtaskId);
        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают.");
        List<Subtask> subtasks = taskManager.getAllSubtasks();
        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasks.get(0), "Подзадачи не совпадают.");
    }

    @Test
        //проверка соответствия подзадачи своей копии
    void subtaskShouldEqualsWithCopyTest() {
        Subtask subtask = new Subtask("Subtask1", "description1");
        Subtask subtaskExpected = subtask;
        assertEquals(subtaskExpected, subtask, "Задачи должны совпадать");
    }

    @Test
    void subtaskShouldHaveEpicTest() throws IOException {
        Epic epicForSubtask1 = new Epic("E1", "ED1");
        taskManager.createEpic(epicForSubtask1);
        Epic epicForSubtask2 = new Epic("E2", "ED2");
        taskManager.createEpic(epicForSubtask2);
        Subtask subtaskE1 = new Subtask(1, TaskType.SUBTASK, "S1", Status.IN_PROGRESS, "SD1",
                LocalDateTime.now(), Duration.ZERO, LocalDateTime.now(), epicForSubtask1.getId());
        taskManager.createSubtask(subtaskE1);
        Subtask subtaskE2 = new Subtask(2, TaskType.SUBTASK, "S2", Status.IN_PROGRESS, "SD2",
                LocalDateTime.now(), Duration.ZERO, LocalDateTime.now(), epicForSubtask2.getId());
        taskManager.createSubtask(subtaskE2);
        assertEquals(subtaskE1.getEpicId(), epicForSubtask1.getId(), "Айди эпика не совпадают");
        assertEquals(subtaskE2.getEpicId(), epicForSubtask2.getId(), "Айди эпика не совпадают");
    }
}