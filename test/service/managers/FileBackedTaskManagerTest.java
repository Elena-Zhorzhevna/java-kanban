package service.managers;

import enums.Status;
import enums.TaskType;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.exception.ManagerSaveException;
import service.in_memory.InMemoryHistoryManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    File testFile;

    @Override
    protected FileBackedTaskManager createManager() {
        return new FileBackedTaskManager(new InMemoryHistoryManager());
    }

    @BeforeEach
    public void setUp() throws ManagerSaveException {
        {
            try {  //создание временного файла
                testFile = File.createTempFile("testTask", ".csv");
                System.out.println(
                        "Temporary file is located on Default location: "
                                + testFile.getAbsolutePath());
            } catch (IOException e) {
                throw new ManagerSaveException(e.getMessage(), e);
            }
        }
    }

    @Test
    void managerSaveExceptionTest() { //проверка корректного перехвата исключений при работе с файлами
        assertThrows(ManagerSaveException.class, () -> {
            FileBackedTaskManager.loadFromFile(Path.of("memory-file.csv").toFile());
        }, "Файл отсутствует");
    }

    @Test     //проверка сохранения нескольких задач
    public void savingTwoTasksInFileTest() throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(testFile));
        Task task = new Task();
        task.setId(1);
        task.setType(TaskType.TASK);
        task.setTaskName("Task1");
        task.setStatus(Status.IN_PROGRESS);
        task.setDescription("dt1");
        task.setStartTime(LocalDateTime.now());
        task.setDuration(Duration.ofMinutes(12000));
        task.setEndTime(task.getEndTime());
        manager.createTask(task);
        Task task2 = new Task();
        task2.setId(6);
        task2.setType(TaskType.TASK);
        task2.setTaskName("t2");
        task2.setStatus(Status.IN_PROGRESS);
        task2.setDescription("t2d");
        task2.setStartTime(LocalDateTime.of(2024, Month.MAY, 24, 0, 0));
        task2.setDuration(Duration.ofMinutes(1500));
        task2.setEndTime(task2.getEndTime());
        manager.createTask(task2);
        bw.write(manager.toString(task));
        bw.write(manager.toString(task2));
        bw.close();
        String expectedString = String.join("", manager.toString(task), manager.toString(task2));
        String actualString = Files.readString(testFile.toPath());
        BufferedReader br = new BufferedReader(new FileReader(testFile));
        br.close();
        assertEquals(expectedString, actualString, "wrong file format");
    }

    @Test    //проверка сохранения и загрузки пустого файла
    public void savingAndUploadingAnEmptyFileTest() throws IOException {
        manager = FileBackedTaskManager.loadFromFile(testFile);
        assertEquals(0, manager.getAllEpics().size());
        assertEquals(0, manager.getAllTasks().size());
        assertEquals(0, manager.getAllSubtasks().size());
    }

    @Test    //проверка загрузки нескольких задач
    public void uploadingTasksTest() throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(testFile));

        Task task8 = new Task();
        task8.setId(8);
        task8.setType(TaskType.TASK);
        task8.setTaskName("TestTask8");
        task8.setStatus(Status.IN_PROGRESS);
        task8.setDescription("dt8");
        task8.setStartTime(LocalDateTime.of(2024, Month.MAY, 22, 0, 0));
        task8.setDuration(Duration.ofMinutes(6000));
        task8.setEndTime(task8.getEndTime());
        manager.createTask(task8);

        Task task9 = new Task();
        task9.setId(9);
        task9.setType(TaskType.TASK);
        task9.setTaskName("t9");
        task9.setStatus(Status.IN_PROGRESS);
        task9.setDescription("t9d");
        task9.setStartTime(LocalDateTime.of(2024, Month.JUNE, 1, 10, 0));
        task9.setDuration(Duration.ofMinutes(4000));
        task9.setEndTime(task9.getEndTime());
        manager.createTask(task9);

        Epic epic10 = new Epic();
        epic10.setId(10);
        epic10.setType(TaskType.EPIC);
        epic10.setTaskName("e10");
        epic10.setStatus(Status.IN_PROGRESS);
        epic10.setDescription("e10d");
        epic10.setStartTime(LocalDateTime.of(2024, Month.JULY, 5, 8, 45));
        epic10.setDuration(Duration.ofMinutes(450));
        epic10.setEndTime(epic10.getEndTime());
        manager.createEpic(epic10);

        Subtask subtask11 = new Subtask();
        subtask11.setId(11);
        subtask11.setType(TaskType.SUBTASK);
        subtask11.setTaskName("Subtask11");
        subtask11.setStatus(Status.NEW);
        subtask11.setDescription("s11d");
        subtask11.setStartTime(LocalDateTime.of(2024, Month.AUGUST, 10, 12, 0));
        subtask11.setDuration(Duration.ofMinutes(120));
        subtask11.setEndTime(subtask11.getEndTime());
        subtask11.setEpicId(epic10.getId());
        manager.createSubtask(subtask11);

        bw.write(manager.toString(task8));
        bw.write(manager.toString(task9));
        bw.write(manager.toString(epic10));
        bw.write(manager.toString(subtask11));
        bw.close();
        manager.loadFromFile(testFile);
        assertEquals(2, manager.getAllTasks().size());
        assertEquals(1, manager.getAllEpics().size());
        assertEquals(1, manager.getAllSubtasks().size());
    }

    @AfterEach
    void deleteTestFileAndTasks() throws IOException {
        manager.deleteAllTasks();
        manager.deleteAllEpics();
        manager.deleteAllSubtasks();
        testFile.deleteOnExit();
    }
}