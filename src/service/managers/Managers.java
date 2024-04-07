package service.managers;

import service.InMemory.InMemoryHistoryManager;
import service.InMemory.InMemoryTaskManager;

public class Managers {
    public static TaskManager getDefault() {

        return new InMemoryTaskManager();
    }

    public static InMemoryHistoryManager getDefaultHistory() {

        return new InMemoryHistoryManager();
    }
}
