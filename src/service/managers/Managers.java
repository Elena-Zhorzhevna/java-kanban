package service.managers;

import service.InMemory.InMemoryHistoryManager;
import service.InMemory.InMemoryTaskManager;

public class Managers { //подбирает нужную реализацию TaskManager и возвращает объект правильного типа
    public static TaskManager getDefault() { //возвращает объект - менеджер

        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() { //возвращает объект - менеджер

        return new InMemoryHistoryManager();
    }
}
