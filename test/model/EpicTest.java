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
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EpicTest {
    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager(historyManager);
    }

    @Test
        //проверка добавления нового эпика
    void addNewEpicTest() throws IOException {
        Epic epic = new Epic(1, TaskType.EPIC, "Test addNewEpic", Status.NEW,
                "Test addNewEpic description", null, null, null);
        int epicId = taskManager.createEpic(epic).getId();
        Epic savedEpic = taskManager.getEpicById(epicId);
        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epic, savedEpic, "Задачи не совпадают.");
        List<Epic> epics = taskManager.getAllEpics();
        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.get(0), "Задачи не совпадают.");
    }

    @Test
        //проверка, совпадает ли эпик со своей копией
    void epicShouldEqualsWithCopyTest() {
        Epic epic = new Epic("Epic1", "description1", Status.NEW);
        Epic epikExpected = epic;
        assertEquals(epikExpected, epic, "Задачи должны совпадать");
    }

    @Test
        //Проверка статуса эпика, если все подзадачи со статусом - NEW
    void changeEpicStatusAllSubtaskStatusNewTest() throws IOException {
        Epic epic = new Epic("EpicForSubtask", "EpicsDescription", Status.NEW);
        taskManager.createEpic(epic);
        //Все подзадачи со статусом NEW
        Subtask subtask1 = new Subtask();
        subtask1.setId(3);
        subtask1.setType(TaskType.SUBTASK);
        subtask1.setTaskName("Subtask1");
        subtask1.setStatus(Status.NEW);
        subtask1.setDescription("SubtaskDescription");
        subtask1.setStartTime(LocalDateTime.of(2024, Month.MAY, 25, 10, 50));
        subtask1.setDuration(Duration.ofMinutes(5));
        subtask1.setEndTime(LocalDateTime.of(2024, Month.MAY, 25, 10, 10));
        subtask1.setEpicId(epic.getId());
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask2",
                "SubtasksDescription2", Status.NEW, epic.getId());
        taskManager.createSubtask(subtask2);
        assertEquals(Status.NEW, taskManager.getEpicById(epic.getId()).getStatus());
        //Если список пуст
        taskManager.deleteAllSubtasks();
        assertEquals(0, taskManager.getAllSubtasks().size());
    }

    @Test
        //Проверка статуса эпика, если все подзадачи со статусом - DONE
    void changeEpicStatusAllSubtaskStatusDoneTest() throws IOException {
        Epic epic = new Epic("EpicForSubtask", "EpicsDescription", Status.NEW);
        taskManager.createEpic(epic);
        //Все подзадачи со статусом DONE
        Subtask subtask1 = taskManager.createSubtask(new Subtask(1, TaskType.SUBTASK, "Subtask1",
                Status.DONE, "SubtasksDescription", LocalDateTime.now(), Duration.ofMinutes(400),
                LocalDateTime.now().plusMinutes(400), epic.getId()));
        Subtask subtask2 = taskManager.createSubtask(new Subtask(1, TaskType.SUBTASK, "Subtask2",
                Status.DONE, "SubtasksDescription2",
                LocalDateTime.of(2024, Month.MAY, 10, 12, 15),
                Duration.ofMinutes(1300),
                LocalDateTime.of(2024, Month.MAY, 10, 14, 45).plusMinutes(1300),
                epic.getId()));
        assertEquals(Status.DONE, taskManager.getEpicById(epic.getId()).getStatus());
        //Если список пуст
        taskManager.deleteAllSubtasks();
        assertEquals(0, taskManager.getAllSubtasks().size());
    }

    @Test
        //Проверка статуса эпика, если все подзадачи IN_PROGRESS
    void changeEpicStatusAllSubtaskStatusInProgressTest() throws IOException {
        Epic epic = new Epic("EpicForSubtask", "EpicsDescription", Status.NEW);
        taskManager.createEpic(epic);
        // Все подзадачи IN_PROGRESS
        Subtask subtask1 = taskManager.createSubtask(new Subtask("Subtask1", "SubtasksDescription",
                Status.IN_PROGRESS, epic.getId()));
        Subtask subtask2 = taskManager.createSubtask(new Subtask("Subtask2",
                "SubtasksDescription2", Status.IN_PROGRESS, epic.getId()));
        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epic.getId()).getStatus());
        //Если список пуст
        taskManager.deleteAllSubtasks();
        assertEquals(0, taskManager.getAllSubtasks().size());
    }

    @Test
        //Проверка статуса эпика, если подзадачи имеют статусы - NEW и DONE
    void changeEpicStatusSubtaskStatusDoneNewTest() throws IOException {
        Epic epic = new Epic("EpicForSubtask", "EpicsDescription", Status.NEW);
        taskManager.createEpic(epic);
        //Подзадачи со статусом DONE и NEW
        Subtask subtask1 = taskManager.createSubtask(new Subtask("Subtask1", "SubtasksDescription",
                Status.DONE, epic.getId()));
        Subtask subtask2 = taskManager.createSubtask(new Subtask("Subtask2",
                "SubtasksDescription2", Status.NEW, epic.getId()));
        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epic.getId()).getStatus());
        //Если список пуст
        taskManager.deleteAllSubtasks();
        assertEquals(0, taskManager.getAllSubtasks().size());
    }

    @Test
        //проверка изменения времени начала и продолжительности эпика относительно его подзадач
    void changeEpicTimeAndDurationTest() throws IOException {
        Epic epicForTimeTest = new Epic(18, TaskType.EPIC, "ЭпикДляТестаИзменменияВремени", Status.NEW,
                "ОписаниеЭ18", LocalDateTime.of(2024, Month.MAY, 18, 18, 18),
                Duration.ofMinutes(60),
                LocalDateTime.of(2024, Month.MAY, 18, 18, 18).plusMinutes(60));
        taskManager.createEpic(epicForTimeTest);
        Subtask subtaskForTimeTest = new Subtask(19, TaskType.SUBTASK, "ПодзадачаДляТестаИзмененияВремени",
                Status.IN_PROGRESS, "ОписаинеП19",
                LocalDateTime.of(2024, Month.MAY, 16, 10, 20), Duration.ofMinutes(10),
                LocalDateTime.of(2024, Month.MAY, 16, 10, 20).plusMinutes(10),
                epicForTimeTest.getId());
        taskManager.createSubtask(subtaskForTimeTest);
        assertEquals(subtaskForTimeTest.getStartTime(), epicForTimeTest.getStartTime());
        assertEquals(subtaskForTimeTest.getDuration(), epicForTimeTest.getDuration());
        assertEquals(subtaskForTimeTest.getEndTime(), epicForTimeTest.getEndTime());
        Subtask subtaskForTimeTest2 = new Subtask(28, TaskType.SUBTASK, "ПДляТестаИзмененияВремени2",
                Status.NEW, "ОписаниеП28",
                LocalDateTime.of(2024, Month.APRIL, 25, 10, 10), Duration.ofMinutes(120),
                LocalDateTime.of(2024, Month.APRIL, 25, 10, 10).plusMinutes(120),
                epicForTimeTest.getId());
        taskManager.createSubtask(subtaskForTimeTest2);
        assertEquals(subtaskForTimeTest2.getStartTime(), epicForTimeTest.getStartTime());
        assertEquals(subtaskForTimeTest.getDuration().plus(subtaskForTimeTest2.getDuration()),
                epicForTimeTest.getDuration());
    }

    @AfterEach
    void afterEach() throws IOException {
        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();
        taskManager.deleteAllSubtasks();
    }
}
