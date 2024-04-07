package model;

import enums.Status;
import org.junit.jupiter.api.Test;
import service.InMemory.InMemoryTaskManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TaskTest {
    InMemoryTaskManager taskManager = new InMemoryTaskManager();

    @Test
    void addNewTaskTest() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", Status.NEW);
        int taskId = taskManager.createTask(task).getId();
        Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
        List<Task> tasks = taskManager.getAllTasks();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test //должен совпадать со своей копией
    void taskShouldEqualsWithCopyTest() {
        Task task = new Task("Task1", "description1", Status.NEW, 1);
        Task taskExpected = task;
        assertEquals(taskExpected, task, "Задачи должны совпадать");
    }
}