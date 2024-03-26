package service;

import enums.Status;
import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;


public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();

    private static int id = 0;

    public static int idGenerator() {
        ++id;
        return id;
    }

    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> list = new ArrayList<Task>();
        list.addAll(tasks.values());
        list.addAll(epics.values());
        list.addAll(subtasks.values());
        return list;
    }

    public ArrayList<Subtask> getAllEpicSubtasks(Integer id) {
        Epic epic = epics.get(id);
        ArrayList<Subtask> list = new ArrayList<>();
        for (Integer i : epic.getSubtaskEpicsId()) {
            list.add(subtasks.get(i));
        }
        return list;
    }

    public boolean deleteAllTasks() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
        return true;
    }

    public Task getTaskById(Integer id) {
        Task task = null;
        if (tasks.containsKey(id)) {
            task = tasks.get(id);
        }
        return task;
    }

    public Epic getEpicById(Integer id) {
        Epic epic = null;
        if (epics.containsKey(id)) {
            epic = epics.get(id);
        }
        return epic;
    }

    public Subtask getSubtaskById(Integer id) {
        Subtask subtask = null;
        if (subtasks.containsKey(id)) {
            subtask = subtasks.get(id);
        }
        return subtask;
    }

    public Task createTask(Task task) {
        task.setId(idGenerator());
        task.setStatus(Status.NEW);
        tasks.put(task.getId(), task);
        return task;
    }

    public Epic createEpic(Epic epic) {
        epic.setId(idGenerator());
        epic.setStatus(Status.NEW);
        epics.put(epic.getId(), epic);
        return epic;
    }

    public Subtask createSubtask(Subtask subtask) {

        if (epics != null) {
            subtask.setId(idGenerator());
            subtask.setStatus(Status.NEW);
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            epic.getSubtaskEpicsId().add(subtask.getId());
            calculateEpicStatus(epic.getId());
        }
        return subtask;
    }

    public boolean updateTask(int id, Task newTask) {
        boolean result = false;
        Task oldTask = tasks.get(id);
        if (tasks != null) {
            tasks.replace(id, oldTask, newTask);
            result = true;
        }
        return result;
    }

    public boolean updateEpic(int id, Epic newEpic) {
        boolean result;
        Epic oldEpic = epics.get(id);
        epics.replace(id, oldEpic, newEpic);
        result = true;
        calculateEpicStatus(id);
        return result;
    }

    public void updateSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        for (Integer Id : epic.subtaskEpicsId) {
            if (Id == subtask.getId()) {
                subtask.setTaskName(subtask.getTaskName());
                subtask.setDescription(subtask.getDescription());
                subtask.setStatus(subtask.getStatus());
            }
        }
        epics.put(epic.getId(), epic);
        calculateEpicStatus(epic.getId());
    }

    public void deleteByTaskId(Task task) {
        if (tasks != null) {
            tasks.remove(task.getId());
        }
    }

    public void deleteSubtaskById(int id) {
        if (epics != null && subtasks.containsKey(id)) {
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

    public void deleteEpicById(int id) {

        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            for (Integer i : epic.getSubtaskEpicsId()) {
                if (subtasks != null) {
                    subtasks.remove(i);
                }
                epics.remove(id);
            }
        }
    }

    private void calculateEpicStatus(Integer id) {
        Epic epic = getEpicById(id);
        ArrayList<Subtask> list = getAllEpicSubtasks(id);
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





