package service.managers;

import enums.Status;
import enums.TaskType;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;

    abstract protected T createManager();

    public TaskManagerTest() {
        manager = createManager();
    }

    @Test
        //проверка добавления задачи
    void createTaskTest() throws IOException {
        Task testTask = new Task();
        testTask.setId(1);
        testTask.setType(TaskType.TASK);
        testTask.setTaskName("Задача1");
        testTask.setStatus(Status.NEW);
        testTask.setDescription("Описание");
        testTask.setStartTime(LocalDateTime.of(2024, Month.MAY, 1, 10, 0));
        testTask.setDuration(Duration.ofMinutes(5000));
        testTask.setEndTime(LocalDateTime.of(2024, Month.MAY, 1, 10, 0).plusMinutes(5000));
        manager.createTask(testTask);
        assertNotNull(manager.getTaskById(testTask.getId()));
        assertEquals(testTask, manager.getTaskById(testTask.getId()));
    }

    @Test
        //проверка получения всех задач
    void getAllTasksTest() throws IOException {
        Task task1 = new Task(1, TaskType.TASK, "Тестовая задача1", Status.IN_PROGRESS,
                "ОписаниеТЗ1",
                LocalDateTime.of(2024, Month.MAY, 15, 10, 15), Duration.ofMinutes(150),
                LocalDateTime.of(2024, Month.MAY, 15, 10, 15).plusMinutes(150));
        manager.createTask(task1);
        Task task2 = new Task(2, TaskType.TASK, "Тестовая задача2", Status.NEW, "Описание ТЗ2",
                LocalDateTime.now(), Duration.ofMinutes(1400), LocalDateTime.now().plusMinutes(1400));
        manager.createTask(task2);
        //Если есть задачи в списке
        List<Task> testList1 = new ArrayList<>(List.of(task1, task2));
        List<Task> testList2 = manager.getAllTasks();
        assertEquals(testList1.size(), testList2.size());
        //Если списк пуст
        manager.deleteAllTasks();
        assertTrue(manager.getAllTasks().isEmpty());
    }


    @Test
        //проверка получения всех эпиков
    void getAllEpicsTest() throws IOException {
        Epic epic1 = new Epic(3, TaskType.EPIC, "Эпик1", Status.NEW, "Описание эпика1",
                LocalDateTime.of(2024, Month.MAY, 11, 11, 11), Duration.ofMinutes(15),
                LocalDateTime.of(2024, Month.MAY, 11, 11, 11).plusMinutes(15));
        manager.createEpic(epic1);
        Epic epic2 = new Epic(4, TaskType.EPIC, "Эпик2", Status.NEW, "Описание эпика2",
                LocalDateTime.of(2024, Month.MAY, 12, 12, 12), Duration.ofMinutes(12),
                LocalDateTime.of(2024, Month.MAY, 12, 12, 12).plusMinutes(12));
        manager.createEpic(epic2);
        Epic epic3 = new Epic(5, TaskType.EPIC, "Эпик3", Status.NEW, "Описание эпика3",
                LocalDateTime.of(2024, Month.MAY, 13, 13, 13), Duration.ofMinutes(13),
                LocalDateTime.of(2024, Month.MAY, 13, 13, 13).plusMinutes(13));
        manager.createEpic(epic3);
        // Если есть задачи в списке
        List<Epic> testList1 = new ArrayList<>(List.of(epic1, epic2, epic3));
        List<Epic> testList2 = manager.getAllEpics();
        assertEquals(testList1.size(), testList2.size());
        //Если список пуст
        manager.deleteAllEpics();
        assertTrue(manager.getAllEpics().isEmpty());
    }

    @Test
        //проверка получения всех подзадач
    void getAllSubtasksTest() throws IOException {
        Epic epic = new Epic(6, TaskType.EPIC, "ЭпикДляПодзадач", Status.NEW,
                "Описание эпикаДляПодзадач", LocalDateTime.of(2024, Month.MAY, 20, 20,
                20), Duration.ofMinutes(200), LocalDateTime.of(2024, Month.MAY, 20, 20,
                20).plusMinutes(200));
        manager.createEpic(epic);
        Subtask subtask1 = new Subtask(7, TaskType.SUBTASK, "Подзадача1", Status.IN_PROGRESS,
                "Описание подзадачи1", LocalDateTime.of(2024, Month.MAY, 15, 15,
                15), Duration.ofMinutes(200), LocalDateTime.of(2024, Month.MAY, 15, 15,
                15).plusMinutes(200), epic.getId());
        manager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask(8, TaskType.SUBTASK, "Подзадача2", Status.IN_PROGRESS,
                "Описание подзадачи2", LocalDateTime.of(2024, Month.MAY, 16, 16,
                16), Duration.ofMinutes(20), LocalDateTime.of(2024, Month.MAY, 16, 16,
                16).plusMinutes(160), epic.getId());
        manager.createSubtask(subtask2);
        Subtask subtask3 = new Subtask(9, TaskType.SUBTASK, "Подзадача3", Status.NEW,
                "Описание подзадачи3", LocalDateTime.of(2024, Month.MAY, 17, 17,
                17), Duration.ofMinutes(200), LocalDateTime.of(2024, Month.MAY, 17, 17,
                17).plusMinutes(200), epic.getId());
        manager.createSubtask(subtask3);
        // Если есть задачи в списке
        List<Subtask> testList1 = new ArrayList<>(List.of(subtask1, subtask2, subtask3));
        List<Subtask> testList2 = manager.getAllSubtasks();
        assertEquals(testList1, testList2);
        //Если список пуст
        manager.deleteAllSubtasks();
        assertTrue(manager.getAllSubtasks().isEmpty());
    }

    @Test
        //проверка получения задачи по айди
    void getTaskByIdTest() throws IOException {
        Task task = new Task("Task1", "Description");
        manager.createTask(task);
        assertEquals(task, manager.getTaskById(task.getId()));
    }

    @Test
        //проверка получения эпика по айди
    void getEpicByIdTest() throws IOException {
        Epic epic = new Epic("Epic1", "EpicsDescription");
        manager.createEpic(epic);
        assertEquals(epic, manager.getEpicById(epic.getId()));
    }

    @Test
        //проверка получения подзадачи по айди
    void getSubtaskByIdTest() throws IOException {
        Epic epic = new Epic("EpicForSubtask", "EpicsDescription");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask1", "SubtasksDescritpion", epic.getId());
        manager.createSubtask(subtask);
        assertEquals(subtask, manager.getSubtaskById(subtask.getId()));
    }

    @Test
        //проверка добавления нового эпика
    void сreateEpicTest() throws IOException {
        //не пустой
        Epic testEpic = new Epic("Эпик 1", "Описание Эпика 1", Status.IN_PROGRESS);
        manager.createEpic(testEpic);
        assertEquals(manager.getEpicById(testEpic.getId()).getTaskName(), testEpic.getTaskName());
        assertEquals(manager.getEpicById(testEpic.getId()).getDescription(), testEpic.getDescription());
        assertEquals(manager.getEpicById(testEpic.getId()).getStatus(), testEpic.getStatus());
        //с пустым списком задач
        manager.deleteAllEpics();
        assertTrue(manager.getAllEpics().isEmpty());
    }

    @Test
        //проверка добавления новой подзадачи
    void createSubtaskTest() throws IOException {
        Epic epic1 = new Epic("Эпик для подзадачи1", "Описание эпика подзадачи1", Status.NEW);
        manager.createEpic(epic1);
        Subtask subtask = new Subtask("Подзадача1", "Описание подзадачи1", Status.NEW,
                4, epic1.getId());
        manager.createSubtask(subtask);
        assertNotNull(manager.getSubtaskById(subtask.getId()));
        manager.createSubtask(subtask);
        assertEquals(subtask, manager.getSubtaskById(subtask.getId()));
    }

    @Test
        //проверка обновления задачи
    void updateTaskTest() throws IOException {
        Task task = new Task("Задача1", "Описание задачи 1", Status.NEW);
        manager.createTask(task);
        //Если не пустой
        Task testTask = new Task("НаЗАМЕНУ1", "НАзаменуОП", Status.IN_PROGRESS, task.getId());
        manager.updateTask(testTask);
        assertEquals(manager.getTaskById(testTask.getId()), testTask);
        assertEquals(manager.getTaskById(testTask.getId()).getTaskName(), testTask.getTaskName());
        assertEquals(manager.getTaskById(testTask.getId()).getDescription(), testTask.getDescription());
        assertEquals(manager.getTaskById(testTask.getId()).getStatus(), testTask.getStatus());
        //С пустым списком задач
        manager.deleteAllTasks();
        assertTrue(manager.getAllTasks().isEmpty());
    }

    @Test
        //проверка обновления эпика
    void updateEpicTest() throws IOException {
        Epic epic = new Epic("Эпик1", "Описание эпика1", Status.IN_PROGRESS);
        manager.createEpic(epic);
        Epic testEpic = new Epic("ЭпикНАЗАМЕНУ","ОписаниеЭНАЗАМЕНУ", Status.NEW, epic.getId());
        manager.updateEpic(testEpic);
        assertEquals(manager.getEpicById(testEpic.getId()), testEpic);
        assertEquals(manager.getEpicById(testEpic.getId()).getTaskName(), testEpic.getTaskName());
        assertEquals(manager.getEpicById(testEpic.getId()).getDescription(), testEpic.getDescription());
        assertEquals(manager.getEpicById(testEpic.getId()).getStatus(), testEpic.getStatus());
        //если список пуст
        manager.deleteAllEpics();
        assertTrue(manager.getAllEpics().isEmpty());
    }

    @Test
        //проверка обновления подзадачи
    void updateSubtaskTest() throws IOException {
        Epic epic = new Epic("Эпик1", "ОписаниеЭ1", Status.IN_PROGRESS);
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача1", "Описание подзадачи1", epic.getId());
        manager.createSubtask(subtask);
        Subtask testSubtask = new Subtask("Подзадача на замену", "ОписаниеЗамена", epic.getId());
        manager.updateSubtask(testSubtask);
        assertEquals(manager.getSubtaskById(testSubtask.getId()).getTaskName(), testSubtask.getTaskName());
        assertEquals(manager.getSubtaskById(testSubtask.getId()).getDescription(), testSubtask.getDescription());
        //Если список пуст
        manager.deleteAllSubtasks();
        assertTrue(manager.getAllSubtasks().isEmpty());
    }

    @Test
        //проверка неизменности задачи при добавлении в менеджер
    void taskTheSameInAllFieldsWhenAddingATaskToManagerTest() throws IOException {
        Task testTask = new Task("Задача1", "Описание задачи1", Status.NEW);
        manager.createTask(testTask);
        assertEquals(manager.getTaskById(testTask.getId()).getTaskName(), testTask.getTaskName());
        assertEquals(manager.getTaskById(testTask.getId()).getDescription(), testTask.getDescription());
        assertEquals(manager.getTaskById(testTask.getId()).getStatus(), testTask.getStatus());
        assertEquals(manager.getTaskById(testTask.getId()).getId(), testTask.getId());
    }

    @Test
        //проверка неизменности эпика при добавлении в менеджер
    void epicTheSameInAllFieldsWhenAddingATaskToManagerTest() throws IOException {
        Epic testEpic = new Epic("Эпик1", "Описание эпика 1", Status.IN_PROGRESS);
        manager.createEpic(testEpic);
        assertEquals(manager.getEpicById(testEpic.getId()).getTaskName(), testEpic.getTaskName());
        assertEquals(manager.getEpicById(testEpic.getId()).getDescription(), testEpic.getDescription());
        assertEquals(manager.getEpicById(testEpic.getId()).getStatus(), testEpic.getStatus());
        assertEquals(manager.getEpicById(testEpic.getId()).getId(), testEpic.getId());
    }

    @Test
        //проверка неизменности подзадачи при добавлении в менеджер
    void subtaskTheSameInAllFieldsWhenAddingATaskToManagerTest() throws IOException {
        Epic testEpic = new Epic("Эпик для подзадачи1", "Описание Эпика для подзадачи");
        manager.createEpic(testEpic);
        Subtask subtask = new Subtask(" Подзадача1", "Описание подзадачи1", Status.IN_PROGRESS,
                testEpic.getId());
        manager.createSubtask(subtask);
        assertEquals(manager.getSubtaskById(subtask.getId()).getTaskName(), subtask.getTaskName());
        assertEquals(manager.getSubtaskById(subtask.getId()).getDescription(), subtask.getDescription());
        assertEquals(manager.getSubtaskById(subtask.getId()).getStatus(), subtask.getStatus());
        assertEquals(manager.getSubtaskById(subtask.getId()).getId(), subtask.getId());
    }

    @Test
        //удаляемые подзадачи не должны хранить внутри себя старые id
    void deletedSubtasksShouldNotStoreOldIDsTest() throws IOException {
        Epic epicForSubtask = new Epic("Test epicForSubtask", "Test epicDescription");
        manager.createEpic(epicForSubtask);
        Subtask subtask1 = new Subtask("Test Subtask1", "Test Subtask1 description",
                epicForSubtask.getId());
        manager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Test Subtask2", "Test Subtask2 description",
                epicForSubtask.getId());
        manager.createSubtask(subtask2);
        int index = subtask1.getId();
        assertTrue(manager.getAllEpicSubtasks(epicForSubtask.getId()).contains(subtask1));
        manager.deleteSubtaskById(index);
        assertFalse(manager.getAllEpicSubtasks(epicForSubtask.getId()).contains(subtask1));
        Subtask subtask3 = new Subtask("S3", "SD3", Status.NEW, epicForSubtask.getId());
        manager.createSubtask(subtask3);
        subtask3.setId(index);
        assertEquals(index, subtask3.getId());
        assertTrue(manager.getAllEpicSubtasks(epicForSubtask.getId()).contains(subtask3));
    }

    @Test
        //внутри эпиков не должно оставаться неактуальных id подзадач
    void ThereShouldBeNoIrrelevantSubtaskIDsInsideTheEpicsTest() throws IOException {
        Epic epicForSubtask = new Epic("Test epicForSubtask", "Test epicDescription");
        manager.createEpic(epicForSubtask);
        Epic epicForSubtask2 = new Epic("Test epicForSubtask", "Test epicDescription");
        manager.createEpic(epicForSubtask2);
        Subtask subtask1 = new Subtask("Test Subtask1", "Test Subtask1 description",
                epicForSubtask.getId());
        manager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Test Subtask2", "Test Subtask2 description",
                epicForSubtask.getId());
        manager.createSubtask(subtask2);
        Subtask subtask3ForEpic2 = new Subtask("TestSubtask3", "Test SD3", epicForSubtask2.getId());
        manager.createSubtask(subtask3ForEpic2);
        assertEquals(2, manager.getAllEpicSubtasks(epicForSubtask.getId()).size());
        assertEquals(1, manager.getAllEpicSubtasks(epicForSubtask2.getId()).size());
        manager.deleteSubtaskById(subtask2.getId());
        assertEquals(1, manager.getAllEpicSubtasks(epicForSubtask.getId()).size());
        manager.deleteSubtaskById(subtask3ForEpic2.getId());
        assertTrue(manager.getAllEpicSubtasks(epicForSubtask2.getId()).isEmpty());
    }

    @AfterEach
    void afterEach() throws IOException {
        manager.deleteAllTasks();
        manager.deleteAllEpics();
        manager.deleteAllSubtasks();
    }
}