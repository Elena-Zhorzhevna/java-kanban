package model;

import enums.Status;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.InMemory.InMemoryHistoryManager;
import service.InMemory.InMemoryTaskManager;
import service.managers.HistoryManager;
import service.managers.TaskManager;

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
    void addNewEpicTest() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description", Status.NEW);
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
    void ChangeEpicStatusAllSubtaskStatusNewTest() {
        Epic epic = new Epic("EpicForSubtask", "EpicsDescription", Status.NEW);
        taskManager.createEpic(epic);
        //Все подзадачи со статусом NEW
        Subtask subtask1 = new Subtask("Subtask1", "SubtasksDescription",
                Status.NEW, epic.getId());
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
    void ChangeEpicStatusAllSubtaskStatusDoneTest() {
        Epic epic = new Epic("EpicForSubtask", "EpicsDescription", Status.NEW);
        taskManager.createEpic(epic);
        //Все подзадачи со статусом DONE
        Subtask subtask1 = taskManager.createSubtask(new Subtask("Subtask1", "SubtasksDescription",
                Status.DONE, epic.getId()));
        Subtask subtask2 = taskManager.createSubtask(new Subtask("Subtask2",
                "SubtasksDescription2", Status.DONE, epic.getId()));
        assertEquals(Status.DONE, taskManager.getEpicById(epic.getId()).getStatus());
        //Если список пуст
        taskManager.deleteAllSubtasks();
        assertEquals(0, taskManager.getAllSubtasks().size());
    }

    @Test
        //Проверка статуса эпика, если все подзадачи IN_PROGRESS
    void ChangeEpicStatusAllSubtaskStatusInProgressTest() {
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
    void ChangeEpicStatusSubtaskStatusDoneNewTest() {
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

    @AfterEach
    void afterEach() {
        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();
        taskManager.deleteAllSubtasks();
    }
}
