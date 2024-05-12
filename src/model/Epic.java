package model;

import enums.Status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subtaskEpicsId = new ArrayList<>(); //список айди подзадач у эпика
    private int epicId; //айди эпика

    public Epic() {
    }

    public Epic(String name, String description) {
        super(name, description);
        setStatus(Status.NEW);
    }

    public Epic(String taskName, String description, Status status) {
        super(taskName, description, status);
    }

    public Epic(int epicId, String name, String description) {
        super(name, description);
        this.epicId = epicId;
    }

    public Epic(int epicId, String name, String description, Status status) {
        super(name, description, status);
        this.epicId = epicId;
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

    public int getEpicId() {
        return epicId;
    } //получает айди эпика

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    } //устанавливает айди эпика

    @Override
    public String toString() {
        return "model.Epic{" +
                "taskName ='" + getTaskName() +
                ", id =" + getId() +
                ", status =" + getStatus() +
                ", subtasksList ='" + Arrays.toString(new List[]{subtaskEpicsId}) + '}';
    }
}