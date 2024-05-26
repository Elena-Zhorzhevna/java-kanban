package service.managers;

import model.Epic;
import model.Subtask;
import model.Task;

import java.io.IOException;
import java.util.List;

public interface TaskManager {
    List<Task> getAllTasks(); //получение всех подзадач

    List<Epic> getAllEpics(); //получение всех эпиков

    List<Subtask> getAllSubtasks(); //получение списка всех подзадач

    List<Subtask> getAllEpicSubtasks(Integer epicId); //получение списка всех подзадач у эпика

    boolean deleteAllTasks() throws IOException; //удаление всех задач

    boolean deleteAllEpics() throws IOException; //удаление всех эпиков и их подзадач

    boolean deleteAllSubtasks() throws IOException; //удаление всех подзадач

    Task getTaskById(Integer id); //получение задачи по айди

    Epic getEpicById(Integer id); //получение эпика по айди

    Subtask getSubtaskById(Integer id); //получение подзадачи по айди

    Task createTask(Task task) throws IOException; //добавление новой задачи

    Epic createEpic(Epic epic) throws IOException; //добавление эпика

    Subtask createSubtask(Subtask subtask) throws IOException; //добавление подзадачи

    void updateTask(Task newTask) throws IOException; //обновление задачи

    void updateEpic(Epic newEpic) throws IOException; //обновление эпика

    void updateSubtask(Subtask newSubtask) throws IOException; //обновление подзадачи

    void deleteByTaskId(int id) throws IOException; //удаление задачи по айди

    void deleteSubtaskById(int id); //удаление подзадачи по айди

    void deleteEpicById(int id) throws IOException; //удаление эпика по айди

    public List<Task> getHistory(); //получение списка 10 последних просмотренных задач
}
