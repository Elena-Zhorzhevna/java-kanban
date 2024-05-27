package model;

import enums.Status;
import enums.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private int epicId; //айди эпика
    private TaskType type = TaskType.SUBTASK; //тип подзадачи

    public Subtask() {
    }

    public Subtask(String name, String description) {
        super(name, description);
        this.setStatus(Status.NEW);
    }

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.setStatus(Status.NEW);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, Status status, int id, int epicId) {
        super(name, description, status, id);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, Status status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public Subtask(int id, TaskType type, String taskName, Status status, String description, int epicId) {
        super(id, type, taskName, status, description);
        this.epicId = epicId;
    }

    public Subtask(int id, TaskType type, String taskName, Status status, String description, LocalDateTime startTime,
                   Duration duration, LocalDateTime endTime, int epicId) {
        super(id, type, taskName, status, description, startTime, duration, endTime);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    } //получение айди эпика

    public void setEpicId(int id) { //установление айди эпика
        this.epicId = id;
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
        return "SubTask {" +
                "taskName ='" + getTaskName() +
                ", status =" + getStatus() +
                ", id =" + getId() +
                ", epic_id =" + epicId +
                ", duration = " + getDuration().toMinutes() +
                ", start_time ='" + getStartTime() +
                ", end_time ='" + getEndTime() + '\'' +
                '}';
    }
}