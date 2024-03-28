package model;

import enums.Status;

import java.util.Objects;

public class Task {
    private String taskName;
    private String description;
    private int id;
    private Status status;


    public Task(String taskName, String description) {
        this.status = Status.NEW;
        this.taskName = taskName;
        this.description = description;
    }

    public Task(String taskName) {
        this.taskName = taskName;
    }

    public Task(String taskName, String description, int id) {
        this.status = Status.NEW;
        this.taskName = taskName;
        this.description = description;
        this.id = id;
    }

    public Task() {
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

