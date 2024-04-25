package service;

import model.Epic;
import model.Subtask;
import model.Task;
import service.InMemory.InMemoryHistoryManager;
import service.InMemory.InMemoryTaskManager;
import service.managers.HistoryManager;
import service.managers.TaskManager;

public class Main {
    public static void main(String[] args) {

        // Необязательное задание из ТЗ
        HistoryManager h = new InMemoryHistoryManager();
        TaskManager m = new InMemoryTaskManager(h);

        Task task1 = new Task("T1", "TD1");
        m.createTask(task1);
        Task task2 = new Task("T2", "TD2");
        m.createTask(task2);
        Epic epic1 = new Epic("E1", "ED1");
        m.createEpic(epic1);
        Subtask s1 = new Subtask("S1", "SD1", epic1.getId());
        m.createSubtask(s1);
        Subtask s2 = new Subtask("S2", "SD2", epic1.getId());
        m.createSubtask(s2);
        Subtask s3 = new Subtask("S3", "SD3", epic1.getId());
        m.createSubtask(s3);
        Epic epic2 = new Epic("E2", "ED2");
        m.createEpic(epic2);

        m.getTaskById(task1.getId());
        m.getTaskById(task2.getId());
        m.getTaskById(task1.getId());
        m.getSubtaskById(s3.getId());
        m.getEpicById(epic1.getId());
        m.getEpicById(epic2.getId());
        m.getSubtaskById(s2.getId());
        m.getSubtaskById(s3.getId());
        System.out.println(h.getHistory());
        m.deleteByTaskId(task2.getId());
        System.out.println(h.getHistory());
        m.deleteEpicById(epic1.getId());
        System.out.println(h.getHistory());
    }
}
