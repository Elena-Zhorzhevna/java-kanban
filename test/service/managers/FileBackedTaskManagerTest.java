package service.managers;

import enums.Status;
import enums.TaskType;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.InMemory.InMemoryHistoryManager;

import java.io.*;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTaskManagerTest {
    File testFile;
    FileBackedTaskManager bm;
    HistoryManager hm = new InMemoryHistoryManager();

    @BeforeEach
    public void beforeEach() {
        {
            try {  //создание временного файла в указанном каталоге
                testFile = File.createTempFile("testTask", ".csv");
                System.out.println(
                        "Temporary file is located on Default location: "
                                + testFile.getAbsolutePath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        bm = new FileBackedTaskManager(testFile, hm);
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
        bm.createTask(task);
        Task task2 = new Task();
        task2.setId(6);
        task2.setType(TaskType.TASK);
        task2.setTaskName("t2");
        task2.setStatus(Status.IN_PROGRESS);
        task2.setDescription("t2d");
        bm.createTask(task2);
        bw.write(bm.toString(task));
        bw.write(bm.toString(task2));
        bw.close();
        String expectedString = String.join("", bm.toString(task), bm.toString(task2));
        String actualString = Files.readString(testFile.toPath());
        BufferedReader br = new BufferedReader(new FileReader(testFile));
        br.close();
        assertEquals(expectedString, actualString, "wrong file format");
    }

    @Test    //проверка сохранения и загрузки пустого файла
    public void savingAndUploadingAnEmptyFileTest() throws IOException {
        bm = FileBackedTaskManager.loadFromFile(testFile);
        assertEquals(0, bm.getAllEpics().size());
        assertEquals(0, bm.getAllTasks().size());
        assertEquals(0, bm.getAllSubtasks().size());
    }

    @Test    //проверка загрузки нескольких задач
    public void uploadingTasksTest() {
        bm = new FileBackedTaskManager(testFile, hm);
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(testFile));
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        Task task8 = new Task();
        task8.setId(8);
        task8.setType(TaskType.TASK);
        task8.setTaskName("TestTask8");
        task8.setStatus(Status.IN_PROGRESS);
        task8.setDescription("dt8");
        bm.createTask(task8);

        Task task9 = new Task();
        task9.setId(9);
        task9.setType(TaskType.TASK);
        task9.setTaskName("t9");
        task9.setStatus(Status.IN_PROGRESS);
        task9.setDescription("t9d");
        bm.createTask(task9);

        Epic epic10 = new Epic();
        epic10.setId(10);
        epic10.setType(TaskType.EPIC);
        epic10.setTaskName("e10");
        epic10.setStatus(Status.IN_PROGRESS);
        epic10.setDescription("e10d");
        bm.createEpic(epic10);

        Subtask subtask11 = new Subtask();
        subtask11.setId(11);
        subtask11.setType(TaskType.SUBTASK);
        subtask11.setTaskName("Subtask11");
        subtask11.setStatus(Status.NEW);
        subtask11.setDescription("s11d");
        subtask11.setEpicId(epic10.getId());
        bm.createSubtask(subtask11);

        try {
            bw.write(bm.toString(task8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            bw.write(bm.toString(task9));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            bw.write(bm.toString(epic10));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            bw.write(bm.toString(subtask11));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            bw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            bm.loadFromFile(testFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertEquals(2, bm.getAllTasks().size());
        assertEquals(1, bm.getAllEpics().size());
        assertEquals(1, bm.getAllSubtasks().size());
    }

    @AfterEach
    void deleteTestFileAndTasks() {
        bm.deleteAllTasks();
        bm.deleteAllEpics();
        bm.deleteAllSubtasks();
        testFile.deleteOnExit();
    }
}
