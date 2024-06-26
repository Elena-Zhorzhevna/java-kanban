package service.managers;

import model.Task;

import java.util.List;

public interface HistoryManager {  //интерфейс для управления историей просмотров

    void add(Task task);  //помечает задачи как просмотренные

    void remove(int id); //удаляет задачи из просмотра

    List<Task> getHistory();  //возвращает список просмотренных задач
}