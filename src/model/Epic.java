package model;

import enums.Status;
import enums.TaskType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subtaskEpicsId = new ArrayList<>(); //список айди подзадач у эпика

    private int id; //айди эпика
    private TaskType type = TaskType.EPIC;

    public Epic() {
    }

    public Epic(String name, String description) {
        super(name, description);
        setStatus(Status.NEW);
    }

    public Epic(String taskName, String description, Status status) {
        super(taskName, description, status);
    }

    public Epic(int id, String name, String description) {
        super(name, description);
        this.id = id;
    }

    public Epic(int id, String name, String description, Status status) {
        super(name, description, status);
        this.id = id;
    }

    public Epic(int id, TaskType type, String taskName, String description, Status status) {
        super(id, type, taskName, status, description);
    }

    public List<Integer> getSubtaskEpicsId() {
        return subtaskEpicsId;
    }

    public void setSubtaskEpicsId(List<Integer> subtaskEpicsId) {
        this.subtaskEpicsId = subtaskEpicsId;
    }

    //удаляет из списка айди подзадачи
    public void deleteSubtaskEpicsId(int id) {
        subtaskEpicsId.remove(id);
    }

    @Override
    public TaskType getType() {
        return type;
    }

    @Override
    public void setType(TaskType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "model.Epic{" +
                "taskName ='" + getTaskName() +
                ", id =" + getId() +
                ", status =" + getStatus() +
                ", subtasksList ='" + Arrays.toString(new List[]{subtaskEpicsId}) + '}';
    }
}