
package service.in_memory;

import enums.Status;
import enums.TaskType;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.managers.HistoryManager;
import service.managers.Managers;
import service.managers.TaskManagerTest;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    private HistoryManager historyManager;

    public InMemoryTaskManagerTest() {
        this.historyManager = Managers.getDefaultHistory();
        this.manager = createManager();
    }

    protected InMemoryTaskManager createManager() {
        return new InMemoryTaskManager(historyManager);
    }

    @Test
        //проверка пересечения задач без наложения по времени
    void addTaskWithoutIntersectionTest() throws IOException {
        Task task1 = new Task(20, TaskType.TASK, "ЗадачаДляПроверкиПересечения1", Status.NEW,
                "Описание1", LocalDateTime.of(2024, Month.MAY, 20, 10, 20),
                Duration.ofMinutes(2880),
                LocalDateTime.of(2024, Month.MAY, 20, 10, 20).plusMinutes(2880));
        Task task2 = new Task(21, TaskType.TASK, "НепересекающаясяЗадача2", Status.NEW,
                "Описание2", LocalDateTime.of(2024, Month.MAY, 10, 11, 10),
                Duration.ofMinutes(300),
                LocalDateTime.of(2024, Month.MAY, 10, 11, 10).plusMinutes(300));
        manager.createTask(task1);
        assertEquals(1, manager.getAllTasks().size());
        assertEquals(1, manager.getPrioritizedTasks().size());
        manager.createTask(task2);
        assertEquals(2, manager.getAllTasks().size());
        assertEquals(2, manager.getPrioritizedTasks().size());
    }

    @Test
        //проверка добавления задачи, пересекающейся частично по времени
    void addTaskWithIntersection() throws IOException {
        final String expectedExceptionMessage = "Произошло наложение задач по времени! Измените время начала.";
        Task task1 = new Task(20, TaskType.TASK, "ЗадачаДляПроверкиПересечения1", Status.NEW,
                "Описание1", LocalDateTime.of(2024, Month.MAY, 20, 10, 20),
                Duration.ofMinutes(2880),
                LocalDateTime.of(2024, Month.MAY, 20, 10, 20).plusMinutes(2880));
        manager.createTask(task1);
        assertEquals(1, manager.getAllTasks().size());
        assertEquals(1, manager.getPrioritizedTasks().size());

        Epic epic3 = new Epic(22, TaskType.TASK, "ЧастичноПересекающийсяЭпик3", Status.NEW,
                "Описание3", LocalDateTime.of(2024, Month.MAY, 20, 20, 20),
                Duration.ofMinutes(3000),
                LocalDateTime.of(2024, Month.MAY, 20, 20, 20).plusMinutes(3000));

        final Exception actualException = Assertions.assertThrows(RuntimeException.class,
                () -> {
                    manager.createEpic(epic3);
                });
        Assertions.assertEquals(expectedExceptionMessage, actualException.getMessage());
        Assertions.assertEquals(1, manager.getAllEpics().size());
        Assertions.assertEquals(1, manager.getPrioritizedTasks().size());
    }

    @Test
        //проверка добавления эпика с равным временем начала существующей задачи
    void addEpicWithBoundaryStartTimeIntersectionTest() throws IOException {
        final String expectedExceptionMessage = "Произошло наложение задач по времени! Измените время начала.";
        Task task12 = new Task(12, TaskType.TASK, "ЗадачаДляПроверкиПересечения", Status.NEW,
                "Описание1", LocalDateTime.of(2024, Month.MAY, 16, 10, 20),
                Duration.ofMinutes(60),
                LocalDateTime.of(2024, Month.MAY, 16, 10, 20).plusMinutes(60));
        manager.createTask(task12);
        assertEquals(1, manager.getAllTasks().size());
        assertEquals(1, manager.getPrioritizedTasks().size());

        Epic epic13 = new Epic(13, TaskType.EPIC, "ЭпикСГраничнымПересечением", Status.NEW,
                "Описание", LocalDateTime.of(2024, Month.MAY, 16, 10, 20),
                Duration.ofMinutes(3000),
                LocalDateTime.of(2024, Month.MAY, 16, 10, 20).plusMinutes(3000));

        final Exception actualException = Assertions.assertThrows(RuntimeException.class,
                () -> {
                    manager.createEpic(epic13);
                });
        Assertions.assertEquals(expectedExceptionMessage, actualException.getMessage());
        assertEquals(1, manager.getAllTasks().size());
        Assertions.assertEquals(1, manager.getAllEpics().size());
        Assertions.assertEquals(1, manager.getPrioritizedTasks().size());
    }

    @Test
        //проверка добавления подзадачи с гпаничным временем начала
    void addSubtaskWithBoundaryStartTimeIntersectionTest() throws IOException {
        final String expectedExceptionMessage = "Произошло наложение задач по времени! Измените время начала.";
        Epic epic14 = new Epic(14, TaskType.EPIC, "ЭпикДляПроверкиПересечения", Status.NEW,
                "ОписаниеЭ14", LocalDateTime.of(2024, Month.MAY, 14, 14, 14),
                Duration.ofMinutes(60),
                LocalDateTime.of(2024, Month.MAY, 14, 14, 14).plusMinutes(60));
        manager.createEpic(epic14);
        assertEquals(1, manager.getAllEpics().size());
        assertEquals(1, manager.getPrioritizedTasks().size());

        Subtask subtask15 = new Subtask(15, TaskType.SUBTASK, "ПодзадачаСГраничнымПересечением", Status.NEW,
                "Описание", LocalDateTime.of(2024, Month.MAY, 14, 15, 13),
                Duration.ofMinutes(20),
                LocalDateTime.of(2024, Month.MAY, 14, 15, 13).plusMinutes(20),
                epic14.getId());

        final Exception actualException = Assertions.assertThrows(RuntimeException.class,
                () -> {
                    manager.createSubtask(subtask15);
                });
        Assertions.assertEquals(expectedExceptionMessage, actualException.getMessage());
        assertEquals(1, manager.getAllEpics().size());
        Assertions.assertEquals(1, manager.getAllSubtasks().size());
        Assertions.assertEquals(1, manager.getPrioritizedTasks().size());
    }

    @AfterEach
    void afterEach() throws IOException {
        manager.deleteAllTasks();
        manager.deleteAllEpics();
        manager.deleteAllSubtasks();
    }
}