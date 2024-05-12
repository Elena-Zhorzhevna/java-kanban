package model;

import enums.Status;
import enums.TaskType;

import java.util.Objects;

public class Task {

    private TaskType type = TaskType.TASK; //тип задачи
    private String taskName; //название задачи
    private String description; //описание задачи
    private int id; //айди задачи
    private Status status; //статус задачи

    public Task() {
    }
    public Task(String taskName) {
        this.taskName = taskName;
    }

    public Task(String taskName, String description) {
        this.status = Status.NEW;
        this.taskName = taskName;
        this.description = description;
    }

    public Task(String taskName, String description, int id) {
        this.status = Status.NEW;
        this.taskName = taskName;
        this.description = description;
        this.id = id;
    }

    public Task(String taskName, String description, Status status) {
        this.taskName = taskName;
        this.description = description;
        this.status = status;
    }

    public Task(String taskName, String description, Status status, int id) {
        this.taskName = taskName;
        this.description = description;
        this.status = status;
        this.id = id;
    }

    public Task(int id, TaskType type, String taskName, Status status, String description) {
        this.id = id;
        this.type = type;
        this.taskName = taskName;
        this.description = description;
        this.status = status;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public TaskType getType() {
        return type;
    }

    public void setType(TaskType type) {
        this.type = type;
    }

    public Integer getId(int id) {
        this.id = id;
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && taskName.equals(task.taskName)
                && description.equals(task.description)
                && status.equals(task.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskName, description, id, status);
    }

    @Override
    public String toString() {
        return "model.Task{" +
                "name ='" + taskName +
                ", description ='" + description +
                ", id =" + id +
                ", status ='" + status + '}';
    }
}