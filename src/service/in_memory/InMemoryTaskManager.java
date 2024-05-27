package service.in_memory;

import enums.Status;
import model.Epic;
import model.Subtask;
import model.Task;
import service.managers.HistoryManager;
import service.managers.TaskManager;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected static final Map<Integer, Task> tasks = new HashMap<>(); //хэш-таблица задач
    protected static final Map<Integer, Subtask> subtasks = new HashMap<>(); //хэш-таблица подзадач
    protected static final Map<Integer, Epic> epics = new HashMap<>(); //хэш-таблица эпиков

    protected HistoryManager historyManager; //объект хранит просмотренные задачи

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    private static int id = 0; //айди задач

    //список задач и подзадач, отсортированный по времени их начала
    protected Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime,
            Comparator.nullsLast(Comparator.naturalOrder())));

    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    public void setPrioritizedTasks(Set<Task> prioritizedTasks) {
        this.prioritizedTasks = prioritizedTasks;
    }

    @Override
    public List<Task> getAllTasks() { //получение списка всех задач
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() { //получение списка всех эпиков
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() { //получение списка всех подзадач
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Subtask> getAllEpicSubtasks(Integer epicId) { //получение списка всех подзадач у эпика
        return epics
                .get(epicId)
                .getSubtaskEpicsId()
                .stream()
                .map(subtasks::get)
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteAllTasks() throws IOException { //удаление всех задач
        tasks.values().stream().peek(t -> historyManager.remove(t.getId()))
                .filter(t -> t.getStartTime() != null)
                .forEach(prioritizedTasks::remove);
        tasks.clear();
        return true;
    }

    @Override
    public boolean deleteAllEpics() { //удаление всех эпиков и их подзадач
        epics.entrySet()
                .stream()
                .peek(it -> {
                            historyManager.remove(it.getKey());
                            if (it.getValue().getStartTime() != null) {
                                prioritizedTasks.remove(it.getValue());
                            }
                        }
                )
                .map(Map.Entry::getValue)
                .flatMap(it -> it.getSubtaskEpicsId().stream())
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .forEach(it -> {
                            if (historyManager.getHistory() != null) {
                                historyManager.remove(it.getId());
                            }

                            if (it.getStartTime() != null) {
                                prioritizedTasks.remove(it);
                            }
                        }
                );
        epics.clear();
        subtasks.clear();
        return true;
    }

    @Override
    public boolean deleteAllSubtasks() { //удаление всех подзадач
        subtasks.entrySet()
                .stream()
                .peek(it -> {
                            historyManager.remove(it.getKey());
                            if (it.getValue().getStartTime() != null) {
                                prioritizedTasks.remove(it.getValue());
                            }
                        }
                );
        epics.values().stream()
                .peek(Epic::deleteSubtaskEpicsId)
                .peek(this::changeEpicTimeAndDuration)
                .map(Epic::getId)
                .peek(this::calculateEpicStatus);
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
    public Task createTask(Task task) throws IOException { //добавление новой задачи
        task.setId(++id);
        tasks.put(task.getId(), task);
        addToPrioritizedList(task);
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) throws IOException { //добавление нового эпика
        epic.setId(++id);
        epics.put(epic.getId(), epic);
        addToPrioritizedList(epic);
        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) throws IOException { //добавление новой подзадачи
        subtask.setId(++id);
        subtasks.put(subtask.getId(), subtask);
        addToPrioritizedList(subtask);
        Epic epic = epics.get(subtask.getEpicId());
        epic.getSubtaskEpicsId().add(subtask.getId());
        calculateEpicStatus(epic.getId());
        if (subtask.getStartTime() != null) {
            changeEpicTimeAndDuration(epic);
        }
        return subtask;
    }

    @Override //обновление задачи
    public void updateTask(Task newTask) throws IOException { //обновление задачи
        tasks.put(newTask.getId(), newTask);
        addToPrioritizedList(newTask);
    }

    @Override //обновление эпика
    public void updateEpic(Epic newEpic) throws IOException {
        epics.put(newEpic.getId(), newEpic);
        addToPrioritizedList(newEpic);
    }

    @Override //обновление подзадачи
    public void updateSubtask(Subtask newSubtask) throws IOException {
        subtasks.put(newSubtask.getId(), newSubtask);
        calculateEpicStatus(newSubtask.getEpicId());
        addToPrioritizedList(newSubtask);
        changeEpicTimeAndDuration(epics.get(newSubtask.getEpicId()));
    }

    @Override
    public void deleteByTaskId(int id) throws IOException { //удаление задачи по айди
        if (tasks.get(id).getStartTime() != null) {
            prioritizedTasks.remove(tasks.get(id));
        }
        tasks.remove(id);
        historyManager.remove(id); //удаление задачи из истории просмотров
    }

    @Override
    public void deleteSubtaskById(int id) { //удаление подзадачи по айди
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            Epic epic = epics.get(subtask.getEpicId());
            epic.getSubtaskEpicsId().remove(Integer.valueOf(id));
            subtasks.remove(id);
            historyManager.remove(id);
            prioritizedTasks.remove(subtask);
        }
    }

    @Override
    public void deleteEpicById(int id) throws IOException { //удаляение эпика по айди
        if (epics.containsKey(id)) {
            epics.get(id).getSubtaskEpicsId()
                    .forEach(subtaskId -> {
                                prioritizedTasks.remove(subtasks.get(subtaskId));
                                historyManager.remove(subtaskId);
                                subtasks.remove(subtaskId);
                            }
                    );
            epics.remove(id);
            historyManager.remove(id);
            prioritizedTasks.remove(epics.get(id));
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

    //метод добавления задач и подзадач в отсортированный список
    private void addToPrioritizedList(Task taskToAdd) {
        if (!ifTasksHaveIntersection(taskToAdd)) {
            return;
        }
        prioritizedTasks.add(taskToAdd);
    }

    // метод для изменения времени начала и продолжительности эпика относительно его подзадач
    protected void changeEpicTimeAndDuration(Epic epic) {
        epic.setStartTime(calculateEpicStartTime(epic));
        epic.setEndTime(calculateEpicEndTime(epic));
        epic.setDuration(calculateEpicDuration(epic));
    }

    //продолжительность эпика — сумма продолжительности всех его подзадач
    protected Duration calculateEpicDuration(Epic epic) {
        return epic.getSubtaskEpicsId().stream()
                .map(subtasks::get)
                .map(Subtask::getDuration)
                .reduce(Duration.ZERO, Duration::plus);
    }

    // время начала эпика — время начала самой ранней подзадачи
    protected LocalDateTime calculateEpicStartTime(Epic epic) {
        return epic.getSubtaskEpicsId().stream()
                .map(subtasks::get)
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);
    }

    //время завершения эпика — время окончания самой поздней из задач
    protected LocalDateTime calculateEpicEndTime(Epic epic) {
        return epic.getSubtaskEpicsId().stream()
                .map(subtasks::get)
                .map(Subtask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }

    //метод проверяет задачи и подзадачи на перечение по времени выполнения
    private boolean ifTasksHaveIntersection(Task taskToAdd) {
        Objects.requireNonNull(taskToAdd);
        if (taskToAdd.getStartTime() == null) {
            return false;
        }
        LocalDateTime startTime = taskToAdd.getStartTime();
        LocalDateTime endTime = taskToAdd.getEndTime();
        Set<Task> listOfTasks = getPrioritizedTasks();
        for (var task : listOfTasks) {
            LocalDateTime taskStart = task.getStartTime();
            LocalDateTime taskEnd = task.getEndTime();
            if ((startTime.isBefore(taskStart) && (endTime.isAfter(taskStart)) ||
                    (startTime.isBefore(taskStart) && endTime.isAfter(taskEnd)) ||
                    (startTime.isBefore(taskEnd)) && endTime.isAfter(taskEnd)) ||
                    (startTime.isAfter(taskStart)) && endTime.isBefore(taskEnd) ||
                    (startTime == taskStart) || (startTime == taskEnd) || (endTime == taskStart) || (endTime == taskEnd)) {

                throw new RuntimeException("Произошло наложение задач по времени! Измените время начала.");
            }
        }
        return true;
    }
}