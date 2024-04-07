package model;

import enums.Status;
import org.junit.jupiter.api.Test;
import service.InMemory.InMemoryTaskManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EpicTest {
    InMemoryTaskManager taskManager = new InMemoryTaskManager();

    @Test
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
        //должен совпадать со своей копией
    void epicShouldEqualsWithCopyTest() {
        Epic epic = new Epic("Epic1", "description1", Status.NEW);
        Epic epikExpected = epic;
        assertEquals(epikExpected, epic, "Задачи должны совпадать");
    }

    @Test
    void ChangeEpicStatusAllSubtaskStatusNewTest() {
        Epic epic = new Epic("EpicForSubtask", "EpicsDescription", Status.NEW);
        taskManager.createEpic(epic);
        //Все подзадачи со статусом NEW
        Subtask subtask1 = taskManager.createSubtask(new Subtask("Subtask1", "SubtasksDescription",
                Status.NEW, epic.getId()));
        Subtask subtask2 = taskManager.createSubtask(new Subtask("Subtask2",
                "SubtasksDescription2", Status.NEW, epic.getId()));
        assertEquals(Status.NEW, taskManager.getEpicById(epic.getId()).getStatus());
        //Если список пуст
        taskManager.deleteAllSubtasks();
        assertEquals(0, taskManager.getAllSubtasks().size());
    }

    @Test
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
}
