package model;

import enums.Status;
import enums.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
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

    public Epic(int id, TaskType type, String taskName, Status status, String description, LocalDateTime startTime,
                Duration duration, LocalDateTime endTime) {
        super(id, type, taskName, status, description, startTime, duration, endTime);
    }

    public List<Integer> getSubtaskEpicsId() {
        return subtaskEpicsId;
    }

    public void setSubtaskEpicsId(List<Integer> subtaskEpicsId) {
        this.subtaskEpicsId = subtaskEpicsId;
    }

    public void deleteSubtaskEpicsId() {
        subtaskEpicsId.clear();
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
                ", duration = " + getDuration().toMinutes() +
                ", start_time ='" + getStartTime() +
                ", end_time ='" + getEndTime() + '\'' +
                ", subtasksList ='" + Arrays.toString(new List[]{subtaskEpicsId}) +
                '}';
    }
}