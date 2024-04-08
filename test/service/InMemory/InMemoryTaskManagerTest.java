package service.InMemory;

import enums.Status;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.managers.TaskManager;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    TaskManager taskManager = new InMemoryTaskManager();

    @Test
        //проверка получения всех задач
    void getAllTasksTest() {
        Task task1 = new Task("Тестовая задача1", "ОписаниеТЗ1", Status.NEW);
        taskManager.createTask(task1);
        Task task2 = new Task("Тестовая задача2", "Описание ТЗ2", Status.NEW);
        taskManager.createTask(task2);
        //Если есть задачи в списке
        List<Task> testList1 = new ArrayList<>(List.of(task1, task2));
        List<Task> testList2 = taskManager.getAllTasks();
        assertEquals(testList1, testList2);
        //Если списк пуст
        taskManager.deleteAllTasks();
        assertTrue(taskManager.getAllTasks().isEmpty());
    }

    @Test
        //проверка получения всех эпиков
    void getAllEpicsTest() {
        Epic epic1 = new Epic("Эпик1", "Описание эпика1");
        taskManager.createEpic(epic1);
        Epic epic2 = new Epic("Эпик2", "Описание эпика2");
        taskManager.createEpic(epic2);
        Epic epic3 = new Epic("Эпик3", "Описание эпика3");
        taskManager.createEpic(epic3);
        // Если есть задачи в списке
        List<Epic> testList1 = new ArrayList<>(List.of(epic1, epic2, epic3));
        List<Epic> testList2 = taskManager.getAllEpics();
        assertEquals(testList1.size(), testList2.size());
        //Если список пуст
        taskManager.deleteAllEpics();
        assertTrue(taskManager.getAllEpics().isEmpty());
    }

    @Test
        //проверка получения всех подзадач
    void getAllSubtasksTest() {
        Epic epic = new Epic("ЭпикДляПодзадач", "ОписаниеЭпика для подзадач");
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Подзадача1", "Описание подзадачи1", epic.getId());
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Подзадача2", "Описание подзадачи2", epic.getId());
        taskManager.createSubtask(subtask2);
        Subtask subtask3 = new Subtask("Подзадача3", "Описание подзадачи3", epic.getId());
        taskManager.createSubtask(subtask3);
        // Если есть задачи в списке
        List<Subtask> testList1 = new ArrayList<>(List.of(subtask1, subtask2, subtask3));
        List<Subtask> testList2 = taskManager.getAllSubtasks();
        assertEquals(testList1, testList2);
        //Если список пуст
        taskManager.deleteAllSubtasks();
        assertTrue(taskManager.getAllSubtasks().isEmpty());
    }

    @Test
        //проверка получения задачи по айди
    void getTaskByIdTest() {
        Task task = new Task("Task1", "Description");
        taskManager.createTask(task);
        Assertions.assertEquals(task, taskManager.getTaskById(task.getId()));
    }

    @Test
        //проверка получения эпика по айди
    void getEpicByIdTest() {
        Epic epic = new Epic("Epic1", "EpicsDescription");
        taskManager.createEpic(epic);
        Assertions.assertEquals(epic, taskManager.getEpicById(epic.getId()));
    }

    @Test
        //проверка получения подзадачи по айди
    void getSubtaskByIdTest() {
        Epic epic = new Epic("EpicForSubtask", "EpicsDescription");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask1", "SubtasksDescritpion", epic.getId());
        taskManager.createSubtask(subtask);
        Assertions.assertEquals(subtask, taskManager.getSubtaskById(subtask.getId()));
    }

    @Test
        //проверка добавления новой задачи
    void createTaskTest() {
        Task task = new Task("Задача1", "Описание задачи 1", Status.NEW, 1);
        assertNull(taskManager.getTaskById(task.getId()));
        taskManager.createTask(task);
        assertNull(taskManager.getTaskById(7));
        Assertions.assertEquals(task, taskManager.getTaskById(task.getId()));
    }

    @Test
        //проверка добавления нового эпика
    void сreateEpicTest() {
        //Не пустой
        Epic testEpic = new Epic("Эпик 1", "Описание Эпика 1", Status.IN_PROGRESS);
        taskManager.createEpic(testEpic);
        assertEquals(taskManager.getEpicById(testEpic.getId()).getTaskName(), testEpic.getTaskName());
        assertEquals(taskManager.getEpicById(testEpic.getId()).getDescription(), testEpic.getDescription());
        assertEquals(taskManager.getEpicById(testEpic.getId()).getStatus(), testEpic.getStatus());
        //С пустым списком задач
        taskManager.deleteAllEpics();
        assertTrue(taskManager.getAllEpics().isEmpty());
    }

    @Test
        //проверка добавления новой подзадачи
    void createSubtaskTest() {
        Epic epic1 = new Epic("Эпик для подзадачи1", "Описание эпика подзадачи1", Status.NEW);
        taskManager.createEpic(epic1);
        Subtask subtask = new Subtask("Подзадача1", "Описание подзадачи1", Status.NEW, 4, epic1.getId());
        assertNull(taskManager.getSubtaskById(subtask.getId()));
        taskManager.createSubtask(subtask);
        assertNull(taskManager.getSubtaskById(9));
        Assertions.assertEquals(subtask, taskManager.getSubtaskById(subtask.getId()));
    }

    @Test
        //проверка обновления задачи
    void updateTaskTest() {
        Task task = new Task("Задача1", "Описание задачи 1", Status.NEW);
        taskManager.createTask(task);
        //Если не пустой
        Task testTask = new Task("НаЗАМЕНУ1", "НАзаменуОП", Status.IN_PROGRESS, task.getId());
        taskManager.updateTask(testTask);
        assertEquals(taskManager.getTaskById(testTask.getId()), testTask);
        assertEquals(taskManager.getTaskById(testTask.getId()).getTaskName(), testTask.getTaskName());
        assertEquals(taskManager.getTaskById(testTask.getId()).getDescription(), testTask.getDescription());
        assertEquals(taskManager.getTaskById(testTask.getId()).getStatus(), testTask.getStatus());
        //С пустым списком задач
        taskManager.deleteAllTasks();
        assertTrue(taskManager.getAllTasks().isEmpty());
    }

    @Test
        //проверка обновления эпика
    void updateEpicTest() {
        Epic epic = new Epic("Эпик1", "Описание эпика1", Status.IN_PROGRESS);
        taskManager.createEpic(epic);
        Epic testEpic = new Epic(epic.getId(), "ЭпикНАЗАМЕНУ", "ОписаниеЭНАЗАМЕНУ", Status.NEW);
        taskManager.updateEpic(testEpic);
        assertEquals(taskManager.getEpicById(testEpic.getId()), testEpic);
        assertEquals(taskManager.getEpicById(testEpic.getId()).getTaskName(), testEpic.getTaskName());
        assertEquals(taskManager.getEpicById(testEpic.getId()).getDescription(), testEpic.getDescription());
        assertEquals(taskManager.getEpicById(testEpic.getId()).getStatus(), testEpic.getStatus());
        //если список пуст
        taskManager.deleteAllEpics();
        assertTrue(taskManager.getAllEpics().isEmpty());
    }

    @Test
        //проверка обновления подзадачи
    void updateSubtaskTest() {
        Epic epic = new Epic("Эпик1", "ОписаниеЭ1", Status.IN_PROGRESS);
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача1", "Описание подзадачи1", epic.getId());
        taskManager.createSubtask(subtask);
        Subtask testSubtask = new Subtask("Подзадача на замену", "ОписаниеЗамена", epic.getId());
        taskManager.updateSubtask(testSubtask);
        assertEquals(taskManager.getSubtaskById(testSubtask.getId()).getTaskName(), testSubtask.getTaskName());
        assertEquals(taskManager.getSubtaskById(testSubtask.getId()).getDescription(), testSubtask.getDescription());
        //Если список пуст
        taskManager.deleteAllSubtasks();
        assertTrue(taskManager.getAllSubtasks().isEmpty());
    }

    @Test
        // проверка неизменности задачи при добавлении в менеджер
    void taskTheSameInAllFieldsWhenAddingATaskToManagerTest() {
        Task testTask = new Task("Задача1", "Описание задачи1", Status.NEW);
        taskManager.createTask(testTask);
        assertEquals(taskManager.getTaskById(testTask.getId()).getTaskName(), testTask.getTaskName());
        assertEquals(taskManager.getTaskById(testTask.getId()).getDescription(), testTask.getDescription());
        assertEquals(taskManager.getTaskById(testTask.getId()).getStatus(), testTask.getStatus());
        assertEquals(taskManager.getTaskById(testTask.getId()).getId(), testTask.getId());
    }

    @Test
        //проверка неизменности эпика при добавлении в менеджер
    void epicTheSameInAllFieldsWhenAddingATaskToManagerTest() {
        Epic testEpic = new Epic("Эпик1", "Описание эпика 1", Status.IN_PROGRESS);
        taskManager.createEpic(testEpic);
        assertEquals(taskManager.getEpicById(testEpic.getId()).getTaskName(), testEpic.getTaskName());
        assertEquals(taskManager.getEpicById(testEpic.getId()).getDescription(), testEpic.getDescription());
        assertEquals(taskManager.getEpicById(testEpic.getId()).getStatus(), testEpic.getStatus());
        assertEquals(taskManager.getEpicById(testEpic.getId()).getId(), testEpic.getId());
    }

    @Test
        //проверка неизменности подзадачи при добавлении в менеджер
    void subtaskTheSameInAllFieldsWhenAddingATaskToManagerTest() {
        Epic testEpic = new Epic("Эпик для подзадачи1", "Описание Эпика для подзадачи");
        taskManager.createEpic(testEpic);
        Subtask subtask = new Subtask(" Подзадача1", "Описание подзадачи1", Status.IN_PROGRESS,
                testEpic.getId());
        taskManager.createSubtask(subtask);
        assertEquals(taskManager.getSubtaskById(subtask.getId()).getTaskName(), subtask.getTaskName());
        assertEquals(taskManager.getSubtaskById(subtask.getId()).getDescription(), subtask.getDescription());
        assertEquals(taskManager.getSubtaskById(subtask.getId()).getStatus(), subtask.getStatus());
        assertEquals(taskManager.getSubtaskById(subtask.getId()).getId(), subtask.getId());
    }
}