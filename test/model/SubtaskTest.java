package model;

import org.junit.jupiter.api.Test;
import service.InMemory.InMemoryTaskManager;
import service.managers.TaskManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SubtaskTest {
    TaskManager taskManager = new InMemoryTaskManager();

    @Test
        //проверка добавления новой подзадачи
    void addNewSubtaskTest() {
        Epic epic = new Epic("Test epicForSubtask", "Test epicDescription");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description",
                epic.getId());
        int subtaskId = taskManager.createSubtask(subtask).getId();
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
}