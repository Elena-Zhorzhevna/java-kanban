package service.InMemory;

import enums.Status;
import model.Epic;
import model.Subtask;
import model.Task;
import service.managers.HistoryManager;
import service.managers.TaskManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    protected static final Map<Integer, Task> tasks = new HashMap<>(); //хэш-таблица задач
    protected static final Map<Integer, Subtask> subtasks = new HashMap<>(); //хэш-таблица подзадач
    protected static final Map<Integer, Epic> epics = new HashMap<>(); //хэш-таблица эпиков

    private final HistoryManager historyManager; //объект хранит просмотренные задачи

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    private static int id = 0; //айди задач

    @Override
    public List<Task> getAllTasks() { //получение списка всех задач
        List<Task> list = new ArrayList<Task>();
        list.addAll(tasks.values());
        return list;
    }

    @Override
    public List<Epic> getAllEpics() { //получение списка всех эпиков
        List<Epic> list = new ArrayList<Epic>();
        list.addAll(epics.values());
        return list;
    }

    @Override
    public List<Subtask> getAllSubtasks() { //получение списка всех подзадач
        List<Subtask> list = new ArrayList<Subtask>();
        list.addAll(subtasks.values());
        return list;
    }

    @Override
    public List<Subtask> getAllEpicSubtasks(Integer epicId) { //получение списка всех подзадач у эпика
        Epic epic = epics.get(epicId);
        List<Subtask> list = new ArrayList<>();
        for (Integer i : epic.getSubtaskEpicsId()) {
            list.add(subtasks.get(i));
        }
        return list;
    }

    @Override
    public boolean deleteAllTasks() { //удаление всех задач
        tasks.clear();
        return true;
    }

    @Override
    public boolean deleteAllEpics() { //удаление всех эпиков и их подзадач
        epics.clear();
        subtasks.clear();
        return true;
    }

    @Override
    public boolean deleteAllSubtasks() { //удаление всех подзадач
        subtasks.clear();
        return true;
    }

    @Override
    public Task getTaskById(Integer id) { //получение задачи по айди
        historyManager.add(tasks.get(id)); //добавление задачи в историю просмотров
        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(Integer id) { //получение эпика по айди
        historyManager.add(epics.get(id)); //добавление эпика в историю просмотров
        return epics.get(id);
    }

    @Override
    public Subtask getSubtaskById(Integer id) { //получение подзадачи по айди
        historyManager.add(subtasks.get(id)); //добавление подзадачи в историю просмотров
        return subtasks.get(id);
    }

    @Override
    public Task createTask(Task task) { //добавление новой задачи
        task.setId(++id);
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) { //добавление нового эпика
        epic.setId(++id);
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) { //добавление новой подзадачи
        subtask.setId(++id);
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        epic.getSubtaskEpicsId().add(subtask.getId());
        calculateEpicStatus(epic.getId());
        return subtask;
    }

    @Override //обновление задачи
    public void updateTask(Task newTask) { //обновление задачи
        tasks.put(newTask.getId(), newTask);
    }

    @Override //обновление эпика
    public void updateEpic(Epic newEpic) {
        epics.put(newEpic.getId(), newEpic);
        calculateEpicStatus(newEpic.getId());
    }

    @Override //обновление подзадачи
    public void updateSubtask(Subtask newSubtask) {
        subtasks.put(newSubtask.getId(), newSubtask);
        calculateEpicStatus(newSubtask.getEpicId());
    }

    @Override
    public void deleteByTaskId(int id) { //удаление задачи по айди
        tasks.remove(id);
        historyManager.remove(id); //удаление задачи из истории просмотров
    }

    @Override
    public void deleteSubtaskById(int id) { //удаление подзадачи по айди
        if (subtasks.containsKey(id)) {
            Epic epic = epics.get(subtasks.get(id).getEpicId());
            if (epic.getSubtaskEpicsId().contains(id)) {
                for (int i = 0; i < epic.getSubtaskEpicsId().size(); i++) {
                    if (epic.getSubtaskEpicsId().get(i) == id) {
                        epic.getSubtaskEpicsId().remove(i);
                        historyManager.remove(i);
                        break;
                    }
                }
            }
            subtasks.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteEpicById(int id) { //удаляение эпика по айди

        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            for (Integer i : epic.getSubtaskEpicsId()) {
                if (subtasks != null) {
                    subtasks.remove(i);
                    historyManager.remove(i);
                }
            }
            epics.remove(id);
            historyManager.remove(id);
        }
    }

    public List<Task> getHistory() { //возвращает 10 последних просмотренных задач
        return historyManager.getHistory();
    }

    private void calculateEpicStatus(Integer epicId) { //рассчитывает статус эпика
        Epic epic = getEpicById(epicId);
        List<Subtask> list = getAllEpicSubtasks(epicId);
        boolean allDone = true;
        boolean allNew = true;
        for (Subtask subtask : list) {
            if (subtask.getStatus() != Status.DONE) {
                allDone = false;
            }
            if (subtask.getStatus() != Status.NEW) {
                allNew = false;
            }
        }
        Status result;
        if (allDone) {
            result = Status.DONE;
        } else if (allNew) {
            result = Status.NEW;
        } else {
            result = Status.IN_PROGRESS;
        }
        epic.setStatus(result);
    }
}