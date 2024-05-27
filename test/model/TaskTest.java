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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TaskTest {
    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager(historyManager);
    }

    @Test
        //проверка добавления новой подзадачи
    void addNewTaskTest() throws IOException {    //убрать нули
        Task task = new Task(1, TaskType.TASK, "Test addNewTask", Status.NEW,
                "Test addNewTask description", null, null, null);
        int taskId = taskManager.createTask(task).getId();
        Task savedTask = taskManager.getTaskById(taskId);
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
        List<Task> tasks = taskManager.getAllTasks();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
        //проверка соответствия задачи своей копии
    void taskShouldEqualsWithCopyTest() {
        Task task = new Task("Task1", "description1", Status.NEW, 1);
        Task taskExpected = task;
        assertEquals(taskExpected, task, "Задачи должны совпадать");
    }

    @AfterEach
    void deleteTestTasks() throws IOException {
        taskManager.deleteAllTasks();
    }
}