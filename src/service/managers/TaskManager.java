package service.managers;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;

public interface TaskManager {
    List<Task> getAllTasks(); //получение всех подзадач

    List<Epic> getAllEpics(); //получение всех эпиков

    List<Subtask> getAllSubtasks(); //получение списка всех подзадач

    List<Subtask> getAllEpicSubtasks(Integer epicId); //получение списка всех подзадач у эпика

    boolean deleteAllTasks(); //удаление всех задач

    boolean deleteAllEpics(); //удаление всех эпиков и их подзадач

    boolean deleteAllSubtasks(); //удаление всех подзадач

    Task getTaskById(Integer id); //получение задачи по айди

    Epic getEpicById(Integer id); //получение эпика по айди

    Subtask getSubtaskById(Integer id); //получение подзадачи по айди

    Task createTask(Task task); //добавление новой задачи

    Epic createEpic(Epic epic); //добавление эпика

    Subtask createSubtask(Subtask subtask); //добавление подзадачи

    void updateTask(Task newTask); //обновление задачи

    void updateEpic(Epic newEpic); //обновление эпика

    void updateSubtask(Subtask newSubtask); //обновление подзадачи

    void deleteByTaskId(int id); //удаление задачи по айди

    void deleteSubtaskById(int id); //удаление подзадачи по айди

    void deleteEpicById(int id); //удаление эпика по айди

    public List<Task> getHistory(); //получение списка 10 последних просмотренных задач
}
