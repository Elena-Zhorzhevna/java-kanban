package model;

import enums.Status;

import java.util.ArrayList;
import java.util.Arrays;

public class Epic extends Task {
    private ArrayList<Integer> subtaskEpicsId = new ArrayList<>();

    public void setSubtaskEpicsId(ArrayList<Integer> subtaskEpicsId) {
        this.subtaskEpicsId = subtaskEpicsId;
    }

    public Epic() {
    }

    public Epic(String name, String description) {
        super(name, description);
        setStatus(Status.NEW);
    }

    public ArrayList<Integer> getSubtaskEpicsId() {
        return subtaskEpicsId;
    }

    public void deleteSubtaskEpicsId(int id) {
        subtaskEpicsId.remove(id);
    }


    @Override
    public String toString() {
        return "model.Epic{" +
                "taskName ='" + getTaskName() +
                ", id =" + getId() +
                ", status =" + getStatus() +
                ", subtasksList ='" + Arrays.toString(new ArrayList[]{subtaskEpicsId}) + '}';

    }
}
