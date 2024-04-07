package service.InMemory;

import enums.Status;
import model.Epic;
import model.Subtask;
import model.Task;
import service.managers.TaskManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();

    InMemoryHistoryManager viewedTasks = new InMemoryHistoryManager();

    private static int id = 0;

    @Override
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> list = new ArrayList<Task>();
        list.addAll(tasks.values());
        return list;
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> list = new ArrayList<Epic>();
        list.addAll(epics.values());
        return list;
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        ArrayList<Subtask> list = new ArrayList<Subtask>();
        list.addAll(subtasks.values());
        return list;
    }

    @Override
    public ArrayList<Subtask> getAllEpicSubtasks(Integer epicId) {
        Epic epic = epics.get(epicId);
        ArrayList<Subtask> list = new ArrayList<>();
        for (Integer i : epic.getSubtaskEpicsId()) {
            list.add(subtasks.get(i));
        }
        return list;
    }

    @Override
    public boolean deleteAllTasks() {
        tasks.clear();
        return true;
    }

    @Override
    public boolean deleteAllEpics() {
        epics.clear();
        subtasks.clear();
        return true;
    }

    @Override
    public boolean deleteAllSubtasks() {
        subtasks.clear();
        return true;
    }

    @Override
    public Task getTaskById(Integer id) {
        viewedTasks.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(Integer id) {
        return epics.get(id);
    }

    @Override
    public Subtask getSubtaskById(Integer id) {
        return subtasks.get(id);
    }

    @Override
    public Task createTask(Task task) {
        task.setId(++id);
        tasks.put(task.getId(), task);

        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(++id);
        epics.put(epic.getId(), epic);

        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        subtask.setId(++id);
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        epic.getSubtaskEpicsId().add(subtask.getId());
        calculateEpicStatus(epic.getId());

        return subtask;
    }

    @Override
    public void updateTask(Task newTask) {
        tasks.put(newTask.getId(), newTask);
    }

    @Override
    public void updateEpic(Epic newEpic) {
        epics.put(newEpic.getId(), newEpic);
        calculateEpicStatus(newEpic.getId());
    }

    @Override
    public void updateSubtask(Subtask newSubtask) {
        subtasks.put(newSubtask.getId(), newSubtask);
        calculateEpicStatus(newSubtask.getEpicId());
    }

    @Override
    public void deleteByTaskId(int id) {
        tasks.remove(id);
    }

    @Override
    public void deleteSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            Epic epic = epics.get(subtasks.get(id).getEpicId());
            if (epic.getSubtaskEpicsId().contains(id)) {
                for (int i = 0; i < epic.getSubtaskEpicsId().size(); i++) {
                    if (epic.getSubtaskEpicsId().get(i) == id) {
                        epic.getSubtaskEpicsId().remove(i);
                        break;
                    }
                }
            }
            subtasks.remove(id);
        }
    }

    @Override
    public void deleteEpicById(int id) {

        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            for (Integer i : epic.getSubtaskEpicsId()) {
                if (subtasks != null) {
                    subtasks.remove(i);
                }
            }
            epics.remove(id);
        }
    }

    public List<Task> getHistory() {
        return viewedTasks.getHistory();
    }

    private void calculateEpicStatus(Integer epicId) {
        Epic epic = getEpicById(epicId);
        ArrayList<Subtask> list = getAllEpicSubtasks(epicId);
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


