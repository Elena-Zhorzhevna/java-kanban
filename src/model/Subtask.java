package model;

import enums.Status;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description) {
        super(name, description);
        this.setStatus(Status.NEW);
    }

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.setStatus(Status.NEW);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int Id) {
        this.epicId = Id;
    }

    @Override
    public String toString() {
        return "SubTask {" +
                "taskName ='" + getTaskName() +
                ", status =" + getStatus() +
                ", id =" + getId() +
                ", epic_id =" + epicId + '}';
    }
}

