package service.InMemory;

import model.Task;
import service.managers.HistoryManager;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> viewedTasks = new ArrayList<>(); //список просмотренных задач

    @Override
    public void add(Task task) {  //добавление задачи в историю просмотров
        if (viewedTasks.size() >= 10) {
            viewedTasks.remove(0);
        }
        viewedTasks.add(task);
    }

    @Override
    public List<Task> getHistory() { //получение списка просмотренных задач
        return viewedTasks;
    } //получение списка 10 последних просмотренных задач

}
